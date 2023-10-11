package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;

import static com.omnipotent.util.KaiaConstantsNbt.blockReachDistance;
import static com.omnipotent.util.UtilityHelper.sendMessageToPlayer;

public class KaiaGui extends GuiScreen {
    EntityPlayer player = null;
    public static int width;
    public static int height;
    public static FontRenderer fontRenderer;
    private InitButtonsForGuiKaia initButtonsForGuiKaia;

    public KaiaGui(InventoryPlayer inventoryPlayer, ItemStack itemStack) {
        this.player = inventoryPlayer.player;
    }

    @Override
    public void initGui() {
        super.initGui();
        width = super.width;
        height = super.height;
        fontRenderer = super.fontRenderer;
        this.initButtonsForGuiKaia = new InitButtonsForGuiKaia();
        initButtonsForGuiKaia.init(player, buttonList);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        initButtonsForGuiKaia.drawButtons(Minecraft.getMinecraft(), mouseX, mouseY, (int) partialTicks);
        initButtonsForGuiKaia.drawLabels(fontRenderer);
        initButtonsForGuiKaia.drawGuiText();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        for (String name : initButtonsForGuiKaia.namesOfGuiTextList) {
            initButtonsForGuiKaia.guiTextFieldList.get(name).mouseClicked(mouseX, mouseY, button);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (String name : initButtonsForGuiKaia.namesOfGuiTextList) {
            initButtonsForGuiKaia.guiTextFieldList.get(name).textboxKeyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        int id = 0;
        int idComplement = 0;
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(id)).getText())) {
            int valueButtonBlockArea;
            try {
                valueButtonBlockArea = Integer.parseInt(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(idComplement)).getText());
            } catch (NumberFormatException e) {
                valueButtonBlockArea = 1;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + valueButtonBlockArea, player);
            }
            if (valueButtonBlockArea % 2 == 0) {
                --valueButtonBlockArea;
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.blockBreakArea, valueButtonBlockArea));
            } else {
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.blockBreakArea, valueButtonBlockArea));
            }
        }
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++id)).getText())) {
            int rangeAttack;
            try {
                rangeAttack = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++idComplement)).getText());
                if (rangeAttack > 10000) {
                    rangeAttack = 10000;
                    sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + rangeAttack, player);
                }
            } catch (NumberFormatException e) {
                rangeAttack = 10;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + rangeAttack, player);
            }
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.rangeAttack, rangeAttack));
        }
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++id)).getText())) {
            int distance;
            try {
                distance = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++idComplement)).getText());
            } catch (NumberFormatException e) {
                distance = 5;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + distance, player);
            }
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(blockReachDistance, distance));
        }
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++id)).getText())) {
            int maxCountSlot;
            try {
                maxCountSlot = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++idComplement)).getText());
            } catch (NumberFormatException e) {
                maxCountSlot = 200_000_000;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + maxCountSlot, player);
            }
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.maxCountSlot, maxCountSlot));
        }
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++id)).getText())) {
            int autoKillRange;
            try {
                autoKillRange = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++idComplement)).getText());
                if (autoKillRange > 1000 || autoKillRange < 1) {
                    sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + autoKillRange, player);
                    autoKillRange = 10;
                }
            } catch (NumberFormatException e) {
                autoKillRange = 10;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + autoKillRange, player);
            }
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.rangeAutoKill, autoKillRange));
        }
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++id)).getText())) {
            int chargeEnergy;
            try {
                chargeEnergy = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++idComplement)).getText());
                if (chargeEnergy > 500 || chargeEnergy < 0) {
                    sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + chargeEnergy, player);
                    chargeEnergy = 0;
                }
            } catch (NumberFormatException e) {
                chargeEnergy = 0;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + chargeEnergy, player);
            }
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.chargeEnergyInBlocksAround, chargeEnergy));
        }
        if (UtilityHelper.isJustNumber(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++id)).getText())) {
            int chargeMana;
            try {
                chargeMana = Integer.valueOf(initButtonsForGuiKaia.guiTextFieldList.get(initButtonsForGuiKaia.namesOfGuiTextList.get(++idComplement)).getText());
                if (chargeMana > 500 || chargeMana < 0) {
                    sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + chargeMana, player);
                    chargeMana = 0;
                }
            } catch (NumberFormatException e) {
                chargeMana = 0;
                sendMessageToPlayer(I18n.format("message.client.error.limitnumber") + " " + chargeMana, player);
            }
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.chargeManaInBlocksAround, chargeMana));
        }
        super.onGuiClosed();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (initButtonsForGuiKaia.buttonsList.contains(button)) {
            initButtonsForGuiKaia.functionsForButtonsList.get(button).run();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
