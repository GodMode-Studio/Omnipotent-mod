package com.omnipotent.server.network;

import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.omnipotent.constant.NbtNumberValues.rangeAttack;

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
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (!player.getServer().isCallingFromMinecraftThread())
                player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
            else {
                if ((KaiaUtil.getKaiaInMainHand(player) == null ? KaiaUtil.getKaiaInInventory(player) : KaiaUtil.getKaiaInMainHand(player)).getTagCompound().getInteger(rangeAttack.getValue()) > 5)
                    KaiaUtil.killArea(player);
            }
            return null;
        }
    }
}