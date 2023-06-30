package com.omnipotent;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    private static Configuration config;
    public static List<String> playerscantrespawn = new ArrayList<>();

    public static void init(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
    }

    public static void addPlayerInListThatCantRespawn(String uuid) {
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], I18n.format("config.playerscantrespawn"));
        ArrayList stringList = new ArrayList(Arrays.asList(property.getStringList()));
        if (!stringList.contains(uuid)) {
            playerscantrespawn.add(uuid);
            property.setValues(playerscantrespawn.toArray(new String[0]));
        }
        config.save();
    }
    public static void reloadConfigs() {
        config.load();
    }
}