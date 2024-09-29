package com.omnipotent.common.network;

import com.omnipotent.common.capability.kaiacap.KaiaBrandItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.omnipotent.util.UtilityHelper.getKaiaCap;

public class PlayerSyncPacket implements IMessage {

    private KaiaBrandItems data;

    public PlayerSyncPacket() {
    }

    public PlayerSyncPacket(KaiaBrandItems data) {
        this.data = data;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data.writeNBT(null, data, null));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        (this.data = new KaiaBrandItems()).readNBT(null, null, null, ByteBufUtils.readTag(buf));
    }

    public static class PlayerSyncPacketHandler implements IMessageHandler<PlayerSyncPacket, IMessage> {

        @Override
        public IMessage onMessage(PlayerSyncPacket message, MessageContext ctx) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            getKaiaCap(player).ifPresent(e -> e.reinserVariables(message.data));
            return null;
        }
    }
}
