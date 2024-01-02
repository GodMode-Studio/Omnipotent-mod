package com.omnipotent.common.gui;

import com.omnipotent.client.gui.*;
import com.omnipotent.client.gui.potion.KaiaGuiBlockPotion;
import com.omnipotent.client.gui.potion.KaiaGuiPotionAddedAndRemove;
import com.omnipotent.common.specialgui.ContainerKaia;
import com.omnipotent.common.specialgui.GUIContainerKaia;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public enum GuiIDs {
        ID_MOD
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 3:
                return new ContainerKaia(player, player.getHeldItem(y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), x);
        }
        throw new IllegalArgumentException("sem gui com o id " + ID);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                return new KaiaGui((EntityPlayerSP) player);
            case 1:
                return new KaiaGuiEnchantment(player);
            case 3:
                return new GUIContainerKaia(new ContainerKaia(player, player.getHeldItem(y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), x));
            case 4:
                return new KaiaGuiPotionAddedAndRemove(player);
            case 5:
                return new KaiaGuiDimension(player);
            case 6:
                return new KaiaPlayerGui(player);
            case 7:
                return new KaiaGuiAntiEntities(player);
            case 8:
                return new KaiaGuiBlockPotion(player);
        }
        throw new IllegalArgumentException("sem gui com o id" + ID);
    }
}
