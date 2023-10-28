package com.omnipotent.client.event;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.ChangedValuePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerNameFormat {
    private long lastNetworkCallTime = 0;
    private int curColor = 0;
    private int tick = 0;
    private final TextFormatting[] colors = {TextFormatting.YELLOW, TextFormatting.GOLD, TextFormatting.AQUA, TextFormatting.BLUE, TextFormatting.RED, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE};

    @SubscribeEvent
    public void getName(PlayerEvent.NameFormat event) {
        if (++tick >= 3) {
            tick = 0;
            if (--curColor < 0) {
                curColor = colors.length - 1;
            }
        }
        EntityPlayer player = event.getEntityPlayer();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNetworkCallTime >= 500) {
            lastNetworkCallTime = currentTime;
            NetworkRegister.ACESS.sendToServer(new ChangedValuePacket(player.getUniqueID().toString(), true));
        }
        if (player.renderSpecialName && player.hasCustomName()) {
            String str = player.getCustomNameTag();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            event.setDisplayname(sb.toString() + TextFormatting.GRAY);
        }
    }
}
