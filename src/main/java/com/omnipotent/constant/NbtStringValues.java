package com.omnipotent.constant;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NbtStringValues {
    customPlayerName("customPlayerName");
    private String value;
    private String description;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    NbtStringValues(String value) {
        this.value = value;
    }

    NbtStringValues(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static final List<String> valuesNbt;

    static {
        Field[] declaredFields = NbtStringValues.class.getDeclaredFields();
        valuesNbt = Arrays.stream(declaredFields).filter(field -> field.getType() == NbtStringValues.class).map(field -> {
            try {
                field.setAccessible(true);
                NbtStringValues nbtNumberValues = (NbtStringValues) field.get(null);
                return nbtNumberValues.getValue();
            } catch (IllegalAccessException e) {
            }
            return null;
        }).collect(Collectors.toList());
    }
}