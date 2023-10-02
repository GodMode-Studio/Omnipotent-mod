package com.omnipotent.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UnbanEntitiesProvider implements ICapabilitySerializable<NBTBase> {

    @CapabilityInject(IUnbanEntities.class)
    public static final Capability<IUnbanEntities> unbanEntities = null;

    private IUnbanEntities instance = unbanEntities.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == unbanEntities;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == unbanEntities ? unbanEntities.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return unbanEntities.getStorage().writeNBT(unbanEntities, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        unbanEntities.getStorage().readNBT(unbanEntities, instance, null, nbt);
    }
}
