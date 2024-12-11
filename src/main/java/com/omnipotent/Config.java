package com.omnipotent;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Config {
    private static Configuration config;
    private static List<String> playerscantrespawn = new ArrayList<>();

    public static void init(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        reloadConfigs();
    }

    public static void addPlayerInListThatCantRespawn(EntityPlayer player) {
        String uuid = player.getUniqueID().toString();
        if (playerscantrespawn.contains(uuid))
            return;
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], "Players can´t respawn");
        playerscantrespawn.add(uuid);
        property.setValues(playerscantrespawn.toArray(new String[0]));
        config.save();
    }

    public static void removePlayerOfListCantRespawn(EntityPlayer player) {
        String uuid = player.getUniqueID().toString();
        if (!playerscantrespawn.contains(uuid))
            return;
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], I18n.format("config.playerscantrespawn"));
        playerscantrespawn.remove(uuid);
        property.setValues(playerscantrespawn.toArray(new String[0]));
        config.save();
    }

    public static List getListPlayersCantRespawn() {
        return Collections.unmodifiableList(playerscantrespawn);
    }

    public static void reloadConfigs() {
        config.load();
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], "players that can't respawn");
        playerscantrespawn.clear();
        playerscantrespawn.addAll(Arrays.asList(property.getStringList()));
    }

    public static void reloadConfigsOfFile() {
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], "players that can't respawn");
        property.setValues(playerscantrespawn.toArray(new String[0]));
        config.save();
    }
}