package com.omnipotent.common.capability;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IKaiaBrand {
    public void habilityBrand(List<ItemStack> kaia);

    public List<ItemStack> getAndExcludeAllKaiaInList();

    public List<ItemStack> returnList();
}
