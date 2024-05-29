package com.omnipotent.common.mixin.mods;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.crazymonsters.CrazyMonstersMod;
import net.crazymonsters.armor.ArmorCrazy;
import net.crazymonsters.block.BlockRegistry;
import net.crazymonsters.damagesource.DamageSourceGod;
import net.crazymonsters.entity.EnemyUpdate;
import net.crazymonsters.entity.EntityCrazyBase;
import net.crazymonsters.entity.EntityCrazyCreature;
import net.crazymonsters.entity.EntityNotch;
import net.crazymonsters.entity.ai.EntityAIAttackRangedWithJump;
import net.crazymonsters.entity.ai.EntityAIStrangeMelee;
import net.crazymonsters.entity.ai.object.EnumMultiAttackerMode;
import net.crazymonsters.entity.weapon.EntityFlameball2;
import net.crazymonsters.entity.weapon.EntityFlameball3;
import net.crazymonsters.event.EventAdvancementTrigger2;
import net.crazymonsters.event.EventCrazyMonsters;
import net.crazymonsters.event.EventCrazyMonstersRegister;
import net.crazymonsters.item.ItemRegistry;
import net.crazymonsters.potions.CrazyPotionMain;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = EntityNotch.class, remap = false)
public abstract class MixinEntityNotch extends EntityCrazyCreature implements IRangedAttackMob {

    @Shadow
    public abstract float getNotchHealth();

    @Shadow
    private static float attackDamage;

    @Shadow
    protected abstract void setKeyChange(int id);

    public MixinEntityNotch(World worldIn) {
        super(worldIn);
    }

    @Shadow
    @Final
    private static DataParameter<Float> NOTCH_HEALTH;

    @Shadow
    private EnumMultiAttackerMode mode;

    @Shadow
    protected abstract void setTargetedEntity(int entityId);

    @Shadow
    private EntityLivingBase attackTarget;

    @Shadow
    protected boolean allowDead;

    @Shadow
    protected abstract void onNotchDeathUpdate();

    @Shadow
    protected abstract void AntiOneShot();

    @Shadow
    private int Tick;

    @Shadow
    public abstract void setModeInt(int id);

    @Shadow
    private int angerValue;

    @Shadow
    private int angerLvl;

    @Shadow
    private int attackID;

    @Shadow
    private int SpawnTime;

    @Shadow
    protected abstract boolean spawnEntity(Entity entityIn);

    @Shadow
    public abstract int getSpawnY(World worldIn, Entity entity);

    @Shadow
    public abstract int getSpawnY(World worldIn, BlockPos pos);

    @Shadow
    protected abstract void CrazyLightningToEntity(EntityLivingBase entity2);

    @Shadow
    protected abstract void CrazyStructureToEntity(EntityLivingBase entity2);

    @Shadow
    public abstract void saveOptions();

    @Shadow
    protected abstract void SummonBlackHole();

    @Shadow
    protected abstract void SummonBlackHoleHard();

    @Shadow
    private String Name;

    @Shadow
    private int angerTimer;

    @Shadow
    public abstract boolean canFly();

    @Shadow
    public abstract int getWatchedTargetId(int head);

    @Shadow
    @Final
    private float[] yRotOHeads;

    @Shadow
    @Final
    private float[] yRotationHeads;

    @Shadow
    @Final
    private float[] xRotationHeads;

    @Shadow
    @Final
    private float[] xRotOHeads;

    @Shadow
    protected abstract double getHeadX(int p_82214_1_);

    @Shadow
    protected abstract double getHeadY(int p_82208_1_);

    @Shadow
    protected abstract double getHeadZ(int p_82213_1_);

    @Shadow
    protected abstract float rotlerp(float p_82204_1_, float p_82204_2_, float p_82204_3_);

    @Shadow
    protected abstract void throwEnderPearl();

    @Shadow
    @Final
    private EntityAIAttackRangedWithJump aiArrowAttack;

    @Shadow
    @Final
    private EntityAIStrangeMelee aiAttackOnCollide;

    @Shadow
    private boolean canFlying;

    @Shadow
    private int attackTick;

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    private void CrazyAttackToPlayer(final EntityPlayer e1) {
        final EntityPlayer player3 = e1;
        if (KaiaUtil.hasInInventoryKaia(player3))
            return;
        if (!this.world.isRemote) {
            player3.hurtResistantTime = 0;
            if (!player3.attackEntityFrom(new DamageSource("cheating").setDamageBypassesArmor().setDamageIsAbsolute().setDamageAllowedInCreativeMode(), MixinEntityNotch.attackDamage)) {
                for (final ItemStack itemStack : player3.inventory.armorInventory) {
                    if (itemStack == null || !(itemStack.getItem() instanceof ArmorCrazy)) {
                        player3.inventory.dropAllItems();
                        player3.clearActivePotions();
                        player3.setHealth(0.0f);
                    }
                }
            } else {
                e1.onDeath((DamageSource) new DamageSourceGod((Entity) this));
                if (e1 instanceof EntityPlayer && !e1.isDead) {
                    e1.attackEntityFrom(new DamageSourceGod((Entity) this).setDamageBypassesArmor().setDamageAllowedInCreativeMode(), 7777.0f);
                    e1.getCombatTracker().trackDamage((DamageSource) new DamageSourceGod((Entity) this), e1.getHealth(), e1.getHealth());
                    e1.addStat(StatList.DEATHS, 1);
                }
            }
            if (e1 instanceof EntityPlayer) {
                final EntityPlayer player4 = e1;
                e1.setHealth(0.0f);
                if (!player4.world.isRemote) {
                    for (int k = 0; k < player4.inventory.getSizeInventory(); ++k) {
                        final ItemStack itemStack2 = player4.inventory.getStackInSlot(k);
                        if (itemStack2 != null && itemStack2.getItemDamage() > 0) {
                            itemStack2.setItemDamage(itemStack2.getItemDamage() + 1);
                        }
                    }
                    final ItemStack boots2 = player4.inventory.armorItemInSlot(0);
                    final ItemStack legs2 = player4.inventory.armorItemInSlot(1);
                    final ItemStack chest2 = player4.inventory.armorItemInSlot(2);
                    final ItemStack helmet2 = player4.inventory.armorItemInSlot(3);
                    if (boots2 != null && boots2.getItemDamage() > 0) {
                        boots2.setItemDamage(boots2.getItemDamage() + 2);
                    }
                    if (legs2 != null && legs2.getItemDamage() > 0) {
                        legs2.setItemDamage(legs2.getItemDamage() + 4);
                    }
                    if (chest2 != null && chest2.getItemDamage() > 0) {
                        chest2.setItemDamage(chest2.getItemDamage() + 5);
                    }
                    if (helmet2 != null && helmet2.getItemDamage() > 0) {
                        helmet2.setItemDamage(helmet2.getItemDamage() + 2);
                    }
                }
            }
            e1.addPotionEffect(new PotionEffect(CrazyMonstersMod.UNKNOWNDAMAGE2, 100, 5));
            e1.addPotionEffect(new PotionEffect(CrazyMonstersMod.ANTICHEATING, 100, 5));
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void setKeyChangeMain() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null && KaiaUtil.hasInInventoryKaia(player))
            return;
        final int max = this.rand.nextInt(10);
        int already0 = 0;
        int already2 = 0;
        int already3 = 0;
        int already4 = 0;
        int already5 = 0;
        int already6 = 0;
        int already7 = 0;
        int already8 = 0;
        int already9 = 0;
        for (int i = 0; i < max; ++i) {
            switch (this.rand.nextInt(10)) {
                case 0: {
                    if (already0 == 0) {
                        this.setKeyChange(0);
                        already0 = 1;
                        break;
                    }
                    break;
                }
                case 1: {
                    if (already2 == 0) {
                        this.setKeyChange(1);
                        already2 = 1;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (already3 == 0) {
                        this.setKeyChange(2);
                        already3 = 1;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (already4 == 0) {
                        this.setKeyChange(3);
                        already4 = 1;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (already5 == 0) {
                        this.setKeyChange(4);
                        already5 = 1;
                        break;
                    }
                    break;
                }
                case 5: {
                    if (already6 == 0) {
                        this.setKeyChange(5);
                        already6 = 1;
                        break;
                    }
                    break;
                }
                case 6: {
                    if (already7 == 0) {
                        this.setKeyChange(6);
                        already7 = 1;
                        break;
                    }
                    break;
                }
                case 7: {
                    if (already8 == 0) {
                        this.setKeyChange(7);
                        already8 = 1;
                        break;
                    }
                    break;
                }
                case 8: {
                    if (already9 == 0) {
                        this.setKeyChange(8);
                        already9 = 1;
                        break;
                    }
                    break;
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void setNotchHealth(final float health) {
        this.dataManager.set((DataParameter) MixinEntityNotch.NOTCH_HEALTH, (Object) MathHelper.clamp(health, 0.0f, this.getMaxHealth()));
        this.setCrazyHealth(health);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setCrazyHealth(float health) {
        super.setCrazyHealth(health);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onDeath(DamageSource damageSource) {
        if (absoluteDead) {
            this.setNotchHealth(0);
            super.onDeath(damageSource);
            return;
        }
        if (this.getNotchHealth() != 1.0F && this.getMaxHealth() < 2.14748365E9F && !this.world.isRemote) {
            this.isDead = false;
        }
        this.setNotchHealth(this.getMaxHealth());
        if (!(this.getNotchHealth() > 1.0F) && (!(this.getNotchHealth() < 1.0F) || this.world.isRemote)) {
            super.onDeath(damageSource);
        }
    }

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void onLivingUpdate() {
//        if (this.isAIDisabled()) {
//            this.setNoAI(false);
//        }
//        if (this.getAttackTarget() != null) {
//            if (this.mode == EnumMultiAttackerMode.SWORD) {
//                this.setTargetedEntity(1);
//            }
//            if (this.mode == EnumMultiAttackerMode.COMMAND) {
//                this.setTargetedEntity(2);
//            }
//            if (this.mode == EnumMultiAttackerMode.EXPLOSION) {
//                this.setTargetedEntity(3);
//            }
//            if (this.mode == EnumMultiAttackerMode.MAGIC) {
//                this.setTargetedEntity(4);
//            }
//            if (this.mode == EnumMultiAttackerMode.WORLDBREAKER) {
//                this.setTargetedEntity(5);
//            }
//        }
//        this.attackTarget = this.getAttackTarget();
//        EventCrazyMonsters.EntityNotch((EntityNotch) (Object) this);
//        EventCrazyMonsters.CRAZYKING = true;
//        CrazyMonstersMod.alive = true;
//        EventCrazyMonsters.SafeWorld = true;
//        EventCrazyMonsters.isAgain = false;
//        this.clearActivePotions();
//        if (this.getNotchHealth() != 1.0f && this.getMaxHealth() < 2.14748365E9f) {
//            this.isDead = false;
//        }
//        if (!this.world.isRemote && this.attackTarget != null) {
//            final boolean flag2 = this.getDistanceSq((Entity) this.attackTarget) > 2500.0;
//            final boolean flag3 = this.attackTarget.posY < 300.0;
//            if (flag3 && flag2) {
//                if (this.ticksExisted % 200 == 0) {
//                    final TextComponentString message3 = new TextComponentString(I18n.translateToLocal("<" + this.getName()) + "> My position is x:" + (int) this.posX + " y:" + (int) this.posY + " z:" + (int) this.posZ);
//                    final TextComponentString message4 = new TextComponentString(I18n.translateToLocal("<" + this.getName()) + "> Target :" + this.attackTarget.getName() + " x:" + (int) this.attackTarget.posX + " y:" + (int) this.attackTarget.posY + " z:" + (int) this.attackTarget.posZ);
//                    Minecraft.getMinecraft().player.sendMessage((ITextComponent) message3);
//                    Minecraft.getMinecraft().player.sendMessage((ITextComponent) message4);
//                }
//                if (this.ticksExisted % 500 == 0) {
//                    this.setLocationAndAngles(this.getAttackTarget().posX + this.getRNG().nextGaussian() * 50.0, this.getAttackTarget().posY + this.getRNG().nextInt(50), this.getAttackTarget().posZ + this.getRNG().nextGaussian() * 50.0, this.rotationYaw, this.rotationPitch);
//                    Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.tp") + this.posX + ", " + this.posY + ", " + this.posZ + "]"));
//                }
//            }
//        }
//        if (!EnemyUpdate.isRetry) {
//            final List<Entity> list3339 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(200.0, 200.0, 200.0).offset(-100.0, -100.0, -100.0));
//            list3339.remove(this);
//            if (list3339 != null && !list3339.isEmpty()) {
//                for (int i1 = 0; i1 < list3339.size(); ++i1) {
//                    final Entity entity = list3339.get(i1);
//                    if (entity instanceof EntityCrazyBase && entity instanceof EntityNotch) {
//                        final MixinEntityNotch Notch = (MixinEntityNotch) entity;
//                        Notch.allowDead = true;
//                        Notch.posY = 2.1E9;
//                        Notch.isDead = true;
//                    }
//                }
//            }
//        }
//        if (this.getNotchHealth() != 0.0f) {
//            this.deathTime = 0;
//        }
//        if (this.getNotchHealth() <= 0.0f) {
//            this.onNotchDeathUpdate();
//        }
//        this.world.setWorldTime(this.world.getWorldTime() + 500L);
//        if (this.getCrazyHealth() < 1.0 && this.getMaxHealth() < 2.14748365E9f && this.getCrazyHealth() > -2.14748365E9f) {
//            this.AntiOneShot();
//        }
//        if (this.getCrazyHealth() != 1.0 && this.getMaxHealth() < 2.14748365E9f && this.isDead) {
//            this.AntiOneShot();
//        }
//        if (this.getNotchHealth() == 1.0 && this.getMaxHealth() == 2.14748365E9f) {
//            this.allowDead = true;
//        }
//        if (!this.world.isRemote) {
//            if (this.rand.nextInt(200) == 0) {
//                if (this.getAttackTarget() != null) {
//                    if (Minecraft.getMinecraft().player != null) {
//                        this.setNoAI(false);
//                        switch (this.rand.nextInt(5)) {
//                            case 0: {
//                                this.mode = EnumMultiAttackerMode.SWORD;
//                                this.Tick = 0;
//                                this.setModeInt(1);
//                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.sword") + "]"));
//                                break;
//                            }
//                            case 1: {
//                                this.mode = EnumMultiAttackerMode.COMMAND;
//                                this.Tick = 0;
//                                this.setModeInt(2);
//                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.command") + "]"));
//                                break;
//                            }
//                            case 2: {
//                                this.mode = EnumMultiAttackerMode.EXPLOSION;
//                                this.Tick = 0;
//                                this.setModeInt(3);
//                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.explosion") + "]"));
//                                break;
//                            }
//                            case 3: {
//                                this.mode = EnumMultiAttackerMode.MAGIC;
//                                this.Tick = 0;
//                                this.setModeInt(4);
//                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.magic") + "]"));
//                                break;
//                            }
//                            case 4: {
//                                this.mode = EnumMultiAttackerMode.WORLDBREAKER;
//                                this.Tick = 0;
//                                this.setModeInt(5);
//                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.worldbreaker") + "]"));
//                                break;
//                            }
//                        }
//                    }
//                } else if (Minecraft.getMinecraft().player != null) {
//                    this.setNoAI(false);
//                    switch (this.rand.nextInt(2)) {
//                        case 0: {
//                            this.mode = EnumMultiAttackerMode.COMMAND;
//                            this.Tick = 0;
//                            this.setModeInt(2);
//                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.command") + "]"));
//                            break;
//                        }
//                        case 1: {
//                            this.mode = EnumMultiAttackerMode.WORLDBREAKER;
//                            this.Tick = 0;
//                            this.setModeInt(5);
//                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.mode") + I18n.translateToLocal("change_mode.worldbreaker") + "]"));
//                            break;
//                        }
//                    }
//                }
//            }
//            if (this.getAttackTarget() != null && !(this.getAttackTarget() instanceof EntityPlayer) && this.Tick == 1) {
//                this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.UNLUCK, 100, 1));
//                if (this.getAttackTarget().isPotionActive(MobEffects.UNLUCK)) {
//                    ++this.angerValue;
//                } else {
//                    this.angerValue += 2;
//                }
//            }
//            if (this.getAttackTarget() != null && this.angerValue > 10) {
//                final Entity target = (Entity) this.getAttackTarget();
//                target.addTag("PeaceOfDeath");
//                MixinEnemyUpdate.isTerrible = true;
//                ++this.angerLvl;
//                this.angerValue = 0;
//            }
//            if (this.getAttackTarget() != null && this.angerLvl > 5) {
//                final Entity target = (Entity) this.getAttackTarget();
//                this.angerLvl = 0;
//                target.addTag("PeaceOfDeath");
//                MixinEnemyUpdate.isTerrible = true;
//                MixinEnemyUpdate.isKicked = true;
//                MixinEnemyUpdate.AntiCrazyWorld((Entity) this);
//            }
//            if (this.getAttackTarget() == null) {
//                this.angerLvl = 0;
//                this.angerValue = 0;
//            }
//            if (this.mode == EnumMultiAttackerMode.SWORD) {
//                if (this.Tick == 1 && this.attackID == 0 && this.getAttackTarget() != null) {
//                    switch (this.rand.nextInt(4)) {
//                        case 0: {
//                            this.attackID = 1;
//                            break;
//                        }
//                        case 1: {
//                            this.attackID = 2;
//                            break;
//                        }
//                        case 2: {
//                            this.attackID = 3;
//                            break;
//                        }
//                        case 3: {
//                            this.attackID = 14;
//                            break;
//                        }
//                    }
//                }
//                if (this.Tick > 250) {
//                    this.Tick = 0;
//                }
//            }
//            if (this.mode == EnumMultiAttackerMode.COMMAND) {
//                if (this.Tick == 1) {
//                    switch (this.getRNG().nextInt(8)) {
//                        case 0: {
//                            if (this.SpawnTime > 2 && this.ticksExisted != 0) {
//                                final List<Entity> list3340 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(200.0, 200.0, 200.0).offset(-100.0, -100.0, -100.0));
//                                try {
//                                    if (list3340 != null && !list3340.isEmpty()) {
//                                        for (int j = 0; j < list3340.size(); ++j) {
//                                            final Entity entity2 = list3340.get(j);
//                                            if (entity2 != null && entity2 instanceof EntityLivingBase) {
//                                                final EntityLivingBase e1 = (EntityLivingBase) entity2;
//                                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal(e1.getName()) + I18n.translateToLocal("commands.notch.kick") + "]"));
//                                                if (e1.getMaxHealth() >= 1.0E8f) {
//                                                    e1.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.0);
//                                                    e1.setHealth(0.0f);
//                                                    if (e1.isEntityAlive()) {
//                                                        e1.onRemovedFromWorld();
//                                                        e1.world.removeEntity((Entity) e1);
//                                                        e1.world.onEntityRemoved((Entity) e1);
//                                                        e1.addTag("PeaceOfDeath");
//                                                        MixinEnemyUpdate.isTerrible = true;
//                                                        MixinEnemyUpdate.isKicked = true;
//                                                        MixinEnemyUpdate.AntiCrazyWorld((Entity) this);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                } catch (Exception e2) {
//                                    e2.printStackTrace();
//                                }
//                                break;
//                            }
//                            if (this.SpawnTime > 2) {
//                                final List<Entity> list3340 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(200.0, 200.0, 200.0).offset(-100.0, -100.0, -100.0));
//                                try {
//                                    if (list3340 != null && !list3340.isEmpty()) {
//                                        for (int i2 = 0; i2 < list3340.size(); ++i2) {
//                                            final Entity entity2 = list3340.get(i2);
//                                            if (entity2 != null && entity2 instanceof EntityLivingBase) {
//                                                final EntityLivingBase e1 = (EntityLivingBase) entity2;
//                                                Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal(e1.getName()) + I18n.translateToLocal("commands.notch.kick") + "]"));
//                                                e1.onRemovedFromWorld();
//                                                e1.world.removeEntity((Entity) e1);
//                                                e1.world.onEntityRemoved((Entity) e1);
//                                                e1.addTag("PeaceOfDeath");
//                                                MixinEnemyUpdate.isTerrible = true;
//                                                MixinEnemyUpdate.isKicked = true;
//                                                MixinEnemyUpdate.AntiCrazyWorld((Entity) this);
//                                            }
//                                        }
//                                    }
//                                } catch (Exception e2) {
//                                    e2.printStackTrace();
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 1: {
//                            final List<Entity> list3341 = this.world.loadedEntityList;
//                            if (!list3341.isEmpty() && list3341 != null) {
//                                for (int j = 0; j < list3341.size(); ++j) {
//                                    final Entity entity = list3341.get(j);
//                                    if (entity instanceof EntityLivingBase) {
//                                        Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal(entity.getName()) + I18n.translateToLocal("commands.notch.kill") + "]"));
//                                        ((EntityLivingBase) entity).setHealth(0.0f);
//                                        if (((EntityLivingBase) entity).getHealth() > 0.0f) {
//                                            entity.addTag("PeaceOfDeath");
//                                            entity.isDead = true;
//                                        }
//                                    } else if (!(entity instanceof EntityCrazyBase)) {
//                                        entity.isDead = true;
//                                        entity.addTag("PeaceOfDeath");
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 2: {
//                            final List<Entity> list3342 = this.world.loadedEntityList;
//                            if (!list3342.isEmpty() && list3342 != null) {
//                                for (int k = 0; k < list3342.size(); ++k) {
//                                    final Entity entity3 = list3342.get(k);
//                                    if (entity3 instanceof EntityLivingBase) {
//                                        Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.effect") + I18n.translateToLocal(entity3.getName()) + I18n.translateToLocal("commands.notch.effect2") + "]"));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.UNLUCK, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.HUNGER, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(MobEffects.WITHER, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        if (entity3 instanceof EntityPlayer && !KaiaUtil.hasInInventoryKaia(entity3) && !EventCrazyMonsters.cancelDead((EntityPlayer) entity3)) {
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyMonstersMod.ANTICHEATING, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.BROKEN, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.UNKNOWNDAMAGE, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.THUNDER, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        } else if (entity3 instanceof EntityPlayer && !KaiaUtil.hasInInventoryKaia(entity3)) {
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.UNKNOWNDAMAGE, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyMonstersMod.ANTICHEATING, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.THUNDER, this.rand.nextInt(150), this.rand.nextInt(255)));
//                                        } else if (!UtilityHelper.isPlayer(entity3) || (entity3 instanceof EntityPlayer && !KaiaUtil.hasInInventoryKaia(entity3))) {
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.UNKNOWNDAMAGE, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                            ((EntityLivingBase) entity3).addPotionEffect(new PotionEffect(CrazyPotionMain.THUNDER, this.rand.nextInt(1500), this.rand.nextInt(255)));
//                                        }
//                                        if (((EntityLivingBase) entity3).isPotionActive(MobEffects.UNLUCK)) {
//                                            entity3.addTag("PeaceOfDeath");
//                                            entity3.isDead = true;
//                                        }
//                                    } else if (!(entity3 instanceof EntityCrazyBase)) {
//                                        entity3.isDead = true;
//                                        entity3.addTag("PeaceOfDeath");
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 3: {
//                            EventCrazyMonsters.setThunderWeatherTime(1500, 0);
//                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.weather") + "]"));
//                            break;
//                        }
//                        case 4: {
//                            final List<Entity> list3343 = this.world.loadedEntityList;
//                            if (!list3343.isEmpty() && list3343 != null) {
//                                for (int l = 0; l < list3343.size(); ++l) {
//                                    final Entity entity4 = list3343.get(l);
//                                    if (entity4 instanceof EntityLivingBase) {
//                                        if (entity4 instanceof EntityPlayer && !EventCrazyMonsters.cancelDead((EntityPlayer) entity4)) {
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.gamemode") + I18n.translateToLocal(entity4.getName()) + I18n.translateToLocal("commands.notch.gamemode2") + "]"));
//                                            ((EntityPlayer) entity4).setGameType(GameType.ADVENTURE);
//                                        } else if (entity4 instanceof EntityPlayer && ((EntityPlayer) entity4).getHeldItemMainhand() != ItemRegistry.crazySword.getDefaultInstance() && ((EntityPlayer) entity4).getHeldItemMainhand() != ItemRegistry.crazySword2.getDefaultInstance()) {
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.gamemode") + I18n.translateToLocal(entity4.getName()) + I18n.translateToLocal("commands.notch.gamemode2") + "]"));
//                                            ((EntityPlayer) entity4).setGameType(GameType.ADVENTURE);
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 5: {
//                            final List<Entity> list3344 = this.world.loadedEntityList;
//                            if (!list3344.isEmpty() && list3344 != null) {
//                                for (int m = 0; m < list3344.size(); ++m) {
//                                    final Entity entity5 = list3344.get(m);
//                                    if (entity5 instanceof EntityLivingBase) {
//                                        if (entity5 instanceof EntityPlayer && !EventCrazyMonsters.cancelDead((EntityPlayer) entity5)) {
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.replaceitem") + I18n.translateToLocal(entity5.getName()) + I18n.translateToLocal("commands.notch.replaceitem2") + "]"));
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.replaceitem") + I18n.translateToLocal(entity5.getName()) + I18n.translateToLocal("commands.notch.replaceitem3") + "]"));
//                                            ((EntityPlayer) entity5).setItemStackToSlot(EntityEquipmentSlot.MAINHAND, Items.APPLE.getDefaultInstance());
//                                            ((EntityPlayer) entity5).setItemStackToSlot(EntityEquipmentSlot.OFFHAND, Items.APPLE.getDefaultInstance());
//                                        } else if (entity5 instanceof EntityPlayer && ((EntityPlayer) entity5).getHeldItemMainhand() != ItemRegistry.crazySword.getDefaultInstance() && ((EntityPlayer) entity5).getHeldItemMainhand() != ItemRegistry.crazySword2.getDefaultInstance()) {
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.replaceitem") + I18n.translateToLocal(entity5.getName()) + I18n.translateToLocal("commands.notch.replaceitem2") + "]"));
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.replaceitem") + I18n.translateToLocal(entity5.getName()) + I18n.translateToLocal("commands.notch.replaceitem3") + "]"));
//                                            ((EntityPlayer) entity5).setItemStackToSlot(EntityEquipmentSlot.MAINHAND, Items.APPLE.getDefaultInstance());
//                                            ((EntityPlayer) entity5).setItemStackToSlot(EntityEquipmentSlot.OFFHAND, Items.APPLE.getDefaultInstance());
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 6: {
//                            this.world.getWorldBorder().setCenter(this.posX, this.posZ);
//                            this.world.getWorldBorder().setSize(10 + this.rand.nextInt(90));
//                            this.world.getWorldBorder().setTransition((double) this.world.getWorldBorder().getSize());
//                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.worldboarder") + "]"));
//                            break;
//                        }
//                        case 7: {
//                            final List<Entity> list3345 = this.world.loadedEntityList;
//                            if (!list3345.isEmpty() && list3345 != null) {
//                                for (int i3 = 0; i3 < list3345.size(); ++i3) {
//                                    final Entity entity6 = list3345.get(i3);
//                                    if (entity6 instanceof EntityLivingBase && entity6 instanceof EntityPlayer) {
//                                        Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString(TextFormatting.GRAY + "[" + I18n.translateToLocal("entity.Notch.name") + ":" + I18n.translateToLocal("commands.notch.enchant") + "]"));
//                                        if (!EventCrazyMonsters.cancelDead((EntityPlayer) entity6)) {
//                                            for (final ItemStack item : ((EntityPlayer) entity6).inventory.armorInventory) {
//                                                item.addEnchantment(Enchantments.BINDING_CURSE, 44);
//                                                item.addEnchantment(Enchantments.VANISHING_CURSE, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.DropCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.EffectCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.LostLandCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.JumpCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.DeadCurse, 44);
//                                            }
//                                        }
//                                        for (final ItemStack item : ((EntityPlayer) entity6).inventory.mainInventory) {
//                                            if (item != ItemRegistry.crazySword.getDefaultInstance() || item != ItemRegistry.crazySword2.getDefaultInstance()) {
//                                                item.addEnchantment(Enchantments.BINDING_CURSE, 44);
//                                                item.addEnchantment(Enchantments.VANISHING_CURSE, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.DropCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.EffectCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.LostLandCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.JumpCurse, 44);
//                                                item.addEnchantment(EventCrazyMonstersRegister.DeadCurse, 44);
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                    }
//                }
//                if (this.Tick > 120) {
//                    this.Tick = 0;
//                }
//            }
//            if (this.mode == EnumMultiAttackerMode.EXPLOSION) {
//                if (this.Tick == 1) {
//                    switch (this.getRNG().nextInt(3)) {
//                        case 0: {
//                            if (this.getAttackTarget() != null) {
//                                switch (this.getRNG().nextInt(2)) {
//                                    case 0: {
//                                        for (int i4 = 0; i4 < 1000; ++i4) {
//                                            final EntityTNTPrimed tnt = new EntityTNTPrimed(this.world);
//                                            tnt.setLocationAndAngles(this.getAttackTarget().posX + this.getRNG().nextGaussian() * 50.0, this.getAttackTarget().posY + 50.0, this.getAttackTarget().posZ + this.getRNG().nextGaussian() * 50.0, this.getAttackTarget().rotationYaw, this.getAttackTarget().rotationPitch);
//                                            this.spawnEntity((Entity) tnt);
//                                        }
//                                        break;
//                                    }
//                                    case 1: {
//                                        for (int i4 = 0; i4 < 1000; ++i4) {
//                                            final EntityTNTPrimed tnt = new EntityTNTPrimed(this.world);
//                                            tnt.setLocationAndAngles(this.getAttackTarget().posX + this.getRNG().nextGaussian() * 50.0, (double) this.getSpawnY(this.world, (Entity) this.getAttackTarget()), this.getAttackTarget().posZ + this.getRNG().nextGaussian() * 50.0, this.getAttackTarget().rotationYaw, this.getAttackTarget().rotationPitch);
//                                            this.spawnEntity((Entity) tnt);
//                                        }
//                                        break;
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 1: {
//                            if (this.getAttackTarget() != null) {
//                                this.setNoAI(true);
//                                for (int iii = 0; iii < 260; ++iii) {
//                                    final double y = this.getRNG().nextDouble() * 0.5;
//                                    final double x = this.getRNG().nextGaussian() * 0.4;
//                                    final double z = this.getRNG().nextGaussian() * 0.4;
//                                    final EntityFlameball2 tnt2 = new EntityFlameball2(this.world, (EntityCrazyBase) this, x, y, z);
//                                    tnt2.posX = this.getAttackTarget().posX + this.getRNG().nextDouble() * 40.0;
//                                    tnt2.posZ = this.getAttackTarget().posZ + this.getRNG().nextDouble() * 40.0;
//                                    tnt2.posY = this.getSpawnY(this.world, new BlockPos(tnt2.posX, this.posY, tnt2.posZ));
//                                    this.spawnEntity(tnt2);
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 2: {
//                            if (this.getAttackTarget() != null) {
//                                this.setNoAI(true);
//                                for (int i4 = 0; i4 < 5; ++i4) {
//                                    for (int ii = 0; ii < 5; ++ii) {
//                                        for (int iii2 = 0; iii2 < 5; ++iii2) {
//                                            final double px = this.posX + this.rand.nextGaussian() * 50.0;
//                                            final double py = this.posY + this.rand.nextInt(50);
//                                            final double pz = this.posZ + this.rand.nextGaussian() * 50.0;
//                                            final double z2 = this.getAttackTarget().posZ + this.rand.nextGaussian() * 20.0 - pz;
//                                            final double x2 = this.getAttackTarget().posX + this.rand.nextGaussian() * 20.0 - px;
//                                            final double y2 = this.getAttackTarget().posY - py;
//                                            final EntityFlameball3 tnt3 = new EntityFlameball3(this.world, (EntityCrazyBase) this, x2, y2, z2);
//                                            tnt3.posX = px;
//                                            tnt3.posY = py;
//                                            tnt3.posZ = pz;
//                                            tnt3.addTag("Bomb");
//                                            this.spawnEntity(tnt3);
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                    }
//                }
//                if (this.Tick > 200) {
//                    this.setNoAI(false);
//                    this.Tick = 0;
//                }
//            }
//            if (this.mode == EnumMultiAttackerMode.MAGIC) {
//                if (this.Tick == 1 && this.attackID == 0 && this.getAttackTarget() != null) {
//                    switch (this.rand.nextInt(3)) {
//                        case 0: {
//                            this.attackID = 4;
//                            break;
//                        }
//                        case 1: {
//                            this.attackID = 5;
//                            break;
//                        }
//                    }
//                }
//                if (this.Tick > 100) {
//                    this.Tick = 0;
//                }
//            }
//            if (this.mode == EnumMultiAttackerMode.WORLDBREAKER) {
//                if (this.Tick == 1) {
//                    switch (this.getRNG().nextInt(9)) {
//                        case 1: {
//                            final List<Entity> list3346 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(200.0, 200.0, 200.0).offset(-100.0, -100.0, -100.0));
//                            if (list3346 != null && !list3346.isEmpty()) {
//                                for (int i2 = 0; i2 < list3346.size(); ++i2) {
//                                    final Entity entity = list3346.get(i2);
//                                    if (entity instanceof EntityLivingBase && !(entity instanceof EntityItem) && !entity.isDead) {
//                                        final EntityLivingBase entity7 = (EntityLivingBase) entity;
//                                        if (entity7 instanceof EntityPlayer && !entity7.isDead && !((EntityPlayer) entity7).isCreative() && !((EntityPlayer) entity7).isSpectator()) {
//                                            this.setAttackTarget(entity7);
//                                        }
//                                        switch (this.rand.nextInt(2)) {
//                                            case 0: {
//                                                this.CrazyLightningToEntity(entity7);
//                                                break;
//                                            }
//                                            case 1: {
//                                                this.CrazyStructureToEntity(entity7);
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            break;
//                        }
//                        case 4: {
//                            final int max = this.rand.nextInt(4);
//                            final int already0 = 0;
//                            int already2 = 0;
//                            for (int m = 0; m < max; ++m) {
//                                switch (this.rand.nextInt(2)) {
//                                    case 0: {
//                                        Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString("FULLSCREEN! "));
//                                        final int c;
//                                        final boolean StartFullscreen = (c = (Minecraft.getMinecraft().isFullScreen() ? 0 : 1)) != 0;
//                                        if (this.world.isRemote) {
//                                            Minecraft.getMinecraft().toggleFullscreen();
//                                        }
//                                        Minecraft.getMinecraft().gameSettings.invertMouse = !Minecraft.getMinecraft().gameSettings.invertMouse;
//                                        Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString("w=" + Minecraft.getMinecraft().gameSettings.overrideWidth + ", h=" + Minecraft.getMinecraft().gameSettings.overrideHeight));
//                                        Minecraft.getMinecraft().gameSettings.saveOptions();
//                                        Minecraft.getMinecraft().gameSettings.loadOptions();
//                                        this.saveOptions();
//                                        break;
//                                    }
//                                    case 1: {
//                                        if (already2 == 0) {
//                                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) new TextComponentString("KEY!"));
//                                            this.setKeyChangeMain();
//                                            already2 = 1;
//                                            break;
//                                        }
//                                        break;
//                                    }
//                                }
//                            }
//                            break;
//                        }
//                        case 6: {
//                            if (this.getAttackTarget() != null) {
//                                final BlockPos pos = new BlockPos(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ);
//                                final double y3 = this.getSpawnY(this.world, pos);
//                                final BlockPos pos2 = new BlockPos(this.getAttackTarget().posX, y3, this.getAttackTarget().posZ);
//                                this.world.setBlockState(pos2, BlockRegistry.crazy_fire.getDefaultState());
//                                break;
//                            }
//                            switch (this.rand.nextInt(2)) {
//                                case 0: {
//                                    this.setKeyChangeMain();
//                                    break;
//                                }
//                                case 1: {
//                                    Minecraft.getMinecraft().gameSettings.fullScreen = !Minecraft.getMinecraft().gameSettings.fullScreen;
//                                    Minecraft.getMinecraft().gameSettings.saveOptions();
//                                    Minecraft.getMinecraft().gameSettings.loadOptions();
//                                    break;
//                                }
//                            }
//                            break;
//                        }
//                        case 7: {
//                            this.SummonBlackHole();
//                            break;
//                        }
//                        case 8: {
//                            this.SummonBlackHoleHard();
//                            break;
//                        }
//                    }
//                }
//                if (this.Tick > 250) {
//                    this.Tick = 0;
//                }
//            }
//        }
//        final List<Entity> list3347 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(8.0, 8.0, 8.0).offset(-4.0, -4.0, -4.0));
//        try {
//            if (list3347 != null && !list3347.isEmpty()) {
//                for (int i2 = 0; i2 < list3347.size(); ++i2) {
//                    final Entity entity2 = list3347.get(i2);
//                    if (entity2 != null && entity2 instanceof EntityThrowable) {
//                        entity2.isDead = true;
//                    }
//                }
//            }
//        } catch (Exception e2) {
//            e2.printStackTrace();
//        }
//        if (this.getAttackTarget() != null && !(this.getAttackTarget() instanceof EntityPlayer)) {
//            if (this.getAttackTarget().getHealth() > 1.0E8f) {
//                if (!this.world.isRemote) {
//                    final List<Entity> list3348 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(200.0, 200.0, 200.0).offset(-100.0, -100.0, -100.0));
//                    try {
//                        if (list3348 != null && !list3348.isEmpty()) {
//                            for (int i5 = 0; i5 < list3348.size(); ++i5) {
//                                final Entity entity3 = list3348.get(i5);
//                                if (entity3 instanceof EntityPlayer) {
//                                    final EntityPlayerMP player5 = (EntityPlayerMP) entity3;
//                                    EventAdvancementTrigger2.FINAL_BOSS_REAL.trigger(player5);
//                                }
//                            }
//                        }
//                    } catch (Exception e3) {
//                        e3.printStackTrace();
//                    }
//                }
//                if (!this.getAttackTarget().getTags().contains("CustomizeName")) {
//                    this.Name = EntityList.getEntityString((Entity) this.getAttackTarget());
//                    this.getAttackTarget().addTag("CustomizeName");
//                }
//                this.getAttackTarget().setCustomNameTag(EventCrazyMonsters.makeColour("entity." + this.Name + ".name"));
//            } else if (this.getAttackTarget().getName() != "null") {
//                this.getAttackTarget().setCustomNameTag(EventCrazyMonsters.makeColour4("null"));
//            }
//        }
//        if (this.world.getGameRules().getBoolean("doMobSpawing")) {
//            this.world.getGameRules().setOrCreateGameRule("doMobSpawning", "false");
//        }
//        if (this.world.getGameRules().getBoolean("doTileDrops")) {
//            this.world.getGameRules().setOrCreateGameRule("doTileDrops", "false");
//        }
//        if (this.attackTarget == null) {
//            this.motionY = -0.5;
//        }
//        if (this.attackTarget != null && !this.world.isRemote) {
//            ++this.angerTimer;
//            if (this.attackTarget.height < 5.0 && this.attackTarget.height < 5.0 && this.angerTimer > 500) {
//                MixinEnemyUpdate.isTerrible = true;
//            } else if (this.attackTarget.height > 5.0 && this.attackTarget.height > 5.0 && this.angerTimer > 5000) {
//                MixinEnemyUpdate.isTerrible = true;
//            } else {
//                MixinEnemyUpdate.isTerrible = false;
//            }
//        }
//        if (this.SpawnTime < 10) {
//            ++this.SpawnTime;
//        }
//        if (MixinEnemyUpdate.isTerrible && ((this.getAttackTarget() == null && !this.world.isRemote) || (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive() && !this.world.isRemote))) {
//            final List<Entity> list3349 = this.world.getEntitiesWithinAABBExcludingEntity((Entity) this, this.getEntityBoundingBox().expand(200.0, 200.0, 200.0).offset(-100.0, -100.0, -100.0));
//            try {
//                if (list3349 != null && !list3349.isEmpty()) {
//                    for (int i5 = 0; i5 < list3349.size(); ++i5) {
//                        final Entity entity3 = list3349.get(i5);
//                        if (entity3 instanceof EntityLivingBase && !(entity3 instanceof EntityItem) && !entity3.isDead) {
//                            final EntityLivingBase entity8 = (EntityLivingBase) entity3;
//                            if (entity8 instanceof EntityPlayer && !((EntityPlayer) entity8).isCreative() && !((EntityPlayer) entity8).isSpectator()) {
//                                this.setAttackTarget(entity8);
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e3) {
//                e3.printStackTrace();
//            }
//        }
//        if (this.canFly()) {
//            this.motionY *= 0.6000000238418579;
//            if (!this.world.isRemote && this.getWatchedTargetId(0) > 0) {
//                final Entity entity9 = this.world.getEntityByID(this.getWatchedTargetId(0));
//                if (entity9 != null) {
//                    if (this.posY < entity9.posY || this.posY < entity9.posY + 5.0) {
//                        if (this.motionY < 0.0) {
//                            this.motionY = 0.0;
//                        }
//                        this.motionY += (0.5 - this.motionY) * 0.6000000238418579;
//                    }
//                    final double d0 = entity9.posX - this.posX;
//                    final double d2 = entity9.posZ - this.posZ;
//                    final double d3 = d0 * d0 + d2 * d2;
//                    if (d3 > 9.0) {
//                        final double d4 = MathHelper.sqrt(d3);
//                        this.motionX += (d0 / d4 * 0.5 - this.motionX) * 0.6000000238418579;
//                        this.motionZ += (d2 / d4 * 0.5 - this.motionZ) * 0.6000000238418579;
//                    }
//                }
//            }
//            if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05000000074505806) {
//                this.rotationYaw = (float) MathHelper.atan2(this.motionZ, this.motionX) * 57.295776f - 90.0f;
//            }
//        }
//        super.onLivingUpdate();
//        if (this.canFly()) {
//            for (int l2 = 0; l2 < 2; ++l2) {
//                this.yRotOHeads[l2] = this.yRotationHeads[l2];
//                this.xRotOHeads[l2] = this.xRotationHeads[l2];
//            }
//            for (int m2 = 0; m2 < 2; ++m2) {
//                final int k2 = this.getWatchedTargetId(m2 + 1);
//                Entity entity10 = null;
//                if (k2 > 0) {
//                    entity10 = this.world.getEntityByID(k2);
//                }
//                if (entity10 != null) {
//                    final double d5 = this.getHeadX(m2 + 1);
//                    final double d6 = this.getHeadY(m2 + 1);
//                    final double d7 = this.getHeadZ(m2 + 1);
//                    final double d8 = entity10.posX - d5;
//                    final double d9 = entity10.posY + entity10.getEyeHeight() - d6;
//                    final double d10 = entity10.posZ - d7;
//                    final double d11 = MathHelper.sqrt(d8 * d8 + d10 * d10);
//                    final float f = (float) (MathHelper.atan2(d10, d8) * 57.29577951308232) - 90.0f;
//                    final float f2 = (float) (-(MathHelper.atan2(d9, d11) * 57.29577951308232));
//                    this.xRotationHeads[m2] = this.rotlerp(this.xRotationHeads[m2], f2, 40.0f);
//                    this.yRotationHeads[m2] = this.rotlerp(this.yRotationHeads[m2], f, 10.0f);
//                } else {
//                    this.yRotationHeads[m2] = this.rotlerp(this.yRotationHeads[m2], this.renderYawOffset, 10.0f);
//                }
//            }
//        } else if (!this.world.isRemote) {
//            this.motionY *= 1.6;
//            if (this.getAttackTarget() != null) {
//                if (this.getAttackTarget().posY > this.posY) {
//                    this.jump();
//                    final BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
//                    this.world.setBlockState(blockpos, Blocks.COBBLESTONE.getDefaultState());
//                } else if (this.motionX <= 0.0 && this.motionZ <= 0.0) {
//                    final int i1 = MathHelper.floor(this.posY);
//                    final int l3 = MathHelper.floor(this.posX);
//                    final int i6 = MathHelper.floor(this.posZ);
//                    boolean flag4 = true;
//                    for (int k3 = -1; k3 <= 1; ++k3) {
//                        for (int l4 = -1; l4 <= 1; ++l4) {
//                            for (int j2 = 0; j2 <= 3; ++j2) {
//                                final int i7 = l3 + k3;
//                                final int k4 = i1 + j2;
//                                final int l5 = i6 + l4;
//                                final BlockPos blockpos2 = new BlockPos(i7, k4, l5);
//                                final IBlockState iblockstate = this.world.getBlockState(blockpos2);
//                                final Block block = iblockstate.getBlock();
//                                if (!block.isAir(iblockstate, (IBlockAccess) this.world, blockpos2) && block.canEntityDestroy(iblockstate, (IBlockAccess) this.world, blockpos2, (Entity) this)) {
//                                    flag4 = (this.world.destroyBlock(blockpos2, false) || flag4);
//                                }
//                            }
//                        }
//                    }
//                }
//                if (this.rand.nextInt(50) == 0) {
//                    this.throwEnderPearl();
//                }
//            }
//        }
//        if (!this.world.isRemote) {
//            if (this.getAttackTarget() != null) {
//                this.getNavigator().tryMoveToEntityCrazyLiving((Entity) this.getAttackTarget(), 3.0);
//            }
//            if (!this.canFly()) {
//                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(5.600000023841858);
//                this.tasks.removeTask((EntityAIBase) this.aiArrowAttack);
//                this.tasks.addTask(1, (EntityAIBase) this.aiAttackOnCollide);
//            } else {
//                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6000000238418579);
//                this.tasks.removeTask((EntityAIBase) this.aiAttackOnCollide);
//                this.tasks.addTask(1, (EntityAIBase) this.aiArrowAttack);
//            }
//        }
//        if (!this.world.isRemote && this.getAttackTarget() != null && this.rand.nextInt(150) == 0 && this.attackTarget instanceof EntityPlayer) {
//            this.attackTarget.addPotionEffect(new PotionEffect(CrazyMonstersMod.ANTICHEATING, 100, 1));
//            this.attackTarget.addPotionEffect(new PotionEffect(MobEffects.LUCK, 100, 1));
//            if (this.attackTarget.isPotionActive(MobEffects.LUCK)) {
//                this.attackTarget.addTag("foreverKick");
//            }
//        }
//        if (!this.world.isRemote && this.rand.nextInt(150) == 0) {
//            final int flag5 = this.canFlying ? 1 : 0;
//            switch (flag5) {
//                case 0: {
//                    this.canFlying = true;
//                    break;
//                }
//                case 1: {
//                    this.canFlying = false;
//                    break;
//                }
//            }
//        }
//        if (this.attackTick == 17 && this.attackID == 3) {
//            for (int i2 = 0; i2 < 4; ++i2) {
//                final Vec3d vec3 = this.getLook(1.0f);
//                final double dx = vec3.x * i2;
//                final double dy = this.getEyeHeight() + vec3.y * i2;
//                final double dz = vec3.z * i2;
//                final int y4 = MathHelper.floor(this.posY + dy);
//                final int x3 = MathHelper.floor(this.posX + dx);
//                final int z3 = MathHelper.floor(this.posZ + dz);
//                if (this.world.isRemote) {
//                    final int i8 = MathHelper.floor((float) y4);
//                    final int l6 = MathHelper.floor((float) x3);
//                    final int i9 = MathHelper.floor((float) z3);
//                    final boolean flag6 = true;
//                    for (int k5 = -80; k5 <= 80; ++k5) {
//                        for (int l7 = -80; l7 <= 80; ++l7) {
//                            for (int j3 = 0; j3 < 1; ++j3) {
//                                final int i10 = l6 + k5;
//                                final int k6 = i8 + j3;
//                                final int l8 = i9 + l7;
//                                for (int i11 = 0; i11 < 1; ++i11) {
//                                    this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (this.attackTick == 17 && this.attackID == 2) {
//            for (int i2 = 0; i2 < 480; ++i2) {
//                final Vec3d vec3 = this.getLook(1.0f);
//                final double dx = vec3.x * i2;
//                final double dy = this.getEyeHeight() + vec3.y * i2;
//                final double dz = vec3.z * i2;
//                final int y4 = MathHelper.floor(this.posY + dy);
//                final int x3 = MathHelper.floor(this.posX + dx);
//                final int z3 = MathHelper.floor(this.posZ + dz);
//                if (this.world.isRemote) {
//                    final int i8 = MathHelper.floor((float) y4);
//                    final int l6 = MathHelper.floor((float) x3);
//                    final int i9 = MathHelper.floor((float) z3);
//                    final boolean flag6 = true;
//                    for (int k5 = 0; k5 < 1; ++k5) {
//                        for (int l7 = 0; l7 < 1; ++l7) {
//                            for (int j3 = -80; j3 <= 80; ++j3) {
//                                final int i10 = l6 + k5;
//                                final int k6 = i8 + j3;
//                                final int l8 = i9 + l7;
//                                for (int i11 = 0; i11 < 1; ++i11) {
//                                    this.world.spawnParticle(EnumParticleTypes.FLAME, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (this.attackID == 14) {
//            if (this.attackTick == 17) {
//                for (int i2 = 0; i2 < 480; ++i2) {
//                    final Vec3d vec3 = this.getLook(1.0f);
//                    final double dx = vec3.x * i2;
//                    final double dy = this.getEyeHeight() + vec3.y * i2;
//                    final double dz = vec3.z * i2;
//                    final int y4 = MathHelper.floor(this.posY + dy);
//                    final int x3 = MathHelper.floor(this.posX + dx);
//                    final int z3 = MathHelper.floor(this.posZ + dz);
//                    if (this.world.isRemote) {
//                        final int i8 = MathHelper.floor((float) y4);
//                        final int l6 = MathHelper.floor((float) x3);
//                        final int i9 = MathHelper.floor((float) z3);
//                        final boolean flag6 = true;
//                        for (int k5 = 0; k5 < 1; ++k5) {
//                            for (int l7 = 0; l7 < 1; ++l7) {
//                                for (int j3 = -80; j3 <= 80; ++j3) {
//                                    final int i10 = l6 + k5;
//                                    final int k6 = i8 + j3;
//                                    final int l8 = i9 + l7;
//                                    for (int i11 = 0; i11 < 1; ++i11) {
//                                        this.world.spawnParticle(EnumParticleTypes.FLAME, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (this.attackTick == 24) {
//                for (int i2 = 0; i2 < 480; ++i2) {
//                    final Vec3d vec3 = this.getLook(1.0f);
//                    final double dx = vec3.x * i2;
//                    final double dy = this.getEyeHeight() + vec3.y * i2;
//                    final double dz = vec3.z * i2;
//                    final int y4 = MathHelper.floor(this.posY + dy);
//                    final int x3 = MathHelper.floor(this.posX + dx);
//                    final int z3 = MathHelper.floor(this.posZ + dz);
//                    if (this.world.isRemote) {
//                        final int i8 = MathHelper.floor((float) y4);
//                        final int l6 = MathHelper.floor((float) x3);
//                        final int i9 = MathHelper.floor((float) z3);
//                        final boolean flag6 = true;
//                        for (int k5 = 0; k5 < 1; ++k5) {
//                            for (int l7 = 0; l7 < 1; ++l7) {
//                                for (int j3 = -80; j3 <= 80; ++j3) {
//                                    final int i10 = l6 + k5;
//                                    final int k6 = i8 + j3;
//                                    final int l8 = i9 + l7;
//                                    for (int i11 = 0; i11 < 1; ++i11) {
//                                        this.world.spawnParticle(EnumParticleTypes.FLAME, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (this.attackTick == 31) {
//                for (int i2 = 0; i2 < 480; ++i2) {
//                    final Vec3d vec3 = this.getLook(1.0f);
//                    final double dx = vec3.x * i2;
//                    final double dy = this.getEyeHeight() + vec3.y * i2;
//                    final double dz = vec3.z * i2;
//                    final int y4 = MathHelper.floor(this.posY + dy);
//                    final int x3 = MathHelper.floor(this.posX + dx);
//                    final int z3 = MathHelper.floor(this.posZ + dz);
//                    if (this.world.isRemote) {
//                        final int i8 = MathHelper.floor((float) y4);
//                        final int l6 = MathHelper.floor((float) x3);
//                        final int i9 = MathHelper.floor((float) z3);
//                        final boolean flag6 = true;
//                        for (int k5 = 0; k5 < 1; ++k5) {
//                            for (int l7 = 0; l7 < 1; ++l7) {
//                                for (int j3 = -80; j3 <= 80; ++j3) {
//                                    final int i10 = l6 + k5;
//                                    final int k6 = i8 + j3;
//                                    final int l8 = i9 + l7;
//                                    for (int i11 = 0; i11 < 1; ++i11) {
//                                        this.world.spawnParticle(EnumParticleTypes.FLAME, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (this.attackTick == 38) {
//                for (int i2 = 0; i2 < 480; ++i2) {
//                    final Vec3d vec3 = this.getLook(1.0f);
//                    final double dx = vec3.x * i2;
//                    final double dy = this.getEyeHeight() + vec3.y * i2;
//                    final double dz = vec3.z * i2;
//                    final int y4 = MathHelper.floor(this.posY + dy);
//                    final int x3 = MathHelper.floor(this.posX + dx);
//                    final int z3 = MathHelper.floor(this.posZ + dz);
//                    if (this.world.isRemote) {
//                        final int i8 = MathHelper.floor((float) y4);
//                        final int l6 = MathHelper.floor((float) x3);
//                        final int i9 = MathHelper.floor((float) z3);
//                        final boolean flag6 = true;
//                        for (int k5 = 0; k5 < 1; ++k5) {
//                            for (int l7 = 0; l7 < 1; ++l7) {
//                                for (int j3 = -80; j3 <= 80; ++j3) {
//                                    final int i10 = l6 + k5;
//                                    final int k6 = i8 + j3;
//                                    final int l8 = i9 + l7;
//                                    for (int i11 = 0; i11 < 1; ++i11) {
//                                        this.world.spawnParticle(EnumParticleTypes.FLAME, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (this.attackTick == 45) {
//                for (int i2 = 0; i2 < 480; ++i2) {
//                    final Vec3d vec3 = this.getLook(1.0f);
//                    final double dx = vec3.x * i2;
//                    final double dy = this.getEyeHeight() + vec3.y * i2;
//                    final double dz = vec3.z * i2;
//                    final int y4 = MathHelper.floor(this.posY + dy);
//                    final int x3 = MathHelper.floor(this.posX + dx);
//                    final int z3 = MathHelper.floor(this.posZ + dz);
//                    if (this.world.isRemote) {
//                        final int i8 = MathHelper.floor((float) y4);
//                        final int l6 = MathHelper.floor((float) x3);
//                        final int i9 = MathHelper.floor((float) z3);
//                        final boolean flag6 = true;
//                        for (int k5 = 0; k5 < 1; ++k5) {
//                            for (int l7 = 0; l7 < 1; ++l7) {
//                                for (int j3 = -80; j3 <= 80; ++j3) {
//                                    final int i10 = l6 + k5;
//                                    final int k6 = i8 + j3;
//                                    final int l8 = i9 + l7;
//                                    for (int i11 = 0; i11 < 1; ++i11) {
//                                        this.world.spawnParticle(EnumParticleTypes.FLAME, (double) i10, (double) k6, (double) l8, this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble(), new int[0]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }
}
