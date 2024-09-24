package com.omnipotent;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public class ConfigModsMixins implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.players_weapon.json", "mixins.crazymobs.json", "mixin.abyssalcraft.json",
                "mixins.modsomnipotent.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return ILateMixinLoader.super.shouldMixinConfigQueue(mixinConfig);
    }
}
