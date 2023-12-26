package com.omnipotent.server;

import com.omnipotent.server.event.*;
import com.omnipotent.server.gui.GuiHandler;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.PacketInicialization;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import static com.omnipotent.Omnipotent.instance;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegister.ACESS.sendToServer(new PacketInicialization());
        EventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(new KaiaEvent());
        eventBus.register(new EntityEvent());
        eventBus.register(new GuiHandler());
        eventBus.register(new EventInitItems());
        eventBus.register(new EntityStruckByLightningEventListener());
        eventBus.register(new LivingSpawnEventCheckSpawnListener());
        eventBus.register(new LivingSpawnEventSpecialSpawnListener());
        eventBus.register(new AttachCapabilitiesEventWorldListener());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {
    }
}
