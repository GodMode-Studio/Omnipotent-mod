package com.omnipotent.common.event;

import com.omnipotent.common.capability.AntiEntityProvider;
import com.omnipotent.common.capability.IAntiEntitySpawn;
import com.omnipotent.common.capability.UnbanEntitiesProvider;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class LivingSpawnEventCheckSpawnListener {
    @SubscribeEvent
    public void LivingSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        if (!event.getWorld().isRemote) {
            IAntiEntitySpawn capability = event.getWorld().getCapability(AntiEntityProvider.antiEntitySpawn, null);
            List<Class> unBannableEntities = event.getWorld().getCapability(UnbanEntitiesProvider.unbanEntities, null).entitiesCannotBannable();
            for (Class classEntity : capability.entitiesDontSpawnInWorld()) {
                if (classEntity == event.getEntity().getClass() && !unBannableEntities.contains(classEntity))
                    event.setResult(Event.Result.DENY);
            }
        }
    }
}
