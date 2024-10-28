package com.omnipotent;

import com.omnipotent.util.UtilityHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import static com.omnipotent.common.event.EventInitItems.kaia;

public class OmnipotentTab extends CreativeTabs {

    public String getSpacialName() {
        return UtilityHelper.coloringSpacial("Omnipotent Mod", 100);
    }

    public OmnipotentTab(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(kaia);
    }

    @Override
    public String getTranslationKey() {
        return UtilityHelper.coloringRainbow("Omnipotent Mod", 50);
    }
}
