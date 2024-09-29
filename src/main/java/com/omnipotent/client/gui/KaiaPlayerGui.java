package com.omnipotent.client.gui;

import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.NbtListUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.constant.NbtBooleanValues.playerDontKillInDirectAttack;
import static com.omnipotent.constant.NbtBooleanValues.playersWhoShouldNotKilledInCounterAttack;
import static com.omnipotent.util.KaiaConstantsNbt.playersDontKill;
import static com.omnipotent.util.UtilityHelper.getEquivalentValueOfscreenHeight;
import static com.omnipotent.util.UtilityHelper.getEquivalentValueOfscreenWidth;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class KaiaPlayerGui extends GuiScreen {

    private final EntityPlayer player;
    private int page;
    private int oldValueOfPage = 0;
    private List<GuiTextField> guiTextFieldList = new ArrayList<GuiTextField>();
    private List<GuiTextField> listGuiTextField = new ArrayList<>();
    int idButtons = -1;

    public KaiaPlayerGui(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButtonsPage();
        playerAdded();
        listGuiTextField.add(new GuiTextField(23930292, fontRenderer, getEquivalentValueOfscreenWidth(110, width), getEquivalentValueOfscreenHeight(240, height), 160, 10));
        listGuiTextField.get(0).setText(I18n.format("guikaia.playermanager.input"));
        ItemStack kaiaInMainHand = KaiaUtil.getKaiaInMainHand(player).get();
        GuiButton button = new GuiButton(++idButtons, getEquivalentValueOfscreenWidth(273, width), getEquivalentValueOfscreenHeight(238, height), I18n.format("guikaia.playermanager.save"));
        button.height = 12;
        button.width = button.displayString.length() * 7;
        buttonList.add(button);
        GuiButton playerDontKillCounterButton = new GuiButton(++idButtons, getEquivalentValueOfscreenWidth(335, width), getEquivalentValueOfscreenHeight(233, height), String.valueOf(kaiaInMainHand.getTagCompound().getBoolean(playersWhoShouldNotKilledInCounterAttack.getValue())));
        playerDontKillCounterButton.height = 12;
        playerDontKillCounterButton.width = playerDontKillCounterButton.displayString.length() * 7;
        buttonList.add(playerDontKillCounterButton);
        GuiButton dontKillPlayersInDirectAttack = new GuiButton(++idButtons, getEquivalentValueOfscreenWidth(373, width), getEquivalentValueOfscreenHeight(233, height), String.valueOf(kaiaInMainHand.getTagCompound().getBoolean(playerDontKillInDirectAttack.getValue())));
        dontKillPlayersInDirectAttack.height = 12;
        dontKillPlayersInDirectAttack.width = dontKillPlayersInDirectAttack.displayString.length() * 7;
        buttonList.add(dontKillPlayersInDirectAttack);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        playerAdded();
        listGuiTextField.forEach(guiTextField -> guiTextField.drawTextBox());
        NBTTagCompound tagCompound = KaiaUtil.getKaiaInMainHand(player).get().getTagCompound();
        for (GuiButton button : buttonList) {
            if (button.id == 3)
                button.displayString = String.valueOf(tagCompound.getBoolean(playersWhoShouldNotKilledInCounterAttack.getValue()));
            else if (button.id == 4)
                button.displayString = String.valueOf(tagCompound.getBoolean(playerDontKillInDirectAttack.getValue()));
            button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
        }
        drawString(fontRenderer, I18n.format("guikaia.playermanager"), 220, 5, Color.WHITE.getRGB());
        //cor pega com base nas cores normais do minecraft em GuiScreen
        drawGradientRect(getEquivalentValueOfscreenWidth(33, width), getEquivalentValueOfscreenHeight(26, height), getEquivalentValueOfscreenWidth(470, width), getEquivalentValueOfscreenHeight(226, height), -1072689136, -804253680);
        if (page != oldValueOfPage) {
            playerAdded();
            oldValueOfPage = page;
        }
        guiTextFieldList.forEach(GuiTextField::drawTextBox);
        for (GuiButton button : buttonList) {
            if (button.isMouseOver() && button.id == 3)
                drawHoveringText(I18n.format("guikaia.playermanager.dontcounterattack"), mouseX, mouseY);
            else if (button.isMouseOver() && button.id == 4)
                drawHoveringText(I18n.format("guikaia.playermanager.dontDirectAttackKillPlayer"), mouseX, mouseY);
        }
        if (listGuiTextField.get(0).isFocused()) {
            GuiTextField gui = listGuiTextField.get(0);
            drawHoveringText("With auto-complete", gui.x - getEquivalentValueOfscreenHeight(10, height), gui.y);
        }
    }

    private void playerAdded() {
        guiTextFieldList.clear();
        NBTTagList compoundTag = KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getTagList(playersDontKill, 8);
        ArrayList<String> allPlayers = new ArrayList<>();
        Iterator<NBTBase> iterator = compoundTag.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next().toString();
            if (string.startsWith("\"") && string.endsWith("\""))
                string = string.substring(1, string.length() - 1);
            String[] split = string.split(NbtListUtil.divisionUUIDAndName);
            allPlayers.add(split[1]);
        }
        int idGuiText = -1;
        int y = getEquivalentValueOfscreenHeight(27, height);
        for (int c = 0; c < allPlayers.size(); c++) {
            if (y < getEquivalentValueOfscreenHeight(240, height)) {
                int number = page * 17;
                if (c + number < allPlayers.size()) {
                    String namePlayer = allPlayers.get(c + number);
                    int widthGui = namePlayer.length() * 7;
                    GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, getEquivalentValueOfscreenHeight(35, height), y, widthGui, 12);
                    guiTextField.setFocused(false);
                    guiTextField.setText(namePlayer);
                    guiTextField.height = 8;
                    guiTextField.drawTextBox();
                    guiTextFieldList.add(guiTextField);
                    y += 12;
                }
            }
        }
    }

    private void addButtonsPage() {
        GuiButton paginaAnterior = new GuiButton(++idButtons, getEquivalentValueOfscreenWidth(190, width), getEquivalentValueOfscreenHeight(15, height), I18n.format("guikaia.enchant.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(++idButtons, getEquivalentValueOfscreenWidth(190, width), getEquivalentValueOfscreenHeight(226, height), I18n.format("guikaia.enchant.nextpage"));
        proximaPagina.height = 11;
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = 8 * displayString.length();
        buttonList.add(proximaPagina);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        listGuiTextField.forEach(guiTextField -> guiTextField.mouseClicked(mouseX, mouseY, mouseButton));
        for (GuiTextField guiField : guiTextFieldList) {
            guiField.mouseClicked(mouseX, mouseY, mouseButton);
            if (guiField.isFocused()) {
                NetworkRegister.sendToServer(new KaiaNbtPacket(playersDontKill, guiField.getText(), 1));
                guiField.setFocused(false);
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (GuiTextField field : listGuiTextField) {
            if (field.isFocused() && field.getId() == 23930292 && keyCode == Keyboard.KEY_TAB) {
                String args[] = new String[2];
                args[0] = "";
                args[1] = field.getText();
                List<String> playersNames = null;
                try {
                    playersNames = Minecraft.getMinecraft().getConnection().getPlayerInfoMap().stream().map(playerInfo -> playerInfo.getGameProfile().getName().toString()).collect(Collectors.toList());
                } catch (Exception e) {
                }
                boolean noExecute = false;
                for (int c = 0; c < playersNames.size(); c++) {
                    if (playersNames.get(c).equals(args[1])) {
                        noExecute = true;
                        try {
                            field.setText(playersNames.get(++c));
                        } catch (IndexOutOfBoundsException e) {
                            field.setText(playersNames.get(c - 1));
                        }
                        break;
                    }
                }
                List<String> listOfStringsMatchingLastWord = getListOfStringsMatchingLastWord(args, playersNames);
                if (!listOfStringsMatchingLastWord.isEmpty() && !noExecute)
                    field.setText(listOfStringsMatchingLastWord.get(0));
            }
            field.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        NBTTagCompound tagCompound = KaiaUtil.getKaiaInMainHand(player).get().getTagCompound();
        switch (button.id) {
            case 0:
                if (!(page == 0))
                    this.page--;
                break;
            case 1:
                this.page++;
                break;
            case 2:
                String namePlayer = listGuiTextField.get(0).getText();
                if (!player.getName().equals(namePlayer))
                    NetworkRegister.sendToServer(new KaiaNbtPacket(playersDontKill, namePlayer, 0));
                break;
            case 3:
                NetworkRegister.sendToServer(new KaiaNbtPacket(playersWhoShouldNotKilledInCounterAttack.getValue(), !tagCompound.getBoolean(playersWhoShouldNotKilledInCounterAttack.getValue())));
                break;
            case 4:
                NetworkRegister.sendToServer(new KaiaNbtPacket(playerDontKillInDirectAttack.getValue(), !tagCompound.getBoolean(playerDontKillInDirectAttack.getValue())));
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
