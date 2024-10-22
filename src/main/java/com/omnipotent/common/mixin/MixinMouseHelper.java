package com.omnipotent.common.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHelper.class)
public abstract class MixinMouseHelper {

    private static boolean deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Inject(method = "grabMouseCursor", at = @At("HEAD"), cancellable = true)
    public void grabMouseCursor(CallbackInfo ci) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null && KaiaUtil.hasInInventoryKaia(player)) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 3) {
                StackTraceElement stackTraceElement = stackTrace[3];
                boolean b = !stackTraceElement.getMethodName().equals(deobfuscatedEnvironment ? "setIngameFocus" : "func_71381_h");
                if (!stackTraceElement.getClassName().equals("net.minecraft.client.Minecraft")
                        || b)
                    ci.cancel();
            }
        }
    }
}
