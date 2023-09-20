package com.omnipotent.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.function.Consumer;

public class KeyMod extends KeyBinding {

    Consumer<Object> function;

    public KeyMod(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }

    public KeyMod(String description, int keyCode, String category, Consumer<Object> function) {
        super(description, keyCode, category);
        this.function = function;
    }

    public KeyMod(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
        super(description, keyConflictContext, keyCode, category);
    }

    public KeyMod(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
    }

    public KeyMod(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, int keyCode, String category, Consumer<Object> function) {
        super(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
        this.function = function;
    }
}
