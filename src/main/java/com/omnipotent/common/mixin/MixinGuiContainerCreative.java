package com.omnipotent.common.mixin;

import com.omnipotent.OmnipotentTab;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative {

    @Redirect(method = "renderCreativeInventoryHoveringText", at = @At(value = "INVOKE", target = "Lnet/minecraft/creativetab/CreativeTabs;getTranslationKey()Ljava/lang/String;"))
    protected String renderCreativeInventoryHoveringText(CreativeTabs tab){
        return tab instanceof OmnipotentTab tab1 ? tab1.getSpacialName() : tab.getTranslationKey();
    }
}
