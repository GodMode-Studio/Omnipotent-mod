package com.omnipotent.server.network;

import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.server.specialgui.net.KaiaContainerOpenPackte;
import com.omnipotent.server.specialgui.net.KaiaContainerPackte;
import com.omnipotent.server.specialgui.net.KaiaSlotChangePacket;
import com.omnipotent.server.specialgui.net.OmnipotentContainerPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

import static com.omnipotent.Omnipotent.channel;

public enum NetworkRegister {
    ACESS;


    private NetworkRegister() {
        int index = 0;
        channel.registerMessage(PacketInicialization.PacketInicializationHandler.class, PacketInicialization.class, index++, Side.SERVER);
        channel.registerMessage(OmnipotentContainerPacket.AazominipotentContainerPacketHandler.class, OmnipotentContainerPacket.class, index++, Side.SERVER);
        channel.registerMessage(ReturnKaiaPacket.ReturnKaiaPacketHandler.class, ReturnKaiaPacket.class, index++, Side.SERVER);
        channel.registerMessage(KaiaNbtPacket.KaiaNbtPacketHandler.class, KaiaNbtPacket.class, index++, Side.SERVER);

        channel.registerMessage(KaiaContainerPackte.MessageHandler.class, KaiaContainerPackte.class, index++, Side.SERVER);
        channel.registerMessage(KaiaSlotChangePacket.MessageHandler.class, KaiaSlotChangePacket.class, index++, Side.CLIENT);
        channel.registerMessage(KaiaContainerOpenPackte.MessageHandler.class, KaiaContainerOpenPackte.class, index++, Side.SERVER);
    }

    public void sendToServer(IMessage message) {
        channel.sendToServer(message);
    }

    public void sendToAll(IMessage message) {
        channel.sendToAll(message);
    }

    public void sendMessageToPlayer(IMessage msg, EntityPlayerMP player) {
        channel.sendTo(msg, player);
    }
}
