package com.omnipotent.server.event;

import com.omnipotent.Omnipotent;
import com.omnipotent.server.block.BlockOre;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Omnipotent.MODID)
public class EventBlockItems {
    public static List<Block> blocksInit = new ArrayList<>();
    public static BlockOre glass_ore_block = new BlockOre("glass_ore_block", Material.GLASS, 2F, SoundType.GLASS, EventInitItems.broken_glass, 2, 5);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) throws IOException {
        event.getRegistry().registerAll(blocksInit.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) throws IOException {
        for (Block block : blocksInit) {
           event.getRegistry().registerAll(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event) throws IOException {
        for (Block block : blocksInit) {
            registerRender(Item.getItemFromBlock(block));
        }
    }

    public static void registerRender(Item item) throws IOException {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

}
