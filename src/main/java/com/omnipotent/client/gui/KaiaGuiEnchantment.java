package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.omnipotent.util.KaiaConstantsNbt.kaiaEnchant;
import static com.omnipotent.util.UtilityHelper.isJustNumber;

public class KaiaGuiEnchantment extends GuiScreen {

    private final EntityPlayer player;
    private int page;
    private int pageRemoved;
    private int oldValueOfPage = 0;
    private int oldValueOfPageRemoved = 0;
    private List<GuiTextField> guiTextFieldList = new ArrayList<GuiTextField>();
    private List<GuiTextField> guiTextFieldListRemove = new ArrayList<GuiTextField>();
    private HashMap<GuiTextField, Enchantment> hashGuiTextEnchant = new HashMap<>();
    private HashMap<GuiTextField, Enchantment> hashGuiTextEnchantRemove = new HashMap<>();
    private GuiTextField guiText;
    private int lvl;

    public KaiaGuiEnchantment(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButtonsPage();
        addButtonsPageRemoved();
        enchantmentsAdded();
        enchantmentsRemove();
        guiText = new GuiTextField(23930290, fontRenderer, 210, 240, 100, 10);
        guiText.setText("level");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        guiText.drawTextBox();
        drawString(fontRenderer, I18n.format("guikaia.enchant"), 220, 5, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        //cor pega com base nas cores normais do minecraft em GuiScreen
        drawGradientRect(getEquivalentValueOfscreenHeight(33, height), getEquivalentValueOfscreenWidth(40, width), getEquivalentValueOfscreenHeight(152, height), getEquivalentValueOfscreenHeight(240, height), -1072689136, -804253680);
        if (page != oldValueOfPage) {
            enchantmentsAdded();
            oldValueOfPage = page;
        }
        for (GuiTextField guiTextField : guiTextFieldList) {
            guiTextField.drawTextBox();
        }
        if (pageRemoved != oldValueOfPageRemoved) {
            enchantmentsRemove();
            oldValueOfPageRemoved = pageRemoved;
        }
        drawGradientRect(getEquivalentValueOfscreenHeight(350, height), getEquivalentValueOfscreenWidth(40, width), getEquivalentValueOfscreenHeight(469, width), getEquivalentValueOfscreenHeight(240, height), -1072689136, -804253680);
        for (GuiTextField guiTextField : guiTextFieldListRemove) {
            guiTextField.drawTextBox();
        }
    }

    public static int getEquivalentValueOfscreenHeight(int value, int height) {
        double ratio = (double) value / height;
        int equivalentValue = (int) (height * ratio);
        return equivalentValue;
    }

    public static int getEquivalentValueOfscreenWidth(int value, int width) {
        double ratio = (double) value / width;
        int equivalentValue = (int) (width * ratio);
        return equivalentValue;
    }

    private void enchantmentsAdded() {
        guiTextFieldList.clear();
        hashGuiTextEnchant.clear();
        Iterator<Enchantment> iteratorTwo = Enchantment.REGISTRY.iterator();
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            Enchantment enchantment = iteratorTwo.next();
            if (!enchantment.isCurse()) {
                enchantments.add(enchantment);
            }
        }
        int y = getEquivalentValueOfscreenHeight(40, height);
        for (int c = 0; c < enchantments.size(); c++) {
            if (y < getEquivalentValueOfscreenHeight(240, height)) {
                int number = page * 17;
                if (c + number < enchantments.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, getEquivalentValueOfscreenHeight(35, height), y, 115, 12);
                    guiTextField.setFocused(false);
                    guiTextField.setText(enchantments.get(c + number).getTranslatedName(1));
                    guiTextField.height = 8;
                    guiTextField.drawTextBox();
                    guiTextFieldList.add(guiTextField);
                    hashGuiTextEnchant.put(guiTextField, enchantments.get(c + number));
                    y += 12;
                }
            }
        }
    }

    private void enchantmentsRemove() {
        guiTextFieldListRemove.clear();
        hashGuiTextEnchantRemove.clear();
        Iterator<Enchantment> iteratorTwo = Enchantment.REGISTRY.iterator();
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            Enchantment enchantment = iteratorTwo.next();
            if (!enchantment.isCurse()) {
                enchantments.add(enchantment);
            }
        }
        int y = getEquivalentValueOfscreenHeight(40, height);
        for (int c = 0; c < enchantments.size(); c++) {
            if (y < 240) {
                int number = pageRemoved * 17;
                if (c + number < enchantments.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, getEquivalentValueOfscreenHeight(352, height), y, 115, 12);
                    guiTextField.setFocused(false);
                    guiTextField.setText(enchantments.get(c + number).getTranslatedName(1));
                    guiTextField.height = 8;
                    guiTextField.drawTextBox();
                    guiTextFieldListRemove.add(guiTextField);
                    hashGuiTextEnchantRemove.put(guiTextField, enchantments.get(c + number));
                    y += 12;
                }
            }
        }
    }

    private void addButtonsPage() {
        GuiButton paginaAnterior = new GuiButton(0, getEquivalentValueOfscreenWidth(34, width), getEquivalentValueOfscreenHeight(28, height), I18n.format("guikaia.enchant.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(1, getEquivalentValueOfscreenWidth(34, width), getEquivalentValueOfscreenHeight(242, height), I18n.format("guikaia.enchant.nextpage"));
        proximaPagina.height = 11;
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = 8 * displayString.length();
        buttonList.add(proximaPagina);
    }

    private void addButtonsPageRemoved() {
        GuiButton paginaAnterior = new GuiButton(2, getEquivalentValueOfscreenWidth(350, width), getEquivalentValueOfscreenHeight(28, height), I18n.format("guikaia.enchant.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(3, getEquivalentValueOfscreenWidth(350, width), getEquivalentValueOfscreenHeight(242, height), I18n.format("guikaia.enchant.nextpage"));
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
                    guiText.setText(I18n.format("guikaia.enchant.label0"));
                    player.sendMessage(new TextComponentString(I18n.format("guikaia.enchant.message")));
                    lvl = 1;
                }
                Enchantment enchantment = hashGuiTextEnchant.get(guiField);
                ResourceLocation registryName = enchantment.getRegistryName();
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(kaiaEnchant, registryName.toString(), lvl));
            }
            guiField.setFocused(false);
        }
        for (GuiTextField guiField : guiTextFieldListRemove) {
            guiField.mouseClicked(mouseX, mouseY, mouseButton);
            if (guiField.isFocused()) {
                Enchantment enchantment = hashGuiTextEnchantRemove.get(guiField);
                ResourceLocation registryName = enchantment.getRegistryName();
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(kaiaEnchant, registryName.toString(), 0));
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
