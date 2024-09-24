package com.omnipotent;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import zone.rong.mixinbooter.IEarlyMixinLoader;
import zone.rong.mixinbooter.ILateMixinLoader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.omnipotent.Omnipotent.MODID;

@IFMLLoadingPlugin.Name(MODID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class ConfigEarlyMixins implements IEarlyMixinLoader, IFMLLoadingPlugin {
    @Override
    public List<String> getMixinConfigs() {
        MixinBootstrap.init();
        return Collections.singletonList("mixins.omnipotent.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
