package com.omnipotent;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class IMixinPluginMod implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        boolean b;
        try {
            Class.forName("com.zeitheron.hammercore.asm.HammerCoreTransformer");
            b = true;
        } catch (ClassNotFoundException e) {
            b = false;
        }
        if (b && s1.equals("com.omnipotent.common.mixin.MixinRenderItem"))
            return false;
        else return b || !s1.equals("com.omnipotent.common.mixin.MixinRenderItemHammerLib");
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
