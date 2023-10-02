package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.DimensionType;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static com.omnipotent.util.KaiaConstantsNbt.kaiaDimension;
import static com.omnipotent.util.UtilityHelper.isJustNumber;

public class KaiaGuiDimension extends GuiScreen {

    private final EntityPlayer player;
    private int page;
    private int oldValueOfPage = 0;
    private List<GuiTextField> guiTextFieldList = new ArrayList<GuiTextField>();
    private HashMap<GuiTextField, DimensionType> hashGuiTextDimensionType = new HashMap<>();
    private int posX;
    private int posY;
    private int posZ;
    List<GuiTextField> listButtonsTeleport = new ArrayList<>();

    public KaiaGuiDimension(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButtonsPage();
        dimensionsAdded();
        addInputCoordenates();
        UtilityHelper.sendMessageToPlayer(I18n.format("message.client.dimensionhelp"), player);
    }

    private void addInputCoordenates() {
        int y = (int) (height / 1.06);
        int widthCompriment = (int) (width / 9.6);
        int heightCompriment = (int) (height / 50.9);
        listButtonsTeleport.add(new GuiTextField(23930292, fontRenderer, (int) (width / 20), y, widthCompriment, heightCompriment));
        listButtonsTeleport.get(0).setText(String.valueOf((int) player.posX));
        listButtonsTeleport.add(new GuiTextField(23930293, fontRenderer, (int) (width / 6), y, widthCompriment, heightCompriment));
        listButtonsTeleport.get(1).setText(String.valueOf((int) player.posY));
        listButtonsTeleport.add(new GuiTextField(23930294, fontRenderer, (int) (width / 3.4), y, widthCompriment, heightCompriment));
        listButtonsTeleport.get(2).setText(String.valueOf((int) player.posZ));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        listButtonsTeleport.forEach(guiTextField -> guiTextField.drawTextBox());
        drawString(fontRenderer, I18n.format("guikaia.dimension"), (int) (width / 2.05), height / 35, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        //cor pega com base nas cores normais do minecraft em GuiScreen
        drawGradientRect(width / 80, height / 22, (int) (width / 1.01), (int) (height / 1.07), -1072689136, -804253680);
        if (page != oldValueOfPage) {
            dimensionsAdded();
            oldValueOfPage = page;
        }
        for (GuiTextField guiTextField : guiTextFieldList) {
            guiTextField.drawTextBox();
        }
    }

    private void dimensionsAdded() {
        guiTextFieldList.clear();
        hashGuiTextDimensionType.clear();
        Iterator<DimensionType> iteratorTwo = Arrays.stream(DimensionType.values()).iterator();
        ArrayList<DimensionType> dimensionTypes = new ArrayList<>();
        int idGuiText = -1;
        while (iteratorTwo.hasNext()) {
            DimensionType dimensionType = iteratorTwo.next();
            dimensionTypes.add(dimensionType);
        }
        int y = (int) (height / 18.8518518519);
        for (int c = 0; c < dimensionTypes.size(); c++) {
            if (y < height / 1.4125) {
                int number = page * 17;
                if (c + number < dimensionTypes.size()) {
                    String nameDimension = dimensionTypes.get(c + number).getName();
                    int widthGui = nameDimension.length() * 7;
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 60), y, widthGui, (int) (height / 28.25));
                    guiTextField.setFocused(false);
                    guiTextField.setText(nameDimension);
                    guiTextField.height = (int) (height / 42.375);
                    guiTextField.drawTextBox();
                    guiTextFieldList.add(guiTextField);
                    hashGuiTextDimensionType.put(guiTextField, dimensionTypes.get(c + number));
                    y += (int) (height / 28.25);
                }
            }
        }
    }

    private void addButtonsPage() {
        //y é 319 heigth é 339 tamanho maximo y 240 heigth 255
        int y = (int) (height / 1.06);
        GuiButton paginaAnterior = new GuiButton(0, (int) (width / 2.4), y, I18n.format("guikaia.enchant.previouspage"));
        paginaAnterior.height = (int) (height / 23.1818181818);
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = width / 80 * displayString2.length();
        buttonList.add(paginaAnterior);

        GuiButton proximaPagina = new GuiButton(1, (int) (width / 1.65), y, I18n.format("guikaia.enchant.nextpage"));
        proximaPagina.height = (int) (height / 23.1818181818);
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = width / 80 * displayString.length();
        buttonList.add(proximaPagina);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiTextField guiTextField : listButtonsTeleport) {
            guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (GuiTextField guiField : guiTextFieldList) {
            guiField.mouseClicked(mouseX, mouseY, mouseButton);
            if (guiField.isFocused()) {
                Boolean posXBoolean = null;
                Boolean posYBoolean = null;
                Boolean posZBoolean = null;
                for (GuiTextField guiTextField : listButtonsTeleport) {
                    if (posXBoolean == null) {
                        posXBoolean = isJustNumber(guiTextField.getText()) && (Integer.parseInt(guiTextField.getText()) <= Integer.MAX_VALUE) && (Integer.parseInt(guiTextField.getText()) > -Integer.MAX_VALUE);
                    } else if (posYBoolean == null) {
                        posYBoolean = isJustNumber(guiTextField.getText()) && (Integer.parseInt(guiTextField.getText()) <= Integer.MAX_VALUE) && (Integer.parseInt(guiTextField.getText()) > -Integer.MAX_VALUE);
                    } else {
                        posZBoolean = isJustNumber(guiTextField.getText()) && (Integer.parseInt(guiTextField.getText()) <= Integer.MAX_VALUE) && (Integer.parseInt(guiTextField.getText()) > -Integer.MAX_VALUE);
                    }
                }
                if (posXBoolean && posYBoolean && posZBoolean) {
                    for (int c = 0; c < listButtonsTeleport.size(); c++) {
                        GuiTextField guiTextField = listButtonsTeleport.get(c);
                        if (c == 0) {
                            this.posX = Integer.parseInt(guiTextField.getText());
                        } else if (c == 1) {
                            this.posY = Integer.parseInt(guiTextField.getText());
                        } else {
                            this.posZ = Integer.parseInt(guiTextField.getText());
                        }
                    }
                    DimensionType dimensionType = hashGuiTextDimensionType.get(guiField);
                    int dimensionID = dimensionType.getId();
                    NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(kaiaDimension, posX + "," + posY + "," + posZ, dimensionID));
                } else {
                    listButtonsTeleport.forEach(gui -> gui.setText("0"));
                    this.posX = 1;
                    this.posY = 1;
                    this.posZ = 1;
                }
            }
            guiField.setFocused(false);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        listButtonsTeleport.forEach(guiTextField -> guiTextField.textboxKeyTyped(typedChar, keyCode));
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
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
