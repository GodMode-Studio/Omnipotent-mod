package com.omnipotent.client;

import com.omnipotent.client.event.EventInitTextures;
import com.omnipotent.server.CommonProxy;
import com.omnipotent.client.event.KaiaToolTip;
import com.omnipotent.client.key.KeyEvent;
import com.omnipotent.client.render.RenderTextures;
import com.omnipotent.client.key.KeyInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new KaiaToolTip());
        MinecraftForge.EVENT_BUS.register(new KeyEvent());
        MinecraftForge.EVENT_BUS.register(new EventInitTextures());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyInit.initKeys();
    }
}
