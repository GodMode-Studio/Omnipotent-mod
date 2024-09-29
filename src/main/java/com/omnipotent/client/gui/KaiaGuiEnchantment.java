package com.omnipotent.client.gui;

import com.omnipotent.client.gui.elementsmod.GuiScrollable;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

import static com.omnipotent.util.KaiaConstantsNbt.kaiaEnchant;
import static com.omnipotent.util.KaiaConstantsNbt.kaiaPotion;

public class KaiaGuiEnchantment extends GuiScrollable {

    private final EntityPlayer player;
    private final HashMap<GuiTextField, Enchantment> mapEnchantment = new HashMap<>();
    private GuiTextField enchantmentLevel;

    public KaiaGuiEnchantment(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void resetGui() {
        mapEnchantment.clear();
        super.resetGui();
    }

    @Override
    protected void addScrollableElements() {
        Iterator<Enchantment> iterator = Enchantment.REGISTRY.iterator();
        int y = (int) ((int) (height / 8.5) / currentScrollOffset);
        while (iterator.hasNext()) {
            Enchantment next = iterator.next();
            GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (width / 1.15), (int) (height / 31.875));
            guiTextField.setText(next.getTranslatedName(1));
            guiTextField.setTextColor(next.isCurse() ? Color.RED.getRGB() : 0xa8ffcf);
            mapEnchantment.put(guiTextField, next);
            allElements.add(guiTextField);
            y += commonHeightElement;
        }
    }

    @Override
    protected void addFixedElements() {
        enchantmentLevel = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 13.7142857143), (int) (height / 1.1), (int) (width / 5), (int) (height / 31.875));
        enchantmentLevel.setText("Level Enchantment");
        fixedElements.add(enchantmentLevel);
        fixedElements.add(searchBar);
    }

    @Override
    protected void clickScrollableElementLogic(GuiTextField guiTextField) {
        Enchantment enchantment = mapEnchantment.get(guiTextField);
        String text = enchantmentLevel.getText();
        int lvl = NumberUtils.toShort(text, (short) -100);
        lvl = lvl < 0 ? 1 : lvl;
        NetworkRegister.sendToServer(new KaiaNbtPacket(kaiaEnchant, enchantment.getRegistryName().toString(), lvl));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
