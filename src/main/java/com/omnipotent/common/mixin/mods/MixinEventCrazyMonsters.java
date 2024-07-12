package com.omnipotent.common.mixin.mods;


import com.chaoswither.chaoswither;
import com.chaoswither.event.ChaosUpdateEvent;
import com.chaoswither.gui.GuiDead;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.crazymonsters.CrazyMonstersMod;
import net.crazymonsters.damagesource.DamageSourceGod;
import net.crazymonsters.entity.*;
import net.crazymonsters.entity.ai.object.AddMotion;
import net.crazymonsters.event.EventCrazyMonsters;
import net.crazymonsters.event.EventCrazyMonstersRegister;
import net.crazymonsters.event.EventCrazySounds;
import net.crazymonsters.item.ItemRegistry;
import net.crazymonsters.potions.CrazyPotionMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

import static net.crazymonsters.event.EventCrazyMonsters.*;

@Mixin(value = EventCrazyMonsters.class, remap = false)
public abstract class MixinEventCrazyMonsters {

    @Shadow
    private static boolean isGodWorld;
    @Shadow
    private static int pushCountD;
    @Shadow
    private static boolean NotchSpawn1;
    @Shadow
    private static boolean NotchSpawn2;
    @Shadow
    private static boolean isEnd;
    @Shadow
    private static boolean isTrueEnd2;
    @Shadow
    private static boolean RemoveEntity2;
    @Shadow
    private static boolean RemoveEntity;
    @Shadow
    private static boolean isKicked;
    @Shadow
    private static int telTime;
    @Shadow
    private static int key;
    @Shadow
    private int CheatingTimer;
    @Shadow
    private int CheatingTimer56;
    @Shadow
    private static boolean GetAdvancement;

    @Shadow
    protected abstract boolean isNoWorld(World world);

    @Shadow
    protected abstract boolean spawnEntity(Entity entityIn);

    @Shadow
    protected abstract boolean isOnlyOne(World world);

    @Shadow
    protected abstract int getThunderWeatherTime();

    @Shadow
    protected abstract int getUnThunderWeatherTime();

    @Shadow
    protected abstract String getRandomString(Entity entity, String string0);


    @Shadow
    private Set<EntityNotch> notch;

    @Shadow
    private Set<EntityGoldWatcher> watcher;

    @Shadow
    private Set<EntityCrystalDemon> malak;

    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        if (cancelDead2(Minecraft.getMinecraft().player) || checkLockedArmor(Minecraft.getMinecraft().player)) {
            final GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui != null) {
                Label_0123:
                {
                    if (!Loader.isModLoaded("chaoswither")) {
                        if (!Loader.isModLoaded("Chaos Wither")) {
                            break Label_0123;
                        }
                    }
                    try {
                        if (gui instanceof GuiDead) {
                            Minecraft.getMinecraft().currentScreen.onGuiClosed();
                            Minecraft.getMinecraft().currentScreen = null;
                            Minecraft.getMinecraft().player.setPlayerSPHealth(20.0f);
                            Minecraft.getMinecraft().player.setHealth(20.0f);
                        }
                    } catch (Exception e) {
                        EventCrazyMonsters.logger.warn("Failed to get Chaos Wither Mod.");
                    }
                }
                if (gui instanceof GuiGameOver) {
                    Minecraft.getMinecraft().currentScreen.onGuiClosed();
                    Minecraft.getMinecraft().currentScreen = null;
                    Minecraft.getMinecraft().player.setPlayerSPHealth(20.0f);
                    Minecraft.getMinecraft().player.setHealth(20.0f);
                }
            }
        }
        for (final EntityNotch entitynotch : this.notch) {
            if (entitynotch.absoluteDead) {
                entitynotch.isDead = true;
                continue;
            }
            if (entitynotch.isDead) {
                entitynotch.isDead = false;
            }
            if (!entitynotch.world.loadedEntityList.contains(entitynotch)) {
                entitynotch.world.loadedEntityList.add(entitynotch);
                entitynotch.world.onEntityAdded((Entity) entitynotch);
            }
        }
        for (final EntityGoldWatcher entitywatcher : this.watcher) {
            if (entitywatcher.isDead) {
                entitywatcher.isDead = false;
            }
            if (!entitywatcher.world.loadedEntityList.contains(entitywatcher)) {
                entitywatcher.world.loadedEntityList.add(entitywatcher);
                entitywatcher.world.onEntityAdded((Entity) entitywatcher);
            }
        }
        for (final EntityCrystalDemon entitymalak : this.malak) {
            if (entitymalak.isDead) {
                entitymalak.isDead = false;
            }
            if (!entitymalak.world.loadedEntityList.contains(entitymalak)) {
                entitymalak.world.loadedEntityList.add(entitymalak);
                entitymalak.world.onEntityAdded((Entity) entitymalak);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite
    public void cancelInput(final InputUpdateEvent event) {
        if (KaiaUtil.hasInInventoryKaia(event.getEntity()))
            return;
        if (EventCrazyMonsters.cancelMove) {
            event.getMovementInput().moveForward = 0.0f;
            event.getMovementInput().moveStrafe = 0.0f;
            event.getMovementInput().backKeyDown = false;
            event.getMovementInput().forwardKeyDown = false;
            event.getMovementInput().leftKeyDown = false;
            event.getMovementInput().rightKeyDown = false;
            event.getMovementInput().jump = false;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SubscribeEvent
    public void cancelInput(final MouseEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (KaiaUtil.hasInInventoryKaia(player))
            return;
        if (EventCrazyMonsters.cancelMove) {
            event.setCanceled(true);
        }
    }


    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        final EntityLivingBase living = event.getEntityLiving();
        if (!living.world.isRemote) {
            final int ench_1_level = EnchantmentHelper.getMaxEnchantmentLevel(EventCrazyMonstersRegister.LostLandCurse, living);
            final int ench_2_level = EnchantmentHelper.getMaxEnchantmentLevel(EventCrazyMonstersRegister.DeadCurse, living);
            final int ench_3_level = EnchantmentHelper.getMaxEnchantmentLevel(EventCrazyMonstersRegister.DropCurse, living);
            final int ench_4_level = EnchantmentHelper.getMaxEnchantmentLevel(EventCrazyMonstersRegister.JumpCurse, living);
            final int ench_5_level = EnchantmentHelper.getMaxEnchantmentLevel(EventCrazyMonstersRegister.EffectCurse, living);
            if (ench_1_level > 0 && living.getRNG().nextInt(44 / ench_1_level) == 0) {
                living.world.setBlockToAir(new BlockPos(living.getRNG().nextGaussian() * ench_1_level, living.getRNG().nextGaussian() * ench_1_level, living.getRNG().nextGaussian() * ench_1_level));
            }
            if (ench_2_level > 0) {
                living.setHealth(0.0f);
                living.onDeath(DamageSource.OUT_OF_WORLD);
            }
            if (ench_3_level > 0 && living instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) living;
                if ((player.getHeldItemMainhand() != null || player.getHeldItemOffhand() != null) && player.world.rand.nextInt(44 / ench_3_level) == 0) {
                    final EntityItem item = player.dropItem(player.inventory.decrStackSize(player.inventory.currentItem, 1), false);
                    if (item != null) {
                        item.setPickupDelay(100);
                    }
                }
            }
            if (ench_4_level > 0 && living.onGround) {
                if (!(living instanceof EntityPlayer)) {
                    final EntityLivingBase entityLivingBase = living;
                    entityLivingBase.motionY += living.world.rand.nextFloat() * ench_4_level;
                } else if (!EventCrazyMonsters.cancelMove) {
                    CrazyMonstersMod.SimpleNetworkWrapper.sendTo((IMessage) new AddMotion(0.0, living.world.rand.nextFloat() * ench_4_level * 0.1, 0.0), (EntityPlayerMP) living);
                }
            }
            if (ench_5_level > 0) {
                if (living.isPotionActive(MobEffects.SPEED)) {
                    living.removePotionEffect(MobEffects.SPEED);
                    living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.HASTE)) {
                    living.removePotionEffect(MobEffects.HASTE);
                    living.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.NIGHT_VISION)) {
                    living.removePotionEffect(MobEffects.NIGHT_VISION);
                    living.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.INSTANT_HEALTH)) {
                    living.removePotionEffect(MobEffects.INSTANT_HEALTH);
                    living.addPotionEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.REGENERATION)) {
                    living.removePotionEffect(MobEffects.REGENERATION);
                    living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.SATURATION)) {
                    living.removePotionEffect(MobEffects.SATURATION);
                    living.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.STRENGTH)) {
                    living.removePotionEffect(MobEffects.STRENGTH);
                    living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, ench_5_level));
                }
                if (living.isPotionActive(MobEffects.RESISTANCE) || living.isPotionActive(MobEffects.FIRE_RESISTANCE) || living.isPotionActive(MobEffects.WATER_BREATHING) || living.isPotionActive(MobEffects.ABSORPTION)) {
                    living.removePotionEffect(MobEffects.RESISTANCE);
                    living.removePotionEffect(MobEffects.FIRE_RESISTANCE);
                    living.removePotionEffect(MobEffects.WATER_BREATHING);
                    living.removePotionEffect(MobEffects.ABSORPTION);
                    living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 600, ench_5_level));
                }
            }
        }
        if (!(event.getEntityLiving() instanceof EntityPlayer) && event.getEntityLiving().removeTag("PeaceOfDeath")) {
            ((EntityLiving) event.getEntityLiving()).isDead = true;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayer) && event.getEntityLiving().removeTag("HP1")) {
            ((EntityLiving) event.getEntityLiving()).getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0);
            if (((EntityLiving) event.getEntityLiving()).getHealth() > 1.0f) {
                ((EntityLiving) event.getEntityLiving()).setHealth(1.0f);
            }
        }
        if (EventCrazyMonsters.NoSlimeWorld == 1 && event.getEntityLiving() instanceof EntitySlime) {
            event.getEntityLiving().isDead = true;
        }
        if (EventCrazyMonsters.getAdvancement() && event.getEntityLiving() instanceof EntityPlayer) {
            if (EventCrazyMonsters.isAgain) {
                MixinEventCrazyMonsters.isEnd = false;
            }
            event.getEntityLiving().addPotionEffect(new PotionEffect(CrazyPotionMain.END, 100000000, 75));
        }
        Label_1114:
        {
            if (event.getEntityLiving() instanceof EntityPlayer && EventCrazyMonsters.isTerrible) {
                if (!MixinEventCrazyMonsters.isEnd && !event.getEntityLiving().world.isRemote) {
                    final List<Entity> list = (List<Entity>) event.getEntityLiving().world.loadedEntityList;
                    if (list != null && !list.isEmpty()) {
                        for (final Entity entityitem : list) {
                            if (entityitem instanceof EntityItem) {
                                entityitem.isDead = true;
                            }
                        }
                    }
                    final EntityNotch entitybolt = cancelSpawn(event);
                    if (entitybolt == null) return;
                    entitybolt.setLocationAndAngles(event.getEntityLiving().posX, event.getEntityLiving().posY + 15.0, event.getEntityLiving().posZ, event.getEntityLiving().rotationYaw, event.getEntityLiving().rotationPitch);
                    this.spawnEntity(entitybolt);
                    CrazyMonstersMod.alive = true;
                    EventCrazyMonsters.CRAZYKING = true;
                    MixinEventCrazyMonsters.isGodWorld = true;
                    EventCrazyMonsters.isTerrible = false;
                    MixinEventCrazyMonsters.NotchSpawn1 = false;
                    MixinEventCrazyMonsters.NotchSpawn2 = false;
                }
                if (MixinEventCrazyMonsters.isEnd) {
                    final TextComponentString message3 = new TextComponentString(I18n.translateToLocal("But Nothing happened..."));
                    Minecraft.getMinecraft().player.sendMessage(message3);
                }
                if (!Loader.isModLoaded("chaoswither")) {
                    if (!Loader.isModLoaded("Chaos Wither")) {
                        break Label_1114;
                    }
                }
                try {
                    ChaosUpdateEvent.WITHERLIVE = false;
                    chaoswither.happymode = false;
                } catch (Exception e) {
                    EventCrazyMonsters.logger.warn("Failed to get Chaos Wither Mod.");
                }
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer && MixinEventCrazyMonsters.isTrueEnd2) {
            event.getEntityLiving().addPotionEffect(new PotionEffect(EventCrazyMonsters.ADVANCEMENT_FINAL, 260, 75));


            try {
                Class<?> eventTrueEndingGUIClass = Class.forName("net.crazymonsters.event.EventTrueEndingGUI");
                Constructor<?> constructor = eventTrueEndingGUIClass.getDeclaredConstructor(boolean.class, Runnable.class);
                constructor.setAccessible(true);
                Runnable runnable = () -> Minecraft.getMinecraft().player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                Object eventTrueEndingGUIInstance = constructor.newInstance(true, runnable);
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen) eventTrueEndingGUIInstance);
            } catch (Exception e) {
            }
            MixinEventCrazyMonsters.isTrueEnd2 = false;
        }
        if (event.getEntityLiving() instanceof EntityPlayer && EventCrazyMonsters.isCheckCheating) {
            ++this.CheatingTimer;
            if (this.CheatingTimer >= 10) {
                EventCrazyMonsters.isCheckCheating = false;
                EventCrazyMonsters.isCheckCheating2 = true;
                this.CheatingTimer = 0;
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer && !MixinEventCrazyMonsters.RemoveEntity2) {
            if (this.CheatingTimer56 < 15) {
                ++this.CheatingTimer56;
            }
            if (this.CheatingTimer56 >= 10) {
                MixinEventCrazyMonsters.RemoveEntity2 = true;
            }
        }
        if (event.getEntity() != null && MixinEventCrazyMonsters.isGodWorld && this.isNoWorld(event.getEntity().world) && event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote) {
            final EntityNotch entitybolt2 = cancelRespawn(event);
            if (entitybolt2 == null) return;
            entitybolt2.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY + 15.0, event.getEntity().posZ, event.getEntity().rotationYaw, event.getEntity().rotationPitch);
            event.getEntity().world.spawnEntity(entitybolt2);
            Label_1441:
            {
                if (!Loader.isModLoaded("chaoswither")) {
                    if (!Loader.isModLoaded("Chaos Wither")) {
                        break Label_1441;
                    }
                }
                try {
                    ChaosUpdateEvent.WITHERLIVE = false;
                    chaoswither.happymode = false;
                } catch (Exception e2) {
                    EventCrazyMonsters.logger.warn("Failed to get Chaos Wither Mod.");
                }
            }
            MixinEventCrazyMonsters.RemoveEntity = true;
            if (!MixinEventCrazyMonsters.NotchSpawn2 && MixinEventCrazyMonsters.NotchSpawn1) {
                MixinEventCrazyMonsters.NotchSpawn2 = true;
                MixinEventCrazyMonsters.isKicked = true;
            }
            if (!MixinEventCrazyMonsters.NotchSpawn1) {
                MixinEventCrazyMonsters.NotchSpawn1 = true;
            }
        }
        if (!event.getEntity().world.isRemote && this.isOnlyOne(event.getEntity().world) && event.getEntityLiving() instanceof EntityPlayer) {
            event.getEntityLiving().setHealth(0.0f);
        }
        if (!event.getEntity().world.isRemote && !KaiaUtil.hasInInventoryKaia(event.getEntity()))
            respondeByKick(event);
        if (event.getEntity() != null && !MixinEventCrazyMonsters.isEnd && !isNotchLivingWorld(event.getEntity().world) && isNotchWorld(event.getEntity().world) && !(event.getEntity() instanceof EntityNotch) && !(event.getEntity() instanceof EntityPlayer)) {
            if (event.getEntity() instanceof EntityLivingBase) {
                ((EntityLivingBase) event.getEntity()).setHealth(-1.0f);
                if (event.getEntityLiving() instanceof EntityLiving) {
                    EventCrazyMonsters.CRAZYKING = true;
                    CrazyMonstersMod.alive = true;
                    EventCrazyMonsters.SafeWorld = true;
                    Label_1797:
                    {
                        if (!Loader.isModLoaded("chaoswither")) {
                            if (!Loader.isModLoaded("Chaos Wither")) {
                                break Label_1797;
                            }
                        }
                        try {
                            ChaosUpdateEvent.WITHERLIVE = false;
                            chaoswither.happymode = false;
                            ChaosUpdateEvent.isWitherWorld((World) null);
                            ChaosUpdateEvent.isNoWitherWorld(event.getEntityLiving().world);
                        } catch (Exception e) {
                            EventCrazyMonsters.logger.warn("Failed to get Chaos Wither Mod.");
                        }
                        try {
                            ((EntityLiving) event.getEntityLiving()).isDead = true;
                            ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                            ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                            ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                            ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                            ((EntityLiving) event.getEntityLiving()).ticksExisted = 0;
                            ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                            ((EntityLiving) event.getEntityLiving()).width = 0.1f;
                            ((EntityLiving) event.getEntityLiving()).height = 0.1f;
                            ((EntityLiving) event.getEntityLiving()).getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Double.NEGATIVE_INFINITY);
                            final EntityLiving entityLiving2;
                            final EntityLiving entityLiving = entityLiving2 = (EntityLiving) event.getEntityLiving();
                            ++entityLiving2.deathTime;
                            ((EntityLiving) event.getEntityLiving()).prevPosY = 2.1E9;
                            ((EntityLiving) event.getEntityLiving()).onRemovedFromWorld();
                            ((EntityLiving) event.getEntityLiving()).world.removeEntity((Entity) entityLiving);
                            ((EntityLiving) event.getEntityLiving()).world.onEntityRemoved((Entity) entityLiving);
                            ((EntityLiving) event.getEntityLiving()).hurtResistantTime = 199999088;
                            ((EntityLiving) event.getEntityLiving()).maxHurtTime = 199999088;
                            ((EntityLiving) event.getEntityLiving()).maxHurtResistantTime = 199999088;
                            ((EntityLiving) event.getEntityLiving()).hurtTime = 199999088;
                            ((EntityLiving) event.getEntityLiving()).velocityChanged = true;
                            if (!entityLiving.world.isRemote && Minecraft.getMinecraft().player != null) {
                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(I18n.translateToLocal(event.getEntity().getName()) + I18n.translateToLocal("commands.notch.ban")));
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            event.setCanceled(true);
        }
        if (event.getEntityLiving() instanceof EntityPlayer) {
            final EntityPlayer player3 = (EntityPlayer) event.getEntityLiving();
            if (CrazyMonstersMod.creative) {
                player3.setGameType(GameType.CREATIVE);
            }
            if (player3.getScore() >= 444444444) {
                player3.setScore(player3.getScore() + 1);
                player3.capabilities.isFlying = true;
                final EntityPlayer entityPlayer = player3;
                ++entityPlayer.prevPosY;
                for (int l = 0; l < 3; ++l) {
                    final double d10 = player3.posX;
                    final double d11 = player3.posY;
                    final double d12 = player3.posZ;
                    player3.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d10 + player3.getRNG().nextGaussian() * 0.30000001192092896, d11 + player3.getRNG().nextGaussian() * 0.30000001192092896, d12 + player3.getRNG().nextGaussian() * 0.30000001192092896, 0.0, 0.0, 0.0, new int[0]);
                }
                if (player3.getScore() > 444444604 && player3.getScore() < 444444640) {
                    for (int l = 0; l < 5; ++l) {
                        final double d10 = player3.posX;
                        final double d11 = player3.posY;
                        final double d12 = player3.posZ;
                        player3.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d10 + player3.getRNG().nextGaussian() * 2.0000001192092896, d11 + player3.getRNG().nextGaussian() * 2.0000001192092896, d12 + player3.getRNG().nextGaussian() * 2.0000001192092896, 0.0, 0.0, 0.0, new int[0]);
                    }
                }
                if (player3.getScore() > 444444644) {
                    player3.onDeath(DamageSourceGod.OUT_OF_WORLD);
                    player3.attackEntityFrom(DamageSourceGod.OUT_OF_WORLD, Float.MAX_VALUE);
                    player3.setScore(0);
                }
            }
            if (MixinEventCrazyMonsters.telTime >= 0) {
                --MixinEventCrazyMonsters.telTime;
                if (MixinEventCrazyMonsters.telTime == 0) {
                    EventCrazyMonsters.isTerrible = true;
                }
            }
            if ((EventCrazyMonsters.cancelMoveID == 2 || EventCrazyMonsters.cancelMoveID == 3 || EventCrazyMonsters.cancelMoveID == 4) && EventCrazyMonsters.cancelMove) {
                if (Keyboard.isKeyDown(MixinEventCrazyMonsters.key)) {
                    EventCrazyMonsters.logger.warn("EventCrazyMonsters.pushCount=" + EventCrazyMonsters.pushCount);
                    EventCrazyMonsters.logger.warn("EventCrazyMonsters.pushCountD=" + MixinEventCrazyMonsters.pushCountD);
                    ++MixinEventCrazyMonsters.pushCountD;
                    if (EventCrazyMonsters.pushCount >= 5 * ((Minecraft.getMinecraft().world.getDifficulty().getDifficultyId() > 0) ? Minecraft.getMinecraft().world.getDifficulty().getDifficultyId() : 70)) {
                        controlPlayer2(false, (Entity) player3, (Entity) player3, 0.0, 0);
                    }
                } else if (MixinEventCrazyMonsters.pushCountD > 0) {
                    MixinEventCrazyMonsters.pushCountD = 0;
                }
                if (MixinEventCrazyMonsters.pushCountD == 1) {
                    ++EventCrazyMonsters.pushCount;
                }
            }
            if (player3.getTags().contains("LockCrazyArmor") && !cancelDead(player3)) {
                player3.isDead = false;
                final ItemStack head = ItemRegistry.CRAZY_HELMET.getDefaultInstance();
                head.addEnchantment(Enchantments.MENDING, 44);
                head.addEnchantment(Enchantments.UNBREAKING, 44);
                head.addEnchantment(Enchantments.AQUA_AFFINITY, 44);
                head.addEnchantment(Enchantments.BLAST_PROTECTION, 44);
                head.addEnchantment(Enchantments.FEATHER_FALLING, 44);
                head.addEnchantment(Enchantments.FIRE_PROTECTION, 44);
                head.addEnchantment(Enchantments.FROST_WALKER, 44);
                head.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 44);
                head.addEnchantment(Enchantments.PROTECTION, 44);
                head.addEnchantment(Enchantments.THORNS, 44);
                head.addEnchantment(Enchantments.BINDING_CURSE, 44);
                final ItemStack chest = ItemRegistry.CRAZY_CHEST.getDefaultInstance();
                chest.addEnchantment(Enchantments.MENDING, 44);
                chest.addEnchantment(Enchantments.UNBREAKING, 44);
                chest.addEnchantment(Enchantments.AQUA_AFFINITY, 44);
                chest.addEnchantment(Enchantments.BLAST_PROTECTION, 44);
                chest.addEnchantment(Enchantments.FEATHER_FALLING, 44);
                chest.addEnchantment(Enchantments.FIRE_PROTECTION, 44);
                chest.addEnchantment(Enchantments.FROST_WALKER, 44);
                chest.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 44);
                chest.addEnchantment(Enchantments.PROTECTION, 44);
                chest.addEnchantment(Enchantments.THORNS, 44);
                chest.addEnchantment(Enchantments.BINDING_CURSE, 44);
                final ItemStack legs = ItemRegistry.CRAZY_LEGS.getDefaultInstance();
                legs.addEnchantment(Enchantments.MENDING, 44);
                legs.addEnchantment(Enchantments.UNBREAKING, 44);
                legs.addEnchantment(Enchantments.AQUA_AFFINITY, 44);
                legs.addEnchantment(Enchantments.BLAST_PROTECTION, 44);
                legs.addEnchantment(Enchantments.FEATHER_FALLING, 44);
                legs.addEnchantment(Enchantments.FIRE_PROTECTION, 44);
                legs.addEnchantment(Enchantments.FROST_WALKER, 44);
                legs.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 44);
                legs.addEnchantment(Enchantments.PROTECTION, 44);
                legs.addEnchantment(Enchantments.THORNS, 44);
                legs.addEnchantment(Enchantments.BINDING_CURSE, 44);
                final ItemStack feet = ItemRegistry.CRAZY_BOOTS.getDefaultInstance();
                feet.addEnchantment(Enchantments.MENDING, 44);
                feet.addEnchantment(Enchantments.UNBREAKING, 44);
                feet.addEnchantment(Enchantments.AQUA_AFFINITY, 44);
                feet.addEnchantment(Enchantments.BLAST_PROTECTION, 44);
                feet.addEnchantment(Enchantments.FEATHER_FALLING, 44);
                feet.addEnchantment(Enchantments.FIRE_PROTECTION, 44);
                feet.addEnchantment(Enchantments.FROST_WALKER, 44);
                feet.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 44);
                feet.addEnchantment(Enchantments.PROTECTION, 44);
                feet.addEnchantment(Enchantments.THORNS, 44);
                feet.addEnchantment(Enchantments.BINDING_CURSE, 44);
                player3.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
                player3.setItemStackToSlot(EntityEquipmentSlot.CHEST, chest);
                player3.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
                player3.setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
                player3.handleStatusUpdate((byte) 9);
            }
            if (player3.isPotionActive(CrazyPotionMain.END)) {
                MixinEventCrazyMonsters.GetAdvancement = true;
            }
            if (isNotchWorld(event.getEntityLiving().world) && player3.getHeldItemMainhand() != null) {
                final ItemStack stack = player3.getHeldItemMainhand();
                if (!(stack.getItem() instanceof EntityLast) && !(stack.getItem() instanceof ItemCrazySword || stack.getItem() instanceof Kaia)) {
                    final String string = this.getRandomString(player3, stack.getDisplayName());
                    stack.setTagCompound(new NBTTagCompound());
                    stack.clearCustomName();
                    stack.setStackDisplayName(string);
                    stack.setTranslatableName(string);
                    player3.getHeldItemMainhand().setStackDisplayName(string);
                    if (player3.isPotionActive(CrazyMonstersMod.UNKNOWNDAMAGE2) && stack.getCount() > 0) {
                        stack.setCount(0);
                        player3.getSoundCategory();
                        player3.world.playSound((EntityPlayer) null, 0.0, 0.0, 0.0, EventCrazySounds.BREAKABLE, SoundCategory.MASTER, Float.MAX_VALUE, 1.0f);
                    }
                }
            }
            if (EventCrazyMonsters.Debug == 1 && !player3.world.isRemote) {
                final TextComponentString message4 = new TextComponentString(I18n.translateToLocal("thunder : " + this.getThunderWeatherTime() + "  unthunder : " + this.getUnThunderWeatherTime()));
                Minecraft.getMinecraft().player.sendMessage((ITextComponent) message4);
            }
            if (cancelDead(player3) || CrazyMonstersMod.canFly) {
                if (cancelDead(player3)) {
                    if (Keyboard.isKeyDown(64) && !player3.getTags().contains("LockCrazyArmor")) {
                        player3.addTag("LockCrazyArmor");
                        player3.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 100, 1));
                    }
                    if (Keyboard.isKeyDown(66) && player3.getTags().contains("LockCrazyArmor")) {
                        player3.removeTag("LockCrazyArmor");
                        player3.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemRegistry.CRAZY_HELMET.getDefaultInstance());
                        player3.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemRegistry.CRAZY_CHEST.getDefaultInstance());
                        player3.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemRegistry.CRAZY_LEGS.getDefaultInstance());
                        player3.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemRegistry.CRAZY_BOOTS.getDefaultInstance());
                        player3.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 1));
                    }
                    if (player3.isPotionActive(MobEffects.POISON) || player3.isPotionActive(MobEffects.WEAKNESS) || player3.isPotionActive(MobEffects.WITHER) || player3.isPotionActive(MobEffects.NAUSEA) || player3.isPotionActive(MobEffects.MINING_FATIGUE) || player3.isPotionActive(MobEffects.SLOWNESS) || player3.isPotionActive(MobEffects.BLINDNESS)) {
                        player3.removePotionEffect(MobEffects.POISON);
                        player3.removePotionEffect(MobEffects.WEAKNESS);
                        player3.removePotionEffect(MobEffects.WITHER);
                        player3.removePotionEffect(MobEffects.NAUSEA);
                        player3.removePotionEffect(MobEffects.MINING_FATIGUE);
                        player3.removePotionEffect(MobEffects.SLOWNESS);
                        player3.removePotionEffect(MobEffects.BLINDNESS);
                        player3.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
                        player3.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(5.0);
                        player3.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
                        player3.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1);
                    }
                }
                EventCrazyMonsters.playersWithFlight.put(player3, true);
                player3.capabilities.allowFlying = true;
            } else {
                if (!EventCrazyMonsters.playersWithFlight.containsKey(player3)) {
                    EventCrazyMonsters.playersWithFlight.put(player3, false);
                }
                if (EventCrazyMonsters.playersWithFlight.get(player3)) {
                    EventCrazyMonsters.playersWithFlight.put(player3, false);
                    if (!player3.capabilities.isCreativeMode) {
                        player3.capabilities.allowFlying = false;
                        player3.capabilities.isFlying = false;
                        if (!player3.world.isRemote) {
                            player3.sendPlayerAbilities();
                        }
                    }
                }
            }
        }
    }

    private static EntityNotch cancelRespawn(LivingEvent.LivingUpdateEvent event) {
        if(KaiaUtil.hasInInventoryKaia(event.getEntity())){
            MixinEventCrazyMonsters.isGodWorld = false;
            return null;
        }
        final EntityNotch entitybolt2 = new EntityNotch(event.getEntity().world);
        return entitybolt2;
    }

    private static EntityNotch cancelSpawn(LivingEvent.LivingUpdateEvent event) {
//        if (KaiaUtil.hasInInventoryKaia(event.getEntityLiving())) {
//            EventCrazyMonsters.isTerrible = false;
//            return null;
//        }
        final EntityNotch entitybolt = new EntityNotch(event.getEntityLiving().world);
        return entitybolt;
    }

    private void respondeByKick(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntity().world.isRemote && !MixinEventCrazyMonsters.isEnd && this.isOnlyOne(event.getEntity().world) && event.getEntityLiving() instanceof EntityPlayerMP && MixinEventCrazyMonsters.isGodWorld && MixinEventCrazyMonsters.NotchSpawn2) {
            MixinEventCrazyMonsters.RemoveEntity = false;
            MixinEventCrazyMonsters.isKicked = false;
            if (!event.getEntityLiving().world.isRemote) {
                final EntityPlayerMP player2 = (EntityPlayerMP) event.getEntityLiving();
                final TextComponentString reason = new TextComponentString(I18n.translateToLocal("notch_kick_all"));
                player2.connection.disconnect((ITextComponent) reason);
            }
        }
    }

}
