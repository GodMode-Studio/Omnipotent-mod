package com.omnipotent.server.event;

import com.omnipotent.server.entity.CustomLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityStruckByLightningEventListener {
    @SubscribeEvent
    public void onLightningStrike(EntityStruckByLightningEvent e) {
        Entity lightning = e.getLightning();
        if (lightning instanceof CustomLightningBolt && !lightning.world.isRemote)
            e.getEntity().attackEntityFrom(DamageSource.LIGHTNING_BOLT, 99999999);
    }
}
