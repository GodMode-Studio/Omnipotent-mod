package com.omnipotent.gui;

import com.omnipotent.client.gui.KaiaGui;
import com.omnipotent.client.gui.KaiaGuiDimension;
import com.omnipotent.client.gui.KaiaGuiEnchantment;
import com.omnipotent.client.gui.KaiaGuiPotion;
import com.omnipotent.test.ContainerKaia;
import com.omnipotent.test.GUIContainerKaia;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public enum GuiIDs {
        ID_MOD
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                return new KaiaGui(player.inventory, player.getHeldItemMainhand());
            case 3:
                return new ContainerKaia(player, player.getHeldItem(y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), x);
        }
        throw new IllegalArgumentException("sem gui com o id" + ID);
    }
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                return new KaiaGui(player.inventory, player.getHeldItemMainhand());
            case 1:
                return new KaiaGuiEnchantment(player);
            case 3:
                return new GUIContainerKaia(new ContainerKaia(player, player.getHeldItem(y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), x));
            case 4:
                return new KaiaGuiPotion(player);
            case 5:
                return new KaiaGuiDimension(player);
        }
        throw new IllegalArgumentException("sem gui com o id" + ID);
    }
}
