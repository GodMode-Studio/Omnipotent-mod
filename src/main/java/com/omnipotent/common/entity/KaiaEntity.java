package com.omnipotent.common.entity;

import com.google.common.base.Optional;
import com.omnipotent.common.damage.AbsoluteOfCreatorDamage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.omnipotent.util.UtilityHelper.getKaiaCap;

public class KaiaEntity extends Entity implements IEntityAdditionalSpawnData {

    private static final DataParameter<Optional<UUID>> PLAYER_OWNER = EntityDataManager.createKey(KaiaEntity.class,
            DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Boolean> ATTACK_MODE = EntityDataManager.createKey(KaiaEntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LIMITED_LIVE = EntityDataManager.createKey(KaiaEntity.class,
            DataSerializers.BOOLEAN);
    private int index;
    private static final DataParameter<Integer> ATTACK_TARGET = EntityDataManager.createKey(KaiaEntity.class,
            DataSerializers.VARINT);
    private long time;
    private double permSpeed = 0;
    private long lastTime;

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
        this.dataManager.register(LIMITED_LIVE, false);
        this.dataManager.register(ATTACK_TARGET, -121212);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (world.isRemote) return;
        time = time == Long.MAX_VALUE ? 0 : ++time;
        EntityPlayer ownerPlayer = getOwnerPlayer();
        if (ownerPlayer == null) {
            return;
        }

        if (getAttackTarget() != null && getAttackTarget().isEntityAlive()) {
            if (!hasLimitedLive() || time >= 140)
                moveToTarget();
        } else {
            orbitalMove(ownerPlayer);
            this.posY = ownerPlayer.posY + 0.5;
            setAttackMode(false);
            setAttackTarget(null);
            if (hasLimitedLive())
                setDead();
        }
    }

    private void orbitalMove(EntityPlayer ownerPlayer) {
        double radius = 1.5;
        double orbitalPeriod = 40;
        double portion = (time % orbitalPeriod) / orbitalPeriod;
        double angleOffset = (2 * Math.PI / 5) * index;
        double angle = portion * 2 * Math.PI + angleOffset;
        double targetX = ownerPlayer.posX + radius * Math.cos(angle);
        double targetZ = ownerPlayer.posZ + radius * Math.sin(angle);
        double baseSpeed = 0.3;
        double catchupThreshold = 2.0;
        double dx = targetX - this.posX;
        double dz = targetZ - this.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double speed = baseSpeed;
        if (distance > catchupThreshold) {
            speed = baseSpeed * (distance / catchupThreshold);
        }
        this.motionX = dx * speed;
        this.motionZ = dz * speed;
        this.posX += this.motionX;
        this.posZ += this.motionZ;
        double targetYaw = Math.toDegrees(Math.atan2(this.motionZ, this.motionX));
        float currentYaw = this.rotationYaw;
        float yawDiff = (float) ((targetYaw - currentYaw + 180) % 360 - 180);
        this.rotationYaw += yawDiff * 0.3f;
    }

    private void moveToTarget() {
        EntityLivingBase attackTarget = getAttackTarget();
        double directionX = attackTarget.posX - this.posX;
        double directionY = (attackTarget.posY + attackTarget.getEyeHeight() / 2.0F) - this.posY;
        double directionZ = attackTarget.posZ - this.posZ;
        double distance = MathHelper.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        double speed = getSpeed(distance);
        if (distance > 0.001) {
            this.posX += (directionX / distance) * speed;
            this.posY += (directionY / distance) * speed;
            this.posZ += (directionZ / distance) * speed;
        }
        if (isColliding(distance)) {
            attackTarget.attackEntityFrom(new AbsoluteOfCreatorDamage(getOwnerPlayer()), Float.MAX_VALUE);
            attackTarget.onAbsoluteDeath(new AbsoluteOfCreatorDamage(getOwnerPlayer()));
            if (hasLimitedLive())
                setDead();
        }
    }

    private double getSpeed(double distance) {
        if (!hasLimitedLive()) return 3;
        double speed = Math.max(distance / 300, 0.5);
        if (time >= 500) {
            lastTime = Math.max(600 - time, 1);
            speed = distance / lastTime;
            permSpeed = Math.max(speed, permSpeed);
        }
        if (permSpeed != 0)
            speed = permSpeed;
        return speed;
    }

    private boolean isColliding(double distance) {
        AxisAlignedBB boundingBox1 = getAttackTarget().getEntityBoundingBox();
        AxisAlignedBB boundingBox2 = new AxisAlignedBB(this.getPosition());
        return boundingBox2.intersects(boundingBox1) || distance < 0.1;
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
        compound.setBoolean("limitedLive", this.dataManager.get(LIMITED_LIVE));
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
        this.dataManager.set(LIMITED_LIVE, compound.getBoolean("limitedLive"));
    }

    public boolean isAttackMode() {
        return this.dataManager.get(ATTACK_MODE);
    }

    public void setAttackMode(boolean attackMode) {
        this.dataManager.set(ATTACK_MODE, attackMode);
    }

    public boolean hasLimitedLive() {
        return this.dataManager.get(LIMITED_LIVE);
    }

    public void setLive(boolean limitedLive) {
        this.dataManager.set(LIMITED_LIVE, limitedLive);
    }

    public EntityLivingBase getAttackTarget() {
        return (EntityLivingBase) this.world.getEntityByID(this.dataManager.get(ATTACK_TARGET));
    }

    public void setAttackTarget(EntityLivingBase attackTarget) {
        this.dataManager.set(ATTACK_TARGET, attackTarget == null ? -121212 : attackTarget.getEntityId());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeDouble(this.posX);
        buffer.writeDouble(this.posY);
        buffer.writeDouble(this.posZ);
        ByteBufUtils.writeUTF8String(buffer, getOwnerId().toString());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        setPosition(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
        setOwnerId(UUID.fromString(ByteBufUtils.readUTF8String(additionalData)));
    }
}
