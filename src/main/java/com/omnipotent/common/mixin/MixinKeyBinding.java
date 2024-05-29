package com.omnipotent.common.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements Comparable<KeyBinding> {

    @Shadow
    private int keyCode;
//
//    @Shadow
//    @Final
//    private static KeyBindingMap HASH;
//
//    @Shadow
//    private KeyModifier keyModifier;
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite(remap = false)
//    @Final
//    public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode) {
//        if (keyCode == 16) {
//            EntityPlayerSP player = Minecraft.getMinecraft().player;
//            if (player != null && KaiaUtil.hasInInventoryKaia(player))
//                return;
//        }
//        this.keyCode = keyCode;
//        if (keyModifier.matches(keyCode)) {
//            keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
//        }
//        HASH.removeKey((KeyBinding) (Object) this);
//        this.keyModifier = keyModifier;
//        HASH.addKey(keyCode, (KeyBinding) (Object) this);
//    }
}
