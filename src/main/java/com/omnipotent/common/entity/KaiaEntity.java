package com.omnipotent.common.entity;

import com.google.common.base.Optional;
import com.omnipotent.common.damage.AbsoluteOfCreatorDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.omnipotent.util.UtilityHelper.getKaiaCap;

public class KaiaEntity extends Entity {

    private static final DataParameter<Optional<UUID>> PLAYER_OWNER = EntityDataManager.createKey(KaiaEntity.class,
            DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Boolean> ATTACK_MODE = EntityDataManager.createKey(KaiaEntity.class,
            DataSerializers.BOOLEAN);
    public float prevRenderYawOffset;
    public float prevRotationYawHead;
    public float rotationYawHead;
    public float renderYawOffset;
    private int index;
    private EntityLivingBase attackTarget;
    private long time;

    public KaiaEntity(World worldIn) {
        super(worldIn);
    }

    public KaiaEntity(World worldIn, EntityPlayerMP entityPlayerMP, int index) {
        super(worldIn);
        setOwnerId(entityPlayerMP.getUniqueID());
        this.index = index;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(PLAYER_OWNER, Optional.absent());
        this.dataManager.register(ATTACK_MODE, false);
    }

    @Override
    public void onEntityUpdate() {
        updateRotation();
        if (world.isRemote) return;
        time = time == Long.MAX_VALUE ? 0 : ++time;
        EntityPlayer ownerPlayer = getOwnerPlayer();
        if (ownerPlayer == null) {
            super.onEntityUpdate();
            return;
        }

        if (attackTarget != null && attackTarget.isEntityAlive()) {
            moveToTarget();
        } else {
            orbitalMove(ownerPlayer);
            this.posY = isAttackMode() ? ownerPlayer.posY + 1 : ownerPlayer.posY;
            setAttackMode(false);
            setAttackTarget(null);
        }
        super.onEntityUpdate();
    }

    private void updateRotation() {
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    private void orbitalMove(EntityPlayer ownerPlayer) {
        int radius = 1;
        double orbitalPeriod = 60; //3 segundos é 60 ticks multiplica por dois
        double portion = (time % orbitalPeriod) / orbitalPeriod;
        double angleOffset = (2 * Math.PI / 5) * index;
        double angle = portion * 2 * Math.PI + angleOffset;
        this.posX = ownerPlayer.posX + radius * Math.cos(angle);
        this.posZ = ownerPlayer.posZ + radius * Math.sin(angle);
    }

    private void moveToTarget() {
        double directionX = attackTarget.posX - this.posX;
        double directionY = (attackTarget.posY + attackTarget.getEyeHeight() / 2.0F) - this.posY;
        double directionZ = attackTarget.posZ - this.posZ;
        double distance = MathHelper.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        double speed = 0.05;
        if (distance > 0.001) {
            this.posX += (directionX / distance) * speed;
            this.posY += (directionY / distance) * speed;
            this.posZ += (directionZ / distance) * speed;
        }

        this.rotationYaw = (float) (Math.atan2(directionZ, directionX) * (180F / Math.PI)) - 90.0F;
        this.rotationPitch = (float) -(Math.atan2(directionY, MathHelper.sqrt(directionX * directionX + directionZ * directionZ)) * (180F / Math.PI));

        if (distance < 0.5) {
            attackTarget.attackEntityFrom(new AbsoluteOfCreatorDamage(getOwnerPlayer()), Float.MAX_VALUE);
            attackTarget.onAbsoluteDeath(new AbsoluteOfCreatorDamage(getOwnerPlayer()));
        }
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Override
    public void setDead() {
        EntityPlayer player = getOwnerPlayer();
        if (player != null)
            getKaiaCap(player).ifPresent(cap -> cap.getKaiaSwordsSummoned().remove(this.getPersistentID().toString()));
        super.setDead();
    }

    private EntityPlayer getOwnerPlayer() {
        return this.dataManager.get(PLAYER_OWNER).toJavaUtil().map(uuid -> world.getPlayerEntityByUUID(uuid)).orElse(null);
    }

    @Nullable
    public UUID getOwnerId() {
        return this.dataManager.get(PLAYER_OWNER).orNull();
    }

    private void setOwnerId(UUID uuid) {
        this.dataManager.set(PLAYER_OWNER, Optional.fromNullable(uuid));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (this.getOwnerId() == null) {
            compound.setString("OwnerUUID", "");
        } else {
            compound.setString("OwnerUUID", this.getOwnerId().toString());
        }
        compound.setInteger("indexKaiaSword", index);
        compound.setBoolean("attackmode", this.dataManager.get(ATTACK_MODE));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        String s;
        if (compound.hasKey("OwnerUUID", 8)) {
            s = compound.getString("OwnerUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }

        if (!s.isEmpty()) {
            try {
                this.setOwnerId(UUID.fromString(s));
            } catch (Throwable var4) {
            }
        }

        index = compound.getInteger("indexKaiaSword");
        this.dataManager.set(ATTACK_MODE, compound.getBoolean("attackmode"));
    }

    public boolean isAttackMode() {
        return this.dataManager.get(ATTACK_MODE);
    }

    public void setAttackMode(boolean attackMode) {
        this.dataManager.set(ATTACK_MODE, attackMode);
    }

    public EntityLivingBase getAttackTarget() {
        return attackTarget;
    }

    public void setAttackTarget(EntityLivingBase attackTarget) {
        this.attackTarget = attackTarget;
    }
}
