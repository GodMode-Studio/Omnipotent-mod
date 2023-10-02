package com.omnipotent.server.capability;

import com.omnipotent.util.NbtListUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class UnbanEntitiesStorage implements Capability.IStorage<IUnbanEntities> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IUnbanEntities> capability, IUnbanEntities instance, EnumFacing side) {
        NBTTagList tagCompound = new NBTTagList();
        for (Class classEntity : instance.entitiesCannotBannable()) {
            tagCompound.appendTag(new NBTTagString(classEntity.toString()));
        }
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IUnbanEntities> capability, IUnbanEntities instance, EnumFacing side, NBTBase nbt) {
        if (!(nbt instanceof NBTTagList))
            return;
        ArrayList<String> valueOfElementsOfNbtList = NbtListUtil.getValueOfElementsOfNbtList((NBTTagList) nbt);
        valueOfElementsOfNbtList.forEach(string ->
        {
            try {
                instance.markEntitiAsUnBanable(Class.forName(string.split("class ")[1]));
            } catch (ClassNotFoundException e) {
            }
        });
    }
}
