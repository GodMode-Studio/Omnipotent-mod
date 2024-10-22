package com.omnipotent.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

public class EventClient {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void guiOver(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver && Minecraft.getMinecraft().player != null && hasInInventoryKaia(Minecraft.getMinecraft().player)) {
            event.setCanceled(true);
        }
    }
}
