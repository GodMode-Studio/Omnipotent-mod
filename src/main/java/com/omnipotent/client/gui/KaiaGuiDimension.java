package com.omnipotent.client.gui;

import com.omnipotent.client.gui.elementsmod.GuiTextFieldMod;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.omnipotent.util.KaiaConstantsNbt.kaiaDimension;

public class KaiaGuiDimension extends GuiScrollable {

    private final EntityPlayer player;
    private int widthOfScreen;
    private int heightOfScreen;
    private Minecraft minecraft;
    private final List<GuiTextFieldMod> elementsOfScreen = new ArrayList<>();
    private final List<GuiTextFieldMod> elementsInScreenRendered = new ArrayList<>();
    private int idGuiText = -1;
    List<GuiTextField> listFieldsTeleport = new ArrayList<>();
    private final HashMap<GuiTextField, Integer> guiFieldAndId = new HashMap<>();


    public KaiaGuiDimension(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        widthOfScreen = super.width;
        heightOfScreen = super.height;
        minecraft = Minecraft.getMinecraft();
        setCurrentScrollOffset(1.0);
        setTargetScrollOffset(1.0);
        elementsOfScreen.clear();
        elementsInScreenRendered.clear();
        listFieldsTeleport.clear();
        createElementsAndPopulateElementsOfScreen();
        addInputCoordenates();
        buttonList.clear();
        buttonList.add(new GuiButton(0, (int) (widthOfScreen / 2.2), (int) (heightOfScreen / 1.06), widthOfScreen / 12, heightOfScreen / 30, "Teleport"));
        setSearchBar(new GuiTextField(++idGuiText, fontRenderer, (int) (widthOfScreen / 2.45), (heightOfScreen / 18), widthOfScreen / 5, heightOfScreen / 25));
        UtilityHelper.sendMessageToPlayer(TextFormatting.GOLD + I18n.format("message.client.dimensionhelp"), player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        drawString(fontRenderer, I18n.format("guikaia.dimension"), (int) (widthOfScreen / 2.13), 1, Color.WHITE.getRGB());
        drawRect(0, (int) (heightOfScreen / 1.36), widthOfScreen, heightOfScreen, -804253680);
        renderElements(mouseX, mouseY, partialTicks);
        getSearchBar().drawTextBox();
        drawRect(0, (int) (heightOfScreen / 1.36), widthOfScreen, (int) (heightOfScreen / 1.4), Color.BLACK.getRGB());
        buttonList.forEach(button -> button.drawButton(minecraft, mouseX, mouseY, partialTicks));
        listFieldsTeleport.forEach(GuiTextField::drawTextBox);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        getSearchBar().mouseClicked(mouseX, mouseY, mouseButton);
        listFieldsTeleport.forEach(gui -> gui.mouseClicked(mouseX, mouseY, mouseButton));
        if (theClickInButtonChangeConfig(mouseX, mouseY, mouseButton)) return;
        for (GuiTextFieldMod gui : elementsOfScreen) {
            if (gui.mouseClicked(mouseX, mouseY, mouseButton)) {
                if (stopClickInGuisNoRended(gui)) {
                    gui.setTextColor(Color.CYAN.getRGB());
                    gui.setFocused(false);
                    continue;
                }
                gui.setSelected(true);
                gui.setTextColor(Color.GREEN.getRGB());
                gui.setFocused(false);
            } else {
                gui.setTextColor(Color.CYAN.getRGB());
                gui.setSelected(false);
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean theClickInButtonChangeConfig(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!buttonList.isEmpty() && buttonList.get(0).mousePressed(minecraft, mouseX, mouseY)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return true;
        }
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        listFieldsTeleport.forEach(guiTextField -> guiTextField.textboxKeyTyped(typedChar, keyCode));
        getSearchBar().textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (!buttonList.isEmpty() && buttonList.get(0) != null) {
            Optional<GuiTextFieldMod> gui = elementsInScreenRendered.stream().filter(GuiTextFieldMod::isSelected).findFirst();
            try {
                int posX;
                int posY;
                int posZ;
                Integer dimensionId = guiFieldAndId.get(gui.orElseThrow(() -> new IllegalArgumentException("a")));
                posX = Integer.parseInt(listFieldsTeleport.get(0).getText());
                posY = Integer.parseInt(listFieldsTeleport.get(1).getText());
                posZ = Integer.parseInt(listFieldsTeleport.get(2).getText());
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(kaiaDimension, posX + "," + posY + "," + posZ, dimensionId));
            } catch (NumberFormatException e) {
                listFieldsTeleport.get(0).setText(String.valueOf((int) player.posX));
                listFieldsTeleport.get(1).setText(String.valueOf((int) player.posY));
                listFieldsTeleport.get(2).setText(String.valueOf((int) player.posZ));
                UtilityHelper.sendMessageToPlayer("coordinates is invalid", player);
            } catch (IllegalArgumentException e) {
                UtilityHelper.sendMessageToPlayer("No dimensions selected", player);
            }
        }
    }

    private void createElementsAndPopulateElementsOfScreen() {
        int y = (int) ((int) (heightOfScreen / 8.5) / getCurrentScrollOffset());
        List<DimensionType> dimensions = Arrays.stream(DimensionType.values()).collect(Collectors.toList());
        for (DimensionType dimensionType : dimensions) {
            GuiTextFieldMod guiTextField = new GuiTextFieldMod(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (widthOfScreen / 1.15), (int) (height / 21.25), "This dimension is from of mod: " + dimensionType.getSuffix());
            guiFieldAndId.put(guiTextField, dimensionType.getId());
            guiTextField.setMaxStringLength(50);
            guiTextField.setFocused(false);
            guiTextField.setText(I18n.format(dimensionType.getName().replace('_', ' ')));
            guiTextField.setTextColor(Color.CYAN.getRGB());
            guiTextField.height = (int) (height / 31.875);
            guiTextField.drawTextBox();
            elementsOfScreen.add(guiTextField);
            y += height / 21.25;
        }
    }

    private void addInputCoordenates() {
        int y = (int) (heightOfScreen / 1.06);
        int widthCompriment = (int) (widthOfScreen / 9.6);
        int heightCompriment = (int) (heightOfScreen / 50.9);
        listFieldsTeleport.add(new GuiTextField(23930292, fontRenderer, (widthOfScreen / 20), y, widthCompriment, heightCompriment));
        listFieldsTeleport.get(0).setText(String.valueOf((int) player.posX));
        listFieldsTeleport.add(new GuiTextField(23930293, fontRenderer, (widthOfScreen / 6), y, widthCompriment, heightCompriment));
        listFieldsTeleport.get(1).setText(String.valueOf((int) player.posY));
        listFieldsTeleport.add(new GuiTextField(23930294, fontRenderer, (int) (width / 3.4), y, widthCompriment, heightCompriment));
        listFieldsTeleport.get(2).setText(String.valueOf((int) player.posZ));
    }

    private boolean stopClickInGuisNoRended(GuiTextFieldMod gui) {
        String text = gui.getText();
        List<String> collect = elementsInScreenRendered.stream().map(GuiTextField::getText).collect(Collectors.toList());
        return !collect.contains(text);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Math.round(-Math.signum(Mouse.getEventDWheel()));
        updateScrollOffset(scroll);
    }

    @Override
    public void renderElements(int mouseX, int mouseY, float partialTicks) {
        setCurrentScrollOffset(getCurrentScrollOffset() + ((getTargetScrollOffset() - getCurrentScrollOffset()) * 0.1));
        double v = getCurrentScrollOffset();
        int minY = heightOfScreen / 10;
        double yOffset = (double) heightOfScreen / 9 * (1 / getCurrentScrollOffset());
        int count = 1;
        Pattern pattern;
        String text = getSearchBar().getText();
        pattern = !text.trim().isEmpty() ? Pattern.compile(text, Pattern.CASE_INSENSITIVE) : null;
        elementsInScreenRendered.clear();
        for (GuiTextFieldMod gui : elementsOfScreen) {
            if (yOffset > heightOfScreen / 1.44871794872)
                break;
            if (!getOldTextInSearchBox().equals(text)) {
                setOldTextInSearchBox(text);
                setTargetScrollOffset(1);
            }
            if (pattern == null) {
                if ((Math.round(v) != 1 && count < v)) {
                    count++;
                    continue;
                }
            } else if (!pattern.matcher(gui.getText()).find()) {
                count++;
                continue;
            }

            if (yOffset >= minY) {
                gui.y = (int) yOffset;
                checkAndDrawActualPartScreen(gui);
                gui.drawTextBox();
                elementsInScreenRendered.add(gui);
            }
            yOffset += height / 21.25;
        }
    }

    private void checkAndDrawActualPartScreen(GuiTextFieldMod gui) {
        if (gui.isSelected())
            drawDescription(gui.getDescription());
    }

    private void drawDescription(String description) {
        List<String> strings = fontRenderer.listFormattedStringToWidth(I18n.format(description), widthOfScreen);
        int spacing = 0;
        for (String string : strings) {
            drawString(fontRenderer, string, 0, (int) (heightOfScreen / 1.36) + spacing, Color.WHITE.getRGB());
            spacing = (int) (heightOfScreen / 21.25);
        }
    }
}
