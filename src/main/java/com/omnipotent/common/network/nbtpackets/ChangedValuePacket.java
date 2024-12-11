package com.omnipotent.common.network.nbtpackets;

import com.omnipotent.common.network.NetworkRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class ChangedValuePacket implements IMessage {
    private String text;
    private boolean booleanValue;
    private int intValue;

    public ChangedValuePacket() {
    }

    public ChangedValuePacket(String text, boolean b) {
        this.text = text;
        this.booleanValue = b;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        text = ByteBufUtils.readUTF8String(buf);
        booleanValue = buf.readBoolean();
        intValue = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            ByteBufUtils.writeUTF8String(buf, text);
            buf.writeBoolean(booleanValue);
            buf.writeInt(intValue);
        } catch (Exception e) {
        }
    }

    public static class ChangedValuePacketHandler implements IMessageHandler<ChangedValuePacket, IMessage> {

        @Override
        public IMessage onMessage(ChangedValuePacket message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                if (!Minecraft.getMinecraft().isCallingFromMinecraftThread())
                    Minecraft.getMinecraft().addScheduledTask(() -> this.onMessage(message, ctx));
                else {
                    World worldClient = Minecraft.getMinecraft().player.world;
                    EntityPlayer playerEntityByUUID = worldClient.getPlayerEntityByUUID(UUID.fromString(message.text));
                    if (message.booleanValue) {
                        if (playerEntityByUUID != null)
                            playerEntityByUUID.renderSpecialName = true;
                    } else {
                        if (playerEntityByUUID != null)
                            playerEntityByUUID.renderSpecialName = false;
                    }
                }
                return null;
            } else {
                EntityPlayerMP player = ctx.getServerHandler().player;
                MinecraftServer server = player.getServer();
                if (!server.isCallingFromMinecraftThread())
                    player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
                else {
                    if (message.booleanValue) {
                        EntityPlayer playerEntityByUUID = player.world.getPlayerEntityByUUID(UUID.fromString(message.text));
                        if (playerEntityByUUID == null) return null;
                        if (playerEntityByUUID.hasKaia)
                            NetworkRegister.sendToAround(new ChangedValuePacket(playerEntityByUUID.getUniqueID().toString(), true), playerEntityByUUID.dimension, playerEntityByUUID.getPosition(), 50);
                        else {
                            NetworkRegister.sendToAround(new ChangedValuePacket(playerEntityByUUID.getUniqueID().toString(), false), playerEntityByUUID.dimension, playerEntityByUUID.getPosition(), 50);
                        }
                    }
                }
            }
            return null;
        }
    }
}
