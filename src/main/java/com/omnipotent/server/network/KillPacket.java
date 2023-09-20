package com.omnipotent.server.network;

import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KillPacket implements IMessage {

    public KillPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class killPacketHandler implements IMessageHandler<KillPacket, IMessage> {

        @Override
        public IMessage onMessage(KillPacket message, MessageContext ctx) {
            if (!ctx.getServerHandler().player.getServer().isCallingFromMinecraftThread())
                ctx.getServerHandler().player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
            else {
                KaiaUtil.killArea(ctx.getServerHandler().player);
            }
            return null;
        }
    }
}