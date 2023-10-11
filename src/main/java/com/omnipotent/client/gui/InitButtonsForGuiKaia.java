package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.KaiaConstantsNbt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.getKaiaInMainHand;

public class InitButtonsForGuiKaia {
    public List<GuiButton> buttonsList = new ArrayList<>();
    private List<String> namesOfButtons = new ArrayList<>();
    public List<String> namesOfGuiTextList = new ArrayList<>();
    public Map<GuiButton, Runnable> functionsForButtonsList = new HashMap<>();
    public Map<String, GuiTextField> guiTextFieldList = new HashMap<>();
    private List<String> textButtonList = new ArrayList<>();
    private static int width = KaiaGui.width;
    private static int height = KaiaGui.height;
    private int buttonID = 0;
    private final Map<String, GuiButton> guiButtonList = new HashMap<>();

    /*Para adicionar um novo botão, adicione ele no método setButtonList,
    adicione o nome dele em setNamesInListNamesOfButtons,
    e o texto dele em setTextButtonList
    e sua função em setFunctionsForButtonsList.
    */
    public void init(EntityPlayer player, List<GuiButton> list) {
        width = KaiaGui.width;
        height = KaiaGui.height;
        guiButtonLogic(player, list);
        guiTextLogic(player);
    }

    private void guiButtonLogic(EntityPlayer player, List<GuiButton> list) {
        setNamesInListNamesOfButtons();
        setButtonList(player);
        setGuiButtonList();
        setFunctionsForButtonsList(player);
        for (String name : namesOfButtons) {
            list.add(guiButtonList.get(name));
        }
    }

    private void guiTextLogic(EntityPlayer player) {
        setNamesOfGuiTextList();
        setGuiTextFieldList(player);
    }

    public void setNamesInListNamesOfButtons() {
        namesOfButtons.add("killfriendentities");
        namesOfButtons.add("killallentities");
        namesOfButtons.add("counterattack");
        namesOfButtons.add("attackyourwolf");
        namesOfButtons.add("interactliquid");
        namesOfButtons.add("noBreakTileEntity");
        namesOfButtons.add("autoBackPack");
        namesOfButtons.add("autoBackPackEntities");
        namesOfButtons.add("playerscantrespawn");
        namesOfButtons.add(chargeEnergyItemsInInventory);
        namesOfButtons.add("summonlightboltsinkill");
        namesOfButtons.add(banEntitiesAttacked);
        namesOfButtons.add(autoKill);
        namesOfButtons.add(chargeManaItemsInInventory);
    }

    public void drawButtons(Minecraft instance, int mouseX, int mouseY, int partialTicks) {
        setTextButtonList(Minecraft.getMinecraft().player);
        int index = 0;
        for (GuiButton button : buttonsList) {
            button.displayString = textButtonList.get(index);
            button.drawButton(instance.getMinecraft(), mouseX, mouseY, partialTicks);
            index++;
        }
    }

    private void setTextButtonList(EntityPlayer player) {
        textButtonList.clear();
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(killFriendEntities)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(killAllEntities)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(counterAttack)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(attackYourWolf)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(interactLiquid)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(noBreakTileEntity)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPack)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPackEntities)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(playersCantRespawn)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(chargeEnergyItemsInInventory)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(summonLightBoltsInKill)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(banEntitiesAttacked)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoKill)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(chargeManaItemsInInventory)));
    }

    private void setButtonList(EntityPlayer player) {
        buttonsList.add(new GuiButton(buttonID, (int) (width / 4.4), (int) (height / 4.7), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(killFriendEntities))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 4.6), (int) (height / 3.67), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(killAllEntities))));
        buttonsList.add(new GuiButton(++buttonID, width / 8, (int) (height / 2.5), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(counterAttack))));

        buttonsList.add(new GuiButton(++buttonID, (int) (width / 7.42), (int) (height / 2.2), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(attackYourWolf))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 4.2), (int) (height / 1.93), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(interactLiquid))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 5.3), (int) (height / 1.73), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(noBreakTileEntity))));

        buttonsList.add(new GuiButton(++buttonID, (int) (width / 6.5), (int) (height / 1.58), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPack))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 4.3), (int) (height / 1.42), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPackEntities))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 3.8), (int) (height / 1.21), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(playersCantRespawn))));

        buttonsList.add(new GuiButton(++buttonID, (width / 6), (int) (height / 1.13), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(chargeEnergyItemsInInventory))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 4.9), (int) (height / 1.08), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(summonLightBoltsInKill))));
        buttonsList.add(new GuiButton(++buttonID, (width / 2), (height / 9), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(banEntitiesAttacked))));
        buttonsList.add(new GuiButton(++buttonID, (width / 2), (height / (9 - 3)), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoKill))));
        buttonsList.add(new GuiButton(++buttonID, (int) (width / 2.35), (int) (height / 2.2), (int) (width / 21.3333333333), 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(chargeManaItemsInInventory))));
    }

    private void setGuiButtonList() {
        for (int c = 0; c < namesOfButtons.size(); c++) {
            guiButtonList.put(namesOfButtons.get(c), buttonsList.get(c));
        }
    }

    public void drawGuiText() {
        for (String name : namesOfGuiTextList) {
            guiTextFieldList.get(name).drawTextBox();
        }
    }

    public void drawLabels(FontRenderer fontRenderer) {
        int white = Color.WHITE.getRGB();
        fontRenderer.drawString(I18n.format("guikaia.config"), (int) (width / 2.2), height / 45, white);
        int yFirst = 9;
        fontRenderer.drawString(I18n.format("guikaia.config.minerationarea"), width / 119, height / yFirst, white);
        fontRenderer.drawString(I18n.format("guikaia.config.rangeattack"), width / 119, height / (yFirst - 3), white);
        fontRenderer.drawString(I18n.format("guikaia.config.attackfriendentities"), width / 119, (int) (height / (yFirst - 4.5)), white);

        fontRenderer.drawString(I18n.format("guikaia.config.attackallentities"), width / 119, (int) (height / (yFirst - 5.5)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.blockinteractiondistance"), width / 119, (int) (height / (yFirst - 6.1)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.counterattack"), width / 119, (int) (height / (yFirst - 6.53)), white);

        fontRenderer.drawString(I18n.format("guikaia.config.attackyourwolf"), width / 119, (int) (height / (yFirst - 6.85)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.interactwithliquidblocks"), width / 119, (int) (height / (yFirst - 7.1)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.donotbreaktileentityblocks"), width / 119, (int) (height / (yFirst - 7.3)), white);

        fontRenderer.drawString(I18n.format("guikaia.config.autobackpack"), width / 119, (int) (height / (yFirst - 7.45)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.autobackpackentities"), width / 119, (int) (height / (yFirst - 7.6)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.maxslotcount"), width / 119, (int) (height / (yFirst - 7.7)), white);

        fontRenderer.drawString(I18n.format("guikaia.config.playerscantrespawn"), width / 119, (int) (height / (yFirst - 7.8)), white);
        fontRenderer.drawString(I18n.format("guikaia.config." + chargeEnergyItemsInInventory), width / 119, (int) (height / (yFirst - 7.87)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.summonlightbolstinkill"), width / 119, (int) (height / (yFirst - 7.93)), white);

        fontRenderer.drawString(I18n.format("guikaia.config.banEntitiesAttacked"), (int) (width / 3.1), (height / 9), white);
        fontRenderer.drawString(I18n.format("guikaia.config.autoKill"), (int) (width / 3.1), (height / (yFirst - 3)), white);
        fontRenderer.drawString(I18n.format("guikaia.config.rangeautokill"), (int) (width / 3.1), (int) (height / (yFirst - 4.5)), white);

        fontRenderer.drawString(I18n.format("guikaia.config." + chargeManaItemsInInventory), (int) (width / 5), (int) (height / 2.2), white);
        fontRenderer.drawString(I18n.format("guikaia.config." + chargeEnergyInBlocksAround), (width / 5), (int) (height / 2.5), white);
        fontRenderer.drawString(I18n.format("guikaia.config." + chargeManaInBlocksAround), (int) (width / 3.5), (int) (height / 1.7), white);
    }

    private void setNamesOfGuiTextList() {
        namesOfGuiTextList.add("minerationArea");
        namesOfGuiTextList.add("rangeattack");
        namesOfGuiTextList.add("blockreachdistance");
        namesOfGuiTextList.add("maxCountSlot");
        namesOfGuiTextList.add(rangeAutoKill);
        namesOfGuiTextList.add(chargeEnergyInBlocksAround);
        namesOfGuiTextList.add(chargeManaInBlocksAround);
    }

    private void setGuiTextFieldList(EntityPlayer player) {
        int rangeAttack = getKaiaInMainHand(player).getTagCompound().getInteger(KaiaConstantsNbt.rangeAttack);
        int id = 0;
        int TEXFIELD_ID = 0;
        guiTextFieldList.put(namesOfGuiTextList.get(id), new GuiTextField(TEXFIELD_ID, KaiaGui.fontRenderer, (int) (width / 5.8), (int) (height / 8.7), (int) (width / 6.85714285714), 20));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(10);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea)));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, (int) (width / 6), (int) (height / 6), (int) (width / 6.85714285714), 20));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(10);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(rangeAttack));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, (int) (width / 3.5), (int) (height / 2.9), (int) (width / 4.57142857143), 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf((int) player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, (int) (width / 3.4), (int) (height / 1.3), (int) (width / 4.57142857143), 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(maxCountSlot)));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, (int) (width / 2.4), (int) (height / (9 - 4.5)), (int) (width / 4.57142857143), 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(rangeAutoKill)));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, (int) (width / 2.2), (int) (height / 2.5), (int) (width / 4.57142857143), 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(chargeEnergyInBlocksAround)));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, (int) (width / 2), (int) (height / 1.7), (int) (width / 4.57142857143), 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).height = height / 39;
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(chargeManaInBlocksAround)));
    }

    private void setFunctionsForButtonsList(EntityPlayer player) {
        int id = 0;
        functionsForButtonsList.put(buttonsList.get(id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killFriendEntities);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.killFriendEntities, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killAllEntities);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.killAllEntities, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.counterAttack);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.counterAttack, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(attackYourWolf);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(attackYourWolf, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(interactLiquid);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(interactLiquid, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(noBreakTileEntity);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(noBreakTileEntity, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPack);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(autoBackPack, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPackEntities);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(autoBackPackEntities, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(playersCantRespawn);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(playersCantRespawn, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(chargeEnergyItemsInInventory);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(chargeEnergyItemsInInventory, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(summonLightBoltsInKill);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(summonLightBoltsInKill, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(banEntitiesAttacked);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(banEntitiesAttacked, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(autoKill);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(autoKill, !value));
        });
        functionsForButtonsList.put(buttonsList.get(++id), () -> {
            boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(chargeManaItemsInInventory);
            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(chargeManaItemsInInventory, !value));
        });
    }
}
