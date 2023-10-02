package com.omnipotent.server.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.omnipotent.util.KaiaConstantsNbt.*;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends GuiIngame {
    @Shadow
    private FontRenderer fontrenderer;
    private int widthUsed;
    private int heigthUsed;
    private FontRenderer fontRendererUsed;

    public MixinGuiIngameForge(Minecraft mcIn) {
        super(mcIn);
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/Gui" +
            "IngameForge;post(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$ElementType;)V"))
    public void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        widthUsed = scaledresolution.getScaledWidth();
        heigthUsed = scaledresolution.getScaledHeight();
        this.fontRendererUsed = fontrenderer;
        renderKaiaInfo();
    }

    private void renderKaiaInfo() {
        EntityPlayerSP player = mc.player;
        GuiScreen currentScreen = mc.currentScreen;
        if (player == null || currentScreen != null) return;
        ItemStack kaiaInMainHand = KaiaUtil.getKaiaInMainHand(player);
        if (kaiaInMainHand == null) return;
        NBTTagCompound tagCompound = kaiaInMainHand.getTagCompound();
        if (tagCompound == null) return;
        HashMap<String, String> ligation = new HashMap();
        List<String> booleanValues = Arrays.asList(counterAttack, killAllEntities,
                killFriendEntities, attackYourWolf, interactLiquid,
                noBreakTileEntity, autoBackPackEntities, chargeItemsInInventory,
                summonLightBoltsInKill, autoBackPack, playersCantRespawn, banEntitiesAttacked, autoKill);
        ligation.put(counterAttack, I18n.format("guikaia.config.counterattack"));
        ligation.put(killAllEntities, I18n.format("guikaia.config.attackallentities"));
        ligation.put(killFriendEntities, I18n.format("guikaia.config.attackfriendentities"));
        ligation.put(attackYourWolf, I18n.format("guikaia.config.attackyourwolf"));
        ligation.put(interactLiquid, I18n.format("guikaia.config.interactwithliquidblocks"));
        ligation.put(noBreakTileEntity, I18n.format("guikaia.config.donotbreaktileentityblocks"));
        ligation.put(autoBackPackEntities, I18n.format("guikaia.config.autobackpackentities"));
        ligation.put(chargeItemsInInventory, I18n.format("guikaia.config.chargeitemsininventory"));
        ligation.put(summonLightBoltsInKill, I18n.format("guikaia.config.summonlightbolstinkill"));
        ligation.put(autoBackPack, I18n.format("guikaia.config.autobackpack"));
        ligation.put(playersCantRespawn, I18n.format("guikaia.config.playerscantrespawn"));
        ligation.put(banEntitiesAttacked, I18n.format("guikaia.config.banEntitiesAttacked"));
        ligation.put(autoKill, I18n.format("guikaia.config.autoKill"));
        List<String> values = new ArrayList<>();
        for (String key : booleanValues) {
            values.add(TextFormatting.GOLD + ligation.get(key) + ": " + TextFormatting.BLUE + tagCompound.getBoolean(key));
        }
        drawHoveringText(values, widthUsed, heigthUsed);
    }

    public void drawHoveringText(List<String> textLines, int x, int y) {
        drawHoveringText(textLines, x, y, fontRendererUsed);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, widthUsed, heigthUsed, -1, font);
        if (false && !textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines) {
                int j = this.fontRendererUsed.getStringWidth(s);

                if (j > i) {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.widthUsed) {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.heigthUsed) {
                i2 = this.heigthUsed - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRenderer.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            int i1 = 1347420415;
            int j1 = 1344798847;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                String s1 = textLines.get(k1);
                this.fontRendererUsed.drawStringWithShadow(s1, (float) l1, (float) i2, -1);

                if (k1 == 0) {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRenderer.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
}
