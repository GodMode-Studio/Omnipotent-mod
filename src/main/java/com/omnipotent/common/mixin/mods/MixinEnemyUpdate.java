package com.omnipotent.common.mixin.mods;

import com.omnipotent.util.KaiaUtil;
import net.crazymonsters.entity.EnemyUpdate;
import net.crazymonsters.entity.EntityNotch;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EnemyUpdate.class, remap = false)
public abstract class MixinEnemyUpdate {
    @Shadow
    protected static boolean isTerrible;
    @Shadow
    protected static boolean isKicked;
    @Shadow
    public static boolean isRetry;
    @Shadow
    private static boolean isKicked2;

    @Shadow
    protected static void AntiCrazyWorld(Entity entity) {
    }

    /**
     * @author
     * @reason
     */
    @SubscribeEvent
    @Overwrite
    public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() != null && (MixinEnemyUpdate.isTerrible || MixinEnemyUpdate.isKicked2) && !(event.getEntity() instanceof EntityNotch) && !(event.getEntity() instanceof EntityPlayer)) {
            if (event.getEntity() instanceof EntityLivingBase) {
                ((EntityLivingBase) event.getEntity()).setHealth(0.0f);
                if (event.getEntityLiving() instanceof EntityLiving) {
                    try {
                        event.getEntityLiving().isDead = true;
                        ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                        ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                        ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                        ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                        event.getEntityLiving().ticksExisted = 0;
                        ((EntityLiving) event.getEntityLiving()).setNoAI(true);
                        event.getEntityLiving().width = 0.1f;
                        event.getEntityLiving().height = 0.1f;
                        event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.0);
                        final EntityLiving entityLiving2;
                        final EntityLiving entityLiving = entityLiving2 = (EntityLiving) event.getEntityLiving();
                        ++entityLiving2.deathTime;
                        event.getEntityLiving().prevPosY = 2.1E9;
                        event.getEntityLiving().hurtResistantTime = 199999088;
                        event.getEntityLiving().maxHurtTime = 199999088;
                        event.getEntityLiving().maxHurtResistantTime = 199999088;
                        event.getEntityLiving().hurtTime = 199999088;
                        event.getEntityLiving().velocityChanged = true;
                        if (!entityLiving.world.isRemote) {
                            final TextComponentString message3 = new TextComponentString(I18n.translateToLocal(event.getEntity().getName()) + I18n.translateToLocal("commands.notch.ban"));
                            Minecraft.getMinecraft().player.sendMessage((ITextComponent) message3);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            event.setCanceled(true);
        }
        if (event.getEntityLiving() instanceof EntityPlayer && MixinEnemyUpdate.isKicked && !event.getEntityLiving().world.isRemote) {
            try {
                final EntityPlayerMP player5 = (EntityPlayerMP) event.getEntityLiving();
                final TextComponentString reason = new TextComponentString(I18n.translateToLocal("notch_kick_all"));
                player5.connection.disconnect((ITextComponent) reason);
                MixinEnemyUpdate.isKicked = false;
            } catch (Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "No kickable player (cheated player)");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Cheating");
                throw new ReportedException(crashreport);
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer && MixinEnemyUpdate.isKicked2 && !event.getEntityLiving().world.isRemote) {
            try {
                final EntityPlayerMP player5 = (EntityPlayerMP) event.getEntityLiving();
                final TextComponentString reason = new TextComponentString(I18n.translateToLocal("Server Closed   (Cause : cheating mob spawned)"));
                player5.connection.disconnect((ITextComponent) reason);
                MixinEnemyUpdate.isKicked2 = false;
            } catch (Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Server Stopped (cheated mob)");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Cheating");
                throw new ReportedException(crashreport);
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer && EnemyUpdate.isRetry && !event.getEntityLiving().world.isRemote) {
            if (cancelRespawn(event)) return;
            final EntityNotch entitybolt = new EntityNotch(event.getEntity().world);
            entitybolt.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY + 15.0, event.getEntity().posZ, event.getEntity().rotationYaw, event.getEntity().rotationPitch);
            event.getEntity().world.spawnEntity((Entity) entitybolt);
            EnemyUpdate.isRetry = false;
        }
        if (event.getEntityLiving() instanceof EntityPlayer && MixinEnemyUpdate.isTerrible) {
            MixinEnemyUpdate.isTerrible = false;
        }
    }

    private static boolean cancelRespawn(LivingEvent.LivingUpdateEvent event) {
        if (KaiaUtil.hasInInventoryKaia(event.getEntityLiving())) {
            EnemyUpdate.isRetry = false;
            return true;
        }
        return false;
    }
}
