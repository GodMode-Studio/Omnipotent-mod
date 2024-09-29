package com.omnipotent.client.gui.potion;

import com.omnipotent.Omnipotent;
import com.omnipotent.client.gui.elementsmod.GuiScrollable;
import com.omnipotent.common.gui.GuiHandler;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static com.omnipotent.util.KaiaConstantsNbt.kaiaPotion;

public class KaiaGuiPotionAddedAndRemove extends GuiScrollable {

    private final EntityPlayer player;
    private HashMap<GuiTextField, Potion> mapPotion = new HashMap<>();
    private GuiTextField guiLevel;

    public KaiaGuiPotionAddedAndRemove(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void resetGui() {
        mapPotion.clear();
        super.resetGui();
    }

    @Override
    protected void addScrollableElements() {
        Iterator<Potion> iterator = Potion.REGISTRY.iterator();
        int y = (int) ((int) (height / 8.5) / currentScrollOffset);
        while (iterator.hasNext()) {
            Potion next = iterator.next();
            if (next.isBadEffect())
                continue;
            GuiTextField guiTextField = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 13.7142857143), y, (int) (width / 1.15), (int) (height / 31.875));
            addElement(guiTextField, next);
            y += commonHeightElement;
        }
    }

    private void addElement(GuiTextField guiTextField, Potion next) {
        guiTextField.setText(I18n.format(next.getName()));
        guiTextField.setTextColor(0xa8ffcf);
        mapPotion.put(guiTextField, next);
        allElements.add(guiTextField);
    }

    @Override
    protected void addFixedElements() {
        guiLevel = new GuiTextField(++idGuiText, fontRenderer, (int) (width / 13.7142857143), (int) (height / 1.1), width / 5, (int) (height / 31.875));
        guiLevel.setText("Level of Effect");
        addButton(new GuiButton(0, (int) (width / 1.14347826087), (int) (height / 1.1), width / 23, (int) (height / 31.875), ">"));
        fixedElements.add(guiLevel);
        fixedElements.add(searchBar);
    }

    @Override
    protected void clickScrollableElementLogic(GuiTextField guiTextField) {
        Potion potion = mapPotion.get(guiTextField);
        String text = guiLevel.getText();
        int lvl = NumberUtils.toShort(text, (short) -100);
        lvl = lvl < 0 || lvl > 255 ? 1 : lvl;
        PotionEffect activePotionEffect = player.getActivePotionEffect(potion);
        boolean b = activePotionEffect != null && activePotionEffect.getAmplifier() >= lvl;
        NetworkRegister.sendToServer(new KaiaNbtPacket(kaiaPotion, potion.getRegistryName().toString(), b ? 0 : lvl));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        player.openGui(Omnipotent.instance, GuiHandler.KaiaGuiBlockPotion, player.world, 0, 0, 0);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
