package com.omnipotent.server.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class KaiaEntity extends EntityItem {

    public boolean kaiaDoNotTrue = false;
    public EntityPlayer playerInvoker = null;

    public KaiaEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public KaiaEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public KaiaEntity(World worldIn) {
        super(worldIn);
    }
}
