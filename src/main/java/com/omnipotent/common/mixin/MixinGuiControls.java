//package com.omnipotent.common.mixin;
//
//import com.omnipotent.util.KaiaUtil;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.gui.GuiControls;
//import net.minecraft.client.gui.GuiKeyBindingList;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.settings.GameSettings;
//import net.minecraft.client.settings.KeyBinding;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//import org.spongepowered.asm.mixin.Shadow;
//
//import java.io.IOException;
//
//@Mixin(GuiControls.class)
//public abstract class MixinGuiControls extends GuiScreen {
//    @Shadow
//    public KeyBinding buttonId;
//
//    @Shadow
//    @Final
//    private GameSettings options;
//
//    @Shadow
//    private GuiKeyBindingList keyBindingList;
//
//    @Shadow
//    public long time;
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    @Final
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        if (this.buttonId != null) {
//            this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), -100 + mouseButton);
//            this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
//            this.buttonId = null;
//            KeyBinding.resetKeyBindingArrayAndHash();
//        } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
//            super.mouseClicked(mouseX, mouseY, mouseButton);
//        }
//    }
//
//    /**
//     * @author
//     * @reason
//     */
//    @Final
//    @Overwrite
//    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        if (this.buttonId != null && this.buttonId.getKeyCodeDefault() == 16) {
//            EntityPlayerSP player = Minecraft.getMinecraft().player;
//            if (player != null && KaiaUtil.hasInInventoryKaia(player))
//                return;
//        }
//        if (this.buttonId != null) {
//            if (keyCode == 1) {
//                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, 0);
//                this.options.setOptionKeyBinding(this.buttonId, 0);
//            } else if (keyCode != 0) {
//                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), keyCode);
//                this.options.setOptionKeyBinding(this.buttonId, keyCode);
//            } else if (typedChar > 0) {
//                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), typedChar + 256);
//                this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
//            }
//
//            if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(keyCode))
//                this.buttonId = null;
//            this.time = Minecraft.getSystemTime();
//            KeyBinding.resetKeyBindingArrayAndHash();
//        } else {
//            super.keyTyped(typedChar, keyCode);
//        }
//    }
//}
