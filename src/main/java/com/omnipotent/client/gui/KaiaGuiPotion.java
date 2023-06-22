package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.omnipotent.util.UtilityHelper.isJustNumber;
import static com.omnipotent.util.KaiaConstantsNbt.kaiaPotion;

public class KaiaGuiPotion extends GuiScreen {

    private final EntityPlayer player;
    private int mouseScrollStartTop = 40;
    private int mouseScrollEndBottom = 55;
    private int page;
    private int pageRemoved;
    private int oldValueOfPage = 0;
    private int oldValueOfPageRemoved = 0;
    private List<GuiTextField> guiTextFieldList = new ArrayList<GuiTextField>();
    private List<GuiTextField> guiTextFieldListRemove = new ArrayList<GuiTextField>();
    private HashMap<GuiTextField, Potion> hashGuiTextPotion = new HashMap<>();
    private HashMap<GuiTextField, Potion> hashGuiTextEnchantPotion = new HashMap<>();
    private GuiTextField guiText;
    private int lvl;

    public KaiaGuiPotion(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButtonsPage();
        addButtonsPageRemoved();
        potionsAdded();
        potionsRemove();
        guiText = new GuiTextField(23930290, fontRenderer, 210, 240, 100, 10);
        guiText.setText("level");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        guiText.drawTextBox();
        drawString(fontRenderer, I18n.format("guikaia.potion"), 220, 5, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        //cor pega com base nas cores normais do minecraft em GuiScreen
        drawGradientRect(getEquivalentValueOfscreenHeight(33), getEquivalentValueOfscreenWidth(40), getEquivalentValueOfscreenHeight(152), getEquivalentValueOfscreenHeight(240), -1072689136, -804253680);
        if (page != oldValueOfPage) {
            potionsAdded();
            oldValueOfPage = page;
        }
        for (GuiTextField guiTextField : guiTextFieldList) {
            guiTextField.drawTextBox();
        }
        if (pageRemoved != oldValueOfPageRemoved) {
            potionsRemove();
            oldValueOfPageRemoved = pageRemoved;
        }
        drawGradientRect(getEquivalentValueOfscreenHeight(350), getEquivalentValueOfscreenWidth(40), getEquivalentValueOfscreenHeight(469), getEquivalentValueOfscreenHeight(240), -1072689136, -804253680);
        for (GuiTextField guiTextField : guiTextFieldListRemove) {
            guiTextField.drawTextBox();
        }
    }

    private int getEquivalentValueOfscreenHeight(int value) {
        double ratio = (double) value / height;
        int equivalentValue = (int) (height * ratio);
        return equivalentValue;
    }

    private int getEquivalentValueOfscreenWidth(int value) {
        double ratio = (double) value / width;
        int equivalentValue = (int) (width * ratio);
        return equivalentValue;
    }

    private void potionsAdded() {
        guiTextFieldList.clear();
        hashGuiTextPotion.clear();
        Iterator<Potion> iteratorTwo = Potion.REGISTRY.iterator();
        ArrayList<Potion> potions = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            Potion Potion = iteratorTwo.next();
            if (!Potion.isBadEffect()) {
                potions.add(Potion);
            }
        }
        int y = getEquivalentValueOfscreenHeight(40);
        for (int c = 0; c < potions.size(); c++) {
            if (y < getEquivalentValueOfscreenHeight(240)) {
                int number = page * 17;
                if (c + number < potions.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, getEquivalentValueOfscreenHeight(35), y, 115, 12);
                    guiTextField.setFocused(false);
                    guiTextField.setText(I18n.format(potions.get(c + number).getName()));
                    guiTextField.height = 8;
                    guiTextField.drawTextBox();
                    guiTextFieldList.add(guiTextField);
                    hashGuiTextPotion.put(guiTextField, potions.get(c + number));
                    y += 12;
                }
            }
        }
    }

    private void potionsRemove() {
        guiTextFieldListRemove.clear();
        hashGuiTextEnchantPotion.clear();
        Iterator<Potion> iteratorTwo = Potion.REGISTRY.iterator();
        ArrayList<Potion> potions = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            Potion potion = iteratorTwo.next();
            if (!potion.isBadEffect()) {
                potions.add(potion);
            }
        }
        int y = getEquivalentValueOfscreenHeight(40);
        for (int c = 0; c < potions.size(); c++) {
            if (y < 240) {
                int number = pageRemoved * 17;
                if (c + number < potions.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, getEquivalentValueOfscreenHeight(352), y, 115, 12);
                    guiTextField.setFocused(false);
                    guiTextField.setText(I18n.format(potions.get(c + number).getName()));
                    guiTextField.height = 8;
                    guiTextField.drawTextBox();
                    guiTextFieldListRemove.add(guiTextField);
                    hashGuiTextEnchantPotion.put(guiTextField, potions.get(c + number));
                    y += 12;
                }
            }
        }
    }

    private void addButtonsPage() {
        GuiButton paginaAnterior = new GuiButton(0, getEquivalentValueOfscreenWidth(34), getEquivalentValueOfscreenHeight(28), I18n.format("guikaia.potion.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(1, getEquivalentValueOfscreenWidth(34), getEquivalentValueOfscreenHeight(242), I18n.format("guikaia.potion.nextpage"));
        proximaPagina.height = 11;
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = 8 * displayString.length();
        buttonList.add(proximaPagina);
    }

    private void addButtonsPageRemoved() {
        GuiButton paginaAnterior = new GuiButton(2, getEquivalentValueOfscreenWidth(350), getEquivalentValueOfscreenHeight(28), I18n.format("guikaia.potion.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(3, getEquivalentValueOfscreenWidth(350), getEquivalentValueOfscreenHeight(242), I18n.format("guikaia.potion.nextpage"));
        proximaPagina.height = 11;
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = 8 * displayString.length();
        buttonList.add(proximaPagina);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        guiText.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTextField guiField : guiTextFieldList) {
            guiField.mouseClicked(mouseX, mouseY, mouseButton);
            if (guiField.isFocused()) {
                if (isJustNumber(guiText.getText()) && (Integer.parseInt(guiText.getText()) <= Short.MAX_VALUE) && (Integer.parseInt(guiText.getText()) > 0)) {
                    lvl = Integer.parseInt(guiText.getText());
                } else {
                    guiText.setText(I18n.format("guikaia.potion.label0"));
                    player.sendMessage(new TextComponentString(I18n.format("guikaia.potion.message")));
                    lvl = 1;
                }
                Potion potion = hashGuiTextPotion.get(guiField);
                ResourceLocation registryName = potion.getRegistryName();
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(kaiaPotion, registryName.toString(), lvl));
            }
            guiField.setFocused(false);
        }
        for (GuiTextField guiField : guiTextFieldListRemove) {
            guiField.mouseClicked(mouseX, mouseY, mouseButton);
            if (guiField.isFocused()) {
                Potion potion = hashGuiTextEnchantPotion.get(guiField);
                ResourceLocation registryName = potion.getRegistryName();
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(kaiaPotion, registryName.toString(), 0));
            }
            guiField.setFocused(false);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        guiText.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                if (!(page == 0)) {
                    this.page--;
                }
                break;
            case 1:
                this.page++;
                break;
            case 2:
                if (!(pageRemoved == 0)) {
                    this.pageRemoved--;
                }
                break;
            case 3:
                this.pageRemoved++;
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
