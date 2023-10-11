package com.omnipotent.client.gui.potion;

import com.omnipotent.Omnipotent;
import com.omnipotent.acessor.IGuiTextFieldAcessor;
import com.omnipotent.client.gui.elementsmod.GuiButtonMod;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.NbtListUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KaiaGuiBlockPotion extends GuiScreen {
    private final EntityPlayer player;
    private int xElementControllerButtons;
    private int yElementControllerButtons;

    private int idButtom = -1;
    private List<GuiTextField> guiTextFieldList = new ArrayList<>();
    private List<GuiTextField> guiTextFieldsRendered = new ArrayList<>();
    private HashMap<GuiTextField, Potion> hashGuiTextPotion = new HashMap<>();
    private GuiTextField guiTextField;
    private Minecraft minecraft;
    private double maxScrollOffset = Integer.MAX_VALUE;
    private double targetScrollOffset = 1.0;
    private double currentScrollOffset = 1.0;
    private String oldTextInSearchBox = "";

    public KaiaGuiBlockPotion(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        xElementControllerButtons = (int) (width / 1.17);
        yElementControllerButtons = (int) (height / 1.17);
        idButtom = -1;
//        guiButton = new GuiButton(++idButtom, (int) (xElementControllerButtons / 0.88), yElementControllerButtons / 5, xElementControllerButtons / 25, yElementControllerButtons / 15, "");
        minecraft = Minecraft.getMinecraft();
        potionsAddedScroll();
        addButtonChangeMainPage();
        guiTextField = new GuiTextField(54354, fontRenderer, (int) (xElementControllerButtons / 2.1), (int) (yElementControllerButtons / 18), xElementControllerButtons / 5, yElementControllerButtons / 25);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawGradientRect(xElementControllerButtons / 415, yElementControllerButtons / 10, (int) (xElementControllerButtons / 0.8), (int) (yElementControllerButtons / 0.85), -1072689136, -804253680);
        renderGuis();
        guiTextField.drawTextBox();
        buttonList.forEach(item -> item.drawButton(minecraft, mouseX, mouseY, partialTicks));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Math.round(-Math.signum(Mouse.getEventDWheel()));
        updateScrollOffset(scroll);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        ((GuiButtonMod) button).runnable.run();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        guisEffects(mouseX, mouseY, mouseButton);
        guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        guiTextField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    private void addButtonChangeMainPage() {
        GuiButton button = new GuiButtonMod(++idButtom, (int) (width / 40), height / 2, "<", () -> player.openGui(Omnipotent.instance, 4, player.world, 0, 0, 0));
        button.setWidth((width / 23));
        button.height = height / 24;
        buttonList.add(button);
    }


    private void updateScrollOffset(int scroll) {
        if (scroll == 0)
            return;
        double scrollFactor = 1.1; // Ajuste este valor conforme necessário
        double newScrollOffset = currentScrollOffset + scroll * scrollFactor;
        newScrollOffset = Math.max(1.0, Math.min(maxScrollOffset, newScrollOffset));
        targetScrollOffset = newScrollOffset;
    }

    private void renderGuis() {
        currentScrollOffset += (targetScrollOffset - currentScrollOffset) * 0.1;
        double v = currentScrollOffset / 1;
        int minY = yElementControllerButtons / 10;
        double yOffset = (yElementControllerButtons / 9) * (1 / currentScrollOffset);
        int count = 1;
        Pattern pattern;
        String text = guiTextField.getText();
        pattern = !text.trim().isEmpty() ? Pattern.compile(text, Pattern.CASE_INSENSITIVE) : null;
        guiTextFieldsRendered.clear();
        for (GuiTextField gui : guiTextFieldList) {
            if ((Math.round(v) != 1 && count < v) || !(pattern == null || pattern.matcher(gui.getText()).find())) {
                count++;
                continue;
            }
            if (!oldTextInSearchBox.equals(text)) {
                oldTextInSearchBox = text;
                targetScrollOffset = 1;
            }
            if (yOffset >= minY) {
                gui.y = (int) yOffset;
                gui.drawTextBox();
            }
            guiTextFieldsRendered.add(gui);
            yOffset += height / 21.25;
        }
    }

    private void guisEffects(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField textField : guiTextFieldList) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
            if (textField.isFocused()) {
                if (stopClickInGuisNoRended(textField)) {
                    textField.setFocused(false);
                    continue;
                }
                boolean effectBlocked;
                effectBlocked = ((IGuiTextFieldAcessor) textField).acessorEnabledColor() == Color.RED.getRGB();
                if (effectBlocked) {
                    textField.setTextColor(14737632);
                    NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.effectsBlockeds, hashGuiTextPotion.get(textField).getRegistryName().toString(), 1));
                } else {
                    textField.setTextColor(Color.RED.getRGB());
                    NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.effectsBlockeds, hashGuiTextPotion.get(textField).getRegistryName().toString(), 0));
                    UtilityHelper.sendMessageToPlayer("Effect blocked", player);
                }
                textField.setFocused(false);
            }
        }
    }

    private boolean stopClickInGuisNoRended(GuiTextField textField) {
        String text = textField.getText();
        List<String> collect = guiTextFieldsRendered.stream().map(GuiTextField::getText).collect(Collectors.toList());
        return !collect.contains(text);
    }

    private void potionsAddedScroll() {
        guiTextFieldList.clear();
        hashGuiTextPotion.clear();
        Iterator<Potion> iteratorTwo = Potion.REGISTRY.iterator();
        ArrayList<Potion> potions = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            Potion potion = iteratorTwo.next();
            potions.add(potion);
        }
        int y = (int) ((int) (yElementControllerButtons / 8.5) / currentScrollOffset);
        ArrayList<String> potionsBlocked = NbtListUtil.getValueOfElementsOfNbtList(KaiaUtil.getKaiaInMainHand(player).getTagCompound().getTagList(KaiaConstantsNbt.effectsBlockeds, 8));
        for (int c = 0; c < potions.size(); c++) {
            GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (xElementControllerButtons / 0.98), (int) (height / 21.25));
            guiTextField.setMaxStringLength(128);
            guiTextField.setFocused(false);
            Potion currentEffectToGuiTextField = potions.get(c);
            if (potionsBlocked.contains(currentEffectToGuiTextField.getRegistryName().toString()))
                guiTextField.setTextColor(Color.RED.getRGB());
            guiTextField.setText(I18n.format(currentEffectToGuiTextField.getName()));
            guiTextField.height = (int) (height / 31.875);
            guiTextField.drawTextBox();
            guiTextFieldList.add(guiTextField);
            hashGuiTextPotion.put(guiTextField, currentEffectToGuiTextField);
            y += height / 21.25;
        }
    }
}
