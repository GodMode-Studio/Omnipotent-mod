package com.omnipotent.server.event;

import com.omnipotent.server.item.ItemBasic;
import com.omnipotent.server.tool.CustomSword;
import com.omnipotent.server.tool.CustomToolSword;
import com.omnipotent.server.tool.Kaia;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventInitItems {

    public static List<Item> itemsInit = new ArrayList<>();

    public static Kaia kaia = new Kaia();
    public static Item broken_glass = new ItemBasic("broken_glass");
    public static CustomSword glass_sword = new CustomSword("glass_sword", CustomToolSword.GLASS_SWORD);

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) throws IOException {
        event.getRegistry().registerAll(itemsInit.toArray(new Item[0]));
    }
}
