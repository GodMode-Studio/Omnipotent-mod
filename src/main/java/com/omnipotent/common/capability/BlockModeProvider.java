package com.omnipotent.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockModeProvider implements ICapabilitySerializable<NBTBase>{
    @CapabilityInject(IBlockMode.class)
    public static final Capability<IBlockMode> blockMode = null;
    private IBlockMode instance = blockMode.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == blockMode;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == blockMode ? blockMode.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return blockMode.getStorage().writeNBT(blockMode, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        blockMode.getStorage().readNBT(blockMode, this.instance, null, nbt);
    }
}
