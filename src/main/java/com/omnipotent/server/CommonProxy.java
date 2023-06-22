package com.omnipotent.server;

import com.omnipotent.server.event.KaiaEvent;
import com.omnipotent.server.event.UpdateEntity;
import com.omnipotent.server.gui.GuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;

import static com.omnipotent.Omnipotent.instance;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new KaiaEvent());
        MinecraftForge.EVENT_BUS.register(new UpdateEntity());
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {
        ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ForgeChunkManager.LoadingCallback() {
            @Override
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                for (ForgeChunkManager.Ticket ticket : tickets) {
                    Entity entity = ticket.getEntity();
                    if (entity != null) {
                        ForgeChunkManager.forceChunk(ticket, entity.getEntityWorld().getChunkFromBlockCoords(entity.getPosition()).getPos());
                    }
                }
            }
        });
    }
}
