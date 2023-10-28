package com.omnipotent;

import com.omnipotent.server.CommonProxy;
import com.omnipotent.server.capability.*;
import com.omnipotent.server.command.CommandOmni;
import com.omnipotent.server.entity.CustomLightningBolt;
import com.omnipotent.server.entity.KaiaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import org.spongepowered.asm.mixin.Mixins;

@Mod(modid = Omnipotent.MODID, name = Omnipotent.NAME, version = Omnipotent.VERSION, useMetadata = true)
@Mod.EventBusSubscriber
public class Omnipotent {
    public static final String MODID = "omnipotent";
    public static final String NAME = "Omnipotent Mod";
    public static final String VERSION = "alpha 9.1";
    public static final OmnipotentTab omnipotentTab = new OmnipotentTab("Omnipotent mod");
    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(Omnipotent.MODID);
    public static final ResourceLocation KAIACAP = new ResourceLocation(MODID, "kaiabrand");
    public static final ResourceLocation BLOCK_MODES_OF_PLAYER = new ResourceLocation(MODID, "blockmodesplayer");
    public static final ResourceLocation ANTIENTITYWORLD = new ResourceLocation(MODID, "antityentityworld");
    public static final ResourceLocation ENTITIESUNBANNABLE = new ResourceLocation(MODID, "entitiesunbannable");


    @Mod.Instance(Omnipotent.MODID)
    public static Omnipotent instance;

    @SidedProxy(clientSide = "com.omnipotent.client.ClientProxy", serverSide = "com.omnipotent.server.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Mixins.addConfiguration("mixins.omnipotent.json");
        MinecraftForge.EVENT_BUS.register(instance);
        CapabilityManager.INSTANCE.register(IKaiaBrand.class, new KaiaStorage(), KaiaBrandItems.class);
        CapabilityManager.INSTANCE.register(IBlockMode.class, new BlockModeStorage(), BlockModePlayer.class);
        CapabilityManager.INSTANCE.register(IAntiEntitySpawn.class, new AntiEntityStorage(), AntiEntitySpawn.class);
        CapabilityManager.INSTANCE.register(IUnbanEntities.class, new UnbanEntitiesStorage(), UnbanEntities.class);
        proxy.preInit(event);
        Config.init(event);
    }

    private static int id = 0;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        Config.reloadConfigs();
        Config.reloadConfigsOfFile();
    }

    @SubscribeEvent
    public void entityRegister(RegistryEvent.Register<EntityEntry> event) {
        EntityEntryBuilder<Entity> entity = EntityEntryBuilder.create().entity(KaiaEntity.class).id("kaia", ++id).name("kaia").tracker(64, 1, true);
        EntityEntryBuilder<Entity> entity2 = EntityEntryBuilder.create().entity(CustomLightningBolt.class).id("customligth", ++id).name("custom").tracker(64, 1, true);
        event.getRegistry().registerAll(entity.build(), entity2.build());
    }

    @EventHandler
    public void ServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandOmni());
    }
}
