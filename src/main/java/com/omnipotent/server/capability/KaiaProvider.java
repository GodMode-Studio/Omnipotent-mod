package com.omnipotent.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KaiaProvider implements ICapabilitySerializable<NBTBase>{
    @CapabilityInject(IKaiaBrand.class)
    public static final Capability<IKaiaBrand> KaiaBrand = null;
    private IKaiaBrand instance = KaiaBrand.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == KaiaBrand;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == KaiaBrand ? KaiaBrand.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return KaiaBrand.getStorage().writeNBT(KaiaBrand, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        KaiaBrand.getStorage().readNBT(KaiaBrand, this.instance, null, nbt);
    }
}
