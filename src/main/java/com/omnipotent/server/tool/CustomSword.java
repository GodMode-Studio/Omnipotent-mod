package com.omnipotent.server.tool;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSword;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;

public class CustomSword extends ItemSword {
    public CustomSword(String name, ToolMaterial material) {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(omnipotentTab);
        texturesItemsInit.add(this);
        itemsInit.add(this);
    }
}
