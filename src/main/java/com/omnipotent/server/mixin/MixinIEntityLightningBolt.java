package com.omnipotent.server.mixin;

import com.omnipotent.acessor.IEntityLightningBoltAcessor;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLightningBolt.class)
public abstract class MixinIEntityLightningBolt extends EntityWeatherEffect implements IEntityLightningBoltAcessor {

    public MixinIEntityLightningBolt(World worldIn) {
        super(worldIn);
    }

    @Accessor("lightningState")
    public abstract int getLightningState();

    @Accessor("boltLivingTime")
    public abstract int getBoltLivingTime();

    @Accessor("effectOnly")
    public abstract boolean getEffectOnly();

    @Override
    public int acessorBoltLivingTime() {
        return getBoltLivingTime();
    }

    @Override
    public int acessorLightningState() {
        return getLightningState();
    }

    @Override
    public boolean acessorEffectOnly() {
        return getEffectOnly();
    }
}
