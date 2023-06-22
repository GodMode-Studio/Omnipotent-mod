package com.omnipotent.server.specialgui.net;

import com.omnipotent.server.specialgui.ContainerKaia;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KaiaContainerPackte implements IMessage {

    private boolean next;

    public KaiaContainerPackte() {
    }

    public KaiaContainerPackte(boolean next) {
        this.next = next;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        next = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(next);
    }

    public boolean isNext() {
        return next;
    }

    public static class MessageHandler implements IMessageHandler<KaiaContainerPackte, IMessage> {

        @Override
        public IMessage onMessage(KaiaContainerPackte message, MessageContext ctx) {
            Container container = ctx.getServerHandler().player.openContainer;
            if (container instanceof ContainerKaia) {
                if (message.isNext()) {
                    ((ContainerKaia) container).nextPage();
                } else {
                    ((ContainerKaia) container).prePage();
                }
            }
            return null;
        }

    }

}