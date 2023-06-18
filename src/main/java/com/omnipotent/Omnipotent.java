package com.omnipotent;

import com.omnipotent.Event.KaiaEvent;
import com.omnipotent.Event.KaiaToolTip;
import com.omnipotent.Event.UpdateEntity;
import com.omnipotent.gui.GuiHandler;
import com.omnipotent.keys.KeyEvent;
import com.omnipotent.keys.KeyInit;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.PacketInicialization;
import com.omnipotent.tools.Kaia;
import com.omnipotent.tools.KaiaEntity;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.io.IOException;
import java.util.List;

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

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(new KaiaEvent());
        MinecraftForge.EVENT_BUS.register(new KaiaUtil());
        MinecraftForge.EVENT_BUS.register(new UpdateEntity());
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(kaia);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new KeyEvent());
        MinecraftForge.EVENT_BUS.register(new KaiaToolTip());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        KeyInit.initKeys();
        ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ForgeChunkManager.LoadingCallback() {
            @Override
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                for (ForgeChunkManager.Ticket ticket : tickets) {
                    Entity entity = ticket.getEntity();
                    if (entity != null) {
                        ForgeChunkManager.forceChunk(ticket, entity.getEntityWorld().getChunkFromBlockCoords(entity.getPosition()).getPos());
                    }
                }
            }
        });
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

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) throws IOException {
        event.getRegistry().registerAll(kaia);
    }
}
