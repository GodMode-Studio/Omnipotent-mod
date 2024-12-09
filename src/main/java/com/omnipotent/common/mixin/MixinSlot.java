package com.omnipotent.common.mixin;

import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaWrapper;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(Slot.class)
public abstract class MixinSlot {

    @Shadow
    @Final
    public IInventory inventory;
    @Shadow
    @Final
    private int slotIndex;

    @Shadow
    public abstract void onSlotChanged();

    @Inject(method = "putStack", at = @At("HEAD"))
    public void putStack(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof Kaia && UtilityHelper.inLogicSide() && !(inventory instanceof InventoryPlayer)) {
            KaiaWrapper kaiaWrapper = new KaiaWrapper(stack);
            kaiaWrapper.getOwner().flatMapJava(UtilityHelper::getKaiaCap).ifPresent(cap -> cap.habilityBrand(Collections.singletonList(stack)));
        }
    }
}
