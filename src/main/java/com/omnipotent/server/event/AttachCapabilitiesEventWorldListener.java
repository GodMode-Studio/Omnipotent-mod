package com.omnipotent.server.event;

import com.omnipotent.server.capability.AntiEntityProvider;
import com.omnipotent.server.capability.UnbanEntitiesProvider;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.omnipotent.Omnipotent.ANTIENTITYWORLD;
import static com.omnipotent.Omnipotent.ENTITIESUNBANNABLE;

public class AttachCapabilitiesEventWorldListener {
    @SubscribeEvent
    public void attachCapabilityWorld(AttachCapabilitiesEvent<World> event) {
        if (!(event.getObject() instanceof WorldServer))
            return;
        event.addCapability(ANTIENTITYWORLD, new AntiEntityProvider());
        event.addCapability(ENTITIESUNBANNABLE, new UnbanEntitiesProvider());
    }
}
