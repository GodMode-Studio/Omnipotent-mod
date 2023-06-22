package com.omnipotent.server.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
    protected abstract void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source);
    public boolean captureDropsAbsolute;
    /**
     * @author
     * @reason
     */
    @Overwrite
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
}
