package com.omnipotent.server.event;

import com.omnipotent.server.network.KillPacket;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
        if (kaia != null) {
            if (kaia.getTagCompound().getInteger(rangeAttack) > 5) {
                NetworkRegister.ACESS.sendToServer(new KillPacket());
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void killEvent(LivingDeathEvent event) {
        if (UtilityHelper.isPlayer(event.getEntityLiving())) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (hasInInventoryKaia(player)) {
                player.setHealth(Integer.MAX_VALUE);
                event.setCanceled(true);
            }
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
