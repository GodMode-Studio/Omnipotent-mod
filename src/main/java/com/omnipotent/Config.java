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
    }

    public static void addPlayerInListThatCantRespawn(EntityPlayer player) {
        String uuid = player.getUniqueID().toString();
        if (playerscantrespawn.contains(uuid))
            return;
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], I18n.format("config.playerscantrespawn"));
        ArrayList stringList = new ArrayList(Arrays.asList(property.getStringList()));
        playerscantrespawn.add(uuid);
        property.setValues(playerscantrespawn.toArray(new String[0]));
        config.save();
    }

    public static void removePlayerOfListCantRespawn(EntityPlayer player) {
        String uuid = player.getUniqueID().toString();
        if (!playerscantrespawn.contains(uuid) || uuid == null || uuid.isEmpty() || uuid.equals(" "))
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
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], I18n.format("config.playerscantrespawn"));
        ArrayList stringList = new ArrayList(Arrays.asList(property.getStringList()));
        playerscantrespawn = stringList;
    }
    public static void reloadConfigsOfFile() {
        Property property = config.get(Configuration.CATEGORY_GENERAL, "playerscantrespawn", new String[0], I18n.format("config.playerscantrespawn"));
        property.setValues(playerscantrespawn.toArray(new String[0]));
        config.save();
    }
}