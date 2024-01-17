package com.omnipotent.common.tool;

import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import com.omnipotent.common.entity.KaiaEntity;
import com.omnipotent.common.specialgui.IContainer;
import com.omnipotent.common.specialgui.InventoryKaia;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaReceiver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.common.event.EventInitItems.itemsInit;
import static com.omnipotent.constant.NbtBooleanValues.*;
import static com.omnipotent.constant.NbtNumberValues.*;
import static com.omnipotent.constant.NbtStringValues.customPlayerName;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.*;
import static com.omnipotent.util.UtilityHelper.isPlayer;


@Optional.InterfaceList({@Optional.Interface(modid = RedstoneFluxProps.MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem", striprefs = true), @Optional.Interface(modid = "botania", iface = "vazkii.botania.api.mana.IManaReceiver", striprefs = true)})
public class Kaia extends ItemPickaxe implements IContainer, IEnergyContainerItem {

    private static final String botaniaModid = "botania";

    public Kaia() {
        super(EnumHelper.addToolMaterial("kaia", Integer.MAX_VALUE, Integer.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Integer.MAX_VALUE));
        setUnlocalizedName("kaia");
        setRegistryName("kaia");
        setCreativeTab(omnipotentTab);
        texturesItemsInit.add(this);
        itemsInit.add(this);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("donoverdadeiro");
        tooltip.add("dono");
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!isPlayer(entityIn) || worldIn.isRemote)
            return;
        EntityPlayer player = (EntityPlayer) entityIn;
        nbtManager(stack, entityIn);
        if (!isOwnerOfKaia(stack, player)) {
            player.world.spawnEntity(new EntityItem(worldIn, player.posX, player.posY, player.posZ + 5, stack));
            player.inventory.deleteStack(stack);
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound.getBoolean(NbtBooleanValues.chargeEnergyItemsInInventory.getValue())) {
            chargeEnergyItems(player);
            if (Loader.isModLoaded(RedstoneFluxProps.MOD_ID))
                chargeRfEnergyInInventory(player);
        }
        if (tagCompound.getBoolean(chargeEnergyInBlocksAround.getValue())) {
            int integer = tagCompound.getInteger(chargeEnergyInBlocksAround.getValue());
            if (integer > 1)
                chargeEnergyInBlocksAround(player, integer);
//            if (Loader.isModLoaded(RedstoneFluxProps.MOD_ID))
//                chargeRfEnergyInBlocksAround(player, stack.getTagCompound().getInteger(chargeEnergyInBlocksAround));
        }
        if (tagCompound.getBoolean(chargeManaItemsInInventory.getValue())) {
            if (Loader.isModLoaded(botaniaModid))
                chargeManaInInventory(player);
        }
        if (tagCompound.getBoolean(chargeManaInBlocksAround.getValue())) {
            int integer = tagCompound.getInteger(chargeManaInBlocksAround.getValue());
            if (integer > 1)
                if (Loader.isModLoaded(botaniaModid))
                    chargeManaInAroundBlocks(player, integer);
        }

        if (!player.getActivePotionEffects().isEmpty()) {
            Iterator<Potion> iterator = Potion.REGISTRY.iterator();
            while (iterator.hasNext()) {
                Potion potion = iterator.next();
                if (effectIsBlockedByKaia(player, potion))
                    player.removePotionEffect(potion);
            }
        }
        if (tagCompound.getBoolean(activeCustomPlayerName.getValue())) {
            String value = customPlayerName.getValue();
            if (!player.getCustomNameTag().equals(value))
                player.setCustomNameTag(tagCompound.getString(value));
            player.setAlwaysRenderNameTag(true);
        } else
            player.setCustomNameTag("");
        KaiaUtil.killInAreaConstantly(player);
    }

    private void chargeEnergyInBlocksAround(EntityPlayer player, int range) {
        BlockPos position = player.getPosition();
        World world = player.world;
        int xNegative = position.getX() - range;
        int xPositive = position.getX() + range;
        int yNegative = position.getY() - range;
        int yPositive = position.getY() + range;
        int zNegative = position.getZ() - range;
        int zPositive = position.getZ() + range;
        List<BlockPos> list = new ArrayList<>();
        BlockPos.getAllInBox(xNegative, yNegative, zNegative, xPositive, yPositive, zPositive).forEach(i -> list.add(i));
        for (BlockPos block : list) {
            TileEntity tileEntity = world.getTileEntity(block);
            if (tileEntity != null) {
                if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, null)) {
                    IEnergyStorage capability = tileEntity.getCapability(CapabilityEnergy.ENERGY, null);
                    if (capability.getEnergyStored() < capability.getMaxEnergyStored())
                        capability.receiveEnergy(capability.getMaxEnergyStored() - capability.getEnergyStored(), false);
                }
            }
        }
    }

    private void chargeEnergyItems(EntityPlayer player) {
        List<ItemStack> itemsReceiverEnergy = player.inventory.mainInventory.stream().filter(item -> item.hasCapability
                (CapabilityEnergy.ENERGY, null)).collect(Collectors.toList());
        for (ItemStack item : itemsReceiverEnergy) {
            IEnergyStorage cap = item.getCapability(CapabilityEnergy.ENERGY, null);
            if (cap != null && cap.canReceive())
                cap.receiveEnergy(cap.getMaxEnergyStored() - cap.getEnergyStored(), false);
        }
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    public void chargeRfEnergyInInventory(EntityPlayer player) {
        List<ItemStack> itemsReceiverEnergy = player.inventory.mainInventory.stream().filter(item -> item.getItem() instanceof
                IEnergyContainerItem).collect(Collectors.toList());
        for (ItemStack item : itemsReceiverEnergy) {
            IEnergyContainerItem itemEnergy = (IEnergyContainerItem) item.getItem();
            int energySended = itemEnergy.receiveEnergy(item, itemEnergy.getMaxEnergyStored(item)
                    - itemEnergy.getEnergyStored(item), false);
            if (energySended <= 0)
                itemEnergy.receiveEnergy(item, itemEnergy.getMaxEnergyStored(item), false);
        }
    }

//    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
//    private void chargeRfEnergyInBlocksAround(EntityPlayer player, int range) {
//        BlockPos position = player.getPosition();
//        World world = player.world;
//        int xNegative = position.getX() - range;
//        int xPositive = position.getX() + range;
//        int yNegative = position.getY() - range;
//        int yPositive = position.getY() + range;
//        int zNegative = position.getZ() - range;
//        int zPositive = position.getZ() + range;
//        List<BlockPos> list = new ArrayList<>();
//        BlockPos.getAllInBox(xNegative, yNegative, zNegative, xPositive, yPositive, zPositive).forEach(i -> list.add(i));
//        for (BlockPos block : list) {
//            TileEntity tileEntity = world.getTileEntity(block);
//            if (tileEntity != null) {
//                if (tileEntity instanceof cofh.redstoneflux.api.IEnergyStorage) {
//                    cofh.redstoneflux.api.IEnergyStorage tileRf = (cofh.redstoneflux.api.IEnergyStorage) tileEntity;
//                    if (tileRf.getEnergyStored() < tileRf.getMaxEnergyStored())
//                        tileRf.receiveEnergy(tileRf.getMaxEnergyStored() - tileRf.getEnergyStored(), false);
//                }
//            }
//        }
//    }

    @Optional.Method(modid = botaniaModid)
    private void chargeManaInInventory(EntityPlayer player) {
        List<ItemStack> itemsReceiverMana = player.inventory.mainInventory.stream().filter(item -> item.getItem() instanceof IManaItem).collect(Collectors.toList());
        for (ItemStack item : itemsReceiverMana) {
            IManaItem itemReceiverMana = (IManaItem) item.getItem();
            itemReceiverMana.addMana(item, itemReceiverMana.getMaxMana(item) - itemReceiverMana.getMana(item));
        }
    }

    @Optional.Method(modid = botaniaModid)
    private void chargeManaInAroundBlocks(EntityPlayer player, int range) {
        BlockPos position = player.getPosition();
        World world = player.world;
        int xNegative = position.getX() - range;
        int xPositive = position.getX() + range;
        int yNegative = position.getY() - range;
        int yPositive = position.getY() + range;
        int zNegative = position.getZ() - range;
        int zPositive = position.getZ() + range;
        List<BlockPos> list = new ArrayList<>();
        BlockPos.getAllInBox(xNegative, yNegative, zNegative, xPositive, yPositive, zPositive).forEach(i -> list.add(i));
        for (BlockPos block : list) {
            TileEntity tileEntity = world.getTileEntity(block);
            if (tileEntity != null && tileEntity instanceof IManaReceiver) {
                IManaReceiver manaReceiver = (IManaReceiver) tileEntity;
                if (!manaReceiver.isFull())
                    manaReceiver.recieveMana(Integer.MAX_VALUE);
            }
        }
    }

    private static void nbtManager(ItemStack stack, Entity entityIn) {
        KaiaUtil.createTagCompoundStatusIfNecessary(stack);
        KaiaUtil.createOwnerIfNecessary(stack, entityIn);
        NBTTagCompound tagCompoundOfKaia = stack.getTagCompound();
        NBTTagCompound status = tagCompoundOfKaia;
        for (String nbtName : NbtBooleanValues.valuesNbt) {
            if (!tagCompoundOfKaia.hasKey(nbtName)) {
                if (!nbtName.equals(killFriendEntities.getValue()))
                    status.setBoolean(nbtName, false);
                else
                    status.setBoolean(nbtName, true);
            }
        }
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, blockBreakArea.getValue(), 1);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, rangeAttack.getValue(), 1);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, playerDontKillInDirectAttack.getValue(), 0);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, rangeAutoKill.getValue(), 0);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, chargeManaInBlocksAround.getValue(), 0);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, chargeEnergyInBlocksAround.getValue(), 0);
        if (!tagCompoundOfKaia.hasKey(listOfCoordenatesKaia))
            status.setIntArray(listOfCoordenatesKaia, new int[]{0, 300, 0});
        if (!tagCompoundOfKaia.hasKey(maxCountSlot.getValue()) || tagCompoundOfKaia.getInteger(maxCountSlot.getValue()) < 1)
            status.setInteger(maxCountSlot.getValue(), 200_000_000);
        if (!tagCompoundOfKaia.hasKey(playersDontKill))
            status.setTag(playersDontKill, new NBTTagList());
        if (!tagCompoundOfKaia.hasKey(entitiesCantKill))
            status.setTag(entitiesCantKill, new NBTTagList());
        if (!tagCompoundOfKaia.hasKey(effectsBlockeds))
            status.setTag(effectsBlockeds, new NBTTagList());
        String valueCustomPlayerName = customPlayerName.getValue();
        if (!tagCompoundOfKaia.hasKey(valueCustomPlayerName))
            status.setTag(valueCustomPlayerName, new NBTTagString());
        int integer = tagCompoundOfKaia.getInteger(optionOfColor.getValue());
        if (!tagCompoundOfKaia.hasKey(optionOfColor.getValue()) || (integer < 0 || integer > 3))
            status.setInteger(optionOfColor.getValue(), 0);
    }

    private static void checkAndSetIntegerNbtTag(NBTTagCompound tagCompoundOfKaia, String nbtTag, int nbtCount) {
        if (!tagCompoundOfKaia.hasKey(nbtTag) || tagCompoundOfKaia.getInteger(nbtTag) < nbtCount) {
            NBTTagCompound status = tagCompoundOfKaia;
            status.setInteger(nbtTag, nbtCount);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        World world = entityItem.getEntityWorld();
        if (!world.isRemote) {
            if (entityItem.getPosition().getY() < -5) {
                entityItem.setPosition(entityItem.posX, 150, entityItem.posZ);
                UtilityHelper.sendMessageToAllPlayers(TextFormatting.DARK_RED + I18n.format("kaia.message.void"));
            }
        }
        return super.onEntityItemUpdate(entityItem);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entityAttacked) {
        if (!player.world.isRemote) {
            boolean cancelAttack = !checkIfKaiaCanKill(entityAttacked, player, true, false);
            if (cancelAttack)
                return cancelAttack;
            if (!player.world.isRemote)
                killChoice(entityAttacked, player, getKaiaInMainHand(player).get().getTagCompound().getBoolean(killAllEntities.getValue()));
            return cancelAttack;
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        if (!player.world.isRemote) {
            if (player.isSneaking()) {
                List<Entity> entitiesInArea = getEntitiesInArea(worldIn, player.getPosition(), 100);
                int entitiesKilled = 0;
                NBTTagCompound tagCompound = player.getHeldItem(handIn).getTagCompound();
                filterEntities(entitiesInArea, tagCompound);
                for (Entity entity : entitiesInArea) {
                    boolean mobKilled = killChoice(entity, player, tagCompound.getBoolean(killAllEntities.getValue()));
                    if (mobKilled) entitiesKilled++;
                }
                UtilityHelper.sendMessageToPlayer(TextFormatting.DARK_RED + "" + entitiesKilled + " Entities Killed", player);
            } else
                player.world.spawnEntity(new EntityXPOrb(player.world, player.posX, player.posY, player.posZ, Integer.MAX_VALUE / 10000));
        }
        return super.onItemRightClick(worldIn, player, handIn);
    }

    @Override
    public boolean hasCustomEntity(@Nonnull ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(@Nonnull World world, Entity location, @Nonnull ItemStack itemstack) {
        return new KaiaEntity(world, location.posX, location.posY, location.posZ, itemstack);
    }

    @Override
    public boolean hasInventory(ItemStack stack) {
        return true;
    }

    @Override
    public InventoryKaia getInventory(ItemStack stack) {
        return new InventoryKaia(stack);
    }

    @Override
    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    public int receiveEnergy(ItemStack itemStack, int i, boolean b) {
        return Integer.MAX_VALUE;
    }

    @Override
    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    public int extractEnergy(ItemStack itemStack, int i, boolean b) {
        return Integer.MAX_VALUE;
    }

    @Override
    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    public int getEnergyStored(ItemStack itemStack) {
        return Integer.MAX_VALUE;
    }

    @Override
    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    public int getMaxEnergyStored(ItemStack itemStack) {
        return Integer.MAX_VALUE;
    }
}