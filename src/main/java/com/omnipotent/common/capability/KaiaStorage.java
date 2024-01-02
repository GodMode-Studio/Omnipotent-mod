package com.omnipotent.common.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class KaiaStorage implements Capability.IStorage<IKaiaBrand> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IKaiaBrand> capability, IKaiaBrand instance, EnumFacing side) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        instance.returnList().forEach(itemStack -> itemStack.writeToNBT(tagCompound));
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IKaiaBrand> capability, IKaiaBrand instance, EnumFacing side, NBTBase nbt) {
        ItemStack kaia = nbt instanceof NBTTagCompound ? new ItemStack((NBTTagCompound) nbt) : null;
        instance.returnList().add(kaia);
    }
}
