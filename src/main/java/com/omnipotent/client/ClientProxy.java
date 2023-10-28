package com.omnipotent.client;

import com.omnipotent.client.event.EventClient;
import com.omnipotent.client.event.EventInitTextures;
import com.omnipotent.client.event.EventPlayerNameFormat;
import com.omnipotent.client.event.KaiaToolTip;
import com.omnipotent.client.key.KeyEvent;
import com.omnipotent.client.key.KeyInit;
import com.omnipotent.client.render.RenderCustomLightningBolt;
import com.omnipotent.server.CommonProxy;
import com.omnipotent.server.entity.CustomLightningBolt;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new KaiaToolTip());
        MinecraftForge.EVENT_BUS.register(new KeyEvent());
        MinecraftForge.EVENT_BUS.register(new EventInitTextures());
        MinecraftForge.EVENT_BUS.register(new EventClient());
        MinecraftForge.EVENT_BUS.register(new EventPlayerNameFormat());
        RenderingRegistry.registerEntityRenderingHandler(CustomLightningBolt.class, RenderCustomLightningBolt::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyInit.initKeys();
    }
}
