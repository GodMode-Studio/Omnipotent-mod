package com.omnipotent.util.player;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor
public class PlayerData {

    private final EntityPlayerMP player;
    private String challenge;
    private static final ConcurrentHashMap<UUID, PlayerData> players = new ConcurrentHashMap<>();

    public PlayerData() {
        player = null;
    }

    public static PlayerData addPlayer(EntityPlayerMP playerTarget) {
        PlayerData player = new PlayerData(playerTarget);
        players.put(playerTarget.getUniqueID(), player);
        return player;
    }

    public void addChallenge(String s) {
        setChallenge(s);
    }

    public static PlayerData getPlayerData(EntityPlayerMP player) {
        return players.get(player.getUniqueID());
    }

    @SubscribeEvent
    public void destroyPlayerData(PlayerEvent.PlayerLoggedOutEvent event){
        players.remove(event.player.getUniqueID());
    }
}
