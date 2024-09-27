package com.omnipotent.common.entity;

import com.google.common.base.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class KaiaEntity extends Entity {

    protected static final DataParameter<Optional<UUID>> PLAYER_OWNER = EntityDataManager.createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    public KaiaEntity(World worldIn) {
        super(worldIn);
    }

    public KaiaEntity(World worldIn, EntityPlayerMP entityPlayerMP) {
        super(worldIn);
        setOwnerId(entityPlayerMP.getUniqueID());
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(PLAYER_OWNER, Optional.absent());
    }

    private long time;

    @Override
    public void onEntityUpdate() {
        if (world.isRemote) return;
        time = time == Long.MAX_VALUE ? 0 : ++time;
        EntityPlayer ownerPlayer = getOwnerPlayer();
        if (ownerPlayer == null) {
            super.onEntityUpdate();
            return;
        }

        int radius = 1;
        double orbitalPeriod = 60; //3 segundos é 60 ticks multiplica por dois
        double portion = (time % orbitalPeriod) / orbitalPeriod;
        double angle = portion * 2 * Math.PI;
        this.posX = ownerPlayer.posX + radius * Math.cos(angle);
        this.posY = ownerPlayer.posY;
        this.posZ = ownerPlayer.posZ + radius * Math.sin(angle);
        super.onEntityUpdate();
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
    }
}
