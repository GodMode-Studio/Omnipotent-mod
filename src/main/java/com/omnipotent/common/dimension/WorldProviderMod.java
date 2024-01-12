package com.omnipotent.common.dimension;

import com.omnipotent.Omnipotent;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class WorldProviderMod extends WorldProvider {
    @Override
    public void init() {
        this.biomeProvider = new BiomeProviderCustom(this.world.getSeed());
        this.nether = Omnipotent.NETHER_TYPE;
        this.hasSkyLight = true;
    }

    @Override
    public DimensionType getDimensionType() {
        return Omnipotent.dimensionType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float par1, float par2) {
        return new Vec3d(0.752941176471, 0.847058823529, 1);
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkProviderModded(this.world, this.world.getSeed() - Omnipotent.DIMID);
    }

    @Override
    public boolean canRespawnHere() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean doesXZShowFog(int par1, int par2) {
        return false;
    }

    @Override
    public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos) {
        return WorldSleepResult.BED_EXPLODES;
    }

    @Override
    public boolean doesWaterVaporize() {
        return true;
    }

    @Nullable
    @Override
    public MusicTicker.MusicType getMusicType() {
        return MusicTicker.MusicType.MENU;
    }

    @Override
    public void onPlayerAdded(EntityPlayerMP entity) {
        UtilityHelper.sendMessageToPlayer("seja bem vindo", entity);
    }

    @Override
    public void onWorldUpdateEntities() {
        List<Entity> loadedEntityList = this.world.getLoadedEntityList();
        if (loadedEntityList.size() <= 100) {
            for (Entity entity : loadedEntityList) {
                if (entity instanceof EntityLiving)
                    ((EntityLiving) entity).enablePersistence();
            }
        }
    }
}