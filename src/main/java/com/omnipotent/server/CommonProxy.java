package com.omnipotent.server;

import com.omnipotent.server.event.EventInitItems;
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
        MinecraftForge.EVENT_BUS.register(new EventInitItems());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {
    }
}
