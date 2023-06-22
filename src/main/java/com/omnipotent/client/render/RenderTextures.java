package com.omnipotent.client.render;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;

public class RenderTextures {
    public static List<Object> texturesItemsInit = new ArrayList<>();

    public static void registerTextures() {
        for (Object obj : texturesItemsInit) {
            if (obj instanceof Item)
                registerTexturesItems((Item) obj);
        }
    }

    public static void registerTexturesItems(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
