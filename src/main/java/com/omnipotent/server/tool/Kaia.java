package com.omnipotent.server.tool;

import com.omnipotent.server.entity.KaiaEntity;
import com.omnipotent.server.specialgui.IContainer;
import com.omnipotent.server.specialgui.InventoryKaia;
import com.omnipotent.util.KaiaConstantsNbt;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.checkIfKaiaCanKillPlayerOwnedWolf;
import static com.omnipotent.util.KaiaUtil.getKaiaInMainHand;

public class Kaia extends ItemPickaxe implements IContainer {
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
        if (!(entityIn instanceof EntityPlayer) || worldIn.isRemote) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entityIn;
        KaiaUtil.createTagCompoundStatusIfNecessary(stack);
        KaiaUtil.createOwnerIfNecessary(stack, entityIn);
        String ownerName = stack.getTagCompound().getString(KaiaConstantsNbt.ownerName);
        String ownerID = stack.getTagCompound().getString(KaiaConstantsNbt.ownerID);
        Random x = new Random();
        if (!stack.getTagCompound().hasKey(blockBreakArea) || stack.getTagCompound().getInteger(blockBreakArea) < 1) {
            NBTTagCompound status = stack.getTagCompound();
            status.setInteger(blockBreakArea, 1);
        }
        if (!stack.getTagCompound().hasKey(noBreakTileEntity)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(noBreakTileEntity, false);
        }
        if (!stack.getTagCompound().hasKey(interactLiquid)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(interactLiquid, false);
        }
        if (!stack.getTagCompound().hasKey(attackYourWolf)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(attackYourWolf, false);
        }
        if (!stack.getTagCompound().hasKey(counterAttack)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(counterAttack, false);
        }
        if (!(stack.getTagCompound().hasKey(idLigation))) {
            stack.getTagCompound().setLong(idLigation, x.nextLong());
        }
        if (!stack.getTagCompound().hasKey(killAllEntities)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(killAllEntities, false);
        }
        if (!stack.getTagCompound().hasKey(rangeAttack) || stack.getTagCompound().getInteger(rangeAttack) < 1) {
            NBTTagCompound status = stack.getTagCompound();
            status.setInteger(rangeAttack, 1);
        }
        if (!stack.getTagCompound().hasKey(killFriendEntities)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(killFriendEntities, true);
        }
        if (!stack.getTagCompound().hasKey(maxCountSlot)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setInteger(maxCountSlot, 200_000_000);
        }
        if (!stack.getTagCompound().hasKey(autoBackPack)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(autoBackPack, false);
        }
        if (!stack.getTagCompound().hasKey(autoBackPackEntities)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(autoBackPackEntities, false);
        }
        if (!player.getUniqueID().toString().equals(ownerID) || !player.getName().equals(ownerName)) {
            player.world.spawnEntity(new EntityItem(worldIn, player.posX, player.posY, player.posZ + 5, stack));
            player.inventory.deleteStack(stack);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        ItemStack kaiaItem = entityItem.getItem();
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
        if (!player.world.isRemote && !KaiaUtil.hasInInventoryKaia(entityAttacked)) {
            boolean killAll = getKaiaInMainHand(player).getTagCompound().getBoolean(killAllEntities);
            KaiaUtil.kill(entityAttacked, player, killAll);
            return checkIfKaiaCanKillPlayerOwnedWolf(entityAttacked, player);
        }
        if (player.world.isRemote && !KaiaUtil.hasInInventoryKaia(entityAttacked)) {
            return checkIfKaiaCanKillPlayerOwnedWolf(entityAttacked, player);
        }
        return false;
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
}