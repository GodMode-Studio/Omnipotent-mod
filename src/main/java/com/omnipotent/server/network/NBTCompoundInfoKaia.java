package com.omnipotent.server.network;

import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.logging.Logger;

public class NBTCompoundInfoKaia implements IMessage {

    private NBTTagCompound tagCompound;

    public NBTCompoundInfoKaia() {

    }

    public NBTCompoundInfoKaia(NBTTagCompound nbt) {
        tagCompound = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tagCompound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    public static class NBTCompoundInfoKaiaHandler implements IMessageHandler<NBTCompoundInfoKaia, IMessage> {

        @Override
        public IMessage onMessage(NBTCompoundInfoKaia message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                if (!Minecraft.getMinecraft().isCallingFromMinecraftThread())
                    Minecraft.getMinecraft().addScheduledTask(() -> this.onMessage(message, ctx));
                else {
//                    Logger.getGlobal().info("AQUI TA INDO");
//                    KaiaGui currentScreen = (KaiaGui) Minecraft.getMinecraft().currentScreen;
//                    currentScreen.kaiaTagCompound = message.tagCompound;
//                    Logger.getGlobal().info("AQUI NÂO TA INDO");
                }
            } else {
                Logger.getGlobal().info("veio");
                EntityPlayerMP player = ctx.getServerHandler().player;
                MinecraftServer server = player.getServer();
                Logger.getGlobal().info("aaaaaaa" + player.getName());
                if (!server.isCallingFromMinecraftThread())
                    player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
                else {
                    Logger.getGlobal().info("AQUI TA INDO");
                    NetworkRegister.ACESS.sendMessageToPlayer(new NBTCompoundInfoKaia(KaiaUtil.getKaiaInMainHand(player).getTagCompound()), player);
                }
            }
            return null;
        }
    }
}