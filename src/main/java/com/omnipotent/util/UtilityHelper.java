package com.omnipotent.util;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;
import static net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect;

public class UtilityHelper {

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

    public static boolean setBlockStateFast(World world, BlockPos pos, IBlockState newState, int flags) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (world.isOutsideBuildHeight(pos)) {
            return false;
        } else if (!world.isRemote && world.getWorldInfo().getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        } else {
            Chunk chunk = world.getChunkFromBlockCoords(pos);

            pos = pos.toImmutable(); // Forge - prevent mutable BlockPos leaks
            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
            if (world.captureBlockSnapshots && !world.isRemote) {
                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, pos, flags);
                world.capturedBlockSnapshots.add(blockSnapshot);
            }
            IBlockState oldState = world.getBlockState(pos);
//            int oldLight = oldState.getLightValue(world, pos);
//            int oldOpacity = oldState.getLightOpacity(world, pos);

            IBlockState iblockstate = setBlockStateChuckFast(chunk, pos, newState);

            if (iblockstate == null) {
                if (blockSnapshot != null) world.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            } else {
//                if (newState.getLightOpacity(world, pos) != oldOpacity || newState.getLightValue(world, pos) != oldLight) {
//                    world.profiler.startSection("checkLight");
//                    world.checkLight(pos);
//                    world.profiler.endSection();
//                }

                if (blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    world.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
                }
                return true;
            }
        }
    }

    private static final Class<Chunk> chunkClass = Chunk.class;

    @Nullable
    public static IBlockState setBlockStateChuckFast(Chunk chunk, BlockPos pos, IBlockState state) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        int l = k << 4 | i;
        Field field = chunkClass.getDeclaredField("precipitationHeightMap");
        field.setAccessible(true);
        final int[] precipitationHeightMap = (int[]) field.get(chunk);

        if (j >= precipitationHeightMap[l] - 1) {
            precipitationHeightMap[l] = -999;
        }

        Field field2 = chunkClass.getDeclaredField("heightMap");
        field2.setAccessible(true);
        final int[] heightMap = (int[]) field2.get(chunk);
        int i1 = heightMap[l];
        IBlockState iblockstate = chunk.getBlockState(pos);

        if (iblockstate == state) {
            return null;
        } else {
            Block block = state.getBlock();
            Block block1 = iblockstate.getBlock();
            ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[j >> 4];
            boolean flag = false;

            if (extendedblockstorage == NULL_BLOCK_STORAGE) {
                if (block == Blocks.AIR) {
                    return null;
                }

                extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, chunk.getWorld().provider.hasSkyLight());
                chunk.getBlockStorageArray()[j >> 4] = extendedblockstorage;
                flag = j >= i1;
            }

            extendedblockstorage.set(i, j & 15, k, state);

            //if (block1 != block)
            {
                if (!chunk.getWorld().isRemote) {
                    if (block1 != block) //Only fire block breaks when the block changes.
                        block1.breakBlock(chunk.getWorld(), pos, iblockstate);
                    TileEntity te = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (te != null && te.shouldRefresh(chunk.getWorld(), pos, iblockstate, state))
                        chunk.getWorld().removeTileEntity(pos);
                } else if (block1.hasTileEntity(iblockstate)) {
                    TileEntity te = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (te != null && te.shouldRefresh(chunk.getWorld(), pos, iblockstate, state))
                        chunk.getWorld().removeTileEntity(pos);
                }
            }

            if (extendedblockstorage.get(i, j & 15, k).getBlock() != block) {
                return null;
            } else {
//                if (flag) {
//                    chunk.generateSkylightMap();
//                } else {
//                    int j1 = state.getLightOpacity(chunk.getWorld(), pos);

//                    if (j1 > 0) {
//                        if (j >= i1) {
//                            Method relightBlock = chunkClass.getMethod("relightBlock", int.class, int.class, int.class);
//                            relightBlock.setAccessible(true);
//                            relightBlock.invoke(chunk, i, j + 1, k);
//                            chunk.relightBlock(i, j + 1, k);
//                        }
//                    } else if (j == i1 - 1) {
//                        Method relightBlock = chunkClass.getMethod("relightBlock", int.class, int.class, int.class);
//                        relightBlock.setAccessible(true);
//                        relightBlock.invoke(chunk, i, j, k);
//                        chunk.relightBlock(i, j, k);
//                    }

//                    if (j1 != k1 && (j1 < k1 || chunk.getLightFor(EnumSkyBlock.SKY, pos) > 0 || chunk.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
//                        Method propagateSkylightOcclusion = chunkClass.getMethod("propagateSkylightOcclusion", int.class, int.class);
//                        propagateSkylightOcclusion.setAccessible(true);
//                        propagateSkylightOcclusion.invoke(chunk, i, k);
////                        propagateSkylightOcclusion(i, k);
//                    }
            }

            // If capturing blocks, only run block physics for TE's. Non-TE's are handled in ForgeHooks.onPlaceItemIntoWorld
            if (!chunk.getWorld().isRemote && block1 != block && (!chunk.getWorld().captureBlockSnapshots || block.hasTileEntity(state))) {
                block.onBlockAdded(chunk.getWorld(), pos, state);
            }

            if (block.hasTileEntity(state)) {
                TileEntity tileentity1 = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

                if (tileentity1 == null) {
                    tileentity1 = block.createTileEntity(chunk.getWorld(), state);
                    chunk.getWorld().setTileEntity(pos, tileentity1);
                }

                if (tileentity1 != null) {
                    tileentity1.updateContainingBlockInfo();
                }
            }

            chunk.setModified(true);
            return iblockstate;
        }
    }
}
