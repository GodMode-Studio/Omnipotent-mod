package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.UtillityHelp;
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
import static com.omnipotent.util.UtillityHelp.isJustNumber;

public class KaiaGuiDimension extends GuiScreen {

    private final EntityPlayer player;
    private int mouseScrollStartTop = 40;
    private int mouseScrollEndBottom = 55;
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
        listButtonsTeleport.add(new GuiTextField(23930292, fontRenderer, UtillityHelp.getEquivalentValueOfscreenHeight(160, height), 240, 100, 10));
        listButtonsTeleport.get(0).setText(String.valueOf((int) player.posX));
        listButtonsTeleport.add(new GuiTextField(23930293, fontRenderer, 266, 240, 100, 10));
        listButtonsTeleport.get(1).setText(String.valueOf((int) player.posY));
        listButtonsTeleport.add(new GuiTextField(23930294, fontRenderer, 370, 240, 100, 10));
        listButtonsTeleport.get(2).setText(String.valueOf((int) player.posZ));
        UtillityHelp.sendmessageToPlayer(I18n.format("message.client.dimensionhelp"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        listButtonsTeleport.forEach(guiTextField -> guiTextField.drawTextBox());
        drawString(fontRenderer, I18n.format("guikaia.dimension"), 220, 5, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        //cor pega com base nas cores normais do minecraft em GuiScreen
        drawGradientRect(UtillityHelp.getEquivalentValueOfscreenHeight(33, height), UtillityHelp.getEquivalentValueOfscreenWidth(40, width), UtillityHelp.getEquivalentValueOfscreenHeight(152, height), UtillityHelp.getEquivalentValueOfscreenHeight(240, height), -1072689136, -804253680);
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
        int y = UtillityHelp.getEquivalentValueOfscreenHeight(40, height);
        for (int c = 0; c < dimensionTypes.size(); c++) {
            if (y < UtillityHelp.getEquivalentValueOfscreenHeight(240, height)) {
                int number = page * 17;
                if (c + number < dimensionTypes.size()) {
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, UtillityHelp.getEquivalentValueOfscreenHeight(35, height), y, 115, 12);
                    guiTextField.setFocused(false);
                    guiTextField.setText(dimensionTypes.get(c + number).getName());
                    guiTextField.height = 8;
                    guiTextField.drawTextBox();
                    guiTextFieldList.add(guiTextField);
                    hashGuiTextDimensionType.put(guiTextField, dimensionTypes.get(c + number));
                    y += 12;
                }
            }
        }
    }

    private void addButtonsPage() {
        GuiButton paginaAnterior = new GuiButton(0, UtillityHelp.getEquivalentValueOfscreenWidth(34, width), UtillityHelp.getEquivalentValueOfscreenHeight(28, height), I18n.format("guikaia.enchant.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(1, UtillityHelp.getEquivalentValueOfscreenWidth(34, width), UtillityHelp.getEquivalentValueOfscreenHeight(242, height), I18n.format("guikaia.enchant.nextpage"));
        proximaPagina.height = 11;
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = 8 * displayString.length();
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
