package com.omnipotent.client.key;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static com.omnipotent.client.key.KeyInit.keyBindingList;

public class KeyEvent {
    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        keyBindingList.forEach(key -> ((KeyMod) key).function.accept(player));
    }
}