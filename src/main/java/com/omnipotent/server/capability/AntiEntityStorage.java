package com.omnipotent.server.capability;

import com.omnipotent.util.NbtListUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AntiEntityStorage implements Capability.IStorage<IAntiEntitySpawn> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IAntiEntitySpawn> capability, IAntiEntitySpawn instance, EnumFacing side) {
        NBTTagList tagCompound = new NBTTagList();
        for (Class classEntity : instance.entitiesDontSpawnInWorld()) {
            tagCompound.appendTag(new NBTTagString(classEntity.toString()));
        }
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IAntiEntitySpawn> capability, IAntiEntitySpawn instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagList))
            return;
        ArrayList<String> valueOfElementsOfNbtList = NbtListUtil.getValueOfElementsOfNbtList((NBTTagList) nbt);
        valueOfElementsOfNbtList.forEach(string ->
        {
            try {
                instance.dennySpawnInWorld(Class.forName(string.split("class ")[1]));
            } catch (ClassNotFoundException e) {
            }
        });
    }
}
