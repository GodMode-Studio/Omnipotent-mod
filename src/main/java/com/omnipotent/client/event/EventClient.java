package com.omnipotent.client.event;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.KaiaWrapper;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

import static com.omnipotent.util.KaiaUtil.findKaiaInInventory;
import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

public class EventClient {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void guiOver(GuiOpenEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        GuiScreen nextGui = event.getGui();
        if(player==null && nextGui instanceof GuiGameOver)
            event.setCanceled(true);
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        Optional<KaiaWrapper> optKaia = findKaiaInInventory(player);
        boolean hasKaia = optKaia.isPresent();
        if (!hasKaia)
            return;
        KaiaWrapper kaia = optKaia.get();
        boolean guiToOpenIsGameOver = nextGui instanceof GuiGameOver;
        if (nextGui == null && !UtilityHelper.isCallerMinecraftOrForgeClassForEvents())
            event.setCanceled(true);
        if (guiToOpenIsGameOver) {
            event.setCanceled(true);
        }
        if (guiToOpenIsGameOver && currentScreen instanceof GuiGameOver) {
            Minecraft.getMinecraft().currentScreen = null;
            event.setCanceled(true);
        }
        if (nextGui != null && kaia.guiIsBanned(nextGui)) {
            event.setCanceled(true);
        }
        if (currentScreen != null && nextGui != null && kaia.guiIsBanned(currentScreen) && kaia.guiIsBanned(nextGui)) {
            Minecraft.getMinecraft().currentScreen = null;
            event.setCanceled(true);
        }
    }
}
