package com.omnipotent.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.function.BiFunction;

public class KeyMod extends KeyBinding {

    BiFunction<Object, Boolean, Boolean> function;

    public KeyMod(String description, int keyCode, String category, BiFunction<Object, Boolean, Boolean> function) {
        super(description, keyCode, category);
        this.function = function;
    }

    public KeyMod(String description, int keyCode, String category, BiFunction<Object, Boolean, Boolean> function, KeyModifier keyModifier,
                  KeyConflictContext context) {
        super(description, context, keyModifier, keyCode, category);
        this.function = function;
    }

    public KeyMod(String description, IKeyConflictContext keyConflictContext, int keyCode, String category, BiFunction<Object, Boolean, Boolean> function) {
        super(description, keyConflictContext, keyCode, category);
        this.function = function;
    }
}
