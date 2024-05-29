//package com.omnipotent.common.mixin;
//
//import com.omnipotent.util.KaiaUtil;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.settings.GameSettings;
//import net.minecraft.client.settings.KeyBinding;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//import org.spongepowered.asm.mixin.Shadow;
//
//@Mixin(GameSettings.class)
//public abstract class MixinGameSettings {
//
//    @Shadow
//    public abstract void saveOptions();
//
//    @Shadow
//    public KeyBinding keyBindDrop;
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    @Final
//    public void setOptionKeyBinding(KeyBinding key, int keyCode) {
//        if (key == this.keyBindDrop) {
//            EntityPlayerSP player = Minecraft.getMinecraft().player;
//            if (player != null && (KaiaUtil.hasInInventoryKaia(player))) {
//                return;
//            }
//        }
//        key.setKeyCode(keyCode);
//        this.saveOptions();
//    }
//}
