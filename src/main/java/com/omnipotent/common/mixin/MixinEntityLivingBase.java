package com.omnipotent.common.mixin;

import com.omnipotent.Config;
import com.omnipotent.acessor.IEntityLivingBaseAcessor;
import com.omnipotent.common.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.omnipotent.util.KaiaUtil.effectIsBlockedByKaia;
import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;
import static com.omnipotent.util.UtilityHelper.isPlayer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements IEntityLivingBaseAcessor {
    @Shadow
    protected int idleTime;
    @Shadow
    private DamageSource lastDamageSource;
    @Shadow
    private long lastDamageStamp;
    @Shadow
    public float limbSwingAmount;
    @Shadow
    protected EntityPlayer attackingPlayer;
    @Shadow
    protected float lastDamage;
    @Shadow
    public int maxHurtResistantTime;
    @Shadow
    public int maxHurtTime;
    @Shadow
    public int hurtTime;
    @Shadow
    public float attackedAtYaw;

    @Override
    public void setRecentlyHit(int recentlyHit) {
        this.recentlyHit = recentlyHit;
    }

    @Shadow
    protected boolean dead;

    @Shadow
    protected abstract void applyEntityAttributes();

    @Shadow
    public float randomUnused1;
    @Shadow
    public float randomUnused2;
    @Shadow
    public float rotationYawHead;

    @Shadow
    public abstract EntityLivingBase getAttackingEntity();

    @Shadow
    public int recentlyHit;
    @Shadow
    protected int scoreValue;

    @Shadow
    protected abstract boolean canDropLoot();

    @Accessor("HEALTH")
    public abstract DataParameter<Float> getHEALTH();

    @Shadow
    protected abstract void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source);

    public boolean captureDropsAbsolute;

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.randomUnused1 = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.randomUnused2 = (float) Math.random() * 12398.0F;
        this.rotationYaw = (float) (Math.random() * (Math.PI * 2D));
        this.rotationYawHead = this.rotationYaw;
        this.stepHeight = 0.6F;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void onDeath(DamageSource cause) {
        EntityLivingBase entity1 = (EntityLivingBase) (Object) this;
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(entity1, cause)) return;
        if (!this.dead) {
            Entity entity = cause.getTrueSource();
            EntityLivingBase entitylivingbase = this.getAttackingEntity();

            if (this.scoreValue >= 0 && entitylivingbase != null) {
                entitylivingbase.awardKillScore(entity1, this.scoreValue, cause);
            }

            if (entity != null) {
                entity.onKillEntity(entity1);
            }

            this.dead = true;
            entity1.getCombatTracker().reset();

            if (!entity1.world.isRemote) {
                int i = net.minecraftforge.common.ForgeHooks.getLootingLevel(entity1, entity, cause);

                captureDrops = true;
                capturedDrops.clear();

                if (this.canDropLoot() && entity1.world.getGameRules().getBoolean("doMobLoot")) {
                    boolean flag = this.recentlyHit > 0;
                    this.dropLoot(flag, i, cause);
                }
                if (captureDropsAbsolute) {
                    boolean flag = this.recentlyHit > 0;
                    this.dropLoot(flag, i, cause);
                }

                captureDrops = false;

                if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(entity1, cause, capturedDrops, i, recentlyHit > 0) && !captureDropsAbsolute) {
                    for (EntityItem item : capturedDrops) {
                        world.spawnEntity(item);
                    }
                }
            }
            entity1.world.setEntityState(this, (byte) 3);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public final float getHealth() {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        Entity source;
        float standardValue = ((Float) this.dataManager.get(this.getHEALTH())).floatValue();
        if (isPlayer(entity) && hasInInventoryKaia(entity))
            return 20;
        else if (Config.getListPlayersCantRespawn().contains(entity.getUniqueID().toString()) && !hasInInventoryKaia(entity)) {
            Config.reloadConfigsOfFile();
            return 0;
        }
        if (entity.getLastDamageSource() != null && entity.getLastDamageSource().getTrueSource() != null) {
            source = entity.getLastDamageSource().getTrueSource();
            boolean equals = entity.getLastDamageSource().getDamageType().equals(new AbsoluteOfCreatorDamage(source).getDamageType());
            if (equals)
                return 0;
        }
        return standardValue;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public final float getMaxHealth() {
        if (Config.getListPlayersCantRespawn().contains(this.getUniqueID().toString()) && !hasInInventoryKaia(this)) {
            Config.reloadConfigsOfFile();
            return 0;
        }
        EntityPlayer player;
        float StandardValue = (float) ((EntityLivingBase) (Object) this).getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
        if (this == null || !((EntityLivingBase) (Object) this instanceof EntityPlayer))
            return StandardValue;
        player = (EntityPlayer) (Object) this;
        boolean inInventoryKaia = hasInInventoryKaia(player);
        if (player.getLastDamageSource() != null && player.getLastDamageSource().getTrueSource() != null) {
            boolean equals = player.getLastDamageSource().damageType.equals(new AbsoluteOfCreatorDamage(player.getLastDamageSource().getTrueSource()).damageType);
            return inInventoryKaia ? 20 : equals ? 0 : StandardValue;
        }
        return inInventoryKaia ? 20 : StandardValue;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void setHealth(float health) {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        DamageSource lastDamage = entity.getLastDamageSource();
        if (UtilityHelper.isPlayer(entity) && hasInInventoryKaia(entity))
            this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(Integer.MAX_VALUE, 0.0F, entity.getMaxHealth())));
        else if (lastDamage != null && lastDamage.getTrueSource() != null) {
            Entity sourceOfDamage = lastDamage.getTrueSource();
            if (lastDamage.damageType.equals(new AbsoluteOfCreatorDamage(sourceOfDamage).damageType))
                this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(0, 0.0F, entity.getMaxHealth())));
            else
                this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(health, 0.0F, entity.getMaxHealth())));
        } else
            this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(health, 0.0F, entity.getMaxHealth())));
    }

    @Inject(method = "addPotionEffect", at = @At(value = "HEAD"), cancellable = true)
    public void addPotionEffect(PotionEffect potioneffectIn, CallbackInfo ci) {
        if ((EntityLivingBase) (Object) this instanceof EntityPlayer)
            if (effectIsBlockedByKaia((EntityPlayer) (Object) this, potioneffectIn.getPotion()))
                ci.cancel();
    }

    public final void setAbsoluteHealth(float health) {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        DamageSource lastDamage = entity.getLastDamageSource();
        if (UtilityHelper.isPlayer(entity) && hasInInventoryKaia(entity))
            this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(Integer.MAX_VALUE, 0.0F, entity.getMaxHealth())));
        else if (lastDamage != null && lastDamage.getTrueSource() != null) {
            Entity sourceOfDamage = lastDamage.getTrueSource();
            if (lastDamage.damageType.equals(new AbsoluteOfCreatorDamage(sourceOfDamage).damageType))
                this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(0, 0.0F, entity.getMaxHealth())));
            else
                this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(health, 0.0F, entity.getMaxHealth())));
        } else
            this.dataManager.set(this.getHEALTH(), Float.valueOf(MathHelper.clamp(health, 0.0F, entity.getMaxHealth())));
    }

    public final void onAbsoluteDeath(DamageSource cause) {
        EntityLivingBase entity1 = (EntityLivingBase) (Object) this;
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(entity1, cause)) return;
        if (!this.dead) {
            Entity entity = cause.getTrueSource();
            EntityLivingBase entitylivingbase = this.getAttackingEntity();

            if (this.scoreValue >= 0 && entitylivingbase != null) {
                entitylivingbase.awardKillScore(entity1, this.scoreValue, cause);
            }

            if (entity != null) {
                entity.onKillEntity(entity1);
            }

            this.dead = true;
            entity1.getCombatTracker().reset();

            if (!entity1.world.isRemote) {
                int i = net.minecraftforge.common.ForgeHooks.getLootingLevel(entity1, entity, cause);

                captureDrops = true;
                capturedDrops.clear();

                if (this.canDropLoot() && entity1.world.getGameRules().getBoolean("doMobLoot")) {
                    boolean flag = this.recentlyHit > 0;
                    this.dropLoot(flag, i, cause);
                }
                if (captureDropsAbsolute) {
                    boolean flag = this.recentlyHit > 0;
                    this.dropLoot(flag, i, cause);
                }

                captureDrops = false;

                if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(entity1, cause, capturedDrops, i, recentlyHit > 0) && !captureDropsAbsolute) {
                    for (EntityItem item : capturedDrops) {
                        world.spawnEntity(item);
                    }
                }
            }
            entity1.world.setEntityState(this, (byte) 3);
        }
    }

    public final boolean AbsoluteAttackEntityFrom(DamageSource source, float amount) {
        EntityLivingBase thisEntity = (EntityLivingBase) (Object) this;
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(thisEntity, source, amount)) return false;
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else if (this.world.isRemote) {
            return false;
        } else {
            this.idleTime = 0;

            if (this.getHealth() <= 0.0F) {
                return false;
            } else if (source.isFireDamage() && this.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                return false;
            } else {
                float f = amount;

                if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
                    this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).damageItem((int) (amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), thisEntity);
                    amount *= 0.75F;
                }

                boolean flag = false;

                if (amount > 0.0F && this.canBlockDamageSource(source)) {
                    this.damageShield(amount);
                    amount = 0.0F;

                    if (!source.isProjectile()) {
                        net.minecraft.entity.Entity entity = source.getImmediateSource();

                        if (entity instanceof net.minecraft.entity.EntityLivingBase) {
                            this.blockUsingShield((net.minecraft.entity.EntityLivingBase) entity);
                        }
                    }

                    flag = true;
                }

                this.limbSwingAmount = 1.5F;
                boolean flag1 = true;

                if ((float) this.hurtResistantTime > (float) this.maxHurtResistantTime / 2.0F) {
                    if (amount <= this.lastDamage) {
                        return false;
                    }

                    this.damageEntity(source, amount - this.lastDamage);
                    this.lastDamage = amount;
                    flag1 = false;
                } else {
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(source, amount);
                    this.maxHurtTime = 10;
                    this.hurtTime = this.maxHurtTime;
                }

                this.attackedAtYaw = 0.0F;
                net.minecraft.entity.Entity entity1 = source.getTrueSource();

                if (entity1 != null) {
                    if (entity1 instanceof net.minecraft.entity.EntityLivingBase) {
                        this.setRevengeTarget((net.minecraft.entity.EntityLivingBase) entity1);
                    }

                    if (entity1 instanceof EntityPlayer) {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer) entity1;
                    } else if (entity1 instanceof net.minecraft.entity.passive.EntityTameable) {
                        net.minecraft.entity.passive.EntityTameable entitywolf = (net.minecraft.entity.passive.EntityTameable) entity1;

                        if (entitywolf.isTamed()) {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (flag1) {
                    if (flag) {
                        this.world.setEntityState(thisEntity, (byte) 29);
                    } else if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage()) {
                        this.world.setEntityState(thisEntity, (byte) 33);
                    } else {
                        byte b0;

                        if (source == DamageSource.DROWN) {
                            b0 = 36;
                        } else if (source.isFireDamage()) {
                            b0 = 37;
                        } else {
                            b0 = 2;
                        }

                        this.world.setEntityState(thisEntity, b0);
                    }

                    if (source != DamageSource.DROWN && (!flag || amount > 0.0F)) {
                        this.markVelocityChanged();
                    }

                    if (entity1 != null) {
                        double d1 = entity1.posX - this.posX;
                        double d0;

                        for (d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float) (MathHelper.atan2(d0, d1) * (180D / Math.PI) - (double) this.rotationYaw);
                        this.knockBack(entity1, 0.4F, d1, d0);
                    } else {
                        this.attackedAtYaw = (float) ((int) (Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F) {
                    if (!this.checkTotemDeathProtection(source)) {
                        SoundEvent soundevent = this.getDeathSound();

                        if (flag1 && soundevent != null) {
                            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
                        }

                        this.onDeath(source);
                    }
                } else if (flag1) {
                    this.playHurtSound(source);
                }

                boolean flag2 = !flag || amount > 0.0F;

                if (flag2) {
                    this.lastDamageSource = source;
                    this.lastDamageStamp = this.world.getTotalWorldTime();
                }

                if (thisEntity instanceof EntityPlayerMP) {
                    CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((EntityPlayerMP) thisEntity, source, f, amount, flag);
                }

                if (entity1 instanceof EntityPlayerMP) {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP) entity1, thisEntity, source, f, amount, flag);
                }

                return flag2;
            }
        }
    }

    @Shadow
    protected abstract void damageEntity(DamageSource source, float v);

    @Shadow
    protected abstract void damageShield(float damage);

    @Shadow
    protected abstract SoundEvent getDeathSound();

    @Shadow
    protected abstract boolean checkTotemDeathProtection(DamageSource source);

    @Shadow
    protected abstract boolean canBlockDamageSource(DamageSource source);

    @Shadow
    public abstract ItemStack getItemStackFromSlot(EntityEquipmentSlot entityEquipmentSlot);

    @Shadow
    protected abstract void blockUsingShield(EntityLivingBase entity);

    @Shadow
    protected abstract void setRevengeTarget(EntityLivingBase entity1);

    @Shadow
    protected abstract void knockBack(Entity entity1, float v, double d1, double d0);

    @Shadow
    protected abstract float getSoundPitch();

    @Shadow
    protected abstract float getSoundVolume();

    @Shadow
    protected abstract void playHurtSound(DamageSource source);

    @Shadow
    protected abstract boolean isPotionActive(Potion fireResistance);
}