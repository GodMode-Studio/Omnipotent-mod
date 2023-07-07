package com.omnipotent.client.key;

import com.omnipotent.Omnipotent;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.ReturnKaiaPacket;
import com.omnipotent.server.specialgui.net.KaiaContainerOpenPackte;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static com.omnipotent.util.KaiaUtil.withKaiaMainHand;

public class KeyEvent {
    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        int id = 0;
        if (KeyInit.keyReturnKaia.isPressed()) {
            NetworkRegister.ACESS.sendToServer(new ReturnKaiaPacket());
        }
        if (KeyInit.KaiaGui.isPressed() && withKaiaMainHand(player)) {
            player.openGui(Omnipotent.instance, id, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiEnchantment.isPressed() && withKaiaMainHand(player)) {
            player.openGui(Omnipotent.instance, ++id, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiBackpack.isPressed() && withKaiaMainHand(player)) {
            NetworkRegister.ACESS.sendToServer(new KaiaContainerOpenPackte(3));
        }
        if (KeyInit.kaiaGuiPotion.isPressed() && withKaiaMainHand(player)) {
            player.openGui(Omnipotent.instance, 4, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiDimension.isPressed() && KaiaUtil.getKaiaInMainHand(player) != null) {
            player.openGui(Omnipotent.instance, 5, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaPlayerGui.isPressed() && KaiaUtil.getKaiaInMainHand(player) != null) {
            player.openGui(Omnipotent.instance, 6, player.world, 0, 0, 0);
        }
    }
}
