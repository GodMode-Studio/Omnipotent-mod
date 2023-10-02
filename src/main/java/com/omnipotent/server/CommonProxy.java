package com.omnipotent.server;

import com.omnipotent.server.event.*;
import com.omnipotent.server.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import static com.omnipotent.Omnipotent.instance;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new KaiaEvent());
        MinecraftForge.EVENT_BUS.register(new EntityEvent());
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new EventInitItems());
        MinecraftForge.EVENT_BUS.register(new EntityStruckByLightningEventListener());
        MinecraftForge.EVENT_BUS.register(new LivingSpawnEventCheckSpawnListener());
        MinecraftForge.EVENT_BUS.register(new LivingSpawnEventSpecialSpawnListener());
        MinecraftForge.EVENT_BUS.register(new AttachCapabilitiesEventWorldListener());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {
    }
}
