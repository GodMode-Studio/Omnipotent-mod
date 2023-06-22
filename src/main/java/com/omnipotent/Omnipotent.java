package com.omnipotent;

import com.omnipotent.server.CommonProxy;
import com.omnipotent.server.capability.IKaiaBrand;
import com.omnipotent.server.capability.KaiaBrandItems;
import com.omnipotent.server.capability.KaiaProvider;
import com.omnipotent.server.capability.KaiaStorage;
import com.omnipotent.server.entity.KaiaEntity;
import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.PacketInicialization;
import com.omnipotent.server.tool.Kaia;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod(modid = Omnipotent.MODID, name = Omnipotent.NAME, version = Omnipotent.VERSION)
@Mod.EventBusSubscriber
public class Omnipotent {
    public static final String MODID = "omnipotent";
    public static final String NAME = "Omnipotent Mod";
    public static final String VERSION = "1.0";
    public static final OmnipotentTab omnipotentTab = new OmnipotentTab("Omnipotent mod");
    public static SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("omnipotent");
    static Kaia kaia = new Kaia();

    @Mod.Instance(Omnipotent.MODID)
    public static Omnipotent instance;

    @SidedProxy(clientSide = "com.omnipotent.client.ClientProxy", serverSide = "com.omnipotent.server.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
        CapabilityManager.INSTANCE.register(IKaiaBrand.class, new KaiaStorage(), KaiaBrandItems.class);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void posinit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public void entityRegister(RegistryEvent.Register<EntityEntry> event) {
        EntityEntryBuilder<Entity> entity = EntityEntryBuilder.create().entity(KaiaEntity.class).id("kaia_entity", 1).name("kaia_entity").tracker(64, 1, true);
        event.getRegistry().register(entity.build());
    }

    @SubscribeEvent
    public void playerJoinWorld(WorldEvent.Load event) {
        NetworkRegister.ACESS.sendToServer(new PacketInicialization());
    }

    public static final ResourceLocation KAIACAP = new ResourceLocation(MODID, "kaiabrand");

    @SubscribeEvent
    public void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer))
            return;
        event.addCapability(KAIACAP, new KaiaProvider());
    }
//    @SubscribeEvent
//    public void attachCapabilityWorld(AttachCapabilitiesEvent<World> event) {
//        if (!(event.getObject() instanceof World))
//            return;
//        event.addCapability(KAIACAP, new KaiaProvider());
//    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        IKaiaBrand kaiaBrand = player.getCapability(KaiaProvider.KaiaBrand, null);
        IKaiaBrand oldKaiaBrand = event.getOriginal().getCapability(KaiaProvider.KaiaBrand, null);
        kaiaBrand.habilityBrand(oldKaiaBrand.returnList());
    }
}
