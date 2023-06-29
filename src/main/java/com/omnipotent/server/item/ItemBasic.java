package com.omnipotent.server.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;

public class ItemBasic extends Item {

    public ItemBasic(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(omnipotentTab);
        texturesItemsInit.add(this);
        itemsInit.add(this);
    }
}
