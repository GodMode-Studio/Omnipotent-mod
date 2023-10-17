package com.omnipotent.server.specialgui;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

import static com.omnipotent.constant.NbtNumberValues.maxCountSlot;

public class InventoryKaia implements IInventory {

    private ItemStack stack;
    private List<NonNullList<ItemStack>> pages;
    private int currentPage;


    public boolean hasCustomName() {
        return false;
    }


    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getName());
    }


    public int getSizeInventory() {
        return 81;
    }


    public boolean isEmpty() {
        for (NonNullList<ItemStack> stacks : pages) {
            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < getSizeInventory() ? getPage(currentPage).get(index) : ItemStack.EMPTY;
    }


    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(getPage(currentPage), index, count);
        if (!stack.isEmpty()) {
            this.markDirty();
        }
        return stack;
    }


    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getPage(currentPage).get(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            getPage(currentPage).set(index, ItemStack.EMPTY);
            return stack;
        }
    }


    public void setInventorySlotContents(int index, ItemStack stack) {
        getPage(currentPage).set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }


    public void markDirty() {
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }


    public void openInventory(EntityPlayer player) {
        if (!stack.isEmpty()) {
            pages.clear();
            NBTTagCompound nbt;
            if (stack.hasTagCompound()) {
                nbt = stack.getTagCompound();
            } else {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            NBTTagCompound nbtPages;
            if (nbt.hasKey("Pages")) {
                nbtPages = nbt.getCompoundTag("Pages");
            } else {
                nbtPages = new NBTTagCompound();
                nbt.setTag("Pages", nbtPages);
            }
            if (nbtPages.hasKey("CurPage")) {
                currentPage = nbtPages.getInteger("CurPage");
            } else {
                currentPage = 0;
                nbtPages.setInteger("CurPage", 0);
            }
            NBTTagList pageList;
            if (nbtPages.hasKey("PageList")) {
                pageList = nbtPages.getTagList("PageList", 10);
            } else {
                pageList = new NBTTagList();
                NBTTagCompound page = new NBTTagCompound();
                pageList.appendTag(page);
                nbtPages.setTag("PageList", pageList);
            }
            for (int i = 0; i < pageList.tagCount(); i++) {
                NBTTagCompound page = pageList.getCompoundTagAt(i);
                NonNullList<ItemStack> stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
                loadAllItems(page, stacks);
                pages.add(stacks);
            }
        }
    }

    public void loadAllItems(NBTTagCompound tag, NonNullList<ItemStack> list) {
        NBTTagList nbttaglist = tag.getTagList("Items", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j >= 0 && j < list.size()) {
                ItemStack stack = new ItemStack(nbttagcompound);
                stack.setCount(nbttagcompound.getInteger("Count"));
                list.set(j, stack);
            }
        }
    }

    public void closeInventory(EntityPlayer player) {
        if (!stack.isEmpty()) {
            NBTTagCompound nbt;
            if (stack.hasTagCompound()) {
                nbt = stack.getTagCompound();
            } else {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            NBTTagCompound nbtPages;
            if (nbt.hasKey("Pages")) {
                nbtPages = nbt.getCompoundTag("Pages");
            } else {
                nbtPages = new NBTTagCompound();
                nbt.setTag("Pages", nbtPages);
            }
            nbtPages.setInteger("CurPage", currentPage);
            NBTTagList pageList = new NBTTagList();
            for (NonNullList<ItemStack> stacks : pages) {
                NBTTagCompound page = new NBTTagCompound();
                saveAllItems(page, stacks, false);
                pageList.appendTag(page);
            }
            nbtPages.setTag("PageList", pageList);
        }
    }

    public NBTTagCompound saveAllItems(NBTTagCompound tag, NonNullList<ItemStack> list, boolean saveEmpty) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound);
                nbttagcompound.removeTag("Count");
                nbttagcompound.setInteger("Count", itemstack.getCount());
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        if (!nbttaglist.hasNoTags() || saveEmpty) {
            tag.setTag("Items", nbttaglist);
        }
        return tag;
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    public int getField(int id) {
        return id == 0 ? currentPage : 0;
    }

    public void setField(int id, int value) {
        if (id == 0) {
            if (value < 0) {
                value = 0;
            } else if (value >= getMaxPage()) {
                value = getMaxPage() - 1;
            }
            if (value >= pages.size()) {
                for (int i = 0; i < value - pages.size() + 1; i++) {
                    pages.add(NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY));
                }
            }
            currentPage = value;
        }
    }

    public int getFieldCount() {
        return 1;
    }

    public void clear() {
        for (NonNullList<ItemStack> stacks : pages) {
            stacks.clear();
        }
        pages.clear();
    }

    public InventoryKaia(ItemStack stack) {
        this.stack = stack;
        this.pages = Lists.newArrayList();
    }

    public String getName() {
        return "container.kaia";
    }

    @Override
    public int getInventoryStackLimit() {
        return stack.getTagCompound().getInteger(maxCountSlot.getValue());
    }

    public int getMaxPage() {
        return 100;
    }

    public boolean cancelStackLimit() {
        return true;
    }

    public NonNullList<ItemStack> getPage(int index) {
        if (index >= 0 && index < getMaxPage()) {
            while (index >= pages.size()) {
                pages.add(NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY));
            }
            return pages.get(index);
        }
        return NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    }
}