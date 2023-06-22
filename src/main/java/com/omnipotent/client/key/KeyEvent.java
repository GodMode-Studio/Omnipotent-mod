package com.omnipotent.client.key;

import com.omnipotent.Omnipotent;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.ReturnKaiaPacket;
import com.omnipotent.server.specialgui.net.KaiaContainerOpenPackte;
import com.omnipotent.server.tool.Kaia;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyEvent {
    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        int id = 0;
        if (KeyInit.keyReturnKaia.isPressed()) {
            NetworkRegister.ACESS.sendToServer(new ReturnKaiaPacket());
        }
        if (KeyInit.keyOpenGuiKaia.isPressed() && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof Kaia) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, id, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiEnchantment.isPressed()) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, ++id, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiBackpack.isPressed()) {
            NetworkRegister.ACESS.sendToServer(new KaiaContainerOpenPackte(3));
        }
        if (KeyInit.kaiaGuiPotion.isPressed()) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, 4, player.world, 0, 0, 0);
        }
        if (KeyInit.kaiaGuiDimension.isPressed()) {
            ItemStack kaiaItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
            player.openGui(Omnipotent.instance, 5, player.world, 0, 0, 0);
        }
    }
}
