package com.omnipotent.common.gui;

import com.omnipotent.client.gui.KaiaGui;
import com.omnipotent.client.gui.KaiaGuiDimension;
import com.omnipotent.client.gui.KaiaGuiEnchantment;
import com.omnipotent.client.gui.KaiaPlayerGui;
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

    public static final int KaiaGui = 0;
    public static final int KaiaGuiEnchantment = 1;
    public static final int KaiaGuiBlockPotion = 2;
    public static final int GUIContainerKaia = 3;
    public static final int KaiaGuiPotionAddedAndRemove = 4;
    public static final int KaiaGuiDimension = 5;
    public static final int KaiaPlayerGui = 6;


    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUIContainerKaia:
                return new ContainerKaia(player, player.getHeldItem(y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), x);
        }
        throw new IllegalArgumentException("sem gui com o id " + ID);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case KaiaGui:
                return new KaiaGui((EntityPlayerSP) player);
            case KaiaGuiEnchantment:
                return new KaiaGuiEnchantment(player);
            case GUIContainerKaia:
                return new GUIContainerKaia(new ContainerKaia(player, player.getHeldItem(y == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), x));
            case KaiaGuiBlockPotion:
                return new KaiaGuiBlockPotion(player);
            case KaiaGuiPotionAddedAndRemove:
                return new KaiaGuiPotionAddedAndRemove(player);
            case KaiaGuiDimension:
                return new KaiaGuiDimension(player);
            case KaiaPlayerGui:
                return new KaiaPlayerGui(player);
//            case 7:
//                return new KaiaGuiAntiEntities(player);
        }
        throw new IllegalArgumentException("sem gui com o id" + ID);
    }
}
