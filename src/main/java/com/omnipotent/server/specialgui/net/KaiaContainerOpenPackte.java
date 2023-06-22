package com.omnipotent.server.specialgui.net;

import com.omnipotent.Omnipotent;
import com.omnipotent.server.specialgui.IContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KaiaContainerOpenPackte implements IMessage {

    private int id;

    public KaiaContainerOpenPackte() {
    }

    public KaiaContainerOpenPackte(int id) {
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public int getId() {
        return id;
    }

    public static class MessageHandler implements IMessageHandler<KaiaContainerOpenPackte, IMessage> {

        @Override
        public IMessage onMessage(KaiaContainerOpenPackte message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (!player.getServer().isCallingFromMinecraftThread()) {
                player.getServer().addScheduledTask(() -> {
                    this.onMessage(message, ctx);
                });
            } else {
                ItemStack stack = player.getHeldItemMainhand();
                boolean mainhand = true;
                if (stack.isEmpty() || !(stack.getItem() instanceof IContainer)) {
                    stack = ctx.getServerHandler().player.getHeldItemOffhand();
                    mainhand = false;
                }
                if (!stack.isEmpty() && stack.getItem() instanceof IContainer && ((IContainer) stack.getItem()).hasInventory(stack)) {
                    player.openGui(Omnipotent.instance, message.getId(), player.world, mainhand ? player.inventory.currentItem : -1, mainhand ? 0 : 1, 0);
                }
            }
            return null;
        }

    }

}