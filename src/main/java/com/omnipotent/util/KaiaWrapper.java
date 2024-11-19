package com.omnipotent.util;

import com.omnipotent.common.specialgui.IContainer;
import com.omnipotent.common.specialgui.InventoryKaia;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.constant.NbtNumberValues;
import com.omnipotent.constant.NbtStringValues;
import lombok.NonNull;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

import java.util.*;

import static com.omnipotent.Omnipotent.log;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.NbtListUtil.divisionUUIDAndName;

public final class KaiaWrapper {

    @Nonnull
    private final ItemStack kaia;
    @Nonnull
    private final NBTTagCompound nbt;

    public static OptionalMod<KaiaWrapper> wrapIfKaia(ItemStack kaia) {
        return OptionalMod.ofNullable(kaia.getItem() instanceof Kaia ? new KaiaWrapper(kaia) : null);
    }

    public KaiaWrapper(ItemStack stack) {
        this.kaia = stack;
        setTagCompound(stack);
        this.nbt = stack.getTagCompound();
        fixOwnerIfNecessary();
    }

    private void fixOwnerIfNecessary() {
        try {
            if (this.nbt.hasKey(ownerID))
                UUID.fromString(this.nbt.getString(ownerID));
        } catch (Exception e) {
            this.nbt.removeTag(ownerID);
            this.nbt.removeTag(ownerName);
        }
    }

    private static void setTagCompound(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
    }

    /**
     * Verifica se o jogador fornecido é o dono da Kaia.
     * <p>
     * A Kaia é considerada sem um dono registrado se o UUID registrado ou o nome registrado estiverem vazios
     * ou inválidos. Caso contrário, a Kaia tem um dono registrado e a verificação é realizada.
     * <p>
     * Se a Kaia não tiver dono ou se o nome e o UUID registrados na Kaia coincidirem com o nome e o UUID do
     * jogador fornecido, o método retornará {@code true}. Caso contrário, retornará {@code false}.
     *
     * @param player O jogador a ser verificado como possível dono da Kaia.
     * @return {@code true} se o jogador for o dono da Kaia ou se a Kaia não tiver dono registrado;
     * {@code false} caso contrário.
     */
    public boolean isOwner(@Nonnull EntityPlayer player) {
        return !hasOwner() || (this.nbt.getString(ownerName).equals(player.getName()) && this.nbt.getString(ownerID).equals(player.getUniqueID().toString()));
    }

    public boolean hasOwner() {
        return getOwnerUUID().isPresent() && getOwnerName().isPresent();
    }

    public void createOwnerIfNecessary(@NonNull Entity entityIn) {
        if (!nbt.hasKey(ownerName))
            nbt.setString(ownerName, entityIn.getName());
        if (!nbt.hasKey(ownerID))
            nbt.setString(ownerID, entityIn.getUniqueID().toString());
    }

    public OptionalMod<EntityPlayer> getOwner() {
        return getOwnerUUID().flatMap(uuid -> OptionalMod.ofNullable(FMLCommonHandler.instance()
                        .getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid)))
                .filter(player -> getOwnerName().map(name -> name.equals(player.getName())).orElse(false)).map(p -> p);
    }

    public OptionalMod<UUID> getOwnerUUID() {
        try {
            return OptionalMod.of(UUID.fromString(this.nbt.getString(ownerID)));
        } catch (IllegalArgumentException e) {
            log.error("UUID in kaia is invalid", e);
            return OptionalMod.empty();
        }
    }

    public OptionalMod<String> getOwnerName() {
        String name = this.nbt.getString(ownerName);
        return OptionalMod.ofNullable(name.isEmpty() ? null : name);
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
        inventory.openInventory(playerOwnerOfKaia);
        for (ItemStack dropStack : drops) {
            for (int currentPage = 0; currentPage < inventory.getMaxPage(); currentPage++) {
                NonNullList<ItemStack> currentPageItems = inventory.getPage(currentPage);
                for (int currentSlot = 0; currentSlot < currentPageItems.size(); currentSlot++) {
                    ItemStack stackInCurrentSlot = currentPageItems.get(currentSlot);
                    if (stackInCurrentSlot.isEmpty()) {
                        currentPageItems.set(currentSlot, dropStack);
                        dropStack = ItemStack.EMPTY;
                        break;
                    }
                    int countSlotFree = Math.min(inventory.getInventoryStackLimit() - stackInCurrentSlot.getCount(), dropStack.getCount());
                    if (countSlotFree > 0 && stackInCurrentSlot.isItemEqual(dropStack) && ItemStack.areItemStackTagsEqual(stackInCurrentSlot, dropStack)) {
                        stackInCurrentSlot.grow(countSlotFree);
                        dropStack.shrink(countSlotFree);
                        if (dropStack.isEmpty()) {
                            break;
                        }
                    }
                }
                if (dropStack.isEmpty()) break;
            }
        }
        inventory.closeInventory(playerOwnerOfKaia);
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

    public boolean addItemsInFirstEmptyPage(EntityPlayer player, NonNullList<ItemStack> drops) {
        InventoryKaia inventory = this.getInventory();
        inventory.openInventory(player);
        UtilityHelper.compactListItemStacks(drops);
        getFirstEmptyPage(inventory).ifPresent(page -> {
            for (ItemStack dropStack : drops) {
                for (int currentSlot = 0; currentSlot < page.size(); currentSlot++) {
                    ItemStack stackInCurrentSlot = page.get(currentSlot);
                    if (stackInCurrentSlot.isEmpty()) {
                        page.set(currentSlot, dropStack);
                        break;
                    }
                }
            }
        });
        inventory.closeInventory(player);
        return false;
    }

    private Optional<NonNullList<ItemStack>> getFirstEmptyPage(InventoryKaia inventory) {
        for (int currentPage = 0; currentPage < inventory.getMaxPage(); currentPage++) {
            NonNullList<ItemStack> currentPageItems = inventory.getPage(currentPage);
            if (pageAreEmpty(currentPageItems))
                return Optional.of(currentPageItems);
        }
        return Optional.empty();
    }

    private boolean pageAreEmpty(NonNullList<ItemStack> currentPageItems) {
        return currentPageItems.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isSameItem(ItemStack i) {
        return i == this.kaia;
    }

    public void banGui(String guiClass) {
        if (!listContainsElement(bannedGuis, guiClass))
            addInList(bannedGuis, new NBTTagString(guiClass));
    }

    public boolean guiIsBanned(@Nonnull GuiScreen gui) {
        return listContainsElement(bannedGuis, gui.getClass().getCanonicalName());
    }

    public void banItem(@Nonnull Item i) {
        String string = i.delegate.name().toString();
        if (string.startsWith("minecraft") || string.startsWith("omnipotent"))
            return;
        if (!listContainsElement(bannedItems, string)) {
            addInList(bannedItems, new NBTTagString(string));
        }
    }

    public boolean itemIsBanned(@Nonnull Item item) {
        return listContainsElement(bannedItems, item.delegate.name().toString());
    }
}
