package com.omnipotent.server.tool;

import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import com.omnipotent.server.entity.KaiaEntity;
import com.omnipotent.server.specialgui.IContainer;
import com.omnipotent.server.specialgui.InventoryKaia;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.*;
import static com.omnipotent.util.UtilityHelper.isPlayer;

@Optional.Interface(modid = RedstoneFluxProps.MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem", striprefs = true)
public class Kaia extends ItemPickaxe implements IContainer, IEnergyContainerItem {
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
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!isPlayer(entityIn) || worldIn.isRemote)
            return;
        EntityPlayer player = (EntityPlayer) entityIn;
        nbtManager(stack, entityIn);
        if (stack.getTagCompound().getBoolean(chargeItemsInInventory)) {
            chargeEnergyItems(player);
            if (Loader.isModLoaded(RedstoneFluxProps.MOD_ID))
                chargeRf(player);
        }
        if (!isOwnerOfKaia(stack, player)) {
            player.world.spawnEntity(new EntityItem(worldIn, player.posX, player.posY, player.posZ + 5, stack));
            player.inventory.deleteStack(stack);
        }
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    public void chargeRf(EntityPlayer player) {
        List<ItemStack> itemsReceiverEnergy = player.inventory.mainInventory.stream().filter(item -> item.getItem() instanceof
                IEnergyContainerItem).collect(Collectors.toList());
        for (ItemStack item : itemsReceiverEnergy) {
            IEnergyContainerItem itemToReceiverEnergy = (IEnergyContainerItem) item.getItem();
            int energySended = itemToReceiverEnergy.receiveEnergy(item, itemToReceiverEnergy.getMaxEnergyStored(item)
                    - itemToReceiverEnergy.getEnergyStored(item), false);
            if (energySended < 0)
                itemToReceiverEnergy.receiveEnergy(item, itemToReceiverEnergy.getMaxEnergyStored(item), false);
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

    private static void nbtManager(ItemStack stack, Entity entityIn) {
        KaiaUtil.createTagCompoundStatusIfNecessary(stack);
        KaiaUtil.createOwnerIfNecessary(stack, entityIn);
        NBTTagCompound tagCompoundOfKaia = stack.getTagCompound();
        ArrayList<String> nbtBoolean = new ArrayList<>();
        nbtBoolean.addAll(Arrays.asList(noBreakTileEntity, interactLiquid, attackYourWolf, counterAttack, killAllEntities, killFriendEntities, autoBackPack, autoBackPackEntities, playersCantRespawn, playersWhoShouldNotKilledInCounterAttack, playerDontKillInDirectAttack, chargeItemsInInventory, summonLightBoltsInKill, banEntitiesAttacked, autoKill, showInfo));
        NBTTagCompound status = tagCompoundOfKaia;
        for (String nbtName : nbtBoolean) {
            if (!tagCompoundOfKaia.hasKey(nbtName)) {
                if (!nbtName.equals(killFriendEntities))
                    status.setBoolean(nbtName, false);
                else
                    status.setBoolean(nbtName, true);
            }
        }
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, blockBreakArea, 1);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, rangeAttack, 1);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, playerDontKillInDirectAttack, 0);
        checkAndSetIntegerNbtTag(tagCompoundOfKaia, rangeAutoKill, 10);
        if (!tagCompoundOfKaia.hasKey(maxCountSlot) || tagCompoundOfKaia.getInteger(maxCountSlot) < 1)
            status.setInteger(maxCountSlot, 200_000_000);
        if (!tagCompoundOfKaia.hasKey(playersDontKill))
            status.setTag(playersDontKill, new NBTTagList());
        if (!tagCompoundOfKaia.hasKey(entitiesCantKill))
            status.setTag(entitiesCantKill, new NBTTagList());
        if (!tagCompoundOfKaia.hasKey(effectsBlockeds))
            status.setTag(effectsBlockeds, new NBTTagList());
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
        boolean cancelAttack = !checkIfKaiaCanKill(entityAttacked, player, true, false);
        if (cancelAttack)
            return cancelAttack;
        if (!player.world.isRemote)
            killChoice(entityAttacked, player, getKaiaInMainHand(player).getTagCompound().getBoolean(killAllEntities));
        return cancelAttack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!playerIn.world.isRemote) {
            playerIn.world.spawnEntity(new EntityXPOrb(playerIn.world, playerIn.posX, playerIn.posY, playerIn.posZ, Integer.MAX_VALUE / 10000));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
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