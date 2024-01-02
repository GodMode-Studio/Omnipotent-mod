package com.omnipotent.common.mixin;

import com.omnipotent.common.tool.Kaia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.awt.*;
import java.util.Random;

import static com.omnipotent.constant.NbtBooleanValues.rgbEnchantmentGlitch;

@Mixin(RenderItem.class)
@SideOnly(Side.CLIENT)
public abstract class MixinRenderItem implements IResourceManagerReloadListener {
    @Shadow
    private final TextureManager textureManager;
    @Shadow
    private final ItemModelMesher itemModelMesher;
    @Shadow
    private final ItemColors itemColors;

    @Accessor("RES_ITEM_GLINT")
    abstract ResourceLocation getRES_ITEM_GLINT();

    private static final Random random = new Random();

    @Shadow
    abstract void renderModel(IBakedModel model, int color);

    @Shadow
    abstract void registerItems();

    @Shadow
    abstract void renderEffect(IBakedModel model);

    public MixinRenderItem(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_) {
        this.textureManager = p_i46552_1_;
        this.itemModelMesher = new net.minecraftforge.client.ItemModelMesherForge(p_i46552_2_);
        this.registerItems();
        this.itemColors = p_i46552_3_;
    }

    private long lastNetworkCallTime = 0;
    private int curColor = 0;
    private int tick = 0;
    private static final Color[] colors = {Color.white, Color.white, Color.white, Color.white, Color.white, Color.white, Color.white, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, /*Color.lightGray, Color.lightGray, Color.lightGray, Color.lightGray, Color.lightGray, Color.lightGray, Color.lightGray, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.gray, Color.gray, Color.gray, Color.gray, Color.gray, Color.gray, Color.gray, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.darkGray, Color.darkGray, Color.darkGray, Color.darkGray, Color.darkGray, Color.darkGray, Color.darkGray, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.black, Color.black, Color.black, Color.black, Color.black, Color.black, Color.black, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, */Color.red, Color.red, Color.red, Color.red, Color.red, Color.red, Color.red, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED, Color.pink, Color.pink, Color.pink, Color.pink, Color.pink, Color.pink, Color.pink, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.PINK, Color.orange, Color.orange, Color.orange, Color.orange, Color.orange, Color.orange, Color.orange, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.yellow, Color.yellow, Color.yellow, Color.yellow, Color.yellow, Color.yellow, Color.yellow, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.green, Color.green, Color.green, Color.green, Color.green, Color.green, Color.green, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.magenta, Color.magenta, Color.magenta, Color.magenta, Color.magenta, Color.magenta, Color.magenta, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.cyan, Color.cyan, Color.cyan, Color.cyan, Color.cyan, Color.cyan, Color.cyan, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN, Color.CYAN, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.blue, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE};
    private static int curColorIndex = 0;
    private static boolean dark = true;

    @Shadow
    public abstract void renderModel(IBakedModel model, ItemStack stack);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderItem(ItemStack stack, IBakedModel model) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            if (model.isBuiltInRenderer()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            } else {
                this.renderModel(model, stack);
                if (stack.hasEffect()) {
                    if (stack.getItem() instanceof Kaia && stack.getTagCompound().hasKey(rgbEnchantmentGlitch.getValue()) && stack.getTagCompound().getBoolean(rgbEnchantmentGlitch.getValue()))
                        renderKaiaEffect(model);
                    else
                        this.renderEffect(model);
                }
            }
            GlStateManager.popMatrix();
        }
    }


    private void renderKaiaEffect(IBakedModel model) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        this.textureManager.bindTexture(this.getRES_ITEM_GLINT());
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        int color;
        if (++tick >= 3) {
            tick = 0;
            if (--curColor < 0) {
                curColor = colors.length - 1;
            }
        }

        int i = random.nextInt(colors.length * 20);
        if (curColorIndex == i) {
            if (i % 2 == 0)
                color = colors[curColorIndex].darker().getRGB();
            else
                color = colors[curColorIndex].brighter().getRGB();
        } else
            color = colors[curColorIndex].getRGB();

        curColorIndex = (curColorIndex + 1) % colors.length;
        this.renderModel(model, color);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        this.renderModel(model, color);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }
}
