package com.omnipotent.util;

import com.google.common.collect.Lists;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.omnipotent.util.KaiaConstantsNbt.*;

public class KaiaUtil {
    public static List<Class> antiEntity = new ArrayList();

    public static boolean hasInInventoryKaia(Entity entity) {
        if (!isPlayer(entity)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof Kaia)
            return true;
        for (ItemStack slot : player.inventory.mainInventory) {
            if (slot.getItem() instanceof Kaia) {
                return true;
            }
        }
        return false;
    }

    public static void killArea(EntityLivingBase player) {
        EntityPlayer playerSource = (EntityPlayer) player;
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
        entities.removeIf(entity -> isPlayer(entity) && hasInInventoryKaia((EntityPlayer) entity));
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
        if (!killFriendEntities) {
            entities.removeIf(entity -> entity instanceof EntityBat || entity instanceof EntitySquid || entity instanceof EntityAgeable || entity instanceof EntityAnimal || entity instanceof EntitySnowman || entity instanceof EntityGolem);
        }
        for (Entity entity : entities) {
            kill(entity, playerSource, killAllEntities);
        }
    }

    public static void kill(Entity entity, EntityPlayer playerSource, boolean killAllEntities) {
        boolean attackYourWolf = getKaiaInInventory(playerSource).getTagCompound().getBoolean(KaiaConstantsNbt.attackYourWolf);
        if (!attackYourWolf) {
            if (entity instanceof EntityWolf && ((EntityWolf) entity).isOwner(playerSource))
                return;
        }
        ItemStack kaia = getKaiaInMainHand(playerSource) == null ? getKaiaInInventory(playerSource) : getKaiaInMainHand(playerSource);
        boolean autoBackpackEntities = kaia.getTagCompound().getBoolean(autoBackPackEntities);
        if (entity instanceof EntityPlayer && !hasInInventoryKaia(entity)) {
            EntityPlayer playerEnemie = (EntityPlayer) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            if (!playerEnemie.isDead) {
                playerEnemie.attemptTeleport(0, -1000, 0);
                dropAllInventory((EntityPlayer) playerEnemie);
                playerEnemie.onUpdate();
                ((EntityPlayer) playerEnemie).onLivingUpdate();
                playerEnemie.onEntityUpdate();
            }
            playerEnemie.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            playerEnemie.setHealth(0.0F);
            playerEnemie.attackEntityFrom(ds, Float.MAX_VALUE);
            playerEnemie.onDeath(ds);

        } else if (entity instanceof EntityLivingBase && !(entity.world.isRemote || entity.isDead || ((EntityLivingBase) entity).getHealth() == 0.0F)) {
            EntityLivingBase entityCreature = (EntityLivingBase) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            entityCreature.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            int enchantmentFire = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, getKaiaInInventory(playerSource));
            if (enchantmentFire != 0) {
                entityCreature.setFire(Integer.MAX_VALUE / 25);
            }
            antiEntity.add(antiEntity.getClass());
            if (autoBackpackEntities) {
                entityCreature.captureDrops = false;
                entityCreature.captureDropsAbsolute = true;
                entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
                entityCreature.onDeath(ds);
                ArrayList<EntityItem> capturedDrops = entityCreature.capturedDrops;
                NonNullList<ItemStack> drops = NonNullList.create();
                capturedDrops.forEach(entityItem -> drops.add(entityItem.getItem()));
                compactListItemStacks(drops);
                addedItemsStacksInKaiaInventory(playerSource, drops, kaia);
            } else {
                entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
                entityCreature.onDeath(ds);
            }
            antiEntity.remove(antiEntity.getClass());
            entityCreature.setHealth(0.0F);
        } else if (killAllEntities) {
            entity.setDead();
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
        compactListItemStacks(drops);
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

    public static void dropKaiaOfInventory(ItemStack stack, EntityPlayer player) {
        player.dropItem(stack, false);
        player.inventory.deleteStack(stack);
    }

    public static void createTagCompoundStatusIfNecessary(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            NBTTagCompound status = new NBTTagCompound();
            stack.setTagCompound(status);
        }
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

    public static void dropAllInventory(EntityPlayer player) {
        for (ItemStack item : player.inventory.mainInventory) {
            player.dropItem(true);
        }
    }

    public static boolean theLastAttackIsKaia(EntityPlayer player) {
        return player.getLastDamageSource() != null && player.getLastDamageSource().damageType.equals(new AbsoluteOfCreatorDamage(player).getDamageType());
    }

    public static void clearPlayer(EntityPlayer player) {
        IInventory playerInventory = player.inventory;
        for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
            ItemStack itemStack = playerInventory.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                playerInventory.removeStackFromSlot(i);
            }
        }
    }

    public static void returnKaiaOfOwner(EntityPlayer player) {
        List<ItemStack> kaiaList = player.getCapability(KaiaProvider.KaiaBrand, null).getAndExcludeAllKaiaInList();
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

    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static void modifyBlockReachDistance(EntityPlayerMP player, int distance) {
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(distance);
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(distance);
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
                if (playerLanguage.equals("pt_br")) {
                    player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA NAO PODE SER MORTA"));
                } else {
                    player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA CANNOT BE KILLED"));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA CANNOT BE KILLED"));
            }
        }
    }
}
