package com.omnipotent.common;

import com.omnipotent.Config;
import com.omnipotent.common.capability.*;
import com.omnipotent.common.capability.kaiacap.IKaiaBrand;
import com.omnipotent.common.capability.kaiacap.KaiaBrandItems;
import com.omnipotent.common.dimension.WorldProviderMod;
import com.omnipotent.common.event.*;
import com.omnipotent.common.gui.GuiHandler;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.PacketInicialization;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import static com.omnipotent.Omnipotent.*;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
//        Mixins.addConfiguration("mixins.omnipotent.json");
        MinecraftForge.EVENT_BUS.register(instance);
        CapabilityManager instanceCap = CapabilityManager.INSTANCE;
        instanceCap.register(IKaiaBrand.class, new KaiaBrandItems(), KaiaBrandItems::new);
        instanceCap.register(IBlockMode.class, new BlockModeStorage(), BlockModePlayer.class);
        instanceCap.register(IAntiEntitySpawn.class, new AntiEntityStorage(), AntiEntitySpawn.class);
        instanceCap.register(IUnbanEntities.class, new UnbanEntitiesStorage(), UnbanEntities.class);
        NetworkRegister.ACESS.sendToServer(new PacketInicialization());
        EventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(new KaiaEvent());
        eventBus.register(new EntityEvent());
        eventBus.register(new GuiHandler());
        eventBus.register(new EventInitItems());
        eventBus.register(new EntityStruckByLightningEventListener());
        eventBus.register(new LivingSpawnEventCheckSpawnListener());
        eventBus.register(new LivingSpawnEventSpecialSpawnListener());
        eventBus.register(new AttachCapabilitiesEventWorldListener());
        registerDimension();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        Config.init(event);
    }

    private static void registerDimension() {
        if (DimensionManager.isDimensionRegistered(DIMID))
            DIMID = DimensionManager.getNextFreeDimId();
        dimensionType = DimensionType.register("sealed", "_sealed", DIMID, WorldProviderMod.class, true);
        DimensionManager.registerDimension(DIMID, dimensionType);
    }

    public void init(FMLInitializationEvent event) {
        Config.reloadConfigs();
        Config.reloadConfigsOfFile();
    }
}
