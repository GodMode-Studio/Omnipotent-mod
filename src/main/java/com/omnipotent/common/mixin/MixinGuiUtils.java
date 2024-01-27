package com.omnipotent.common.mixin;

import com.omnipotent.common.tool.Kaia;
import com.omnipotent.constant.NbtNumberValues;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiUtils.class)
public abstract class MixinGuiUtils {

    @Shadow(remap = false)
    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
    }

    private static int[] colors = {Color.YELLOW.getRGB(), Color.YELLOW.getRGB(), Color.YELLOW.getRGB(), Color.YELLOW.getRGB(), Color.YELLOW.getRGB(), Color.YELLOW.getRGB(), Color.YELLOW.getRGB(), Color.cyan.getRGB(), Color.cyan.getRGB(), Color.cyan.getRGB(), Color.cyan.getRGB(), Color.cyan.getRGB(), Color.cyan.getRGB(), Color.cyan.getRGB(), Color.blue.getRGB(), Color.blue.getRGB(), Color.blue.getRGB(), Color.blue.getRGB(), Color.blue.getRGB(), Color.blue.getRGB(), Color.blue.getRGB(), Color.RED.getRGB(), Color.RED.getRGB(), Color.RED.getRGB(), Color.RED.getRGB(), Color.RED.getRGB(), Color.RED.getRGB(), Color.RED.getRGB(), Color.GREEN.getRGB(), Color.GREEN.getRGB(), Color.GREEN.getRGB(), Color.GREEN.getRGB(), Color.GREEN.getRGB(), Color.GREEN.getRGB(), Color.GREEN.getRGB(), Color.MAGENTA.getRGB(), Color.MAGENTA.getRGB(), Color.MAGENTA.getRGB(), Color.MAGENTA.getRGB(), Color.MAGENTA.getRGB(), Color.MAGENTA.getRGB(), Color.MAGENTA.getRGB()};
    private static int curColorIndex = 0;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2) {
                        tooltipTextWidth = mouseX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY < 4) {
                tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4;
            }

            int backgroundColor;
            int borderColorStart;
            int borderColorEnd;

            final int zLevel = 300;
            if (stack.getItem() instanceof Kaia) {
                int option = 0;
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null)
                    option = tagCompound.getInteger(NbtNumberValues.optionOfColor.getValue());
                int curColor = colors[curColorIndex];
                switch (option) {
                    case 1:
                        backgroundColor = (curColor & 0xFFFFFF) | 0x20000000;
                        borderColorStart = curColor;
                        borderColorEnd = colors[(curColorIndex + 1) % colors.length];
                        curColorIndex = (curColorIndex + 1) % colors.length;
                        specialMethod(borderColorStart, borderColorEnd, backgroundColor, stack, textLines, font, tooltipX, tooltipY, zLevel, tooltipTextWidth, tooltipHeight, titleLinesCount);
                        return;
                    case 2:
                        backgroundColor = 0xF0100010;
                        borderColorStart = curColor;
                        borderColorEnd = colors[(curColorIndex + 1) % colors.length];
                        curColorIndex = (curColorIndex + 1) % colors.length;
                        specialMethod(borderColorStart, borderColorEnd, backgroundColor, stack, textLines, font, tooltipX, tooltipY, zLevel, tooltipTextWidth, tooltipHeight, titleLinesCount);
                        return;
                    case 3:
                        backgroundColor = colors[(curColorIndex + 1) % colors.length];
                        borderColorStart = curColor;
                        borderColorEnd = colors[(curColorIndex + 1) % colors.length];
                        curColorIndex = (curColorIndex + 1) % colors.length;
                        specialMethod(borderColorStart, borderColorEnd, backgroundColor, stack, textLines, font, tooltipX, tooltipY, zLevel, tooltipTextWidth, tooltipHeight, titleLinesCount);
                        return;
                }
            }
            defaultMethod(stack, textLines, font, tooltipX, tooltipY, zLevel, tooltipTextWidth, tooltipHeight, titleLinesCount);
        }
    }

    private static void specialMethod(int borderColorStart, int borderColorEnd, int backgroundColor, ItemStack stack, List<String> textLines, FontRenderer font, int tooltipX, int tooltipY, int zLevel, int tooltipTextWidth, int tooltipHeight, int titleLinesCount) {
        RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
        if (!Loader.isModLoaded("legendarytooltips"))
            MinecraftForge.EVENT_BUS.post(colorEvent);
        backgroundColor = colorEvent.getBackground();
        borderColorStart = colorEvent.getBorderStart();
        borderColorEnd = colorEvent.getBorderEnd();
        render(borderColorStart, borderColorEnd, backgroundColor, tooltipX, tooltipY, zLevel, tooltipTextWidth, tooltipHeight);
        MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
        int tooltipTop = tooltipY;

        for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
            String line = textLines.get(lineNumber);
            font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

            if (lineNumber + 1 == titleLinesCount) {
                tooltipY += 2;
            }

            tooltipY += 10;
        }

        MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }

    private static void render(int borderColorStart, int borderColorEnd, int backgroundColor, int tooltipX, int tooltipY, int zLevel, int tooltipTextWidth, int tooltipHeight) {
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);
    }

    private static void defaultMethod(ItemStack stack, List<String> textLines, FontRenderer font, int tooltipX, int tooltipY, int zLevel, int tooltipTextWidth, int tooltipHeight, int titleLinesCount) {

        int borderColorEnd;
        int backgroundColor;
        int borderColorStart;
        Minecraft mc = Minecraft.getMinecraft();
        if (KaiaUtil.withKaiaMainHand(mc.player) && mc.currentScreen == null) {
            backgroundColor = 0x200020FF;
            borderColorStart = Color.GRAY.getRGB();
            borderColorEnd = Color.magenta.getRGB();
        } else {
            backgroundColor = 0xF0100010;
            borderColorStart = 0x505000FF;
            borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        }
        RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
        MinecraftForge.EVENT_BUS.post(colorEvent);
        backgroundColor = colorEvent.getBackground();
        borderColorStart = colorEvent.getBorderStart();
        borderColorEnd = colorEvent.getBorderEnd();
        render(borderColorStart, borderColorEnd, backgroundColor, tooltipX, tooltipY, zLevel, tooltipTextWidth, tooltipHeight);

        MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
        int tooltipTop = tooltipY;

        for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
            String line = textLines.get(lineNumber);
            font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

            if (lineNumber + 1 == titleLinesCount) {
                tooltipY += 2;
            }

            tooltipY += 10;
        }

        MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }
}
