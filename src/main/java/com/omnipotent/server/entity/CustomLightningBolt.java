package com.omnipotent.server.entity;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

public class CustomLightningBolt extends EntityLightningBolt {
    public CustomLightningBolt(World world) {
        super(world, 0, 2, 0, false);
    }

    public CustomLightningBolt(World worldIn, double x, double y, double z, boolean effectOnlyIn) {
        super(worldIn, x, y, z, effectOnlyIn);
    }
}
