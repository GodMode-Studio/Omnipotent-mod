package com.omnipotent.server.mixin;

import com.omnipotent.acessor.MinecraftAcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.IThreadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IThreadListener, ISnooperInfo, MinecraftAcessor {

    @Accessor("modelManager")
    public abstract ModelManager getModelManager();

    @Override
    public ModelManager acessorMinecraftInstance() {
        return getModelManager();
    }
}
