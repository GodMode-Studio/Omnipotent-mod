package com.omnipotent.common.capability;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KaiaBrandItems implements IKaiaBrand {

    private List<ItemStack> kaiaItems = new ArrayList<>();

    @Override
    public void habilityBrand(List<ItemStack> kaiaList) {
        kaiaList.forEach(item -> kaiaItems.add(item));
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
}
