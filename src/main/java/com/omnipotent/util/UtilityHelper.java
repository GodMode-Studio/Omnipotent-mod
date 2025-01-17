package com.omnipotent.util;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.omnipotent.common.capability.kaiacap.IKaiaBrand;
import com.omnipotent.common.capability.kaiacap.KaiaProvider;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.ValidationPacket;
import com.omnipotent.util.player.PlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;

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
        Map<ItemStackKey, ItemStack> compactedStacks = new HashMap<>();
        for (ItemStack stack : drops) {
            if (stack.isEmpty()) continue;
            ItemStackKey key = new ItemStackKey(stack);
            if (compactedStacks.containsKey(key)) {
                ItemStack existingStack = compactedStacks.get(key);
                existingStack.grow(stack.getCount());
            } else {
                compactedStacks.put(key, stack.copy());
            }
        }
        drops.clear();
        drops.addAll(compactedStacks.values());
    }

    public static void generateAndSendChallenge(EntityPlayerMP playerTarget, String type) {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        String challenge = Base64.getEncoder().encodeToString(randomBytes);
        PlayerData.addPlayer(playerTarget).addChallenge(challenge);
        NetworkRegister.sendMessageToPlayer(new ValidationPacket(type, challenge), playerTarget);
    }

    public static EntityPlayerMP getPlayerByName(String args) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args);
    }

    public static boolean inLogicServerSide(World world) {
        return !world.isRemote;
    }

    private static class ItemStackKey {
        private final Item item;
        private final NBTTagCompound tag;

        public ItemStackKey(ItemStack stack) {
            this.item = stack.getItem();
            this.tag = stack.getTagCompound();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackKey that = (ItemStackKey) o;
            return item.equals(that.item) && Objects.equals(tag, that.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, tag);
        }
    }

    /**
     * Este método verifica se a entidade é é uma instancia de EntityPlayer
     *
     * @Author gamerYToffi
     */
    public static boolean isPlayer(@Nullable Entity entity) {
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
    public static Optional<File> getPlayerDataFileOfPlayer(@Nonnull UUID uuid) {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        String worldName = minecraftServer.getFolderName();
        Path playerDataPath = Paths.get(System.getProperty("user.dir"), "saves", worldName, "playerdata", uuid + ".dat");
        if (Files.exists(playerDataPath)) {
            return Optional.of(playerDataPath.toFile());
        }
        return Optional.empty();
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
            Chunk chunk = world.getChunk(pos);

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

    @SideOnly(Side.CLIENT)
    public static RayTraceResult rayTraceClient(Minecraft mc, Class<? extends Entity> excludeClass, int distance, float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            mc.profiler.startSection("pick");
            RayTraceResult originalObjectMouseOver = entity.rayTrace(distance, partialTicks);
            Vec3d vec3d = entity.getPositionEyes(partialTicks);
            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
            Entity pointedEntity = null;
            Vec3d vec3d3 = null;
            List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                public boolean apply(@Nullable Entity entity22) {
                    if (excludeClass != null)
                        return (entity22 != null && entity22.canBeCollidedWith()) || excludeClass.isInstance(entity22);
                    else
                        return entity22 != null && entity22.canBeCollidedWith();
                }
            }));
            double d2 = distance;
            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = list.get(j);
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double) entity1.getCollisionBorderSize());
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                if (axisalignedbb.contains(vec3d)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                        d2 = 0.0D;
                    }
                } else if (raytraceresult != null) {
                    double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                            }
                        } else {
                            pointedEntity = entity1;
                            vec3d3 = raytraceresult.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }
            if (pointedEntity != null && vec3d.distanceTo(vec3d3) > distance) {
                pointedEntity = null;
                originalObjectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing) null, new BlockPos(vec3d3));
            }
            if (pointedEntity != null && (d2 < distance || originalObjectMouseOver == null)) {
                originalObjectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
            }

            mc.profiler.endSection();

            return originalObjectMouseOver;
        }
        return null;
    }

    public static Optional<IKaiaBrand> getKaiaCap(EntityPlayer entityPlayer) {
        return Optional.ofNullable(entityPlayer.getCapability(KaiaProvider.KaiaBrand, null));
    }

    public static boolean isMinecraftOrOmnipotentClass(String canonicalName) {
        return canonicalName.startsWith("net.minecraft") || canonicalName.startsWith("net.minecraftforge")
                || canonicalName.startsWith("com.omnipotent");
    }

    public static boolean injectMixinIsCallerMinecraftOrForgeClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 4)
            return isMinecraftOrOmnipotentClass(stackTrace[4].getClassName());
        return true;
    }

    public static boolean isCallerMinecraftOrForgeClassForEvents() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 4)
            return isMinecraftOrOmnipotentClass(stackTrace[4].getClassName());
        return true;
    }

    public static boolean inLogicSide() {
        FMLCommonHandler instance = FMLCommonHandler.instance();
        MinecraftServer server = instance.getMinecraftServerInstance();
        if (server == null) return false;
        return server.isDedicatedServer() || instance.getEffectiveSide() == Side.SERVER;
    }

    private static final TextFormatting RAINBOW[] = new TextFormatting[]{TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.AQUA, TextFormatting.BLUE, TextFormatting.LIGHT_PURPLE, TextFormatting.RED, TextFormatting.GOLD, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.AQUA, TextFormatting.LIGHT_PURPLE, TextFormatting.RED, TextFormatting.GOLD};
    private static final TextFormatting SPACIAL[] = new TextFormatting[]{TextFormatting.WHITE, TextFormatting.AQUA, TextFormatting.BLUE, TextFormatting.WHITE, TextFormatting.AQUA, TextFormatting.BLUE};

    public static String coloringRainbow(String toColor, double delay) {
        StringBuilder sb = new StringBuilder(toColor.length() * 3);
        delay = delay == 0 ? 0.001 : delay;
        int offset = (int) Math.floor((System.currentTimeMillis() & 16383L) / delay) % RAINBOW.length;
        for (int i = 0; i < toColor.length(); ++i) {
            char c = toColor.charAt(i);
            int col = (i + RAINBOW.length - offset) % RAINBOW.length;
            sb.append(RAINBOW[col]);
            sb.append(c);
        }
        return sb.toString();
    }

    public static String coloringSpacial(String toColor, double delay) {
        StringBuilder sb = new StringBuilder(toColor.length() * 3);
        delay = delay == 0 ? 0.001 : delay;
        int offset = (int) Math.floor((System.currentTimeMillis() & 16383L) / delay) % SPACIAL.length;
        for (int i = 0; i < toColor.length(); ++i) {
            char c = toColor.charAt(i);
            int col = (i + SPACIAL.length - offset) % SPACIAL.length;
            sb.append(SPACIAL[col]);
            sb.append(c);
        }
        return sb.toString();
    }

    @Nullable
    public static RayTraceResult rayTraceBlocks(World world, double distance, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = distance != -1 ? (int) Math.max(200, Math.min(distance + 10, 1500)) : 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);

                            if (raytraceresult1 != null) {
                                return raytraceresult1;
                            }
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
