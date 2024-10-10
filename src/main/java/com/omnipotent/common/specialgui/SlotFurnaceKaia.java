package com.omnipotent.common.specialgui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class SlotFurnaceKaia extends Slot {

    public SlotFurnaceKaia(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public void onSlotChanged() {
        ItemStack stack = this.getStack();
        if (!stack.isEmpty()) {
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
            if(!result.isEmpty()){
                this.inventory.setInventorySlotContents(this.getSlotIndex(), ItemStack.EMPTY);
                this.inventory.setInventorySlotContents(91, result);
            }
        }
        super.onSlotChanged();
    }
}
