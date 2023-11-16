package com.omnipotent.server.mixin;

import com.omnipotent.Config;
import com.omnipotent.acessor.IEntityLivingBaseAcessor;
import com.omnipotent.server.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
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
}