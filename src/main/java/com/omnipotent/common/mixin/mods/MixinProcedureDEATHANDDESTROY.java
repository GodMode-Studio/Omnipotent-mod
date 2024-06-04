package com.omnipotent.common.mixin.mods;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import players_are_gods.nhat.players_weapon.procedure.ProcedureDEATHANDDESTROY;

import java.util.HashMap;

@Mixin(value = ProcedureDEATHANDDESTROY.class, remap = false)
public abstract class MixinProcedureDEATHANDDESTROY {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true)
    private static void executeProcedure(HashMap<String, Object> dependencies, CallbackInfo ci) {
        if (KaiaUtil.hasInInventoryKaia((Entity) dependencies.get("entity")))
            ci.cancel();
    }
}
