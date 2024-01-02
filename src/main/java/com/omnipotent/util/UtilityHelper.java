package com.omnipotent.util;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect;

public class UtilityHelper {

    /**
     * Este método retorna verdadeiro caso a string contenha apenas numeros inteiros e falso caso contrario.
     *
     * @Author gamerYToffi
     */
    public static boolean isJustNumber(String text) {
        return text.matches("[-]?\\d+");
    }

    /**
     * Este método com base no valor recebido e na altura da tela retorna um valor equivalente para qualquer monitor.
     *
     * @Author gamerYToffi
     */
    public static int getEquivalentValueOfscreenHeight(int value, int height) {
        double ratio = (double) value / height;
        int equivalentValue = (int) (height * ratio);
        return equivalentValue;
    }

    /**
     * Este método usa como base o valor 480 para retorna um valor universal para qualquer monitor.
     *
     * @Author gamerYToffi
     */
    public static int getEquivalentValue(int value, int valueActualMonitor) {
        final int heightFinal = 480;
        float i = heightFinal / 100.0F;
        float valueFixedOfPercentage = value / i;
        float i1 = valueFixedOfPercentage / 100 * valueActualMonitor;
        return Math.round(i1);
    }
//    public static int getEquivalentValue(int value, int valueActualMonitor) {
//        final BigDecimal heightFinal = new BigDecimal("480").setScale(20);
//        final BigDecimal bigDecimalPercentage = new BigDecimal("100").setScale(20);
//        BigDecimal i = bigDecimalPercentage.divide(heightFinal).setScale(20);
//        BigDecimal valueFixedOfPercentage = new BigDecimal(value).divide(i);
//        BigDecimal multiply = (valueFixedOfPercentage.divide(bigDecimalPercentage).setScale(20)).multiply(new BigDecimal(valueActualMonitor).setScale(20)).setScale(20);
//        return multiply.intValue();
//    }


    /**
     * Este método com base no valor recebido e na largura da tela retorna um valor equivalente para qualquer monitor.
     *
     * @Author gamerYToffi
     */
    public static int getEquivalentValueOfscreenWidth(int value, int width) {
        double ratio = (double) value / width;
        int equivalentValue = (int) (width * ratio);
        return equivalentValue;
    }

    /**
     * Este método envia uma mensagem apenas para o player que lançou a própria mensagem, util quando se quer enviar informações que apenas um player deve ver.
     *
     * @Author gamerYToffi
     */
    public static void sendMessageToPlayer(String message, EntityPlayer player) {
        player.sendMessage(new TextComponentString(message));
    }

    /**
     * Este método envia uma mensagem para todos os players.
     *
     * @Author gamerYToffi
     */
    public static void sendMessageToAllPlayers(String message) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(player -> player.sendMessage(new TextComponentString(message)));
    }

    /**
     * Este método envia verifica se os itemStack são iguais e suas tags, ele ignora a tag de contagem.
     *
     * @Author gamerYToffi
     **/
    public static boolean itemsAreEquals(ItemStack item1, ItemStack item2) {
        return ItemStack.areItemStackTagsEqual(item1, item2) && item1.isItemEqual(item2);
    }

    /**
     * Este método tenta compactar todos os itemStack que são iguais e que tenham as mesmas tags ignorando a durabilidade em um unico itemStack, ele modifica a lista que recebe.
     *
     * @Author gamerYToffi
     **/
    public static void compactListItemStacks(List<ItemStack> drops) {
        ItemStack prevStack = null;
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((ItemStack) o1).getUnlocalizedName().compareTo(((ItemStack) o2).getUnlocalizedName());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };
        drops.sort(comparator);
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int c = 0; c < drops.size(); c++) {
            ItemStack stack = drops.get(c);
            if (prevStack == null) {
                prevStack = stack;
                continue;
            }
            if (stack.isItemEqual(prevStack) && ItemStack.areItemStackTagsEqual(prevStack, stack)) {
                prevStack.setCount(prevStack.getCount() + stack.getCount());
                if (c == drops.size() - 1) {
                    itemStacks.add(prevStack);
                }
            } else {
                itemStacks.add(prevStack);
                prevStack = stack;
                if (c == drops.size() - 1) {
                    itemStacks.add(prevStack);
                }
            }
        }
        if (drops.size() == 1) {
            ItemStack e = drops.get(0);
            drops.clear();
            drops.add(e);
        } else {
            drops.clear();
            drops.addAll(itemStacks);
        }
    }

    /**
     * Este método verifica se a entidade é é uma instancia de EntityPlayer
     *
     * @Author gamerYToffi
     */
    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    /**
     * Este método modifica a distancia de interação dos blocos do player recebido
     *
     * @Author gamerYToffi
     */
    public static void modifyBlockReachDistance(EntityPlayerMP player, int distance) {
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(distance);
    }

    /**
     * Este método retorna o Arquivo PlayerData Do player do mundo atual, deve ser invocado apenas do lado do servidor lógico.
     *
     * @Author gamerYToffi
     */
    public static File getPlayerDataFileOfPlayer(@Nonnull UUID uuid) {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        String worldName = minecraftServer.getFolderName();
        File playerData = new File(System.getProperty("user.dir").concat("\\saves").concat("\\" + worldName).concat("\\playerdata").concat("\\" + uuid.toString() + ".dat"));
        return playerData;
    }


    /**
     * Este método lança a exception dada a ele caso o objeto passado seja null .
     *
     * @Author gamerYToffi
     */
    public static <T, X extends Throwable> void throwExceptionIfNull(T object, X e) throws X {
        if (object == null) throw e;
    }


    //Em testes
    private static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            final RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
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
            for (final String textLine : textLines) {
                final int textLineWidth = font.getStringWidth(textLine);
                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }
            boolean needsWrap = false;
            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) {
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
                final List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); ++i) {
                    final String textLine2 = textLines.get(i);
                    final List<String> wrappedLine = (List<String>) font.listFormattedStringToWidth(textLine2, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }
                    for (final String line : wrappedLine) {
                        final int lineWidth = font.getStringWidth(line);
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
                    tooltipHeight += 2;
                }
            }
            if (tooltipY < 4) {
                tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4;
            }
            final int zLevel = 300;
            int backgroundColor = -267386864;
            int borderColorStart = 1347420415;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | (borderColorStart & 0xFF000000);
            final RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, (List) textLines, tooltipX, tooltipY, (FontRenderer) font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post((Event) colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();
            drawGradientRect(300, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(300, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(300, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(300, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(300, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);
            MinecraftForge.EVENT_BUS.post((Event) new RenderTooltipEvent.PostBackground(stack, (List) textLines, tooltipX, tooltipY, (FontRenderer) font, tooltipTextWidth, tooltipHeight));
            final int tooltipTop = tooltipY;
            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                final String line2 = textLines.get(lineNumber);
                font.drawStringWithShadow(line2, (float) tooltipX, (float) tooltipY, -1);
                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }
                tooltipY += 10;
            }
            MinecraftForge.EVENT_BUS.post((Event) new RenderTooltipEvent.PostText(stack, (List) textLines, tooltipX, tooltipTop, (FontRenderer) font, tooltipTextWidth, tooltipHeight));
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
}
