package com.omnipotent.server.mixin.mods;

import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = DEEventHandler.class, remap = false)
public interface IMixinDEEventHandler {
    @Invoker("getDropChanceFromItem")
    int callGetDropChanceFromItem(ItemStack stack);
}