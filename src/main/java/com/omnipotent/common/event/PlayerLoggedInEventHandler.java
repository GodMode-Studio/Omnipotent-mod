package com.omnipotent.common.event;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.omnipotent.util.UtilityHelper.getKaiaCap;

public class PlayerLoggedInEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        getKaiaCap(player).ifPresent(cap -> cap.syncWithServer(player));
    }
}
