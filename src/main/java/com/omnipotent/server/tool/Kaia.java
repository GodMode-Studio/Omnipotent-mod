package com.omnipotent.server.tool;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.*;
import static com.omnipotent.util.UtilityHelper.isPlayer;

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
        if (!isPlayer(entityIn) || worldIn.isRemote)
            return;
        EntityPlayer player = (EntityPlayer) entityIn;
        KaiaUtil.createTagCompoundStatusIfNecessary(stack);
        KaiaUtil.createOwnerIfNecessary(stack, entityIn);
        NBTTagCompound tagCompoundOfKaia = stack.getTagCompound();
        ArrayList<String> nbtBoolean = new ArrayList<>();
        nbtBoolean.addAll(Arrays.asList(noBreakTileEntity, interactLiquid, attackYourWolf, counterAttack, killAllEntities, killFriendEntities, autoBackPack, autoBackPackEntities, playersCantRespawn));
        for (String nbtName : nbtBoolean) {
            if (!tagCompoundOfKaia.hasKey(nbtName)) {
                NBTTagCompound status = tagCompoundOfKaia;
                if (!nbtName.equals(killFriendEntities))
                    status.setBoolean(nbtName, true);
                else
                    status.setBoolean(nbtName, false);
            }
        }
        if (!isOwnerOfKaia(stack, player)) {
            player.world.spawnEntity(new EntityItem(worldIn, player.posX, player.posY, player.posZ + 5, stack));
            player.inventory.deleteStack(stack);
        }
        if (!tagCompoundOfKaia.hasKey(blockBreakArea) || tagCompoundOfKaia.getInteger(blockBreakArea) < 1) {
            NBTTagCompound status = tagCompoundOfKaia;
            status.setInteger(blockBreakArea, 1);
        }
        if (!tagCompoundOfKaia.hasKey(rangeAttack) || tagCompoundOfKaia.getInteger(rangeAttack) < 1) {
            NBTTagCompound status = tagCompoundOfKaia;
            status.setInteger(rangeAttack, 1);
        }
        if (!tagCompoundOfKaia.hasKey(maxCountSlot)) {
            NBTTagCompound status = tagCompoundOfKaia;
            status.setInteger(maxCountSlot, 200_000_000);
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