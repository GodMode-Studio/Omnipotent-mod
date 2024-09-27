package com.omnipotent.common.capability;

import net.minecraft.item.ItemStack;

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
}
