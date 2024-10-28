package com.omnipotent.common.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "applyBobbing", at = @At("HEAD"))
    private void applyBobbing(float partialTicks, CallbackInfo ci) {
        if (!(this.mc.getRenderViewEntity() instanceof EntityPlayer player && player.hasKaia))
            return;
        float maxAllowedChange = 5.0F;
        float originalYaw = player.prevCameraYaw;
        float originalPitch = player.prevCameraPitch;
        if (Math.abs(player.cameraYaw - originalYaw) > maxAllowedChange) {
            player.cameraYaw = originalYaw;
        }
        if (Math.abs(player.cameraPitch - originalPitch) > maxAllowedChange) {
            player.cameraPitch = originalPitch;
        }
    }
}
