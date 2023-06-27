package com.omnipotent.server.event;

import com.omnipotent.server.network.KillPacket;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.omnipotent.util.KaiaConstantsNbt.rangeAttack;
import static com.omnipotent.util.KaiaUtil.*;

public class KaiaEvent {
    @SubscribeEvent
    public void playerAttack(PlayerInteractEvent.LeftClickEmpty event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack kaia = getKaiaInMainHand(player);
        if (kaia!=null) {
            if (kaia.getTagCompound().getInteger(rangeAttack) > 5) {
                NetworkRegister.ACESS.sendToServer(new KillPacket());
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void killEvent(LivingDeathEvent event) {
        if (isPlayer(event.getEntityLiving())) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (hasInInventoryKaia(player)) {
                player.setHealth(Integer.MAX_VALUE);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void attackEvent(LivingAttackEvent event) {
        if (isPlayer(event.getEntity())) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (hasInInventoryKaia(player)) {
                player.setHealth(Integer.MAX_VALUE);
                event.setCanceled(true);
                NBTTagCompound tagCompoundOfKaia = (getKaiaInMainHand(player) == null ? getKaiaInInventory(player) : getKaiaInMainHand(player)).getTagCompound();
                if (tagCompoundOfKaia.getBoolean(KaiaConstantsNbt.counterAttack)) {
                    if (event.getSource().getTrueSource() != null && !isPlayer(event.getSource().getTrueSource()) || (isPlayer(event.getSource().getTrueSource()) && !hasInInventoryKaia(event.getSource().getTrueSource()))) {
                        KaiaUtil.kill(event.getSource().getTrueSource(), player, tagCompoundOfKaia.getBoolean(KaiaConstantsNbt.killAllEntities));
                    }
                }
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void damageHurt(LivingHurtEvent event) {
        if (isPlayer(event.getEntity()) && hasInInventoryKaia((EntityPlayer) event.getEntity())) {
            event.setCanceled(true);
        } else if (event.getSource().getTrueSource() != null && isPlayer(event.getSource().getTrueSource()) && withKaiaMainHand((EntityPlayer) event.getSource().getTrueSource())) {
            event.setCanceled(false);
            EntityPlayer source = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack kaia = getKaiaInMainHand(source) == null ? getKaiaInInventory(source) : getKaiaInMainHand(source);
            KaiaUtil.kill(event.getEntity(), source, kaia.getTagCompound().getBoolean(KaiaConstantsNbt.killAllEntities));
        }
    }

    @SubscribeEvent
    public void playerClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayer entityPlayer = event.getEntityPlayer();
        World world = entityPlayer.world;
        if (withKaiaMainHand(entityPlayer) && !world.isRemote && entityPlayer instanceof EntityPlayerMP && !entityPlayer.capabilities.isCreativeMode) {
            decideBreakBlock((EntityPlayerMP) entityPlayer, event.getPos());
        }
    }
}
