package com.omnipotent.constant;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NbtBooleanValues {
    counterAttack("counterAttack", "function.description.counterAttack"), killAllEntities("killAllEntities", "function.description.killAllEntities"), killFriendEntities("killFriendEntities", "function.description.killFriendEntities"), attackYourWolf("attackYourWolf", "function.description.attackYourWolf"), interactLiquid("interactLiquid", "function.description.interactLiquid"), noBreakTileEntity("noBreakTileEntity", "function.description.noBreakTileEntity"), autoBackPackEntities("autobackpackentities", "function.description.autobackpackentities"), chargeEnergyItemsInInventory("chargeEnergyItemsInInventory", "function.description.chargeEnergyItemsInInventory"), summonLightBoltsInKill("summonlightboltsinkill", "function.description.summonlightboltsinkill"), autoBackPack("autobackpack", "function.description.autobackpack"), playersCantRespawn("playerscantresSpawn", "function.description.playersCantRespawn"), banEntitiesAttacked("banEntitiesAttacked", "function.description.banEntitiesAttacked"), autoKill("autoKill", "function.description.autoKill"), showInfo("showinfo", "NADA"), chargeManaItemsInInventory("chargeManaItemsInInventory", "function.description.chargeManaItemsInInventory"), playersWhoShouldNotKilledInCounterAttack("playerdontkillcounter", "NADA"), playerDontKillInDirectAttack("playerdontkillindirectAttack", "NADA"), ignoreKeepInventory("ignoreKeepInventory", "function.description.ignoreKeepInventory"), activeCustomPlayerName("activeCustomPlayerName", "function.description.customPlayerName"), fastBreakBlocks("fastBreakBlocks", "function.description.fastBreakBlocks"), rgbEnchantmentGlitch("rgbEnchantmentGlitch", "function.description.rgbEnchantmentGlitch"), banEntityToSealedDimension("banEntityToSealedDimension", "function.description.banEntityToSealedDimension");
    private String value;
    private String description;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    NbtBooleanValues(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static final List<String> valuesNbt;

    static {
        Field[] declaredFields = NbtBooleanValues.class.getDeclaredFields();
        valuesNbt = Arrays.stream(declaredFields).filter(field -> field.getType() == NbtBooleanValues.class).map(field -> {
            try {
                field.setAccessible(true);
                NbtBooleanValues nbtBooleanValues = (NbtBooleanValues) field.get(null);
                return nbtBooleanValues.getValue();
            } catch (IllegalAccessException e) {
            }
            return null;
        }).collect(Collectors.toList());
    }
}