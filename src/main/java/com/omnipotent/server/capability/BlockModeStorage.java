package com.omnipotent.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BlockModeStorage implements Capability.IStorage<IBlockMode> {

    private final String blockSpectatorMode = "blockNoEditMode";

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IBlockMode> capability, IBlockMode instance, EnumFacing side) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setBoolean(blockSpectatorMode, instance.getBlockNoEditMode());
        tagCompound.setBoolean("blockSurvivalMode", instance.getBlockCreativeMode());
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IBlockMode> capability, IBlockMode instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound))
            return;
        NBTTagCompound nbtOfPlayer = (NBTTagCompound) nbt;
        boolean blockSpectatorMode = nbtOfPlayer.getBoolean(this.blockSpectatorMode);
        boolean blockSurvivalMode = nbtOfPlayer.getBoolean("blockSurvivalMode");
        instance.setBlockNoEditMode(blockSpectatorMode);
        instance.setBlockNoEditMode(blockSurvivalMode);
    }
}
