package com.omnipotent.util;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.achievements.Achievements;
import com.brandon3055.draconicevolution.handlers.DEEventHandler;
import com.google.common.collect.Lists;
import com.omnipotent.Config;
import com.omnipotent.acessor.IEntityLivingBaseAcessor;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.server.capability.AntiEntityProvider;
import com.omnipotent.server.capability.IAntiEntitySpawn;
import com.omnipotent.server.capability.KaiaProvider;
import com.omnipotent.server.capability.UnbanEntitiesProvider;
import com.omnipotent.server.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.server.entity.CustomLightningBolt;
import com.omnipotent.server.mixin.mods.IMixinDEEventHandler;
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
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import static com.omnipotent.constant.NbtBooleanValues.*;
import static com.omnipotent.constant.NbtNumberValues.*;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.NbtListUtil.getUUIDOfNbtList;

public class KaiaUtil {
    public static List<Class> antiEntity = new ArrayList<>();


    //invoke apenas do lado do servidor ou no proprio cliente em si
    public static boolean hasInInventoryKaia(Entity entity) {
        if (!UtilityHelper.isPlayer(entity))
            return false;
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

    public static void killInAreaConstantly(EntityPlayer playerSource) {
        World world = playerSource.getEntityWorld();
        NBTTagCompound tagCompoundOfKaia = (getKaiaInMainHand(playerSource) == null ? getKaiaInInventory(playerSource) : getKaiaInMainHand(playerSource)).getTagCompound();
        if (tagCompoundOfKaia == null) return;
        boolean autoKill = tagCompoundOfKaia.getBoolean(NbtBooleanValues.autoKill.getValue());
        if (!autoKill)
            return;
        BlockPos position = playerSource.getPosition();
        boolean killAllEntities = tagCompoundOfKaia.getBoolean(NbtBooleanValues.killAllEntities.getValue());
        int rangeOfBlocks = tagCompoundOfKaia.getInteger(rangeAutoKill.getValue());
        List<Entity> entities = getEntitiesInArea(world, position, rangeOfBlocks);
        filterEntities(entities, tagCompoundOfKaia);
        for (Entity entity : entities) {
            killChoice(entity, playerSource, killAllEntities);
        }
    }

    public static List<Entity> getEntitiesInArea(World world, BlockPos position, int rangeOfBlocks) {
        int xNegative = position.getX() - rangeOfBlocks;
        int xPositive = position.getX() + rangeOfBlocks;
        int yNegative = position.getY() - rangeOfBlocks;
        int yPositive = position.getY() + rangeOfBlocks;
        int zNegative = position.getZ() - rangeOfBlocks;
        int zPositive = position.getZ() + rangeOfBlocks;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xNegative, yNegative, zNegative, xPositive, yPositive, zPositive);
        return world.getEntitiesWithinAABB(Entity.class, axisAlignedBB);
    }

    public static void killArea(EntityPlayer playerSource) {
        World world = playerSource.world;
        List<Entity> entities = Lists.newArrayList();
        NBTTagCompound tagCompoundOfKaia = (getKaiaInMainHand(playerSource) == null ? getKaiaInInventory(playerSource) : getKaiaInMainHand(playerSource)).getTagCompound();
        int range = tagCompoundOfKaia.getInteger(rangeAttack.getValue());
        boolean killAllEntities = tagCompoundOfKaia.getBoolean(NbtBooleanValues.killAllEntities.getValue());
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
//        List<String> idEntitiesList = new ArrayList<>();
//        Iterator<Entity> entityIterator = entities.iterator();
//        while (entityIterator.hasNext()) {
//            Entity nextEntity = entityIterator.next();
//            if (idEntitiesList != null) {
//                if (idEntitiesList.contains(nextEntity.getUniqueID().toString())) {
//                    entityIterator.remove();
//                } else {
//                    idEntitiesList.add(nextEntity.getUniqueID().toString());
//                }
//            } else {
//                idEntitiesList.add(nextEntity.getUniqueID().toString());
//            }
//        }
//        NBTTagList tagList = tagCompoundOfKaia.getTagList(entitiesCantKill, 8);
//        if (tagList.tagCount() > 0)
//            entities.removeIf(entity -> getUUIDOfNbtList(tagList).stream().filter(uuid -> uuid.equals(entity.getUniqueID().toString())).collect(Collectors.toList()).size() > 0);

        filterEntities(entities, tagCompoundOfKaia);
        for (Entity entity : entities) {
            killChoice(entity, playerSource, killAllEntities);
        }
    }

    private static void filterEntities(List<Entity> entities, NBTTagCompound tagCompoundOfKaia) {
        entities.removeIf(entity -> UtilityHelper.isPlayer(entity) && hasInInventoryKaia(entity));
        entities.removeIf(entity -> UtilityHelper.isPlayer(entity) && !entityIsPlayerAndKaiaCanKillPlayer(tagCompoundOfKaia, false, entity));
        entities.removeIf(entity -> entityIsFriendEntity(entity) && !entityFriendCanKilledByKaia(tagCompoundOfKaia, entity));
    }

    public static boolean killChoice(Entity entity, EntityPlayer playerSource, boolean killAllEntities) {
        boolean mobKilled = false;
        ItemStack kaia = getKaiaInMainHand(playerSource) == null ? getKaiaInInventory(playerSource) : getKaiaInMainHand(playerSource);
        NBTTagCompound tagCompound = kaia.getTagCompound();
        if (!tagCompound.getBoolean(NbtBooleanValues.attackYourWolf.getValue()))
            if (entity instanceof EntityWolf && ((EntityWolf) entity).isOwner(playerSource))
                return mobKilled;
        if (UtilityHelper.isPlayer(entity)) {
            if (!hasInInventoryKaia(entity)) {
                mobKilled = true;
                killPlayer((EntityPlayer) entity, playerSource, tagCompound);
            }
        } else if (entity instanceof EntityLivingBase && !(entity.world.isRemote || entity.isDead || ((EntityLivingBase) entity).getHealth() == 0.0F)) {
            mobKilled = true;
            killMobs((EntityLivingBase) entity, playerSource, kaia);
        } else if (killAllEntities) {
            mobKilled = true;
            entity.setDead();
            if (tagCompound.getBoolean(banEntitiesAttacked.getValue()))
                dennyEntitySpawnInWorld(playerSource.world, entity);
        }
        return mobKilled;
    }

    public static boolean checkIfKaiaCanKill(Entity entityTarget, EntityPlayer playerSource, boolean directAttack, boolean isCounterAttack) {
        if (playerSource == null)
            throw new RuntimeException("Player is null in checkKaia");
        ItemStack kaia = getKaiaInMainHand(playerSource) == null ? getKaiaInMainHand(playerSource) : getKaiaInInventory(playerSource);
        if (kaia == null)
            throw new RuntimeException("Use of method is incorrect, playerSource dont have Kaia");
        if (entityTarget == null || (UtilityHelper.isPlayer(entityTarget) && hasInInventoryKaia(entityTarget)))
            return false;
        NBTTagCompound tagCompound = kaia.getTagCompound();
        if (!EntityIsWolfAndKaiaCanKillPlayerOwnedWolf(tagCompound, entityTarget, playerSource))
            return false;
        if (entityIsFriendEntity(entityTarget)) {
            if (entityFriendCanKilledByKaia(tagCompound, entityTarget))
                return true;
            else
                return false;
        }
        if (entityNoIsNormalAndCanKilledByKaia(tagCompound, entityTarget))
            return true;
        if (UtilityHelper.isPlayer(entityTarget)) {
            if (entityIsPlayerAndKaiaCanKillPlayer(tagCompound, directAttack, entityTarget))
                return true;
            else
                return false;
        } else
            return true;
    }

    public static boolean entityFriendCanKilledByKaia(NBTTagCompound tagCompound, Entity entityTarget) {
        return tagCompound.getBoolean(killFriendEntities.getValue()) ? true : false;
    }

    public static boolean entityIsFriendEntity(Entity entityTarget) {
        return (entityTarget instanceof EntityBat || entityTarget instanceof EntitySquid || entityTarget instanceof EntityAgeable || entityTarget instanceof EntityGolem);
    }

    public static boolean entityNoIsNormalAndCanKilledByKaia(NBTTagCompound tagCompound, Entity entityTarget) {
        if (!tagCompound.getBoolean(killAllEntities.getValue()) || entityTarget instanceof EntityLivingBase)
            return false;
        return true;
    }

    public static boolean entityIsPlayerAndKaiaCanKillPlayer(NBTTagCompound tagCompound, boolean directAttack, Entity entityTarget) {
        if (!(entityTarget instanceof EntityPlayer))
            return false;
        if (getUUIDOfNbtList(tagCompound.getTagList(playersDontKill, 8)).contains(entityTarget.getUniqueID().toString())) {
            if (directAttack)
                if (tagCompound.getBoolean(playerDontKillInDirectAttack.getValue()))
                    return false;
                else
                    return true;
            return false;
        }
        return true;
    }

    public static boolean EntityIsWolfAndKaiaCanKillPlayerOwnedWolf(NBTTagCompound tagCompound, Entity entityTarget, EntityPlayer playerSource) {
        boolean kaiaCantKillYourWolf = !tagCompound.getBoolean(attackYourWolf.getValue());
        if (kaiaCantKillYourWolf)
            if (entityTarget instanceof EntityWolf && ((EntityWolf) entityTarget).isOwner(playerSource))
                return false;
        return true;
    }

    private static void killMobs(EntityLivingBase entity, EntityPlayer playerSource, ItemStack kaia) {
        if (kaia.getTagCompound().getBoolean(summonLightBoltsInKill.getValue()))
            generateLightBolts(playerSource);
        EntityLivingBase entityCreature = entity;
        ((IEntityLivingBaseAcessor) entityCreature).setRecentlyHit(60);
        DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
        entityCreature.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
        verifyFireEnchantmentAndExecute(playerSource, entityCreature);
        antiEntity.add(entityCreature.getClass());
        verifyAndManagerAutoBackEntitiesAndApplyDamage(entityCreature, ds, playerSource, kaia);
        antiEntity.remove(entityCreature.getClass());
        entityCreature.setHealth(0.0F);
        if (kaia.getTagCompound().getBoolean(banEntitiesAttacked.getValue()))
            dennyEntitySpawnInWorld(playerSource.world, entity);
    }

    private static void verifyFireEnchantmentAndExecute(EntityPlayer playerSource, EntityLivingBase entityCreature) {
        int enchantmentFire = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, getKaiaInInventory(playerSource));
        if (enchantmentFire != 0) entityCreature.setFire(Integer.MAX_VALUE / 25);
    }

    private static void killPlayer(EntityPlayer playerEnemie, EntityPlayer playerSource, NBTTagCompound tagCompound) {
        DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
        playerEnemie.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
        boolean activekeepInventory = false;
        GameRules gameRules = playerSource.getEntityWorld().getGameRules();
        String keepInventory = "keepInventory";
        if (tagCompound.getBoolean(ignoreKeepInventory.getValue())) {
            if (gameRules.getBoolean(keepInventory)) {
                gameRules.setOrCreateGameRule(keepInventory, "false");
                activekeepInventory = true;
            }
        }
        playerEnemie.setHealth(0.0F);
        playerEnemie.attackEntityFrom(ds, Float.MAX_VALUE);
        playerEnemie.onDeath(ds);
        if (activekeepInventory)
            gameRules.setOrCreateGameRule(keepInventory, "true");
        if (tagCompound.getBoolean(playersCantRespawn.getValue()))
            Config.addPlayerInListThatCantRespawn(playerEnemie);
    }

    private static void verifyAndManagerAutoBackEntitiesAndApplyDamage(EntityLivingBase entityCreature, DamageSource ds, EntityPlayer playerSource, ItemStack kaia) {
        NBTTagCompound tagCompound = kaia.getTagCompound();
        boolean autoBackpackEntities = tagCompound.getBoolean(autoBackPackEntities.getValue());
        ItemStack soulReaper = null;
        if (autoBackpackEntities) {
            entityCreature.captureDrops = false;
            entityCreature.captureDropsAbsolute = true;
            if (Loader.isModLoaded(DraconicEvolution.MODID))
                soulReaper = setReaperEnchant(kaia, playerSource, entityCreature);
            entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
            entityCreature.onDeath(ds);
            ArrayList<EntityItem> capturedDrops = entityCreature.capturedDrops;
            NonNullList<ItemStack> drops = NonNullList.create();
            capturedDrops.forEach(entityItem -> drops.add(entityItem.getItem()));
            if (soulReaper != null) drops.add(soulReaper);
            UtilityHelper.compactListItemStacks(drops);
            addedItemsStacksInKaiaInventory(playerSource, drops, kaia);
        } else {
            if (Loader.isModLoaded(DraconicEvolution.MODID)) {
                World world = playerSource.world;
                ItemStack stack = setReaperEnchant(kaia, playerSource, entityCreature);
                if (stack != null)
                    world.spawnEntity(new EntityItem(world, entityCreature.posX, entityCreature.posY, entityCreature.posZ, stack));
            }
            entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
            entityCreature.onDeath(ds);
        }
    }

    @Optional.Method(modid = DraconicEvolution.MODID)
    private static ItemStack setReaperEnchant(ItemStack kaia, EntityPlayer playerSource, EntityLivingBase entityCreature) {
        ItemStack soul = null;
        DEEventHandler deEventHandler = new DEEventHandler();
        int dropChanceOfReaperEnchantment = ((IMixinDEEventHandler) deEventHandler).callGetDropChanceFromItem(kaia) + 5;
        Random random = new Random();
        int rand = random.nextInt(Math.max(DEConfig.soulDropChance / dropChanceOfReaperEnchantment, 1));
        int rand2 = random.nextInt(Math.max(DEConfig.passiveSoulDropChance / dropChanceOfReaperEnchantment, 1));
        boolean isAnimal = entityCreature instanceof EntityAnimal;
        if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal)) {
            soul = DEFeatures.mobSoul.getSoulFromEntity(entityCreature, false);
            Achievements.triggerAchievement(playerSource, "draconicevolution.soul");
        }
        return soul;
    }

    private static void dennyEntitySpawnInWorld(World world, Entity entity) {
        if (world.getCapability(AntiEntityProvider.antiEntitySpawn, null).entitiesDontSpawnInWorld().contains(entity.getClass()) || world.getCapability(UnbanEntitiesProvider.unbanEntities, null).entitiesCannotBannable().contains(entity.getClass()))
            return;
        UtilityHelper.sendMessageToAllPlayers("The Entity: " + entity.getName() + " are blocked spawn in " + DimensionType.getById(world.provider.getDimension()).getName());
        IAntiEntitySpawn capability = world.getCapability(AntiEntityProvider.antiEntitySpawn, null);
        capability.dennySpawnInWorld(entity.getClass());
    }

    private static void generateLightBolts(EntityPlayer playerSource) {
        Random rand = new Random();
        Vec3d lookPlayer = playerSource.getLookVec().normalize();
        DoubleSupplier posXLight = () -> playerSource.posX + lookPlayer.x * rand.nextInt(100) - 20;
        DoubleSupplier posYLight = () -> playerSource.posY + lookPlayer.y * rand.nextInt(7);
        DoubleSupplier posZLight = () -> playerSource.posZ + lookPlayer.z * rand.nextInt(100) - 20;
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", "omnipotent:customligth");
        Consumer<Boolean> spawnLight = (value) -> playerSource.world.spawnEntity(new CustomLightningBolt(playerSource.world, posXLight.getAsDouble(), posYLight.getAsDouble(), posZLight.getAsDouble(), true));
        BiConsumer<Integer, Object> loop = (quantityOfLoop, object) -> {
            for (int c = 0; c < quantityOfLoop; c++) {
                if (c % 4 == 0)
                    spawnLight.accept(true);
                spawnLight.accept(false);
            }
        };
        loop.accept(30, spawnLight);
    }

    public static boolean withKaiaMainHand(EntityPlayer trueSource) {
        return trueSource.getHeldItemMainhand().getItem() instanceof Kaia;
    }

    public static void decideBreakBlock(EntityPlayerMP player, BlockPos pos) {
        if (getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea.getValue()) > 1) {
            int areaBlock = getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea.getValue());
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
        if (getKaiaInMainHand(player).getTagCompound().getBoolean(noBreakTileEntity.getValue())) {
            if (checkTheAreaForTileEntityBlock(startX, startY, startZ, endX, endY, endZ, player.world)) {
                world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, (int) xp));
                return;
            }
        }
        long startTime = System.currentTimeMillis();
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
            drops.add(new ItemStack(block));
        }
        UtilityHelper.compactListItemStacks(drops);
        if (kaiaInMainHand.getTagCompound().getBoolean(autoBackPack.getValue())) {
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

    public static boolean effectIsBlockedByKaia(EntityPlayer player, Potion potion) {
        if (!hasInInventoryKaia(player))
            return false;
        NBTTagCompound tagCompound = (getKaiaInMainHand(player) == null ? getKaiaInInventory(player) : getKaiaInMainHand(player)).getTagCompound();
        if (tagCompound == null)
            return false;
        NBTTagList tagList = tagCompound.getTagList(effectsBlockeds, 8);
        return NbtListUtil.isElementAlreadyExists(tagList, potion.getRegistryName().toString());
    }
}
