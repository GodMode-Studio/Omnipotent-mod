package com.omnipotent;

import com.omnipotent.common.CommonProxy;
import com.omnipotent.common.command.CommandOmni;
import com.omnipotent.common.entity.CustomLightningBolt;
import com.omnipotent.common.entity.KaiaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
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

@Mod(modid = Omnipotent.MODID, name = Omnipotent.NAME, version = Omnipotent.VERSION, useMetadata = true)
@Mod.EventBusSubscriber
public class Omnipotent {
    public static final String MODID = "omnipotent";
    public static final String NAME = "Omnipotent Mod";
    public static final String VERSION = "alpha 9.2";
    public static final OmnipotentTab omnipotentTab = new OmnipotentTab("Omnipotent mod");
    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(Omnipotent.MODID);
    public static final ResourceLocation KAIACAP = new ResourceLocation(MODID, "kaiabrand");
    public static final ResourceLocation BLOCK_MODES_OF_PLAYER = new ResourceLocation(MODID, "blockmodesplayer");
    public static final ResourceLocation ANTIENTITYWORLD = new ResourceLocation(MODID, "antityentityworld");
    public static final ResourceLocation ENTITIESUNBANNABLE = new ResourceLocation(MODID, "entitiesunbannable");
    public static final boolean NETHER_TYPE = false;


    @Mod.Instance(Omnipotent.MODID)
    public static Omnipotent instance;

    @SidedProxy(clientSide = "com.omnipotent.client.ClientProxy", serverSide = "com.omnipotent.common.CommonProxy")
    public static CommonProxy proxy;
    public static int DIMID = 2832783;
    public static DimensionType dimensionType;
    private static int id = 0;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
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
