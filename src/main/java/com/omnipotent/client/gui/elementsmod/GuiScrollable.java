package com.omnipotent.client.gui.elementsmod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class GuiScrollable extends GuiScreen implements IScrollableGui {
    private static final String SEARCH = "search";
    protected GuiTextField searchBar;
    protected final List<GuiTextField> allElements = new ArrayList<>();
    protected final List<GuiTextField> fixedElements = new ArrayList<>();
    protected final List<GuiTextField> renderedScrollableElements = new ArrayList<>();
    protected String oldTextInSearchBox = "";
    protected boolean renderEffectsOnClick = false;
    protected int yAxisToRender;
    protected int commonHeightElement;
    protected int firstScrollableElementAxisY;
    protected int spacing;
    protected int idGuiText;
    protected float scrollSpeed = 1.1f;
    protected double posYStartRenderContinuesElements;
    protected double currentScrollOffset = 1;
    protected double targetScrollOffset = 1.0;

    @Override
    public void initGui() {
        resetGui();
        Keyboard.enableRepeatEvents(true);
        posYStartRenderContinuesElements = (double) height / 14;
        commonHeightElement = (int) (height / 21.25);
        searchBar = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 2.45), (height / 18), width / 5, height / 25);
        searchBar.setText(SEARCH);
        oldTextInSearchBox = SEARCH;
        spacing = height / 17;
        firstScrollableElementAxisY = (int) (posYStartRenderContinuesElements + spacing);
        addElements();
        yAxisToRender = height;
    }

    @Override
    public void resetGui() {
        fixedElements.clear();
        idGuiText = -1;
        allElements.clear();
        renderedScrollableElements.clear();
    }

    @Override
    public void addElements() {
        addScrollableElements();
        addFixedElements();
    }

    protected abstract void addScrollableElements();

    protected abstract void addFixedElements();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderElements(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Math.round(-Math.signum(Mouse.getEventDWheel()));
        updateScrollOffset(scroll);
    }

    @Override
    public void updateScrollOffset(int scroll) {
        if (scroll == 0)
            return;
        double newScrollOffset = currentScrollOffset + scroll * scrollSpeed;
        newScrollOffset = Math.max(1.0, Math.min(Integer.MAX_VALUE, newScrollOffset));
        targetScrollOffset = newScrollOffset;
    }

    @Override
    public void renderElements(int mouseX, int mouseY, float partialTicks) {
        fixedElements.forEach(GuiTextField::drawTextBox);
        renderedScrollableElements.clear();
        currentScrollOffset = currentScrollOffset + ((targetScrollOffset - currentScrollOffset) * 0.1);
        double currentScroll = currentScrollOffset;
        long round = Math.round(currentScroll);
        int minY = height / 10;
        double yOffset = (double) height / 9 * (1 / currentScrollOffset);
        int unrenderedElements = 1;
        Pattern pattern;
        String text = searchBar.getText();
        pattern = !text.trim().isEmpty() ? Pattern.compile(text, Pattern.CASE_INSENSITIVE) : null;
        double limit = height / 1.3;
        for (GuiTextField gui : allElements) {
            if (beyondLimit(yOffset, limit))
                break;
            if (!oldTextInSearchBox.equals(text)) {
                oldTextInSearchBox = text;
                targetScrollOffset = 1;
            }
            if (pattern == null || searchBar.getText().equals(SEARCH)) {
                if ((round != 1 && unrenderedElements < currentScroll)) {
                    unrenderedElements++;
                    continue;
                }
            } else if (!elementMatchingSearch(gui, pattern)) {
                unrenderedElements++;
                continue;
            }
            if (yOffset >= minY) {
                gui.y = (int) yOffset;
                gui.drawTextBox();
                renderedScrollableElements.add(gui);
            }
            yOffset += commonHeightElement;
        }
    }

    protected boolean beyondLimit(double yOffset, double limit) {
        return yOffset > limit;
    }

    protected boolean elementMatchingSearch(GuiTextField gui, Pattern pattern) {
        return pattern.matcher(gui.getText()).find();
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiTextField gui : fixedElements) {
            if (gui.mouseClicked(mouseX, mouseY, mouseButton)) {
                clickFixedElementsLogic(gui);
                break;
            }
        }
        if (mouseButton == 0)
            mouseClickLeft(mouseX, mouseY, mouseButton);
        else
            othersMouseClicks(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void clickFixedElementsLogic(GuiTextField gui) {
        int id = gui.getId();
        for (GuiTextField guiT : fixedElements) {
            if (guiT.getId() == id)
                continue;
            guiT.setFocused(false);
        }
    }

    protected void mouseClickLeft(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField guiTxt : renderedScrollableElements) {
            if (guiTxt.mouseClicked(mouseX, mouseY, mouseButton)) {
                guiTxt.setFocused(renderEffectsOnClick);
                clickScrollableElementLogic(guiTxt);
                break;
            }
        }
    }

    protected void othersMouseClicks(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField guiTxt : renderedScrollableElements) {
            guiTxt.mouseClicked(mouseX, mouseY, mouseButton);
            boolean isClicked = mouseX >= guiTxt.x && mouseX < guiTxt.x + guiTxt.width && mouseY >= guiTxt.y && mouseY < guiTxt.y + guiTxt.height;
            if (isClicked) {
                guiTxt.setFocused(renderEffectsOnClick);
                clickScrollableElementLogic(guiTxt);
                break;
            }
        }
    }

    protected abstract void clickScrollableElementLogic(GuiTextField guiTextField);

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        fixedElements.forEach(gui -> gui.textboxKeyTyped(typedChar, keyCode));
        renderedScrollableElements.forEach(guiTxt -> guiTxt.textboxKeyTyped(typedChar, keyCode));
        super.keyTyped(typedChar, keyCode);

    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
