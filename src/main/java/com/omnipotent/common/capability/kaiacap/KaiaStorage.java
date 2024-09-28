package com.omnipotent.common.capability.kaiacap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;

public class KaiaStorage implements Capability.IStorage<IKaiaBrand> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IKaiaBrand> capability, IKaiaBrand instance, EnumFacing side) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList kaiaSwordsSummoned = new NBTTagList();
        NBTTagList kaias = new NBTTagList();

        instance.returnList().forEach(itemStack -> kaias.appendTag(itemStack.serializeNBT()));
        instance.getKaiaSwordsSummoned().forEach(uuid -> kaiaSwordsSummoned.appendTag(new NBTTagString(uuid)));
        tagCompound.setTag("kaias", kaias);
        tagCompound.setTag("kaiaswordssummoned", kaiaSwordsSummoned);
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IKaiaBrand> capability, IKaiaBrand instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;
        NBTTagList kaiaSwordsSummonedsTags = nbtTagCompound.getTagList("kaiaswordssummoned", 8);
        NBTTagList kaias = nbtTagCompound.getTagList("kaias", 10);

        List<ItemStack> kaiasPlayer = instance.returnList();
        List<String> kaiaSwordsSummoned = instance.getKaiaSwordsSummoned();
        kaias.forEach(tagKaia -> kaiasPlayer.add(new ItemStack((NBTTagCompound) tagKaia)));
        kaiaSwordsSummonedsTags.forEach(uuid -> kaiaSwordsSummoned.add(((NBTTagString) uuid).getString()));
    }
}
