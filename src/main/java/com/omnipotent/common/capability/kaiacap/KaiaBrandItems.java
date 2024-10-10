package com.omnipotent.common.capability.kaiacap;

import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.PlayerSyncPacket;
import com.omnipotent.util.KaiaWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.*;

public class KaiaBrandItems implements IKaiaBrand, Capability.IStorage<IKaiaBrand> {

    private List<ItemStack> kaiaItems = new ArrayList<>();

    private List<String> kaiaSwordsSummoned = new ArrayList<>();

    @Override
    public void habilityBrand(List<ItemStack> kaiaList) {
        Set<UUID> kaiaIds = new HashSet<>();
        kaiaList.forEach(item -> kaiaIds.add(new KaiaWrapper(item).getIdentify()));
        kaiaItems.removeIf(item -> kaiaIds.contains(new KaiaWrapper(item).getIdentify()));
        kaiaItems.addAll(kaiaList);
    }

    @Override
    public List<ItemStack> getAndExcludeAllKaiaInList() {
        ArrayList<ItemStack> kaiaList = new ArrayList<>();
        kaiaList.addAll(kaiaItems);
        kaiaItems.clear();
        return kaiaList;
    }

    @Override
    public List<ItemStack> returnList() {
        return kaiaItems;
    }

    @Override
    public void addKaiaSummoned(String uuid) {
        kaiaSwordsSummoned.add(uuid);
    }

    @Override
    public List<String> getKaiaSwordsSummoned() {
        return kaiaSwordsSummoned;
    }

    @Override
    public void reinserVariables(KaiaBrandItems data) {
        this.kaiaItems = data.kaiaItems;
        this.kaiaSwordsSummoned = data.kaiaSwordsSummoned;
    }

    @Override
    public void syncWithServer(Entity entity) {
        if (entity instanceof EntityPlayerMP player)
            NetworkRegister.sendMessageToPlayer(new PlayerSyncPacket(this), player);
    }

    @Nullable
    @Override
    public NBTTagCompound writeNBT(Capability<IKaiaBrand> capability, IKaiaBrand instance, EnumFacing side) {
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

        IKaiaBrand common = instance != null ? instance : this;
        List<ItemStack> kaiasPlayer = common.returnList();
        List<String> kaiaSwordsSummoned = common.getKaiaSwordsSummoned();
        kaias.forEach(tagKaia -> kaiasPlayer.add(new ItemStack((NBTTagCompound) tagKaia)));
        kaiaSwordsSummonedsTags.forEach(uuid -> kaiaSwordsSummoned.add(((NBTTagString) uuid).getString()));
    }

}
