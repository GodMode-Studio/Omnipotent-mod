package com.omnipotent.common.network;

import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReturnKaiaPacket implements IMessage {

    public ReturnKaiaPacket(){
    }
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
    public static class ReturnKaiaPacketHandler implements IMessageHandler<ReturnKaiaPacket, IMessage>{
        @Override
        public IMessage onMessage(ReturnKaiaPacket message, MessageContext ctx) {
            KaiaUtil.returnKaiaOfOwner(ctx.getServerHandler().player);
            return null;
        }
    }
}
