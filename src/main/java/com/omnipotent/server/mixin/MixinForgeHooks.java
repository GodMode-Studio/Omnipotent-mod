package com.omnipotent.server.mixin;

import com.omnipotent.server.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class MixinForgeHooks {
//    se onLivingDeath retorna true onDeath será cancelado

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public static boolean onLivingDeath(EntityLivingBase entity, DamageSource src) {
        if (!UtilityHelper.isPlayer(entity)) {
            return !src.getDamageType().equals(new AbsoluteOfCreatorDamage(src.getTrueSource()).getDamageType()) && MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
        }
        if (hasInInventoryKaia(entity)) {
            entity.setHealth(Float.MAX_VALUE);
            entity.isDead = false;
            entity.deathTime = 0;
            return true;
        }
        if (entity.getLastDamageSource() != null && entity.getLastDamageSource().damageType.equals(new AbsoluteOfCreatorDamage(src.getTrueSource()).getDamageType())) {
            entity.isDead = false;
            entity.deathTime = Integer.MAX_VALUE;
        }
        return !src.getDamageType().equals(new AbsoluteOfCreatorDamage(src.getTrueSource()).getDamageType()) && MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }
//    se onLivingAttack retorna false attackentityFrom será cancelado

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public static boolean onLivingAttack(EntityLivingBase entity, DamageSource src, float amount) {
        if (UtilityHelper.isPlayer(entity))
            return true;
        else if (src.getDamageType().equals(new AbsoluteOfCreatorDamage(src.getTrueSource()).getDamageType())) {
            return true;
        }
        return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }
}
