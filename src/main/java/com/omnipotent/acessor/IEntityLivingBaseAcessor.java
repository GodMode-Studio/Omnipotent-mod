package com.omnipotent.acessor;

import net.minecraft.util.DamageSource;

public interface IEntityLivingBaseAcessor {

    public int getRecentlyHit();

    public void setRecentlyHit(int recentlyHit);

    void setlastDamageSource(DamageSource lastDamageSource);

    void setlastDamageStamp(long lastDamageStamp);
}

