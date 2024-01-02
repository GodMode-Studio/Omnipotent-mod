package com.omnipotent.common.mixin;

import com.omnipotent.acessor.IGuiTextFieldAcessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiTextField.class)
public abstract class MixinGuiTextField extends Gui implements IGuiTextFieldAcessor {
    @Accessor("enabledColor")
    public abstract int getEnabledColor();

    @Override
    public int acessorEnabledColor() {
        return getEnabledColor();
    }
}
