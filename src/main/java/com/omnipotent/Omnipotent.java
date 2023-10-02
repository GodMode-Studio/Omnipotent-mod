package com.omnipotent;

import com.omnipotent.client.render.RenderCustomLightningBolt;
import com.omnipotent.server.CommonProxy;
import com.omnipotent.server.capability.*;
import com.omnipotent.server.command.CommandOmni;
import com.omnipotent.server.entity.CustomLightningBolt;
import com.omnipotent.server.entity.KaiaEntity;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.PacketInicialization;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = Omnipotent.MODID, name = Omnipotent.NAME, version = Omnipotent.VERSION, useMetadata = true)
@Mod.EventBusSubscriber
public class Omnipotent {
    public static final String MODID = "omnipotent";
    public static final String NAME = "Omnipotent Mod";
    public static final String VERSION = "1.0";
    public static final OmnipotentTab omnipotentTab = new OmnipotentTab("Omnipotent mod");
    public static SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("omnipotent");
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
        MinecraftForge.EVENT_BUS.register(instance);
        CapabilityManager.INSTANCE.register(IKaiaBrand.class, new KaiaStorage(), KaiaBrandItems.class);
        CapabilityManager.INSTANCE.register(IBlockMode.class, new BlockModeStorage(), BlockModePlayer.class);
        CapabilityManager.INSTANCE.register(IAntiEntitySpawn.class, new AntiEntityStorage(), AntiEntitySpawn.class);
        CapabilityManager.INSTANCE.register(IUnbanEntities.class, new UnbanEntitiesStorage(), UnbanEntities.class);
        proxy.preInit(event);
        Config.init(event);
        register();
        registerRenderizador();
    }

    public static void register() {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":customligth"), CustomLightningBolt.class, "custom", 1, MODID, 64, 1, true);
    }

    public static void registerRenderizador() {
        RenderingRegistry.registerEntityRenderingHandler(CustomLightningBolt.class, manager -> new RenderCustomLightningBolt(manager));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        Config.reloadConfigs();
        Config.reloadConfigsOfFile();
    }

    @SubscribeEvent
    public void entityRegister(RegistryEvent.Register<EntityEntry> event) {
        EntityEntryBuilder<Entity> entity = EntityEntryBuilder.create().entity(KaiaEntity.class).id("kaia", 1).name("kaia").tracker(64, 1, true);
        event.getRegistry().register(entity.build());
    }

    @SubscribeEvent
    public void playerJoinWorld(WorldEvent.Load event) {
        NetworkRegister.ACESS.sendToServer(new PacketInicialization());
    }

    @EventHandler
    public void ServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandOmni());
    }
}
