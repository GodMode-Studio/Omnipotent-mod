package com.omnipotent.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AntiEntityProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IAntiEntitySpawn.class)
    public static final Capability<IAntiEntitySpawn> antiEntitySpawn = null;

    private IAntiEntitySpawn instance = antiEntitySpawn.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == antiEntitySpawn;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == antiEntitySpawn ? antiEntitySpawn.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return antiEntitySpawn.getStorage().writeNBT(antiEntitySpawn, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        antiEntitySpawn.getStorage().readNBT(antiEntitySpawn, instance, null, nbt);
    }
}
