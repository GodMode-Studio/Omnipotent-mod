package com.omnipotent.init;

import com.omnipotent.gui.KaiaGui;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.tools.KaiaConstantsNbt;
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

import static com.omnipotent.tools.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.getKaiaInMainHand;

public class InitButtonsForGuiKaia {
    public List<GuiButton> buttonsList = new ArrayList<GuiButton>();
    public List<String> namesOfButtons = new ArrayList<String>();
    public List<String> namesOfGuiTextList = new ArrayList<String>();
    public Map<GuiButton, Runnable> functionsForButtonsList = new HashMap<>();
    public Map<String, GuiTextField> guiTextFieldList = new HashMap<>();
    public List<String> textButtonList = new ArrayList<>();
    public static int width = KaiaGui.width;
    public static int height = KaiaGui.height;
    public int buttonID = 0;
    private final Map<String, GuiButton> guiButtonList = new HashMap<>();

    /*Para adicionar um novo botão, adicione ele no método setButtonList, adicione o nome dele em setNamesInListNamesOfButtons, e o texto dele em
    setTextButtonList e sua função em setFunctionsForButtonsList.
    */
    public void init(EntityPlayer player, List<GuiButton> list) {
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
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killFriendEntities)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killAllEntities)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.counterAttack)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.attackYourWolf)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.interactLiquid)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.noBreakTileEntity)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.autoBackPack)));
        textButtonList.add(String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPackEntities)));

    }

    private void setButtonList(EntityPlayer player) {
        buttonsList.add(new GuiButton(buttonID, width / 2 - 62, height / 2 - 38, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killFriendEntities))));
        buttonsList.add(new GuiButton(++buttonID, width / 2 - 62, height / 2 - 19, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killAllEntities))));
        buttonsList.add(new GuiButton(++buttonID, width / 2 - 115, height / 2 - -20, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.counterAttack))));
        buttonsList.add(new GuiButton(++buttonID, width / 2 - 115, height / 2 - -40, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.attackYourWolf))));
        buttonsList.add(new GuiButton(++buttonID, width / 2 - 50, height / 2 - -60, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.interactLiquid))));
        buttonsList.add(new GuiButton(++buttonID, width / 2 - 47, height / 2 - -80, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.noBreakTileEntity))));
        buttonsList.add(new GuiButton(++buttonID, width / 2 - 90, height / 2 - -100, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.autoBackPack))));
        buttonsList.add(new GuiButton(++buttonID, width - width / 3, height / 2 + 40, 30, 15, String.valueOf(getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.autoBackPackEntities))));
    }

    private void setGuiButtonList() {
        int c = 0;
        for (String name : namesOfButtons) {
            guiButtonList.put(name, buttonsList.get(c));
            c++;
        }
    }

    public void drawGuiText() {
        for (String name : namesOfGuiTextList) {
            guiTextFieldList.get(name).drawTextBox();
        }
    }

    public void drawLabels(FontRenderer fontRenderer) {
        fontRenderer.drawString(I18n.format("guikaia.config"), (width - fontRenderer.getStringWidth(I18n.format("guikaia.config"))) / 2, height / 20, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.minerationarea"), width / 2 - 200, height / 2 - 85, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.rangeattack"), width / 2 - 200, height / 2 - 60, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.attackfriendentities"), width / 2 - 200, height / 2 - 35, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.attackallentities"), width / 2 - 200, height / 2 - 15, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.blockinteractiondistance"), width / 2 - 200, height / 2 - -5, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.counterattack"), width / 2 - 200, height / 2 - -25, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.attackyourwolf"), width / 2 - 200, height / 2 - -45, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.interactwithliquidblocks"), width / 2 - 200, height / 2 - -65, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.donotbreaktileentityblocks"), width / 2 - 200, height / 2 - -85, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.maxslotcount"), width / 2 - 75, height / 2 - -25, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.autobackpack"), width / 2 - 200, height / 2 - -105, Color.WHITE.getRGB());
        fontRenderer.drawString(I18n.format("guikaia.config.autobackpackentities"), width / 3, height / 2 + 45, Color.WHITE.getRGB());
    }

    private void setNamesOfGuiTextList() {
        namesOfGuiTextList.add("minerationArea");
        namesOfGuiTextList.add("rangeattack");
        namesOfGuiTextList.add("blockreachdistance");
        namesOfGuiTextList.add("maxCountSlot");
    }

    private void setGuiTextFieldList(EntityPlayer player) {
        int rangeAttack = getKaiaInMainHand(player).getTagCompound().getInteger(KaiaConstantsNbt.rangeAttack);
        int id = 0;
        int TEXFIELD_ID = 0;
        guiTextFieldList.put(namesOfGuiTextList.get(id), new GuiTextField(TEXFIELD_ID, KaiaGui.fontRenderer, width / 2 - 100, height / 2 - 90, 200, 20));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(10);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea)));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, width / 2 - 100, height / 2 - 65, 200, 20));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(rangeAttack));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, width / 2 - 10, height / 2 - -3, 105, 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf((int) player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()));

        guiTextFieldList.put(namesOfGuiTextList.get(++id), new GuiTextField(TEXFIELD_ID++, KaiaGui.fontRenderer, width / 2 + 50, height / 2 - -25, 105, 10));
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setMaxStringLength(100);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setFocused(false);
        guiTextFieldList.get(namesOfGuiTextList.get(id)).setText(String.valueOf(getKaiaInMainHand(player).getTagCompound().getInteger(maxCountSlot)));
    }

    private void setFunctionsForButtonsList(EntityPlayer player) {
        int id = 0;
        functionsForButtonsList.put(buttonsList.get(id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killFriendEntities);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.killFriendEntities, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.killAllEntities);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.killAllEntities, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.counterAttack);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.counterAttack, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(attackYourWolf);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(attackYourWolf, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(interactLiquid);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(interactLiquid, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(noBreakTileEntity);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(noBreakTileEntity, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPack);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(autoBackPack, !value));
            }
        });
        functionsForButtonsList.put(buttonsList.get(++id), new Runnable() {
            @Override
            public void run() {
                boolean value = getKaiaInMainHand(player).getTagCompound().getBoolean(autoBackPackEntities);
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(autoBackPackEntities, !value));
            }
        });
    }
}
