package com.omnipotent.server.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class KaiaEntity extends EntityItem implements IForgeRegistryEntry {
    public KaiaEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public KaiaEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public KaiaEntity(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    public Object setRegistryName(ResourceLocation name) {
        return new ResourceLocation("entity2226");
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation("entity2226");
    }

    @Override
    public Class getRegistryType() {
        return null;
    }
}
