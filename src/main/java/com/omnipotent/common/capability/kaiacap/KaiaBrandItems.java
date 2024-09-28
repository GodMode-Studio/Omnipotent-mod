package com.omnipotent.common.capability.kaiacap;

import com.omnipotent.common.network.NetworkRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class KaiaBrandItems implements IKaiaBrand {

    private final List<ItemStack> kaiaItems = new ArrayList<>();

    private final List<String> kaiaSwordsSummoned = new ArrayList<>();

    @Override
    public void habilityBrand(List<ItemStack> kaiaList) {
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

    public void readNBT(NBTTagCompound nbtTagCompound) {

    }

    public void syncWithServer(Entity entity) {
        if (entity instanceof EntityPlayerMP player)
            NetworkRegister.ACESS.sendMessageToPlayer(player, new PlayerSyncPacket(this));
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.putBoolean("isYuta", this.isYuta);
        nbt.putIntArray("copiedTechniques", copiedTechniques);
        nbt.putInt("currentIndex", this.currentIndex);
        return nbt;
    }
}
