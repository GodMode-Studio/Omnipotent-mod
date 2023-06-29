package com.omnipotent.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventBlockItems.blocksInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;

public class BlockBasic extends Block {
    public BlockBasic(String name, Material blockMaterialIn) {
        super(blockMaterialIn);
        setRegistryName(name);
        setUnlocalizedName(name);
        texturesItemsInit.add(this);
        blocksInit.add(this);
    }
}
