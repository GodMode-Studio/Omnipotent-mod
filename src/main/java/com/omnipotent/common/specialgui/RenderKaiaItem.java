package com.omnipotent.common.specialgui;

import com.omnipotent.acessor.IMinecraftAcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class RenderKaiaItem extends RenderItem {

    public static RenderKaiaItem instance;

    private RenderKaiaItem(TextureManager textureManager, ModelManager modelMesher, ItemColors itemColors) {
        super(textureManager, modelMesher, itemColors);
    }

    public static void init() {
        Minecraft mc = Minecraft.getMinecraft();
        instance = new RenderKaiaItem(mc.renderEngine, ((IMinecraftAcessor) mc).acessorMinecraftInstance(), mc.getItemColors());
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(instance);
    }

    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty() && text == null) {
            int count = stack.getCount();
            if (count != 1) {
                if (count > 1000000000) {
                    text = String.valueOf(stack.getCount() / 1000000000) + "G";
                } else if (count > 1000000) {
                    text = String.valueOf(stack.getCount() / 1000000) + "M";
                } else if (count > 1000) {
                    text = String.valueOf(stack.getCount() / 1000) + "K";
                }
            }
        }
        super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
    }

}