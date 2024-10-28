package com.omnipotent.common.mixin;

import com.mojang.authlib.GameProfile;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    public void dropItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
        boolean hasKaia = hasInInventoryKaia(this);
        if (dropAll && hasKaia && !UtilityHelper.injectMixinIsCallerMinecraftOrForgeClass())
            cir.cancel();
        else if (hasKaia) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 3) {
                StackTraceElement stackTraceElement = stackTrace[3];
                if (!stackTraceElement.getClassName().startsWith("net.minecraft") && !stackTraceElement.getClassName().startsWith("com.omnipotent") && !stackTraceElement.getClassName().startsWith("net.minecraftforge"))
                    cir.cancel();
            }
        }
    }
}
