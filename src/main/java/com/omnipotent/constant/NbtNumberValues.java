package com.omnipotent.constant;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NbtNumberValues {
    blockBreakArea("areaBloco", "function.description.areaBloco"), rangeAttack("rangeAttack", "function.description.rangeAttack"), maxCountSlot("maxcountslot", "function.description.maxcountslot"), blockReachDistance("blockReachDistance", "function.description.blockReachDistance"), rangeAutoKill("rangeautokill"), chargeEnergyInBlocksAround("chargeEnergyInBlocksAround", "function.description.chargeEnergyInBlocksAround"), chargeManaInBlocksAround("chargeManaInBlocksAround", "function.description.chargeManaInBlocksAround");
    private String value;
    private String description;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    NbtNumberValues(String value) {
        this.value = value;
    }

    NbtNumberValues(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static final List<String> valuesNbt;

    static {
        Field[] declaredFields = NbtNumberValues.class.getDeclaredFields();
        valuesNbt = Arrays.stream(declaredFields).filter(field -> field.getType() == NbtNumberValues.class).map(field -> {
            try {
                field.setAccessible(true);
                NbtNumberValues nbtNumberValues = (NbtNumberValues) field.get(null);
                return nbtNumberValues.getValue();
            } catch (IllegalAccessException e) {
            }
            return null;
        }).collect(Collectors.toList());
    }
}