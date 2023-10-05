package com.omnipotent.client.key;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static com.omnipotent.client.key.KeyInit.keyBindingRequireKaiaInMainHandList;
import static com.omnipotent.client.key.KeyInit.othersKeyBindingList;

public class KeyEvent {
    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        boolean u = KaiaUtil.withKaiaMainHand(player);
        for (KeyMod key : keyBindingRequireKaiaInMainHandList) {
            Boolean apply = key.function.apply(player, u);
            if (apply)
                return;
        }
        othersKeyBindingList.forEach(key -> key.function.apply(player, false));
    }
}