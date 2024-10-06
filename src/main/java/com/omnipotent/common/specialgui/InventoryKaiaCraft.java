package com.omnipotent.common.specialgui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import static com.omnipotent.constant.NbtNumberValues.maxCountSlot;

public class InventoryKaiaCraft extends InventoryCrafting {

    private ItemStack stack;
    private ContainerKaia container;

    public InventoryKaiaCraft(Container eventHandlerIn, int width, int height, ItemStack stack) {
        super(eventHandlerIn, width, height);
        this.stack = stack;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (!stack.isEmpty()) {
            NBTTagCompound nbt;
            if (stack.hasTagCompound()) {
                nbt = stack.getTagCompound();
            } else {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            NBTTagList craftSlots;
            if (nbt.hasKey("CraftSlots")) {
                craftSlots = nbt.getTagList("CraftSlots", 10);
            } else {
                craftSlots = new NBTTagList();
                nbt.setTag("CraftSlots", craftSlots);
            }
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int index = 0; index < craftSlots.tagCount(); ++index) {
                NBTTagCompound nbttagcompound = craftSlots.getCompoundTagAt(index);
                ItemStack stack = new ItemStack(nbttagcompound);
                stack.setCount(nbttagcompound.getInteger("Count"));
                stacks.add(stack);
            }


            for (int index = 0; index < stacks.size(); index++) {
                this.container.putStackInSlot(index+1, stacks.get(index));
            }
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!stack.isEmpty()) {
            NBTTagCompound nbt;
            if (stack.hasTagCompound()) {
                nbt = stack.getTagCompound();
            } else {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            NBTTagList craftSlots = new NBTTagList();
            nbt.setTag("CraftSlots", craftSlots);
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    stacks.add(this.container.getSlotFromInventory(this, x + y * 3).getStack());
                }
            }
            for (ItemStack stack : stacks) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                stack.writeToNBT(nbttagcompound);
                nbttagcompound.removeTag("Count");
                nbttagcompound.setInteger("Count", stack.getCount());
                craftSlots.appendTag(nbttagcompound);
            }
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return stack.getTagCompound().getInteger(maxCountSlot.getValue());
    }

    public void setContainer(ContainerKaia containerKaia) {
        this.container = containerKaia;
    }
}
