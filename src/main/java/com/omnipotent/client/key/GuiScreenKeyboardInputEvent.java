package com.omnipotent.client.key;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import static com.omnipotent.client.key.KeyInit.othersKeyBindingList;

public class GuiScreenKeyboardInputEvent {

    @SubscribeEvent
    public void keyPressed(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        othersKeyBindingList.forEach(key -> key.function.apply(player, false));
    }
}
