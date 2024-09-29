package com.omnipotent.common.capability.kaiacap;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IKaiaBrand {
    public void habilityBrand(List<ItemStack> kaia);

    public List<ItemStack> getAndExcludeAllKaiaInList();

    public List<ItemStack> returnList();

    public void addKaiaSummoned(String kaiaUUID);

    public List<String> getKaiaSwordsSummoned();

    void reinserVariables(KaiaBrandItems data);

    void syncWithServer(Entity entity);
}
