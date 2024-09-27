package com.omnipotent.client;

import com.omnipotent.client.event.*;
import com.omnipotent.client.key.KeyEvent;
import com.omnipotent.client.key.KeyInit;
import com.omnipotent.client.render.RenderCustomLightningBolt;
import com.omnipotent.client.render.RenderKaia;
import com.omnipotent.common.CommonProxy;
import com.omnipotent.common.entity.CustomLightningBolt;
import com.omnipotent.common.entity.KaiaEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        EventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(new KaiaToolTip());
        eventBus.register(new KeyEvent());
        eventBus.register(new EventInitTextures());
        eventBus.register(new EventClient());
        eventBus.register(new EventPlayerNameFormat());
        eventBus.register(new HandlerRenderGamerOverlay());
        RenderingRegistry.registerEntityRenderingHandler(CustomLightningBolt.class, RenderCustomLightningBolt::new);
        RenderingRegistry.registerEntityRenderingHandler(KaiaEntity.class, RenderKaia::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyInit.initKeys();
    }
}
