package com.omnipotent.common.mixin.mods;

import com.github.alexthe666.iceandfire.entity.IBlacklistedFromStatues;
import com.omnipotent.util.KaiaUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.common.entity.EntityGatekeeperEssence;
import com.shinoow.abyssalcraft.common.entity.EntityJzahar;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACLib;
import com.shinoow.abyssalcraft.lib.ACSounds;
import com.shinoow.abyssalcraft.lib.util.SpecialTextUtil;
import com.shinoow.abyssalcraft.lib.world.TeleporterDarkRealm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EntityJzahar.class, remap = false)
@Optional.Interface(iface = "com.github.alexthe666.iceandfire.entity.IBlacklistedFromStatues", modid = "iceandfire")
public abstract class MixinEntityJzahar extends EntityMob implements IRangedAttackMob, IOmotholEntity, IBlacklistedFromStatues {
    @Shadow
    public int deathTicks;

    @Shadow
    private double speed;

    public MixinEntityJzahar(World worldIn) {
        super(worldIn);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void func_70609_aI() {
        this.motionX = this.motionY = this.motionZ = 0.0;
        ++this.deathTicks;
        if (this.deathTicks <= 800) {
            if (this.deathTicks == 410) {
                this.playSound(ACSounds.jzahar_charge, 1, 1);
            }

            if (this.deathTicks < 400) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 2.5, this.posZ, 0.0, 0.0, 0.0);
            }

            float f = (this.rand.nextFloat() - 0.5F) * 3.0F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 3.0F;
            if (this.deathTicks >= 100 && this.deathTicks < 400) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (double) f, this.posY + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
            }

            if (this.deathTicks >= 200 && this.deathTicks < 400) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (double) f, this.posY + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D);
                this.world.spawnParticle(EnumParticleTypes.LAVA, this.posX, this.posY + 2.5D, this.posZ, 0.0, 0.0, 0.0, 0);
            }

            this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 1.5, this.posZ, 0.0, 0.0, 0.0);
            if (this.deathTicks >= 790 && this.deathTicks <= 800) {
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX, this.posY + 1.5, this.posZ, 0.0, 0.0, 0.0);
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 4.0F, (1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F) * 0.7F);
            }

            if (this.deathTicks > 400 && this.deathTicks < 800) {
                float size = 32.0F;
                List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(size, size, size));
                for (Entity entity : list) {
                    if (KaiaUtil.hasInInventoryKaia(entity))
                        continue;
                    double scale = (size - entity.getDistance(this.posX, this.posY, this.posZ)) / size;
                    Vec3d dir = new Vec3d(entity.posX - this.posX, entity.posY - this.posY, entity.posZ - this.posZ);
                    dir = dir.normalize();
                    entity.addVelocity(dir.x * -this.speed * scale, dir.y * -this.speed * scale, dir.z * -this.speed * scale);
                }

                this.speed += 0.0001;
            }
        }
        int i;
        int j;
        if (!world.isRemote)
            if (deathTicks > 750 && deathTicks % 5 == 0) {
                i = 500;

                while (i > 0) {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    world.spawnEntity(new EntityXPOrb(world, posX, posY, posZ, j));
                }
            }
        if (deathTicks == 790 && !world.isRemote) {
            if (world.getGameRules().getBoolean("mobGriefing")) {
                List<BlockPos> blocks = new ArrayList<>();
                for (int x = 0; x < 10; x++)
                    for (int y = 0; y < 10; y++)
                        for (int z = 0; z < 10; z++) {
                            if (!world.isAirBlock(new BlockPos(posX + x, posY + y, posZ + z)))
                                blocks.add(new BlockPos(posX + x, posY + y, posZ + z));
                            if (!world.isAirBlock(new BlockPos(posX - x, posY + y, posZ + z)))
                                blocks.add(new BlockPos(posX - x, posY + y, posZ + z));
                            if (!world.isAirBlock(new BlockPos(posX + x, posY + y, posZ - z)))
                                blocks.add(new BlockPos(posX + x, posY + y, posZ - z));
                            if (!world.isAirBlock(new BlockPos(posX - x, posY + y, posZ - z)))
                                blocks.add(new BlockPos(posX - x, posY + y, posZ - z));
                            if (!world.isAirBlock(new BlockPos(posX + x, posY - y, posZ + z)))
                                blocks.add(new BlockPos(posX + x, posY - y, posZ + z));
                            if (!world.isAirBlock(new BlockPos(posX - x, posY - y, posZ + z)))
                                blocks.add(new BlockPos(posX - x, posY - y, posZ + z));
                            if (!world.isAirBlock(new BlockPos(posX + x, posY - y, posZ - z)))
                                blocks.add(new BlockPos(posX + x, posY - y, posZ - z));
                            if (!world.isAirBlock(new BlockPos(posX - x, posY - y, posZ - z)))
                                blocks.add(new BlockPos(posX - x, posY - y, posZ - z));
                        }
                for (BlockPos pos : blocks)
                    if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK)
                        world.setBlockToAir(pos);
            }

            if (!world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(3, 1, 3)).isEmpty()) {
                List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(3, 1, 3));
                for (Entity entity : entities)
                    if (entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) entity;
                        player.setHealth(1);
                        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 2400, 5));
                        player.addPotionEffect(new PotionEffect(MobEffects.POISON, 2400, 5));
                        if (player instanceof EntityPlayerMP) {
                            WorldServer worldServer = (WorldServer) player.world;
                            EntityPlayerMP mp = (EntityPlayerMP) player;
                            mp.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 80, 255));
                            mp.mcServer.getPlayerList().transferPlayerToDimension(mp, ACLib.dark_realm_id, new TeleporterDarkRealm(worldServer));
                            //							player.addStat(ACAchievements.enter_dark_realm, 1);
                        }
                    } else if (entity instanceof EntityLivingBase || entity instanceof EntityItem)
                        entity.setDead();
            }
            if (world.getClosestPlayerToEntity(this, 48) != null)
                world.spawnEntity(new EntityGatekeeperEssence(world, posX, posY, posZ));
        }

        if (ACConfig.showBossDialogs) {
            if (deathTicks == 20 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.1"));
            if (deathTicks == 100 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.2"));
            if (deathTicks == 180 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.3"));
            if (deathTicks == 260 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.4"));
            if (deathTicks == 340 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.5"));
            if (deathTicks == 420 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.6"));
            if (deathTicks == 500 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.7"));
            if (deathTicks == 580 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.8"));
            if (deathTicks == 660 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.9"));
            if (deathTicks == 800 && !world.isRemote)
                SpecialTextUtil.JzaharGroup(world, I18n.translateToLocal("message.jzahar.death.10"));
        }
        if (deathTicks == 800 && !world.isRemote)
            setDead();
    }
}
