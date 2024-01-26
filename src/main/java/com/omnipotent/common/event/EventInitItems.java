package com.omnipotent.common.event;

import com.omnipotent.common.tool.DevTool;
import com.omnipotent.common.tool.Kaia;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventInitItems {

    public static List<Item> itemsInit = new ArrayList<>();

    public static Kaia kaia = new Kaia();
    public static DevTool testDev = new DevTool();

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) throws IOException {
        event.getRegistry().registerAll(itemsInit.toArray(new Item[0]));
    }
}
