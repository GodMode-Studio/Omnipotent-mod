package com.omnipotent.common.specialgui;

import net.minecraft.item.ItemStack;

public interface IContainer {
    boolean hasInventory(ItemStack stack);

    InventoryKaia getInventory(ItemStack stack);
}