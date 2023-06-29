package com.omnipotent.server.tool;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

public class CustomToolSword {
    public static final Item.ToolMaterial GLASS_SWORD = EnumHelper.addToolMaterial(
            "GLASS_SWORD",
            3,
            20,
            0.0F,
            8.0F,
            15
    );
}