package com.omnipotent.client.gui.potion;

import com.omnipotent.Omnipotent;
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

import static com.omnipotent.util.KaiaConstantsNbt.kaiaPotion;
import static com.omnipotent.util.UtilityHelper.isJustNumber;

public class KaiaGuiPotionAddedAndRemove extends GuiScreen {

    private final EntityPlayer player;
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
    private int idButtom = -1;
    private int xElementControllerOfRemoveButtons;

    public KaiaGuiPotionAddedAndRemove(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        xElementControllerOfRemoveButtons = (int) (width / 1.17);
        idButtom = -1;
        addButtonsPageOfAddedPotion();
        addButtonsPageOfRemovedPotion();
        potionsAdded();
        potionsRemove();
        addButtonsChangeMainPage();
        guiText = new GuiTextField(23930290, fontRenderer, (int) (width / 2.48181818182), (int) (height / 1.0625), (int) (width / 4.8), (int) (height / 25.5));
        guiText.setText("level");
    }

    private void addButtonsChangeMainPage() {
        GuiButton button = new GuiButton(++idButtom, (int) (width / 1.04347826087), height / 2, ">");
        button.setWidth((width / 23));
        button.height = height / 24;
        buttonList.add(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        guiText.drawTextBox();
        drawString(fontRenderer, I18n.format("guikaia.potion"), (int) (width / 2.18181818182), height / 51, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        //cor pega com base nas cores normais do minecraft em GuiScreen
        int topY = (int) (height / 9.44444444444);
        int bottomY = (int) (height / 1.09);
        drawGradientRect((int) (width / 14.5454545455), topY, (int) (width / 3.15789473684), bottomY, -1072689136, -804253680);
        drawButtonsAndGuisOfTypeAdd();
        drawGradientRect((int) (xElementControllerOfRemoveButtons / 1.37142857143), topY, (int) (xElementControllerOfRemoveButtons / 1.02345415778), bottomY, -1072689136, -804253680);
        drawButtonsAndGuisOfTypeRemove();
    }

    private void drawButtonsAndGuisOfTypeRemove() {
        if (pageRemoved != oldValueOfPageRemoved) {
            potionsRemove();
            oldValueOfPageRemoved = pageRemoved;
        }
        for (GuiTextField guiTextField : guiTextFieldListRemove) {
            guiTextField.drawTextBox();
        }
    }

    private void drawButtonsAndGuisOfTypeAdd() {
        if (page != oldValueOfPage) {
            potionsAdded();
            oldValueOfPage = page;
        }
        for (GuiTextField guiTextField : guiTextFieldList) {
            guiTextField.drawTextBox();
        }
    }

    private void addButtonsPageOfAddedPotion() {
        int x = (int) (xElementControllerOfRemoveButtons / 12.0588235294);
        GuiButton paginaAnterior = new GuiButton(++idButtom, x, height / 17, I18n.format("guikaia.potion.previouspage"));
        paginaAnterior.height = (int) (height / 23.1818181818);
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = width / 60 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(++idButtom, x, (int) (height / 1.089), I18n.format("guikaia.potion.nextpage"));
        proximaPagina.height = (int) (height / 23.1818181818);
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = width / 60 * displayString.length();
        buttonList.add(proximaPagina);
    }

    private void addButtonsPageOfRemovedPotion() {
        int xAll = (int) (xElementControllerOfRemoveButtons / 1.38);
        GuiButton paginaAnterior = new GuiButton(++idButtom, xAll, height / 17, I18n.format("guikaia.potion.previouspage"));
        paginaAnterior.height = (int) (height / 23.1818181818);
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = xElementControllerOfRemoveButtons / 60 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(++idButtom, xAll, (int) (height / 1.089), I18n.format("guikaia.potion.nextpage"));
        proximaPagina.height = (int) (height / 23.1818181818);
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = xElementControllerOfRemoveButtons / 60 * displayString.length();
        buttonList.add(proximaPagina);
    }

    private void potionsAdded() {
        guiTextFieldList.clear();
        hashGuiTextPotion.clear();
        Iterator<Potion> iteratorTwo = Potion.REGISTRY.iterator();
        ArrayList<Potion> potions = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            Potion Potion = iteratorTwo.next();
            if (!Potion.isBadEffect())
                potions.add(Potion);
        }
        int y = (int) (height / 8.5);
        for (int c = 0; c < potions.size(); c++) {
            if (y < height / 1.1) {
                int number = page * (height / 15);
                if (c + number < potions.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (width / 4.17391304348), (int) (height / 21.25));
                    guiTextField.setFocused(false);
                    guiTextField.setText(I18n.format(potions.get(c + number).getName()));
                    guiTextField.height = (int) (height / 31.875);
                    guiTextField.drawTextBox();
                    guiTextFieldList.add(guiTextField);
                    hashGuiTextPotion.put(guiTextField, potions.get(c + number));
                    y += height / 21.25;
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
        int y = (int) (height / 8.5);
        for (int c = 0; c < potions.size(); c++) {
            if (y < height / 1.1) {
                int number = pageRemoved * (height / 15);
                if (c + number < potions.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, (int) (xElementControllerOfRemoveButtons / 1.36363636364), y, (int) (width / 4.17391304348), (int) (height / 21.25));
                    guiTextField.setFocused(false);
                    guiTextField.width = (int) (xElementControllerOfRemoveButtons / 4.200);
                    guiTextField.setText(I18n.format(potions.get(c + number).getName()));
                    guiTextField.height = (int) (height / 31.875);
                    guiTextField.drawTextBox();
                    guiTextFieldListRemove.add(guiTextField);
                    hashGuiTextEnchantPotion.put(guiTextField, potions.get(c + number));
                    y += height / 21.25;
                }
            }
        }
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
                if (page != 0)
                    this.page--;
                break;
            case 1:
                this.page++;
                break;
            case 2:
                if (pageRemoved != 0)
                    this.pageRemoved--;
                break;
            case 3:
                this.pageRemoved++;
                break;
            case 4:
                player.openGui(Omnipotent.instance, 8, player.world, 0, 0, 0);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
