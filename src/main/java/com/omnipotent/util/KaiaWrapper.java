package com.omnipotent.util;

import com.omnipotent.common.specialgui.IContainer;
import com.omnipotent.common.specialgui.InventoryKaia;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.constant.NbtNumberValues;
import com.omnipotent.constant.NbtStringValues;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

import java.util.*;

import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.NbtListUtil.divisionUUIDAndName;

public final class KaiaWrapper {

    private final ItemStack kaia;
    private final NBTTagCompound nbt;

    public static Optional<KaiaWrapper> wrapIfKaia(ItemStack kaia) {
        return Optional.ofNullable(kaia.getItem() instanceof Kaia ? new KaiaWrapper(kaia) : null);
    }

    public KaiaWrapper(ItemStack stack) {
        this.kaia = stack;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        this.nbt = stack.getTagCompound();
    }

    public boolean isOwner(@Nonnull EntityPlayer player) {
        return this.nbt.getString(ownerName).equals(player.getName()) && this.nbt.getString(ownerID).equals(player.getUniqueID().toString());
    }

    public void createOwnerIfNecessary(@Nonnull Entity entityIn) {
        if (!nbt.hasKey(ownerName))
            nbt.setString(ownerName, entityIn.getName());
        if (!nbt.hasKey(ownerID))
            nbt.setString(ownerID, entityIn.getUniqueID().toString());
    }

    public Optional<EntityPlayer> getOwner() {
        return Optional.of(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(this.nbt
                .getString(ownerID))));
    }

    public UUID getIdentify() {
        return this.nbt.getUniqueId("identify");
    }

    public boolean getBoolean(NbtBooleanValues bool) {
        return this.nbt.getBoolean(bool.getValue());
    }

    public int getInteger(NbtNumberValues number) {
        return this.nbt.getInteger(number.getValue());
    }

    public int getEnchantmentLevel(Enchantment enchantment) {
        return EnchantmentHelper.getEnchantmentLevel(enchantment, this.kaia);
    }

    public String getString(NbtStringValues key) {
        return this.nbt.getString(key.getValue());
    }

    public List<String> getStringList(String effectsBlockeds) {
        ArrayList<String> nbts = new ArrayList<>();
        this.nbt.getTagList(effectsBlockeds, 8).forEach(nbtElement -> nbts.add(((NBTTagString) nbtElement).getString()));
        return nbts;
    }

    public List<NBTBase> playersNotToKill() {
        ArrayList<NBTBase> nbts = new ArrayList<>();
        this.nbt.getTagList(playersDontKill, 8).forEach(nbts::add);
        return nbts;
    }

    public boolean playerIsProtected(String uuid) {
        for (NBTBase tagString : this.nbt.getTagList(playersDontKill, 8)) {
            if (((NBTTagString) tagString).getString().split(divisionUUIDAndName)[0].contains(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return EnchantmentHelper.getEnchantments(this.kaia);
    }

    public void setString(String key, String text) {
        this.nbt.setString(key, text);
    }

    public void setBoolean(String key, boolean booleanValue) {
        this.nbt.setBoolean(key, booleanValue);
    }

    public void setInteger(String key, int intValue) {
        this.nbt.setInteger(key, intValue);
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        EnchantmentHelper.setEnchantments(enchantments, this.kaia);
    }

    public void addInList(String key, NBTTagString nbtTagString) {
        this.nbt.getTagList(key, 8).appendTag(nbtTagString);
    }

    public void removeInList(String listKey, String text) {
        NBTTagList tagList = this.nbt.getTagList(listKey, 8);
        for (int i = 0; i < tagList.tagCount(); i++) {
            if (((NBTTagString) tagList.get(i)).getString().equals(text)) {
                tagList.removeTag(i);
                return;
            }
        }
    }

    public List<NBTBase> getList(String effectsBlockeds, int i) {
        ArrayList<NBTBase> nbts = new ArrayList<>();
        this.nbt.getTagList(effectsBlockeds, i).forEach(nbts::add);
        return nbts;
    }

    public void addItemStacksInInventory(EntityPlayer playerOwnerOfKaia, NonNullList<ItemStack> drops) {
        InventoryKaia inventory = this.getInventory();
        for (ItemStack dropStack : drops) {
            boolean breakMainLoop = false;
            for (int currentPage = 0; currentPage < inventory.getMaxPage(); currentPage++) {
                if (breakMainLoop) {
                    break;
                }
                inventory.openInventory(playerOwnerOfKaia);
                NonNullList<ItemStack> currentPageItems = inventory.getPage(currentPage);
                for (int currentSlot = 0; currentSlot < currentPageItems.size(); currentSlot++) {
                    if (breakMainLoop) {
                        break;
                    }
                    ItemStack stackInCurrentSlot = currentPageItems.get(currentSlot);
                    if (stackInCurrentSlot == null || stackInCurrentSlot.isEmpty()) {
                        currentPageItems.set(currentSlot, dropStack);
                        breakMainLoop = true;
                        break;
                    } else {
                        int maxCountSlot = inventory.cancelStackLimit() ? inventory.getInventoryStackLimit() : Math.min(inventory.getInventoryStackLimit(), dropStack.getMaxStackSize());
                        int countSlotFree = Math.min(maxCountSlot - stackInCurrentSlot.getCount(), dropStack.getCount());
                        if (countSlotFree > 0 && stackInCurrentSlot.isItemEqual(dropStack) && ItemStack.areItemStackTagsEqual(stackInCurrentSlot, dropStack)) {
                            stackInCurrentSlot.grow(countSlotFree);
                            dropStack.shrink(countSlotFree);
                            if (dropStack.isEmpty()) {
                                breakMainLoop = true;
                                break;
                            }
                        }
                    }
                }
                inventory.closeInventory(playerOwnerOfKaia);
            }
        }
    }

    public InventoryKaia getInventory() {
        return ((IContainer) this.kaia.getItem()).getInventory(this.kaia);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        this.kaia.writeToNBT(nbt);
    }

    public boolean listContainsElement(String keyList, String string) {
        for (NBTBase ele : this.nbt.getTagList(keyList, 8)) {
            if (((NBTTagString) ele).getString().equals(string)) {
                return true;
            }
        }
        return false;
    }
}
