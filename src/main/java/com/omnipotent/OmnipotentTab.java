package com.omnipotent;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import static com.omnipotent.common.event.EventInitItems.kaia;

public class OmnipotentTab extends CreativeTabs {
    public OmnipotentTab(String label) {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(kaia);
    }
}
