package com.omnipotent.common.event;

import com.omnipotent.common.capability.BlockModeProvider;
import com.omnipotent.common.network.KillPacket;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.util.KaiaUtil.*;

public class KaiaEvent {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void playerAttack(PlayerInteractEvent.LeftClickEmpty event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack kaia = getKaiaInMainHand(player);
        if (kaia != null)
            NetworkRegister.ACESS.sendToServer(new KillPacket());
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

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void blockCreativeAndnotAllowEditMode(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (world.isRemote)
            return;
        List<EntityPlayer> players = world.playerEntities.stream().filter(player -> hasInInventoryKaia(player)).collect(Collectors.toList());
        for (EntityPlayer player : players) {
            boolean playerInNoEditModeAndBlockMode = !player.isAllowEdit() && player.getCapability(BlockModeProvider.blockMode, null).getBlockNoEditMode();
            boolean playerInCreativeAndBlockMode = player.isCreative() && player.getCapability(BlockModeProvider.blockMode, null).getBlockCreativeMode();
            if (playerInNoEditModeAndBlockMode || playerInCreativeAndBlockMode)
                player.setGameType(GameType.SURVIVAL);
        }
    }
}
