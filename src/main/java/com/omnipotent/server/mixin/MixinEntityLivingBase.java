package com.omnipotent.server.mixin;

import com.omnipotent.server.damage.AbsoluteOfCreatorDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;
import static com.omnipotent.util.UtilityHelper.isPlayer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    @Shadow
    protected boolean dead;

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract EntityLivingBase getAttackingEntity();

    @Shadow
    public int recentlyHit;
    @Shadow
    protected int scoreValue;

    @Shadow
    protected abstract boolean canDropLoot();

    @Shadow
    private int jumpTicks;

    @Shadow
    protected abstract void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source);

    @Shadow
    protected int newPosRotationIncrements;
    @Shadow
    protected double interpTargetX;

    @Shadow
    protected double interpTargetY;
    @Shadow
    protected double interpTargetZ;
    @Shadow
    protected double interpTargetYaw;

    @Shadow
    public abstract boolean isServerWorld();

    @Shadow
    protected double interpTargetPitch;
    @Shadow
    public float randomYawVelocity;

    @Shadow
    public float moveVertical;

    @Shadow
    public float moveStrafing;

    @Shadow
    public abstract void travel(float strafe, float vertical, float forward);

    @Shadow
    public abstract void updateElytra();

    @Shadow
    public float moveForward;

    @Shadow
    protected abstract void collideWithNearbyEntities();

    @Shadow
    protected abstract boolean isMovementBlocked();

    @Shadow
    protected abstract void handleJumpWater();

    @Shadow
    protected boolean isJumping;

    @Shadow
    protected abstract void handleJumpLava();

    @Shadow
    protected abstract void jump();

    @Shadow
    protected abstract void updateEntityActionState();

    @Shadow
    protected abstract void updateActiveHand();

    public boolean captureDropsAbsolute;
    @Shadow
    public int arrowHitTimer;
    @Shadow
    private final NonNullList<ItemStack> handInventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
    @Shadow
    private final NonNullList<ItemStack> armorArray = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
    @Shadow
    protected float prevOnGroundSpeedFactor;
    @Shadow
    protected float movedDistance;
    @Shadow
    protected int ticksElytraFlying;

    @Shadow
    protected abstract float updateDistance(float p_110146_1_, float p_110146_2_);

    @Shadow
    protected float onGroundSpeedFactor;

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

    @Shadow
    private static final DataParameter<Float> HEALTH = EntityDataManager.<Float>createKey(EntityLivingBase.class, DataSerializers.FLOAT);

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public final float getHealth() {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        EntityPlayer player;
        Entity source;
        float standardValue = ((Float) this.dataManager.get(HEALTH)).floatValue();
        if(isPlayer(entity) && hasInInventoryKaia(entity))
            return 20;
        if (entity.getLastDamageSource() != null && entity.getLastDamageSource().getTrueSource() != null) {
            source = entity.getLastDamageSource().getTrueSource();
            boolean equals = entity.getLastDamageSource().getDamageType().equals(new AbsoluteOfCreatorDamage(source).getDamageType());
            if(equals)
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
        if (entity instanceof EntityPlayer && hasInInventoryKaia(entity))
            this.dataManager.set(HEALTH, Float.valueOf(MathHelper.clamp(Integer.MAX_VALUE, 0.0F, entity.getMaxHealth())));
        else if (lastDamage != null && lastDamage.getTrueSource() != null) {
            Entity sourceOfDamage = lastDamage.getTrueSource();
            if (lastDamage.damageType.equals(new AbsoluteOfCreatorDamage(sourceOfDamage).damageType))
                this.dataManager.set(HEALTH, Float.valueOf(MathHelper.clamp(0, 0.0F, entity.getMaxHealth())));
        } else
            this.dataManager.set(HEALTH, Float.valueOf(MathHelper.clamp(health, 0.0F, entity.getMaxHealth())));
    }
}