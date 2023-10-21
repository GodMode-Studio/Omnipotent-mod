package com.omnipotent;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public class ImplementMixinsOfMods implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.modsomnipotent.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return ILateMixinLoader.super.shouldMixinConfigQueue(mixinConfig);
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig) {
        ILateMixinLoader.super.onMixinConfigQueued(mixinConfig);
    }
}
