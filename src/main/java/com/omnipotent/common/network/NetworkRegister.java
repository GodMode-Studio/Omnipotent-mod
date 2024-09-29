package com.omnipotent.common.network;

import com.omnipotent.Omnipotent;
import com.omnipotent.common.network.nbtpackets.ChangedValuePacket;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.common.specialgui.net.KaiaContainerOpenPackte;
import com.omnipotent.common.specialgui.net.KaiaContainerPacket;
import com.omnipotent.common.specialgui.net.KaiaSlotChangePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum NetworkRegister {
    ACESS;

    private final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(Omnipotent.MODID);


    NetworkRegister() {
        int index = 0;
        channel.registerMessage(PacketInicialization.PacketInicializationHandler.class, PacketInicialization.class, ++index, Side.SERVER);
        channel.registerMessage(ReturnKaiaPacket.ReturnKaiaPacketHandler.class, ReturnKaiaPacket.class, ++index, Side.SERVER);
        channel.registerMessage(KaiaNbtPacket.KaiaNbtPacketHandler.class, KaiaNbtPacket.class, ++index, Side.SERVER);
        channel.registerMessage(KillPacket.killPacketHandler.class, KillPacket.class, ++index, Side.SERVER);

        channel.registerMessage(KaiaContainerPacket.MessageHandler.class, KaiaContainerPacket.class, ++index, Side.SERVER);
        channel.registerMessage(KaiaSlotChangePacket.MessageHandler.class, KaiaSlotChangePacket.class, ++index, Side.CLIENT);
        channel.registerMessage(KaiaContainerOpenPackte.MessageHandler.class, KaiaContainerOpenPackte.class, ++index, Side.SERVER);
        channel.registerMessage(ChangedValuePacket.ChangedValuePacketHandler.class, ChangedValuePacket.class, ++index, Side.CLIENT);
        channel.registerMessage(ChangedValuePacket.ChangedValuePacketHandler.class, ChangedValuePacket.class, ++index, Side.SERVER);
        channel.registerMessage(PlayerSyncPacket.PlayerSyncPacketHandler.class, PlayerSyncPacket.class, ++index, Side.CLIENT);
    }

    public void sendToServer(IMessage message) {
        channel.sendToServer(message);
    }

    public void sendToAll(IMessage message) {
        channel.sendToAll(message);
    }

    public void sendToAround(IMessage message, int dimensionId, BlockPos pos, int range) {
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(dimensionId, pos.getX(), pos.getY(), pos.getZ(), range);
        channel.sendToAllAround(message, targetPoint);
    }

    public void sendMessageToPlayer(IMessage msg, EntityPlayerMP player) {
        channel.sendTo(msg, player);
    }
}
