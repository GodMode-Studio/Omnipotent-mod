package com.omnipotent.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;

import java.util.function.BiFunction;

public class KeyMod extends KeyBinding {

    BiFunction<Object, Boolean, Boolean> function;

    public KeyMod(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }

    public KeyMod(String description, int keyCode, String category, BiFunction<Object, Boolean, Boolean> function) {
        super(description, keyCode, category);
        this.function = function;
    }

//    public KeyMod(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
//        super(description, keyConflictContext, keyCode, category);
//    }
//
//    public KeyMod(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
//        super(description, keyConflictContext, keyModifier, keyCode, category);
//    }

    public KeyMod(String description, IKeyConflictContext keyConflictContext, int keyCode, String category, BiFunction<Object, Boolean, Boolean> function) {
        super(description, keyConflictContext, keyCode, category);
        this.function = function;
    }
}
