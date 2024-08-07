package com.omnipotent;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ImplementMixinsOfMods implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.modsomnipotent.json", "mixin.abyssalcraft.json", "mixins.crazymobs.json", "mixins.players_weapon.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return ILateMixinLoader.super.shouldMixinConfigQueue(mixinConfig);
    }
}
