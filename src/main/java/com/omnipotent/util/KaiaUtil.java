package com.omnipotent.util;

import com.google.common.collect.Lists;
import com.omnipotent.Config;
import com.omnipotent.client.gui.KaiaPlayerGui;
import com.omnipotent.server.capability.KaiaProvider;
import com.omnipotent.server.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.server.specialgui.IContainer;
import com.omnipotent.server.specialgui.InventoryKaia;
import com.omnipotent.server.tool.Kaia;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.util.KaiaConstantsNbt.*;

public class KaiaUtil {
    public static List<Class> antiEntity = new ArrayList<>();

    public static boolean hasInInventoryKaia(Entity entity) {
        if (!UtilityHelper.isPlayer(entity)) {
            return false;
        }
        try {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof Kaia)
                return true;
            for (ItemStack slot : player.inventory.mainInventory) {
                if (slot.getItem() instanceof Kaia) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void killArea(EntityPlayer playerSource) {
        World world = playerSource.world;
        List<Entity> entities = Lists.newArrayList();
        NBTTagCompound tagCompoundOfKaia = (getKaiaInMainHand(playerSource) == null ? getKaiaInInventory(playerSource) : getKaiaInMainHand(playerSource)).getTagCompound();
        int range = tagCompoundOfKaia.getInteger(rangeAttack);
        boolean killAllEntities = tagCompoundOfKaia.getBoolean(KaiaConstantsNbt.killAllEntities);
        boolean killFriendEntities = tagCompoundOfKaia.getBoolean(KaiaConstantsNbt.killFriendEntities);
        double slope = 0.1;
        for (int dist = 0; dist <= range; dist += 2) {
            AxisAlignedBB bb = playerSource.getEntityBoundingBox();
            Vec3d vec = playerSource.getLookVec();
            vec = vec.normalize();
            bb = bb.grow(slope * dist + 2.0, slope * dist + 0.25, slope * dist + 2.0);
            bb = bb.offset(vec.x * dist, vec.y * dist, vec.z * dist);
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bb);
            entities.addAll(list);
        }
        entities.removeIf(entity -> UtilityHelper.isPlayer(entity) && hasInInventoryKaia((EntityPlayer) entity));
        List<String> idEntitiesList = new ArrayList<>();
        Iterator<Entity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            Entity nextEntity = entityIterator.next();
            if (idEntitiesList != null) {
                if (idEntitiesList.contains(nextEntity.getUniqueID().toString())) {
                    entityIterator.remove();
                } else {
                    idEntitiesList.add(nextEntity.getUniqueID().toString());
                }
            } else {
                idEntitiesList.add(nextEntity.getUniqueID().toString());
            }
        }
        Iterator<NBTBase> iterator = tagCompoundOfKaia.getTagList(playersDontKill, 8).iterator();
        while (iterator.hasNext()) {
            String string = iterator.next().toString();
            if (string.startsWith("\"") && string.endsWith("\""))
                string = string.substring(1, string.length() - 1);
            String playerCantKill = string.split(KaiaPlayerGui.divisionUUIDAndNameOfPlayer)[0];
            entities.removeIf(entity -> entity.getUniqueID().toString().equals(playerCantKill));
        }
        if (!killFriendEntities) {
            entities.removeIf(entity -> entity instanceof EntityBat || entity instanceof EntitySquid || entity instanceof EntityAgeable || entity instanceof EntityAnimal || entity instanceof EntitySnowman || entity instanceof EntityGolem);
        }
        for (Entity entity : entities) {
            kill(entity, playerSource, killAllEntities);
        }
    }

    public static void kill(Entity entity, EntityPlayer playerSource, boolean killAllEntities) {
        ItemStack kaia = getKaiaInMainHand(playerSource) == null ? getKaiaInInventory(playerSource) : getKaiaInMainHand(playerSource);
        if (!kaia.getTagCompound().getBoolean(KaiaConstantsNbt.attackYourWolf))
            if (entity instanceof EntityWolf && ((EntityWolf) entity).isOwner(playerSource))
                return;
        boolean autoBackpackEntities = kaia.getTagCompound().getBoolean(autoBackPackEntities);
        if (UtilityHelper.isPlayer(entity) && !hasInInventoryKaia(entity)) {
            EntityPlayer playerEnemie = (EntityPlayer) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            playerEnemie.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            playerEnemie.setHealth(0.0F);
            playerEnemie.attackEntityFrom(ds, Float.MAX_VALUE);
            playerEnemie.onDeath(ds);
            if (kaia.getTagCompound().getBoolean(playersCantRespawn))
                Config.addPlayerInListThatCantRespawn(playerEnemie);
        } else if (entity instanceof EntityLivingBase && !(entity.world.isRemote || entity.isDead || ((EntityLivingBase) entity).getHealth() == 0.0F)) {
            EntityLivingBase entityCreature = (EntityLivingBase) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            entityCreature.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            int enchantmentFire = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, getKaiaInInventory(playerSource));
            if (enchantmentFire != 0) entityCreature.setFire(Integer.MAX_VALUE / 25);
            antiEntity.add(entityCreature.getClass());
            if (autoBackpackEntities) {
                entityCreature.captureDrops = false;
                entityCreature.captureDropsAbsolute = true;
                entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
                entityCreature.onDeath(ds);
                ArrayList<EntityItem> capturedDrops = entityCreature.capturedDrops;
                NonNullList<ItemStack> drops = NonNullList.create();
                capturedDrops.forEach(entityItem -> drops.add(entityItem.getItem()));
                UtilityHelper.compactListItemStacks(drops);
                addedItemsStacksInKaiaInventory(playerSource, drops, kaia);
            } else {
                entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
                entityCreature.onDeath(ds);
            }
            antiEntity.remove(entityCreature.getClass());
            entityCreature.setHealth(0.0F);
        } else if (killAllEntities) {
            entity.setDead();
            playerSource.world.onEntityRemoved(entity);
        }

    }

    public static boolean withKaiaMainHand(EntityPlayer trueSource) {
        return trueSource.getHeldItemMainhand().getItem() instanceof Kaia;
    }

    public static void decideBreakBlock(EntityPlayerMP player, BlockPos pos) {
        if (getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea) > 1) {
            int areaBlock = getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea);
            if (!player.world.isRemote && !player.capabilities.isCreativeMode && withKaiaMainHand(player)) {
                if (areaBlock % 2 != 0) {
                    breakBlocksInArea(areaBlock, player, pos);
                }
            }
        } else {
            player.world.spawnEntity(new EntityXPOrb(player.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (int) breakBlockIfDropsIsEmpty(player, pos)));
        }
    }

    public static void breakBlocksInArea(int areaBlock, EntityPlayer player, BlockPos centerPos) {
        World world = player.world;
        int startX = centerPos.getX() - areaBlock / 2;
        int endX = centerPos.getX() + areaBlock / 2;
        int startZ = centerPos.getZ() - areaBlock / 2;
        int endZ = centerPos.getZ() + areaBlock / 2;
        int startY = centerPos.getY() - areaBlock / 2;
        int endY = centerPos.getY() + areaBlock / 2;
        float xp = 0f;
        xp += breakBlockIfDropsIsEmpty((EntityPlayerMP) player, centerPos);
        if (getKaiaInMainHand(player).getTagCompound().getBoolean(noBreakTileEntity)) {
            if (checkTheAreaForTileEntityBlock(startX, startY, startZ, endX, endY, endZ, player.world)) {
                world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, (int) xp));
                return;
            }
        }
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (!world.isAirBlock(blockPos)) {
                        xp += breakBlockIfDropsIsEmpty((EntityPlayerMP) player, blockPos);
                    }
                }
            }
        }
        world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, (int) xp));
    }

    private static boolean checkTheAreaForTileEntityBlock(int startX, int startY, int startZ, int endX, int endY, int endZ, World world) {
        BlockPos startBlockPos = new BlockPos(startX, startY, startZ);
        for (BlockPos blockPos : BlockPos.getAllInBox(startBlockPos, new BlockPos(endX, endY, endZ))) {
            if (world.getBlockState(blockPos).getBlock().hasTileEntity(world.getBlockState(blockPos))) return true;
        }
        return false;
    }

    public static float breakBlockIfDropsIsEmpty(EntityPlayerMP player, BlockPos pos) {
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        NonNullList<ItemStack> drops = NonNullList.create();
        ItemStack kaiaInMainHand = getKaiaInMainHand(player);
        int enchLevelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, kaiaInMainHand);
        Float xp = 0f;
        block.getDrops(drops, player.world, pos, state, enchLevelFortune);
        drops.removeIf(item -> item.getItem() instanceof ItemAir);
        if (drops.isEmpty()) {
            drops.add(block.getPickBlock(state, player.rayTrace(0.0f, 0.0f), player.world, pos, player));
        }
        UtilityHelper.compactListItemStacks(drops);
        if (kaiaInMainHand.getTagCompound().getBoolean(autoBackPack)) {
            addedItemsStacksInKaiaInventory(player, drops, kaiaInMainHand);
        } else {
            drops.forEach(dropStack -> player.world.spawnEntity(new EntityItem(player.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack)));
        }
        xp += block.getExpDrop(state, player.world, pos, enchLevelFortune);
        //linha comentada devida a lentidão dela talvez volte no futuro.
//        player.addStat(StatList.getBlockStats(block));
        player.world.destroyBlock(pos, false);
        return xp;
    }

    private static void addedItemsStacksInKaiaInventory(EntityPlayer playerOwnerOfKaia, NonNullList<ItemStack> drops, ItemStack kaiaItemStack) {
        InventoryKaia inventory = ((IContainer) kaiaItemStack.getItem()).getInventory(kaiaItemStack);
        for (ItemStack dropStack : drops) {
            boolean breakMainLoop = false;
            for (int currentPage = 0; currentPage < inventory.getMaxPage(); currentPage++) {
                if (breakMainLoop) {
                    break;
                }
                inventory.openInventory(playerOwnerOfKaia);
                NonNullList<ItemStack> currentPageItems = inventory.getPage(currentPage);
                for (int currentSlot = 0; currentSlot < currentPageItems.size(); currentSlot++) {
                    if (breakMainLoop) {
                        break;
                    }
                    ItemStack stackInCurrentSlot = currentPageItems.get(currentSlot);
                    if (stackInCurrentSlot == null || stackInCurrentSlot.isEmpty()) {
                        currentPageItems.set(currentSlot, dropStack);
                        breakMainLoop = true;
                        break;
                    } else {
                        int maxCountSlot = inventory.cancelStackLimit() ? inventory.getInventoryStackLimit() : Math.min(inventory.getInventoryStackLimit(), dropStack.getMaxStackSize());
                        int countSlotFree = Math.min(maxCountSlot - stackInCurrentSlot.getCount(), dropStack.getCount());
                        if (countSlotFree > 0 && stackInCurrentSlot.isItemEqual(dropStack) && ItemStack.areItemStackTagsEqual(stackInCurrentSlot, dropStack)) {
                            stackInCurrentSlot.grow(countSlotFree);
                            dropStack.shrink(countSlotFree);
                            if (dropStack.isEmpty()) {
                                breakMainLoop = true;
                                break;
                            }
                        }
                    }
                }
                inventory.closeInventory(playerOwnerOfKaia);
            }
        }
    }

    public static void createTagCompoundStatusIfNecessary(ItemStack stack) {
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
    }

    public static void createOwnerIfNecessary(ItemStack stack, Entity entityIn) {
        if (!stack.getTagCompound().hasKey(ownerName)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setString(ownerName, entityIn.getName());
        }
        if (!stack.getTagCompound().hasKey(ownerID)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setString(ownerID, entityIn.getUniqueID().toString());
        }
    }

    public static boolean theLastAttackOfKaia(EntityLivingBase entity) {
        return entity.getLastDamageSource() != null && entity.getLastDamageSource().getTrueSource() != null && entity.getLastDamageSource().getDamageType().equals(new AbsoluteOfCreatorDamage(entity).getDamageType());
    }

    /**
     * Este método retorna a entidade responsavel pelo dano de AbsoluteOfCreator.
     * Retorna null caso o ultimo dano na entidade recebiada não seja do tipo AbsoluteOfCreator, sua verdadeira fonte de dano seja null ou não seja uma instancia de entityPlayer
     * e caso o ultimo dano seja null
     *
     * @Author gamerYToffi
     */
    public static Entity ReturnDamageSourceByKaia(EntityLivingBase entity) {
        DamageSource source = entity.getLastDamageSource();
        Entity trueSource = source.getTrueSource();
        if (source != null && trueSource != null && UtilityHelper.isPlayer(trueSource)) {
            return source.getDamageType().equals(new AbsoluteOfCreatorDamage(trueSource).getDamageType()) ? trueSource : null;
        } else
            return null;
    }

    public static void returnKaiaOfOwner(EntityPlayer player) {
        List<ItemStack> kaiaList = player.getCapability(KaiaProvider.KaiaBrand, null).getAndExcludeAllKaiaInList();
        kaiaList = kaiaList.stream().filter(item -> item.getItem() instanceof Kaia).collect(Collectors.toList());
        for (ItemStack kaia : kaiaList) {
            if (isOwnerOfKaia(kaia, player)) {
                if (!(player.inventory.addItemStackToInventory(kaia))) {
                    if (player.inventory.offHandInventory.get(0).isEmpty())
                        player.inventory.offHandInventory.set(0, kaia);
                    else {
                        for (int index = 0; index < player.inventory.mainInventory.size(); index++) {
                            ItemStack itemStack = player.inventory.mainInventory.get(index);
                            if (!itemStack.isEmpty() && !(itemStack.getItem() instanceof Kaia)) {
                                player.world.spawnEntity(new EntityItem(player.world, player.posX + 20, player.posY, player.posZ, player.inventory.mainInventory.get(index)));
                                player.sendMessage(new TextComponentString(I18n.format("kaia.message.returndropitems") + " X: " + ((int) player.posX + 20) + "Y: " + ((int) player.posY) + "Z: " + ((int) player.posZ)));
                                player.inventory.mainInventory.set(index, kaia.copy());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static ItemStack getKaiaInMainHand(EntityPlayer player) {
        return withKaiaMainHand(player) ? player.getHeldItemMainhand() : null;
    }

    public static ItemStack getKaiaInInventory(EntityPlayer player) throws RuntimeException {
        if (!player.inventory.offHandInventory.isEmpty() && player.inventory.offHandInventory.get(0).getItem() instanceof Kaia)
            return player.inventory.offHandInventory.get(0);
        if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof Kaia)
            return player.getHeldItemMainhand();
        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof Kaia)
                return itemStack;
        }
        throw new RuntimeException("without Kaia in Inventory");
    }

    public static boolean isOwnerOfKaia(ItemStack kaiaStack, EntityPlayer player) {
        return kaiaStack.getTagCompound().getString(ownerName).equals(player.getName()) && kaiaStack.getTagCompound().getString(ownerID).equals(player.getUniqueID().toString());
    }

    public static boolean checkIfKaiaCanKillPlayerOwnedWolf(Entity entity, EntityPlayer player) {
        boolean attackYourWolf = KaiaUtil.getKaiaInMainHand(player).getTagCompound().getBoolean(KaiaConstantsNbt.attackYourWolf);
        if (!attackYourWolf) {
            if (entity instanceof EntityWolf) {
                EntityWolf wolf = (EntityWolf) entity;
                return wolf.isOwner(player);
            }
        }
        return false;
    }

    public static void kaiaKillCommandMessage() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        for (EntityPlayerMP player : players) {
            try {
                Field languageField = EntityPlayerMP.class.getDeclaredField("language");
                languageField.setAccessible(true);
                String playerLanguage = (String) languageField.get(player);
                if (playerLanguage.equals("pt_br"))
                    player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA NAO PODE SER MORTA"));
                else player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA CANNOT BE KILLED"));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA CANNOT BE KILLED"));
            }
        }
    }
}
