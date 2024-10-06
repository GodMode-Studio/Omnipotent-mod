package com.omnipotent.common.specialgui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class SlotCraftKaia extends SlotCrafting {

    private boolean skipRefillCraft;
    private InventoryCrafting craftMatrix;

    public SlotCraftKaia(EntityPlayer player, InventoryCrafting craftingInventory, IInventory craftResult,
                         int slotIndex, int xPosition, int yPosition, boolean skipRefillCraft) {
        super(player, craftingInventory, craftResult, slotIndex, xPosition, yPosition);
        this.skipRefillCraft = skipRefillCraft;
        this.craftMatrix = craftingInventory;
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        super.onCrafting(stack);
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        if (skipRefillCraft) {
            return super.onTake(player, stack);
        }

        ItemStack[] prevItems = new ItemStack[craftMatrix.getSizeInventory()];

        for (int i = 0; i < prevItems.length; i++) {
            prevItems[i] = craftMatrix.getStackInSlot(i);

            if (!prevItems[i].isEmpty()) {
                prevItems[i] = ItemHandlerHelper.copyStackWithSize(prevItems[i], 1);
            }
        }

        ItemStack is = super.onTake(player, stack);
        return is;
    }
}
