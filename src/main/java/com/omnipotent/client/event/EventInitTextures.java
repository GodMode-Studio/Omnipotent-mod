package com.omnipotent.client.event;

import com.omnipotent.client.render.RenderTextures;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventInitTextures {
    @SubscribeEvent
    public void renderItems(ModelRegistryEvent event){
        RenderTextures.registerTextures();
    }
}
