package com.omnipotent.util;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.achievements.Achievements;
import com.brandon3055.draconicevolution.magic.EnchantmentReaper;
import com.google.common.collect.Lists;
import com.omnipotent.Config;
import com.omnipotent.Omnipotent;
import com.omnipotent.acessor.IEntityLivingBaseAcessor;
import com.omnipotent.common.capability.*;
import com.omnipotent.common.capability.kaiacap.IKaiaBrand;
import com.omnipotent.common.capability.kaiacap.KaiaProvider;
import com.omnipotent.common.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.common.entity.CustomLightningBolt;
import com.omnipotent.common.entity.KaiaEntity;
import com.omnipotent.common.specialgui.ContainerKaia;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.constant.NbtBooleanValues;
import net.crazymonsters.entity.EntityNotch;
import net.crazymonsters.event.EventCrazyMonsters;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
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
import thebetweenlands.common.entity.mobs.EntitySludgeMenace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.omnipotent.Omnipotent.log;
import static com.omnipotent.constant.NbtBooleanValues.*;
import static com.omnipotent.constant.NbtNumberValues.*;
import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.UtilityHelper.getKaiaCap;

public final class KaiaUtil {
    private static final String DRACONIC_MODID = "draconicevolution";
    public static List<Class> antiEntity = new ArrayList<>();


    //invoke apenas do lado do servidor ou no proprio cliente em si
    public static boolean hasInInventoryKaia(@Nullable Entity entity) {
        return findKaiaInInventory(entity).isPresent();
    }

    public static Optional<KaiaWrapper> findKaiaInInventory(@Nullable Entity entity) {
        final Optional<KaiaWrapper> emp = Optional.empty();
        if (!UtilityHelper.isPlayer(entity))
            return emp;
        try {
            EntityPlayer player = (EntityPlayer) entity;
            deleteDuplicateKaias(player);
            if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof Kaia)
                return KaiaWrapper.wrapIfKaia(player.getHeldItem(EnumHand.OFF_HAND));
            for (ItemStack slot : player.inventory.mainInventory) {
                if (slot.getItem() instanceof Kaia) {
                    return KaiaWrapper.wrapIfKaia(slot);
                }
            }
        } catch (Exception e) {
            log.error("Error in findKaiaInInventory", e);
            return emp;
        }
        return emp;
    }

    public static Optional<KaiaWrapper> findKaiaInMainHand(EntityPlayer player) {
        return KaiaWrapper.wrapIfKaia(player.getHeldItemMainhand());
    }

    public static KaiaWrapper getKaiaInMainHand(EntityPlayer player) {
        return findKaiaInMainHand(player).orElseThrow(() -> new RuntimeException("without kaia in mainhand"));
    }

    public static KaiaWrapper getKaiaInMainHandOrInventory(EntityPlayer playerSource) {
        return findKaiaInMainHand(playerSource).orElseGet(() -> getKaiaInInventory(playerSource));
    }

    public static KaiaWrapper getKaiaInInventory(EntityPlayer player) {
        return findKaiaInInventory(player).orElseThrow(() -> new RuntimeException("without Kaia in Inventory"));
    }

    private static void deleteDuplicateKaias(EntityPlayer player) {
        NonNullList<ItemStack> inventory = player.inventory.mainInventory;
        ArrayList<KaiaWrapper> kaias = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack currentItem = inventory.get(i);
            if (currentItem.getItem() instanceof Kaia) {
                for (int j = i + 1; j < inventory.size(); j++) {
                    ItemStack otherItem = inventory.get(j);
                    if (otherItem.getItem() instanceof Kaia) {
                        KaiaWrapper kaiaWrapper = new KaiaWrapper(currentItem);
                        UUID identify = kaiaWrapper.getIdentify();
                        UUID identify1 = new KaiaWrapper(otherItem).getIdentify();
                        if (identify.equals(identify1)) {
                            player.inventory.setInventorySlotContents(j, ItemStack.EMPTY);
                            kaias.add(kaiaWrapper);
                        }
                    }
                }
            }
        }
        KaiaWrapper.wrapIfKaia(player.getHeldItem(EnumHand.OFF_HAND)).ifPresent(kaiaWrapper -> {
            boolean isDuplicate = kaias.stream()
                    .anyMatch(item -> item.getIdentify().equals(kaiaWrapper.getIdentify()));
            if (isDuplicate)
                player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
        });
    }

    public static void killInAreaConstantly(EntityPlayer playerSource) {
        World world = playerSource.getEntityWorld();
        KaiaWrapper kaiaWrapper = getKaiaInMainHandOrInventory(playerSource);
        boolean autoKill = kaiaWrapper.getBoolean(NbtBooleanValues.autoKill);
        if (!autoKill)
            return;
        BlockPos position = playerSource.getPosition();
        boolean killAllEntities = kaiaWrapper.getBoolean(NbtBooleanValues.killAllEntities);
        int rangeOfBlocks = kaiaWrapper.getInteger(rangeAutoKill);
        List<Entity> entities = getEntitiesInArea(world, position, rangeOfBlocks);
        filterEntities(entities, kaiaWrapper);
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
        KaiaWrapper kaiaWrapper = getKaiaInMainHandOrInventory(playerSource);
        int range = kaiaWrapper.getInteger(rangeAttack);
        boolean killAllEntities = kaiaWrapper.getBoolean(NbtBooleanValues.killAllEntities);
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

        filterEntities(entities, kaiaWrapper);
        for (Entity entity : entities) {
            killChoice(entity, playerSource, killAllEntities);
        }
    }

    public static void filterEntities(List<Entity> entities, KaiaWrapper kaiaWrapper) {
        entities.removeIf(entity -> UtilityHelper.isPlayer(entity) && hasInInventoryKaia(entity));
        entities.removeIf(entity -> UtilityHelper.isPlayer(entity) && !entityIsPlayerAndKaiaCanKillPlayer(kaiaWrapper, false, entity));
        entities.removeIf(entity -> entityIsFriendEntity(entity) && !entityFriendCanKilledByKaia(kaiaWrapper, entity, false));
    }

    public static boolean killChoice(Entity entity, EntityPlayer playerSource, boolean killAllEntities) {
        boolean mobKilled = false;
        KaiaWrapper kaia = getKaiaInMainHandOrInventory(playerSource);
        if (!kaia.getBoolean(NbtBooleanValues.attackYourWolf))
            if (entity instanceof EntityWolf && ((EntityWolf) entity).isOwner(playerSource))
                return mobKilled;
        if (UtilityHelper.isPlayer(entity)) {
            if (!hasInInventoryKaia(entity)) {
                mobKilled = true;
                killPlayer((EntityPlayer) entity, playerSource, kaia);
            }
        } else if (entity instanceof MultiPartEntityPart && ((MultiPartEntityPart) entity).parent instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) ((MultiPartEntityPart) entity).parent;
            if (!entity.world.isRemote && livingBase.getHealth() != 0.0F) {
                mobKilled = true;
                killMobs(livingBase, playerSource, kaia);
            }
        } else if (Loader.isModLoaded("thebetweenlands") && entity instanceof EntitySludgeMenace.DummyPart dummy
                && dummy.getParent() != null && dummy.getParent().parent instanceof EntityLivingBase livingBase) {
            if (!entity.world.isRemote && livingBase.getHealth() != 0.0F) {
                mobKilled = true;
                killMobs(livingBase, playerSource, kaia);
            }
        } else if (entity instanceof EntityLivingBase && !(entity.world.isRemote || entity.isDead || ((EntityLivingBase) entity).getHealth() == 0.0F)) {
            mobKilled = true;
            killMobs((EntityLivingBase) entity, playerSource, kaia);
        } else if (killAllEntities) {
            mobKilled = true;
            entity.setAbsoluteDead();
            entity.setDead();
            if (Loader.isModLoaded("crazymonsters") && entity instanceof EntityNotch notch) {
                crazyDeath(notch, playerSource);
            }
            if (kaia.getBoolean(banEntitiesAttacked))
                dennyEntitySpawnInWorld(playerSource.world, entity);
        }
        return mobKilled;
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "crazymonsters")
    private static void crazyDeath(EntityNotch entity, EntityPlayer playerSource) {
        entity.setCrazyHealth(0);
        DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
        entity.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
        entity.onDeath(ds);
        entity.onRemovedFromWorld();
        EventCrazyMonsters.setTelTime(0);
    }

    public static boolean checkIfKaiaCanKill(Entity entityTarget, EntityPlayer playerSource, boolean directAttack, boolean isCounterAttack) {
        if (playerSource == null)
            throw new RuntimeException("Player is null in checkKaia");
        KaiaWrapper kaia = getKaiaInMainHandOrInventory(playerSource);
        if (kaia == null)
            throw new RuntimeException("Use of method is incorrect, playerSource dont have Kaia");
        if (entityTarget == null || (UtilityHelper.isPlayer(entityTarget) && hasInInventoryKaia(entityTarget)))
            return false;
        if (!EntityIsWolfAndKaiaCanKillPlayerOwnedWolf(kaia, entityTarget, playerSource))
            return false;
        if (entityTarget instanceof EntityLivingBase && kaia.getBoolean(banEntityToSealedDimension))
            if (banEntityToSealedDimension((EntityLivingBase) entityTarget))
                return false;
        if (entityIsFriendEntity(entityTarget)) {
            if (entityFriendCanKilledByKaia(kaia, entityTarget, directAttack))
                return true;
            else
                return false;
        }
        if (entityNoIsNormalAndCanKilledByKaia(kaia, entityTarget))
            return true;
        if (UtilityHelper.isPlayer(entityTarget)) {
            if (entityIsPlayerAndKaiaCanKillPlayer(kaia, directAttack, entityTarget))
                return true;
            else
                return false;
        } else
            return true;
    }

    public static boolean entityFriendCanKilledByKaia(KaiaWrapper kaia, Entity entityTarget, boolean directAttack) {
        boolean killFriendEntity = kaia.getBoolean(killFriendEntities);
        if (!killFriendEntity) {
            return directAttack;
        } else
            return true;
    }

    public static boolean entityIsFriendEntity(Entity entityTarget) {
        return (entityTarget instanceof EntityBat || entityTarget instanceof EntitySquid || entityTarget instanceof EntityAgeable || entityTarget instanceof EntityGolem);
    }

    public static boolean entityNoIsNormalAndCanKilledByKaia(KaiaWrapper kaia, Entity entityTarget) {
        if (!kaia.getBoolean(killAllEntities) || entityTarget instanceof EntityLivingBase)
            return false;
        return true;
    }

    public static boolean entityIsPlayerAndKaiaCanKillPlayer(KaiaWrapper kaia, boolean directAttack, Entity entityTarget) {
        if (!(entityTarget instanceof EntityPlayer))
            return false;
        if (kaia.playerIsProtected(entityTarget.getUniqueID().toString())) {
            if (directAttack)
                return !kaia.getBoolean(playerDontKillInDirectAttack);
            return false;
        }
        return true;
    }

    public static boolean EntityIsWolfAndKaiaCanKillPlayerOwnedWolf(KaiaWrapper kaia, Entity entityTarget, EntityPlayer playerSource) {
        boolean kaiaCantKillYourWolf = !kaia.getBoolean(attackYourWolf);
        if (kaiaCantKillYourWolf)
            if (entityTarget instanceof EntityWolf && ((EntityWolf) entityTarget).isOwner(playerSource))
                return false;
        return true;
    }

    private static void killMobs(EntityLivingBase entity, EntityPlayer playerSource, KaiaWrapper kaia) {
        if (kaia.getBoolean(banEntityToSealedDimension) && (banEntityToSealedDimension(entity)))
            return;
        if (kaia.getBoolean(summonLightBoltsInKill))
            generateLightBolts(playerSource);
        EntityLivingBase entityCreature = entity;
        ((IEntityLivingBaseAcessor) entityCreature).setRecentlyHit(60);
        DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
        entityCreature.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
        verifyFireEnchantmentAndExecute(playerSource, entityCreature);
        antiEntity.add(entityCreature.getClass());
        verifyAndManagerAutoBackEntitiesAndApplyDamage(entityCreature, ds, playerSource, kaia);
        antiEntity.remove(entityCreature.getClass());
        if (kaia.getBoolean(banEntitiesAttacked))
            dennyEntitySpawnInWorld(playerSource.world, entity);
    }

    private static boolean banEntityToSealedDimension(EntityLivingBase entity) {
        if (entity instanceof EntityPlayerMP)
            ((EntityPlayerMP) entity).setSpawnDimension(Omnipotent.dimensionType.getId());
        else if (entity instanceof EntityLiving) {
            entity.changeDimension(Omnipotent.dimensionType.getId());
            return true;
        }
        return false;
    }

    private static void verifyFireEnchantmentAndExecute(EntityPlayer playerSource, EntityLivingBase entityCreature) {
        int enchantmentFire = getKaiaInInventory(playerSource).getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        if (enchantmentFire != 0) entityCreature.setFire(Integer.MAX_VALUE / 25);
    }

    private static void killPlayer(EntityPlayer playerEnemie, EntityPlayer playerSource, KaiaWrapper kaia) {
        DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
        playerEnemie.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
        boolean activekeepInventory = false;
        GameRules gameRules = playerSource.getEntityWorld().getGameRules();
        String keepInventory = "keepInventory";
        if (kaia.getBoolean(ignoreKeepInventory)) {
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
        if (kaia.getBoolean(playersCantRespawn))
            Config.addPlayerInListThatCantRespawn(playerEnemie);
    }

    private static void verifyAndManagerAutoBackEntitiesAndApplyDamage(EntityLivingBase entityCreature, DamageSource ds, EntityPlayer playerSource, KaiaWrapper kaia) {
        boolean autoBackpackEntities = kaia.getBoolean(autoBackPackEntities);
        ItemStack soulReaper = null;
        if (autoBackpackEntities) {
            entityCreature.captureDrops = false;
            entityCreature.captureDropsAbsolute = true;
            if (Loader.isModLoaded(DRACONIC_MODID))
                soulReaper = setReaperEnchant(kaia, playerSource, entityCreature);
            entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
            entityCreature.onDeath(ds);
            ArrayList<EntityItem> capturedDrops = entityCreature.capturedDrops;
            NonNullList<ItemStack> drops = NonNullList.create();
            capturedDrops.forEach(entityItem -> drops.add(entityItem.getItem()));
            if (soulReaper != null) drops.add(soulReaper);
            UtilityHelper.compactListItemStacks(drops);
            if (playerSource.openContainer instanceof ContainerKaia containerKaia)
                containerKaia.addExternItemStack(drops);
            else kaia.addItemStacksInInventory(playerSource, drops);
        } else {
            if (Loader.isModLoaded(DRACONIC_MODID)) {
                World world = playerSource.world;
                ItemStack stack = setReaperEnchant(kaia, playerSource, entityCreature);
                if (stack != null)
                    world.spawnEntity(new EntityItem(world, entityCreature.posX, entityCreature.posY, entityCreature.posZ, stack));
            }
        }
        attackEntity(entityCreature, ds);
    }

    private static void attackEntity(EntityLivingBase entityCreature, DamageSource ds) {
        entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
        entityCreature.absoluteAttackEntityFrom(ds, Float.MAX_VALUE);
        ((IEntityLivingBaseAcessor) entityCreature).setlastDamageStamp(entityCreature.world.getTotalWorldTime());
        ((IEntityLivingBaseAcessor) entityCreature).setlastDamageSource(ds);
        entityCreature.setHealth(0.0F);
        entityCreature.setAbsoluteHealth(0.0F);
        entityCreature.onDeath(ds);
        entityCreature.onAbsoluteDeath(ds);
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = DRACONIC_MODID)
    private static ItemStack setReaperEnchant(KaiaWrapper kaia, EntityPlayer playerSource, EntityLivingBase entityCreature) {
        ItemStack soul = null;
        int dropChanceOfReaperEnchantment = kaia.getEnchantmentLevel(EnchantmentReaper.instance) + 5;
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
        KaiaWrapper kaiaWrapper = getKaiaInMainHand(player);
        if (kaiaWrapper.getInteger(blockBreakArea) > 1) {
            int areaBlock = kaiaWrapper.getInteger(blockBreakArea);
            if (!player.world.isRemote && !player.capabilities.isCreativeMode && withKaiaMainHand(player)) {
                if (areaBlock % 2 != 0) {
                    if (kaiaWrapper.getBoolean(fastBreakBlocks))
                        breakBlocksInArea(areaBlock, player, pos);//                        fastBreakBlocksInAreaOld(areaBlock, player, pos, kaiaWrapper);
                    else
                        breakBlocksInArea(areaBlock, player, pos);
                }
            }
        } else {
            NonNullList<ItemStack> drops = NonNullList.create();
            int xp = (int) fillDropListAndCompactIfNecessary(player, pos, drops, kaiaWrapper.getEnchantmentLevel(Enchantments.FORTUNE), kaiaWrapper.getBoolean(automaticSmelting));
            processDropsForPlayer(player, drops, kaiaWrapper, Collections.singletonList(pos));
            player.world.destroyBlock(pos, false);
            spawnXP(player, pos, player.world, xp);
        }
    }

    public static void breakBlocksInArea(int areaBlock, final EntityPlayer player, BlockPos centerPos) {
        final World world = player.world;
        int halfArea = areaBlock / 2;
        int startX = centerPos.getX() - halfArea;
        int endX = centerPos.getX() + halfArea;
        int startZ = centerPos.getZ() - halfArea;
        int endZ = centerPos.getZ() + halfArea;
        int startY = centerPos.getY() - halfArea;
        int endY = centerPos.getY() + halfArea;
        float xp = 0f;
        NonNullList<ItemStack> drops = NonNullList.create();
        KaiaWrapper kaiaWrapper = getKaiaInMainHand(player);
        List<BlockPos> blocksToBreak = new ArrayList<>((int) (Math.pow(areaBlock, 3) * 0.75));
        if (kaiaWrapper.getBoolean(noBreakTileEntity) && checkTheAreaForTileEntityBlock(startX, startY, startZ, endX, endY, endZ, player.world)) {
            xp = (int) fillDropListAndCompactIfNecessary((EntityPlayerMP) player, centerPos, drops, kaiaWrapper.getEnchantmentLevel(Enchantments.FORTUNE), kaiaWrapper.getBoolean(automaticSmelting));
            processDropsForPlayer(player, drops, kaiaWrapper, Collections.singletonList(centerPos));
            player.world.destroyBlock(centerPos, false);
            spawnXP(player, centerPos, world, (int) xp);
            return;
        }
        boolean noExcludeTileEntities = !kaiaWrapper.getBoolean(excludeTileEntityDestruction);
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (!world.isAirBlock(blockPos) && (noExcludeTileEntities || world.getTileEntity(blockPos) == null)) {
                        blocksToBreak.add(blockPos);
                    }
                }
            }
        }
        for (BlockPos blockPos : blocksToBreak) {
            xp += fillDropListAndCompactIfNecessary((EntityPlayerMP) player, blockPos, drops, kaiaWrapper.getEnchantmentLevel(Enchantments.FORTUNE), kaiaWrapper.getBoolean(automaticSmelting));
        }
        processDropsForPlayer(player, drops, kaiaWrapper, blocksToBreak);
        final boolean b = blocksToBreak.size() > (150000);

        TickScheduler.scheduleWithCondition(Duration.of(50, ChronoUnit.MILLIS), new Callable<>() {
            private int currentIndex;

            @Override
            public Boolean call() {
                int limit = Math.min(currentIndex + 1000, blocksToBreak.size());
                for (; currentIndex < limit; currentIndex++) {
                    BlockPos blockPos = blocksToBreak.get(currentIndex);
                    boolean x = b ? world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3) : player.world.destroyBlock(blockPos, false);
                }
                return currentIndex >= blocksToBreak.size();
            }
        });
//        for (BlockPos blockPos : blocksToBreak) {
//            boolean x = b ? world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3) : player.world.destroyBlock(blockPos, false);
//        }
        spawnXP(player, centerPos, world, (int) xp);
    }

    private static boolean spawnXP(EntityPlayer player, BlockPos centerPos, World world, int xp) {
        return world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, xp));
    }

    private static void processDropsForPlayer(EntityPlayer player, NonNullList<ItemStack> drops, KaiaWrapper kaiaWrapper, List<BlockPos> blocksToDrop) {
        UtilityHelper.compactListItemStacks(drops);
        drops.removeIf(ItemStack::isEmpty);
        if (kaiaWrapper.getBoolean(autoBackPack)) {
            kaiaWrapper.addItemStacksInInventory(player, drops);
        } else {
            for (BlockPos blockPos : blocksToDrop) {
                drops.forEach(dropStack -> player.world.spawnEntity(new EntityItem(player.world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, dropStack)));
            }
        }
    }


    public static float fillDropListAndCompactIfNecessary(EntityPlayerMP player, BlockPos pos, NonNullList<ItemStack> drops, int enchLevelFortune, boolean smeltItem) {
        if (drops.size() > 5000) {
            UtilityHelper.compactListItemStacks(drops);
        }
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        AtomicReference<Float> xp = new AtomicReference<>(0f);
        NonNullList<ItemStack> dropsBlock = NonNullList.create();
        block.getDrops(dropsBlock, player.world, pos, state, enchLevelFortune);
        dropsBlock.removeIf(item -> item.isEmpty() || item.getItem() instanceof ItemAir);
        if (dropsBlock.isEmpty()) {
            drops.add(new ItemStack(block));
        } else
            drops.addAll(dropsBlock);
        xp.updateAndGet(v -> v + block.getExpDrop(state, player.world, pos, enchLevelFortune));
        if (smeltItem) {
            ArrayList<ItemStack> furnaced = new ArrayList<>();
            final FurnaceRecipes instance = FurnaceRecipes.instance();
            drops.forEach(i -> {
                ItemStack result = instance.getSmeltingResult(i).copy();
                if (!result.isEmpty()) {
                    result.setCount(result.getCount() * i.getCount());
                    if (enchLevelFortune > 0) {
                        int l = player.world.rand.nextInt(enchLevelFortune + 2);
                        if (l == 0)
                            l = 1;
                        result.setCount(result.getCount() * l);
                        int finalPower = l;
                        xp.updateAndGet(v -> v * finalPower);
                    }
                    xp.updateAndGet(v -> v + instance.getSmeltingExperience(result) * i.getCount());
                    furnaced.add(result);
                    i.setCount(0);
                }
            });
            drops.addAll(furnaced);
        }
        drops.removeIf(item -> item.isEmpty() || item.getItem() instanceof ItemAir);
        //linha comentada devida a lentidão dela talvez volte no futuro.
//        player.addStat(StatList.getBlockStats(block));
        return xp.get();
    }

    private static boolean checkTheAreaForTileEntityBlock(int startX, int startY, int startZ, int endX, int endY, int endZ, World world) {
        BlockPos startBlockPos = new BlockPos(startX, startY, startZ);
        for (BlockPos blockPos : BlockPos.getAllInBox(startBlockPos, new BlockPos(endX, endY, endZ))) {
            if (world.getBlockState(blockPos).getBlock().hasTileEntity(world.getBlockState(blockPos))) return true;
        }
        return false;
    }

    public static void fastBreakBlocksInAreaOld(int areaBlock, EntityPlayer player, BlockPos centerPos, KaiaWrapper kaiaWrapper) {
        World world = player.world;
        int startX = centerPos.getX() - areaBlock / 2;
        int endX = centerPos.getX() + areaBlock / 2;
        int startZ = centerPos.getZ() - areaBlock / 2;
        int endZ = centerPos.getZ() + areaBlock / 2;
        int startY = centerPos.getY() - areaBlock / 2;
        int endY = centerPos.getY() + areaBlock / 2;
        float xp = 0f;
        xp += fastBreakBlock((EntityPlayerMP) player, centerPos, kaiaWrapper);
        if (kaiaWrapper.getBoolean(noBreakTileEntity)) {
            if (checkTheAreaForTileEntityBlock(startX, startY, startZ, endX, endY, endZ, player.world)) {
                spawnXP(player, centerPos, world, (int) xp);
                return;
            }
        }
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (!world.isAirBlock(blockPos)) {
                        xp += fastBreakBlock((EntityPlayerMP) player, blockPos, kaiaWrapper);
                    }
                }
            }
        }
        spawnXP(player, centerPos, world, (int) xp);
    }

    public static float fastBreakBlock(EntityPlayerMP player, BlockPos pos, KaiaWrapper kaiaWrapper) {
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        NonNullList<ItemStack> drops = NonNullList.create();
        KaiaWrapper kaiaInMainHand = getKaiaInMainHand(player);
        int enchLevelFortune = kaiaInMainHand.getEnchantmentLevel(Enchantments.FORTUNE);
        Float xp = 0f;
        block.getDrops(drops, player.world, pos, state, enchLevelFortune);
        drops.removeIf(item -> item.getItem() instanceof ItemAir);
        if (drops.isEmpty())
            drops.add(new ItemStack(block));
        UtilityHelper.compactListItemStacks(drops);
        if (kaiaWrapper.getBoolean(autoBackPack))
            kaiaInMainHand.addItemStacksInInventory(player, drops);
        else
            drops.forEach(dropStack -> player.world.spawnEntity(new EntityItem(player.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack)));
        xp += block.getExpDrop(state, player.world, pos, enchLevelFortune);
        try {
            UtilityHelper.setBlockStateFast(player.world, pos, Blocks.AIR.getDefaultState(), 3);
        } catch (Exception e) {
        }
        return xp;
    }

    public static void createTagCompoundStatusIfNecessary(ItemStack stack) {
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
    }


    //excluir
    public static void createOwnerIfNecessary(ItemStack stack, Entity entityIn) {
        Objects.requireNonNull(stack, "kaia ItemStack should be non-null");
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (!tagCompound.hasKey(ownerName))
            tagCompound.setString(ownerName, entityIn.getName());
        if (!tagCompound.hasKey(ownerID))
            tagCompound.setString(ownerID, entityIn.getUniqueID().toString());
    }

    public static boolean theLastAttackOfKaia(EntityLivingBase entity) {
        return entity.getLastDamageSource() != null && entity.getLastDamageSource().getTrueSource() != null && entity.getLastDamageSource().getDamageType().equals(new AbsoluteOfCreatorDamage(entity).getDamageType());
    }

    public static void returnKaiaOfOwner(EntityPlayerMP player) {
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
                                player.world.spawnEntity(new EntityItem(player.world, player.posX + 8, player.posY, player.posZ, player.inventory.mainInventory.get(index)));
                                player.sendMessage(new TextComponentString(I18n.format("kaia.message.returndropitems") + " X: " + ((int) player.posX + 8) + " Y: " + ((int) player.posY) + " Z: " + ((int) player.posZ)));
                                player.inventory.mainInventory.set(index, kaia.copy());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //excluir
    public static boolean isOwnerOfKaia(ItemStack kaiaStack, @Nonnull EntityPlayer player) {
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
                else
                    player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA CANNOT BE KILLED"));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "KAIA CANNOT BE KILLED"));
            }
        }
    }

    public static boolean effectIsBlockedByKaia(EntityPlayer player, Potion potion) {
        return findKaiaInInventory(player)
                .map(wrapper -> wrapper.listContainsElement(effectsBlockeds, potion.getRegistryName().toString()))
                .orElse(false);
    }

    public static void kaiaAttackShow(EntityPlayerMP entityPlayerMP, EntityLivingBase entityByID) {
        getKaiaCap(entityPlayerMP).ifPresent(cap -> {
            List<String> kaiaSwordsSummoned = cap.getKaiaSwordsSummoned();
            Stream<KaiaEntity> entityStream = entityPlayerMP.getEntityWorld().loadedEntityList.stream().filter(e ->
                    e instanceof KaiaEntity && kaiaSwordsSummoned.contains(e.getPersistentID().toString())).map(KaiaEntity.class::cast);
            entityStream.forEach(e -> {
                e.setAttackMode(true);
                e.setAttackTarget(entityByID);
            });
        });
    }


    public static void kaiaSummonSwords(EntityPlayerMP entityPlayerMP, IKaiaBrand cap) {
        World world = entityPlayerMP.world;
        double increaseMov = 0.1;
        double x = entityPlayerMP.posX + increaseMov;
        double z = entityPlayerMP.posZ + increaseMov;

        ArrayList<String> ids = new ArrayList<>();
        List<String> kaiaSwordsSummoned = cap.getKaiaSwordsSummoned();
        int kaiasInWorld = kaiaSwordsSummoned.size();

        if (kaiasInWorld == 5)
            killKaiaEntities(entityPlayerMP, kaiaSwordsSummoned);

        for (int c = 0; c < 5 - kaiasInWorld; c++) {
            KaiaEntity kaiaEntity = new KaiaEntity(world, entityPlayerMP, c);
            x += increaseMov;
            z += increaseMov;
            kaiaEntity.setPosition(x, entityPlayerMP.posY, z);
            world.spawnEntity(kaiaEntity);
            ids.add(kaiaEntity.getPersistentID().toString());
        }
        cap.getKaiaSwordsSummoned().clear();
        ids.forEach(cap::addKaiaSummoned);
    }

    private static void killKaiaEntities(EntityPlayerMP entityPlayerMP, List<String> cap) {
        entityPlayerMP.world.getEntities(KaiaEntity.class, e -> cap.contains(e.getPersistentID().toString()))
                .forEach(Entity::setDead);
        cap.clear();
    }

    public static void addKaiaAndManagerInPlayarDataFile(@Nonnull KaiaWrapper kaia, @Nonnull String
            absolutePathOfPlayerDataFile) throws IOException {
//        if (playerNbt == null) throw new FileNotFoundException("playerdata File not is valid or not found");
        try {
            NBTTagCompound playerNbt = CompressedStreamTools.readCompressed(new FileInputStream(absolutePathOfPlayerDataFile));
            NBTTagList inventory = playerNbt.getTagList("Inventory", 10);
            ArrayList<Byte> slots = new ArrayList<>();
            for (NBTBase nbt : inventory) {
                if (nbt instanceof NBTTagCompound) {
                    NBTTagCompound nbt1 = (NBTTagCompound) nbt;
                    if (nbt1.hasKey("Slot")) {
                        byte slot = nbt1.getByte("Slot");
                        if (slot != 100 && slot != 101 && slot != 102 && slot != 103) {
                            slots.add(slot);
                        }
                    }
                }
            }
            if (slots.size() <= 36) {
                NBTTagCompound nbt = new NBTTagCompound();
                if (!slots.contains((byte) -106)) {
                    kaia.writeToNBT(nbt);
                    nbt.setByte("Slot", (byte) -106);
                    inventory.appendTag(nbt);
                } else {
                    for (byte i = 0; i <= 35; i++) {
                        if (!slots.contains(i)) {
                            kaia.writeToNBT(nbt);
                            nbt.setByte("Slot", i);
                            inventory.appendTag(nbt);
                            break;
                        }
                    }
                }
            } else {
                try {
                    for (byte i : slots) {
                        NBTTagCompound nbtTag = (NBTTagCompound) inventory.get(i);
                        if (!nbtTag.getString("id").equals("omnipotent:kaia")) {
                            NBTTagCompound kaiaNbt = new NBTTagCompound();
                            kaia.writeToNBT(kaiaNbt);
                            kaiaNbt.setByte("Slot", i);
                            NBTTagCompound nbtBase = (NBTTagCompound) inventory.get(i);
                            ItemStack itemStack = new ItemStack(nbtBase);
                            EntityPlayerMP entityPlayerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().get(0);
                            NonNullList<ItemStack> items = NonNullList.create();
                            items.add(itemStack);
                            kaia.addItemStacksInInventory(entityPlayerMP, items);
                            inventory.set(i, kaiaNbt);
                            break;
                        }
                    }
                } catch (Exception e) {
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(absolutePathOfPlayerDataFile);
            CompressedStreamTools.writeCompressed(playerNbt, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
        }
    }

    public static KaiaWrapper getHeldKaia(EntityPlayer player, EnumHand enumHand) {
        return new KaiaWrapper(player.getHeldItem(enumHand));
    }


//    public static void fastBreakBlocksInArea(final int areaBlock, final EntityPlayer player, final BlockPos centerPos, final NBTTagCompound tagCompound) {
//        breakBlocksInArea(areaBlock, player, centerPos);

//        World world = player.world;
//        int startX = centerPos.getX() - areaBlock / 2;
//        int endX = centerPos.getX() + areaBlock / 2;
//        int startZ = centerPos.getZ() - areaBlock / 2;
//        int endZ = centerPos.getZ() + areaBlock / 2;
//        int startY = centerPos.getY() - areaBlock / 2;
//        int endY = centerPos.getY() + areaBlock / 2;
//        float xp = 0f;
//        int totalArea = areaBlock * areaBlock * areaBlock;
//        int nThreads = Runtime.getRuntime().availableProcessors();
//        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
//        int initialCapacity = (int) Math.ceil(totalArea / nThreads);
//        ArrayList<BlockPos> blocksToBreak = new ArrayList<>(totalArea);
//        ArrayList<BlockPos> blocksToBreak2 = new ArrayList<>(initialCapacity);
//        ArrayList<BlockPos> blocksToBreak3 = new ArrayList<>(initialCapacity);
//        xp += fastBreakBlock((EntityPlayerMP) player, centerPos, tagCompound);
//        if (tagCompound.getBoolean(noBreakTileEntity.getValue())) {
//            if (checkTheAreaForTileEntityBlock(startX, startY, startZ, endX, endY, endZ, player.world)) {
//                world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, (int) xp));
//                return;
//            }
//        }
//        int count = 0;
//        for (int x = startX; x <= endX; x++) {
//            for (int z = startZ; z <= endZ; z++) {
//                for (int y = startY; y <= endY; y++) {
//                    BlockPos blockPos = new BlockPos(x, y, z);
//                    if (!world.isAirBlock(blockPos)) {
//                        if (count <= initialCapacity)
//                            blocksToBreak.add(blockPos);
//                        else if (count <= initialCapacity * 2)
//                            blocksToBreak2.add(blockPos);
//                        else
//                            blocksToBreak3.add(blockPos);
//                        count++;
//                    }
//                }
//            }
//        }

    //deppois
//       try {
//           ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
//           Long invoke = forkJoinPool.invoke(new BreakBlocksTask(blocksToBreak, (EntityPlayerMP) player, tagCompound));
//           UtilityHelper.sendMessageToAllPlayers(String.valueOf(invoke));
//       }catch (Exception e){
//           System.out.println(e);
//       }
    //-------
//        ArrayList<CompletableFuture<Integer>> blocksToBreakAllTask = new ArrayList<>();
//        blocksToBreakAllTask.add(CompletableFuture.supplyAsync(() -> {
//            int xp1 = 0;
//            for (BlockPos b : blocksToBreak) {
//                xp1 += fastBreakBlock((EntityPlayerMP) player, b, tagCompound);
//            }
//            return xp1;
//        }));
//        blocksToBreakAllTask.add(CompletableFuture.supplyAsync(() -> {
//            int xp1 = 0;
//            for (BlockPos b : blocksToBreak2) {
//                xp1 += fastBreakBlock((EntityPlayerMP) player, b, tagCompound);
//            }
//            return xp1;
//        }));
//        blocksToBreakAllTask.add(CompletableFuture.supplyAsync(() -> {
//            int xp1 = 0;
//            for (BlockPos b : blocksToBreak3) {
//                xp1 += fastBreakBlock((EntityPlayerMP) player, b, tagCompound);
//            }
//            return xp1;
//        }));
//        CompletableFuture<Void> blocksToBreakFuture = CompletableFuture.allOf(blocksToBreakAllTask.toArray(new CompletableFuture[0]));
//        CompletableFuture<Integer> integerCompletableFuture = blocksToBreakFuture.thenApply(v -> blocksToBreakAllTask.stream().map(CompletableFuture::join).reduce(0, Integer::sum));
//        try {
//            xp += integerCompletableFuture.get();
//        } catch (InterruptedException e) {
//        } catch (ExecutionException e) {
//        }
//        world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX() + 0.5, centerPos.getY() + 0.5, centerPos.getZ() + 0.5, (int) xp));
//    }
}
