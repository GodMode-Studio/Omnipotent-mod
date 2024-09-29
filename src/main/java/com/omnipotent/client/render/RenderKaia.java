package com.omnipotent.client.render;

import com.omnipotent.common.entity.KaiaEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderKaia extends Render<KaiaEntity> {

    private final RenderItem itemRenderer;

    public RenderKaia(RenderManager renderManager) {
        super(renderManager);
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void doRender(KaiaEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack itemstack = new ItemStack(Item.getByNameOrId("omnipotent:kaia"));
        boolean flag = false;

        if (this.bindEntityTexture(entity)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, null);
        this.transform(entity, x, y, z, partialTicks, ibakedmodel);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        GlStateManager.pushMatrix();
        this.itemRenderer.renderItem(itemstack, ibakedmodel);
        GlStateManager.popMatrix();

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (flag) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void transform(KaiaEntity entity, double x, double y, double z, float partialTicks, IBakedModel kaiaModel) {
        float f1 = MathHelper.sin(((float) 6000 + partialTicks) / 10.0F + (float) (1 * Math.PI * 2.0D)) * 0.1F + 0.1F;
        float f2 = kaiaModel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
        GlStateManager.translate((float) x, (float) y + f1 + 0.25F * f2, (float) z);
        if (entity.isAttackMode()) {
            rotationEntityToTarget(entity);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void rotationEntityToTarget(KaiaEntity entity) {
        final float deltaX = (float) (entity.posX - entity.getAttackTarget().posX);
        GlStateManager.rotate(deltaX > 0 ? 90 : 270, 0.0F, 0.0F, 1.0F);
        final float deltaZ = (float) (entity.posZ - entity.getAttackTarget().posZ);
        GlStateManager.rotate(deltaZ > 0 && deltaZ < 1 ? 0 : deltaZ > 0 ? 270 : 90, 1.0F, 0.0F, 0.0F);
    }

//    private String[] readChat() {
//        Minecraft mc = Minecraft.getMinecraft();
//        try {
//            List<String> sentMessages = mc.ingameGUI.getChatGUI().getSentMessages();
//            String s = sentMessages.get(sentMessages.size() - 1);
//            String[] words = s.split("=");
//            if (words.length != 2)
//                return null;
//            Double.valueOf(words[1]);
//            return words;
//        } catch (Exception e) {
//        }
//        if (arr != null) {
//            String s = arr[0];
//            float v = 1.0F;
//            float v1 = 0.0F;
//            float x1 = s.equalsIgnoreCase("x") ? v : v1;
//            float y1 = s.equalsIgnoreCase("y") ? v : v1;
//            float z1 = s.equalsIgnoreCase("z") ? v : v1;
//            float angle = Float.parseFloat(arr[1]);
//            if (x1 == v1 || y1 == v1 || z1 == v1)
//                GlStateManager.rotate(angle, x1,
//                        y1,
//                        z1);
//        }
//        return null;
//    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(KaiaEntity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public boolean shouldRender(KaiaEntity livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }
}
