package com.omnipotent.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class TeleporteKaia implements ITeleporter {
    double posX;
    double posY;
    double posZ;

    public TeleporteKaia(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        entity.setPositionAndRotation(posX, posY, posZ, 0, 0);
    }
}
