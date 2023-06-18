package com.omnipotent.keys;

import com.omnipotent.Omnipotent;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.ReturnKaiaPacket;
import com.omnipotent.test.net.KaiaContainerOpenPackte;
import com.omnipotent.tools.Kaia;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyEvent {
    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (KeyInit.keyReturnKaia.isPressed()) {
            NetworkRegister.ACESS.sendToServer(new ReturnKaiaPacket());
        }
        if (KeyInit.keyOpenGuiKaia.isPressed() && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof Kaia) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, 0, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiEnchantment.isPressed()) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, 1, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiConfig.isPressed()) {
            NetworkRegister.ACESS.sendToServer(new KaiaContainerOpenPackte(3));
        }
    }
}
