package com.omnipotent.test;

import net.minecraft.item.ItemStack;

public interface IContainer {
    boolean hasInventory(ItemStack stack);

    InventoryKaiaPickaxe getInventory(ItemStack stack);
}