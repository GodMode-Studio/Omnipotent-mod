package com.omnipotent.client.gui;

import com.omnipotent.client.gui.elementsmod.GuiButtonMod;
import com.omnipotent.client.gui.elementsmod.GuiTextFieldMod;
import com.omnipotent.client.gui.elementsmod.IScrollableGui;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.constant.NbtNumberValues;
import com.omnipotent.constant.NbtStringValues;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.omnipotent.constant.NbtBooleanValues.*;
import static com.omnipotent.constant.NbtNumberValues.rangeAutoKill;
import static com.omnipotent.constant.NbtStringValues.customPlayerName;

public class KaiaGui extends GuiScreen implements IScrollableGui {
    private ItemStack kaia;
    private final EntityPlayer player;
    private int widthOfScreen;
    private int heightOfScreen;
    private double currentScrollOffset = 1.0;
    private int maxScrollOffset = Integer.MAX_VALUE;
    private double targetScrollOffset = 1.0;
    private GuiTextField searchBar;
    private List<GuiTextFieldMod> configElements = new ArrayList<>();
    private List<GuiTextFieldMod> configElementsRendered = new ArrayList<>();
    private String oldTextInSearchBox = "";
    private int idGuiText = -1;
    private Minecraft minecraft;
    private GuiTextFieldMod actualGuiChangeNumberConfigRended;
    private NbtNumberValues actualValueSended;
    private NbtStringValues actualValueSendedString;
    private Integer valueSend;
    private String valueStringSend;

    public KaiaGui(EntityPlayerSP player) {
        this.player = player;
        this.kaia = KaiaUtil.getKaiaInMainHand(player).get();
    }

    @Override
    public void initGui() {
        super.initGui();
        widthOfScreen = super.width;
        heightOfScreen = super.height;
        minecraft = Minecraft.getMinecraft();
        currentScrollOffset = 1.0;
        targetScrollOffset = 1.0;
        configElements.clear();
        configElementsRendered.clear();
        actualGuiChangeNumberConfigRended = null;
        createAndFillElements();
        searchBar = new GuiTextField(++idGuiText, fontRenderer, (int) (widthOfScreen / 2.45), (heightOfScreen / 18), widthOfScreen / 5, heightOfScreen / 25);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        drawString(fontRenderer, I18n.format("guikaia.config"), (int) (widthOfScreen / 2.3), 1, Color.CYAN.getRGB());
        drawRect(0, (int) (heightOfScreen / 1.36), widthOfScreen, heightOfScreen, -804253680);
        renderElements(mouseX, mouseY, partialTicks);
        searchBar.drawTextBox();
        drawRect(0, (int) (heightOfScreen / 1.36), widthOfScreen, (int) (heightOfScreen / 1.4), Color.BLACK.getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (actualGuiChangeNumberConfigRended != null) {
            checkAndSendValueToNetwork();
            if (actualGuiChangeNumberConfigRended.mouseClicked(mouseX, mouseY, button)) {
                super.mouseClicked(mouseX, mouseY, button);
                return;
            }
        }
        searchBar.mouseClicked(mouseX, mouseY, button);
        if (theClickInButtonChangeConfig(mouseX, mouseY, button)) return;
        for (GuiTextFieldMod gui : configElements) {
            if (gui.mouseClicked(mouseX, mouseY, button)) {
                if (stopClickInGuisNoRended(gui)) {
                    gui.setTextColor(Color.CYAN.getRGB());
                    gui.setFocused(false);
                    continue;
                }
                gui.setSelected(true);
                gui.setTextColor(Color.GREEN.getRGB());
                gui.setFocused(false);
            } else {
                gui.setTextColor(Color.CYAN.getRGB());
                gui.setSelected(false);
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    private void checkAndSendValueToNetwork() {
        NbtNumberValues nbtNumberValue = actualGuiChangeNumberConfigRended.getNbtNumberValue();
        if (nbtNumberValue != null)
            actualGuiValueIsNbtNumber(nbtNumberValue);
        else {
            actualGuiValueIsNbtString(actualGuiChangeNumberConfigRended.getNbtStringValue());
        }
    }

    private void actualGuiValueIsNbtString(NbtStringValues nbtString) {
        String text = actualGuiChangeNumberConfigRended.getText();
        if (!text.trim().isEmpty()) {
            if (actualValueSendedString != nbtString || !valueStringSend.equals(text)) {
                NetworkRegister.sendToServer(new KaiaNbtPacket(nbtString.getValue(), text, 0));
                actualValueSendedString = nbtString;
                valueStringSend = text;
            }
        }
    }

    private void actualGuiValueIsNbtNumber(NbtNumberValues NbtNumber) {
        if (isCorrectFormat()) {
            int integer = Integer.parseInt(actualGuiChangeNumberConfigRended.getText());
            if (actualValueSended != NbtNumber || !valueSend.equals(integer)) {
                if (valueIsValid()) {
                    NetworkRegister.sendToServer(new KaiaNbtPacket(NbtNumber.getValue(), integer));
                } else {
                    UtilityHelper.sendMessageToPlayer("The value " + actualGuiChangeNumberConfigRended.getText() + " is bellow 1 or above 1000, this not allow value not changed.", player);
                }
                actualValueSended = NbtNumber;
                valueSend = integer;
            }
        } else {
            UtilityHelper.sendMessageToPlayer("The value " + actualGuiChangeNumberConfigRended.getText() + " is invalid value not changed.", player);
        }
    }

    private boolean isCorrectFormat() {
        try {
            Integer.valueOf(actualGuiChangeNumberConfigRended.getText());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean valueIsValid() {
        if (!thisNbtNumberIsValid(actualGuiChangeNumberConfigRended.getNbtNumberValue(), Integer.valueOf(actualGuiChangeNumberConfigRended.getText())))
            return false;
        return true;
    }

    private boolean thisNbtNumberIsValid(NbtNumberValues nbtNumberValue, Integer valueOfActualGuiChangeNumber) {
        List<NbtNumberValues> standartValues = Arrays.stream(NbtNumberValues.values()).filter(nbtValue -> nbtValue != NbtNumberValues.maxCountSlot).collect(Collectors.toList());
        if (standartValues.contains(nbtNumberValue) && (valueOfActualGuiChangeNumber < 1 || valueOfActualGuiChangeNumber > 1000)) {
            return false;
        } else {
            if (nbtNumberValue == NbtNumberValues.maxCountSlot && (valueOfActualGuiChangeNumber > Integer.MAX_VALUE - 1 || valueOfActualGuiChangeNumber < 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean theClickInButtonChangeConfig(int mouseX, int mouseY, int button) throws IOException {
        if (!buttonList.isEmpty() && buttonList.get(0).mousePressed(minecraft, mouseX, mouseY)) {
            super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        searchBar.textboxKeyTyped(typedChar, keyCode);
        if (actualGuiChangeNumberConfigRended != null)
            actualGuiChangeNumberConfigRended.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        if (actualGuiChangeNumberConfigRended != null)
            checkAndSendValueToNetwork();
        super.onGuiClosed();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        GuiButtonMod buttonMod = (GuiButtonMod) button;
        NbtBooleanValues valueNbt = buttonMod.getValueNbtBoolean();
        this.kaia = KaiaUtil.getKaiaInMainHand(player).get();
        NetworkRegister.sendToServer(new KaiaNbtPacket(valueNbt.getValue(), !kaia.getTagCompound().getBoolean(valueNbt.getValue())));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Math.round(-Math.signum(Mouse.getEventDWheel()));
        updateScrollOffset(scroll);
    }

    @Override
    public void updateScrollOffset(int scroll) {
        if (scroll == 0)
            return;
        double scrollFactor = 1.1;
        double newScrollOffset = currentScrollOffset + scroll * scrollFactor;
        newScrollOffset = Math.max(1.0, Math.min(maxScrollOffset, newScrollOffset));
        targetScrollOffset = newScrollOffset;
    }

    @Override
    public void renderElements(int mouseX, int mouseY, float partialTicks) {
        currentScrollOffset += (targetScrollOffset - currentScrollOffset) * 0.1;
        double v = currentScrollOffset;
        int minY = heightOfScreen / 10;
        double yOffset = (heightOfScreen / 9) * (1 / currentScrollOffset);
        int count = 1;
        Pattern pattern;
        String text = searchBar.getText();
        pattern = !text.trim().isEmpty() ? Pattern.compile(text, Pattern.CASE_INSENSITIVE) : null;
        configElementsRendered.clear();
        for (GuiTextFieldMod gui : configElements) {
            if (yOffset > heightOfScreen / 1.44871794872)
                break;
            if (!oldTextInSearchBox.equals(text)) {
                oldTextInSearchBox = text;
                targetScrollOffset = 1;
            }
            if (pattern == null) {
                if ((Math.round(v) != 1 && count < v)) {
                    count++;
                    continue;
                }
            } else if (!pattern.matcher(gui.getText()).find()) {
                count++;
                continue;
            }
            if (yOffset >= minY) {
                gui.y = (int) yOffset;
                checkAndDrawActualPartScreen(mouseX, mouseY, partialTicks, gui);
                gui.drawTextBox();
                configElementsRendered.add(gui);
            }
            yOffset += height / 21.25;
        }
    }

    @Override
    public void addElements() {

    }

    @Override
    public void resetGui() {

    }

    private void checkAndDrawActualPartScreen(int mouseX, int mouseY, float partialTicks, GuiTextFieldMod gui) {
        if (gui.isSelected()) {
            drawDescription(gui.getDescription());
            if (gui.getValue() != null) {
                if (gui.getNbtNumberValue() != null)
                    drawAndCreateButtonAndSelectValue(gui.getValue(), gui.getNbtNumberValue(), mouseX, mouseY, partialTicks);
                else if (gui.getNbtStringValue() != null)
                    drawAndCreateButtonAndSelectValueString(gui.getValue(), gui.getNbtStringValue(), mouseX, mouseY, partialTicks);
                else
                    drawAndCreateButtonAndSelectValue(gui.getValue(), null, mouseX, mouseY, partialTicks);
            } else
                drawAndCreateGuiTextFieldInJustNumberOptions(gui.getNbtNumberValue(), mouseX, mouseY, partialTicks);
        }
    }

    private void drawAndCreateGuiTextFieldInJustNumberOptions(NbtNumberValues value, int mouseX, int mouseY, float partialTicks) {
        if (actualGuiChangeNumberConfigRended != null && actualGuiChangeNumberConfigRended.getNbtNumberValue() == value) {
            actualGuiChangeNumberConfigRended.drawTextBox();
            return;
        }
        actualGuiChangeNumberConfigRended = new GuiTextFieldMod(5000, fontRenderer, (int) (widthOfScreen / 2.5), (int) (heightOfScreen / 1.05), widthOfScreen / 5, heightOfScreen / 20, String.valueOf(kaia.getTagCompound().getInteger(value.getValue())));
        actualGuiChangeNumberConfigRended.setNbtNumberValue(value);
        actualGuiChangeNumberConfigRended.setText(String.valueOf(KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getInteger(value.getValue())));
    }

    private void drawAndCreateButtonAndSelectValue(NbtBooleanValues value, NbtNumberValues valueNumber, int mouseX, int mouseY, float partialTicks) {
        if (!buttonList.isEmpty() && ((GuiButtonMod) buttonList.get(0)).getValueNbtBoolean() == value) {
            buttonList.get(0).displayString = String.valueOf(KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getBoolean(value.getValue()));
            buttonList.get(0).drawButton(minecraft, mouseX, mouseY, partialTicks);
            if (valueNumber != null)
                createGuiIntegerValue(valueNumber);
            return;
        }
        buttonList.clear();
        GuiButtonMod guiButton = new GuiButtonMod(0, 0, (int) (heightOfScreen / 1.1), widthOfScreen / 10, heightOfScreen / 20, String.valueOf(kaia.getTagCompound().getBoolean(value.getValue())));
        guiButton.setValueNbtBoolean(value);
        if (valueNumber != null)
            createGuiIntegerValue(valueNumber);
        buttonList.add(guiButton);
    }

    private void drawAndCreateButtonAndSelectValueString(NbtBooleanValues value, NbtStringValues valueString, int mouseX, int mouseY, float partialTicks) {
        if (!buttonList.isEmpty() && ((GuiButtonMod) buttonList.get(0)).getValueNbtBoolean() == value) {
            buttonList.get(0).displayString = String.valueOf(KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getBoolean(value.getValue()));
            buttonList.get(0).drawButton(minecraft, mouseX, mouseY, partialTicks);
            if (valueString != null)
                createGuiStringValue(valueString);
            return;
        }
        buttonList.clear();
        GuiButtonMod guiButton = new GuiButtonMod(0, 0, (int) (heightOfScreen / 1.1), widthOfScreen / 10, heightOfScreen / 20, String.valueOf(kaia.getTagCompound().getBoolean(value.getValue())));
        guiButton.setValueNbtBoolean(value);
        if (valueString != null)
            createGuiStringValue(valueString);
        buttonList.add(guiButton);
    }

    private void createGuiStringValue(NbtStringValues valueString) {
        if (actualGuiChangeNumberConfigRended != null && actualGuiChangeNumberConfigRended.getNbtStringValue() == valueString) {
            actualGuiChangeNumberConfigRended.drawTextBox();
            return;
        }
        actualGuiChangeNumberConfigRended = new GuiTextFieldMod(5000, fontRenderer, (int) (widthOfScreen / 2.5), (int) (heightOfScreen / 1.05), widthOfScreen / 5, heightOfScreen / 20, kaia.getTagCompound().getString(valueString.getValue()));
        actualGuiChangeNumberConfigRended.setNbtStringValue(valueString);
        actualGuiChangeNumberConfigRended.setText(KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getString(valueString.getValue()));
    }

    private void createGuiIntegerValue(NbtNumberValues valueNumber) {
        if (actualGuiChangeNumberConfigRended != null && actualGuiChangeNumberConfigRended.getNbtNumberValue() == valueNumber) {
            actualGuiChangeNumberConfigRended.drawTextBox();
            return;
        }
        actualGuiChangeNumberConfigRended = new GuiTextFieldMod(5000, fontRenderer, (int) (widthOfScreen / 2.5), (int) (heightOfScreen / 1.05), widthOfScreen / 5, heightOfScreen / 20, String.valueOf(kaia.getTagCompound().getInteger(valueNumber.getValue())));
        actualGuiChangeNumberConfigRended.setNbtNumberValue(valueNumber);
        actualGuiChangeNumberConfigRended.setText(String.valueOf(KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getInteger(valueNumber.getValue())));
    }

    private void drawDescription(String description) {
        List<String> strings = fontRenderer.listFormattedStringToWidth(I18n.format(description), widthOfScreen);
        int spacing = 0;
        for (String string : strings) {
            drawString(fontRenderer, string, 0, (int) (heightOfScreen / 1.36) + spacing, Color.WHITE.getRGB());
            spacing = (int) (heightOfScreen / 21.25);
        }
    }

    private void createAndFillElements() {
        int y = (int) ((int) (heightOfScreen / 8.5) / currentScrollOffset);
        createAndFillNbtBooleanValues(y);
        createAndFillNbtNumberValues(y);
    }

    private void createAndFillNbtNumberValues(int y) {
        List<String> elementsOfKaiaScreenConfig = NbtNumberValues.valuesNbt.stream().filter(string -> !(string.equals(rangeAutoKill.getValue()))).collect(Collectors.toList());
        for (int c = 0; c < elementsOfKaiaScreenConfig.size(); c++) {
            int finalC = c;
            NbtNumberValues nbtNumberValue = Arrays.stream(NbtNumberValues.values()).filter(value -> value.getValue().equals(elementsOfKaiaScreenConfig.get(finalC))).findFirst().get();
            GuiTextFieldMod guiTextField = new GuiTextFieldMod(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (widthOfScreen / 1.15), (int) (height / 21.25), nbtNumberValue.getDescription());
            guiTextField.setMaxStringLength(50);
            guiTextField.setFocused(false);
            guiTextField.setText(I18n.format("guikaia.config." + elementsOfKaiaScreenConfig.get(c)));
            guiTextField.setNbtNumberValue(nbtNumberValue);
            guiTextField.setTextColor(Color.CYAN.getRGB());
            guiTextField.height = (int) (height / 31.875);
            guiTextField.drawTextBox();
            configElements.add(guiTextField);
            y += height / 21.25;
        }
    }

    private void createAndFillNbtBooleanValues(int y) {
        List<String> elementsOfKaiaScreenConfig = NbtBooleanValues.valuesNbt.stream().filter(string -> !(string.equals(playerDontKillInDirectAttack.getValue()) || string.equals(playersWhoShouldNotKilledInCounterAttack.getValue()) || string.equals(showInfo.getValue()))).collect(Collectors.toList());
        for (int c = 0; c < elementsOfKaiaScreenConfig.size(); c++) {
            int finalC = c;
            NbtBooleanValues nbtBooleanValue = Arrays.stream(NbtBooleanValues.values()).filter(value -> value.getValue().equals(elementsOfKaiaScreenConfig.get(finalC))).findFirst().get();
            GuiTextFieldMod guiTextField = new GuiTextFieldMod(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (widthOfScreen / 1.15), (int) (height / 21.25), nbtBooleanValue.getDescription());
            guiTextField.setMaxStringLength(50);
            guiTextField.setFocused(false);
            guiTextField.setText(I18n.format("guikaia.config." + elementsOfKaiaScreenConfig.get(c)));
            guiTextField.setValue(nbtBooleanValue);
            if (nbtBooleanValue == autoKill)
                guiTextField.setNbtNumberValue(rangeAutoKill);
            else if (nbtBooleanValue == activeCustomPlayerName)
                guiTextField.setNbtStringValue(customPlayerName);
            guiTextField.setTextColor(Color.CYAN.getRGB());
            guiTextField.height = (int) (height / 31.875);
            guiTextField.drawTextBox();
            configElements.add(guiTextField);
            y += height / 21.25;
        }
    }

    private boolean stopClickInGuisNoRended(GuiTextField textField) {
        String text = textField.getText();
        List<String> collect = configElementsRendered.stream().map(GuiTextField::getText).collect(Collectors.toList());
        return !collect.contains(text);
    }
}
