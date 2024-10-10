package com.omnipotent.client.key;

import com.omnipotent.Omnipotent;
import com.omnipotent.common.capability.kaiacap.KaiaProvider;
import com.omnipotent.common.entity.KaiaEntity;
import com.omnipotent.common.gui.GuiHandler;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.ReturnKaiaPacket;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.common.specialgui.net.KaiaContainerOpenPackte;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.omnipotent.common.gui.GuiHandler.*;
import static com.omnipotent.constant.NbtBooleanValues.showInfo;
import static com.omnipotent.util.UtilityHelper.rayTraceClient;
import static net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;

public class KeyInit {

    public static final List<KeyMod> keyBindingRequireKaiaInMainHandList = new ArrayList<>();
    public static final List<KeyMod> othersKeyBindingList = new ArrayList<>();
    private static final String translateKeyOfCategory = "keykaia.category";
//    private static final Consumer<Object> functionAntiEntities = object -> {
//        EntityPlayer player = (EntityPlayer) object;
//        if (KeyInit.kaiaAntiEntities.isPressed() && KaiaUtil.getKaiaInMainHand(player) != null) {
//            AxisAlignedBB bb = player.getEntityBoundingBox();
//            Vec3d lookVec = player.getLookVec();
//            lookVec = lookVec.normalize();
//            double reachDistance = 5.0;
//            Vec3d endVec = player.getPositionEyes(1.0F).addVector(lookVec.x * reachDistance, lookVec.y * reachDistance, lookVec.z * reachDistance);
//            bb = bb.expand(lookVec.x * reachDistance, lookVec.y * reachDistance, lookVec.z * reachDistance).expand(1.0, 1.0, 1.0);
//            List<Entity> entities = player.world.getEntitiesWithinAABBExcludingEntity(player, bb);
//            Entity targetEntity = null;
//            float distanceEntity;
//            Float distance = Float.MAX_VALUE;
//            for (Entity entity : entities) {
//                if (entity.getEntityBoundingBox().calculateIntercept(player.getPositionEyes(1.0F), endVec) != null) {
//                    distanceEntity = player.getDistance(entity);
//                    if (distanceEntity < distance) {
//                        targetEntity = entity;
//                        distance = distanceEntity;
//                    }
//                }
//            }
//            if (targetEntity != null)
//                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(entitiesCantKill, targetEntity.getUniqueID().toString() + divisionUUIDAndName + targetEntity.getName(), 0));
//        }
//    };

    //    private static final KeyBinding kaiaAntiEntities = new KeyMod(I18n.format("keykaia.antientities"), Keyboard.KEY_Y, I18n.format(translateKeyOfCategory), object -> {
//        EntityPlayer player = (EntityPlayer) object;
//        if (KeyInit.kaiaAntiEntitiesGui.isPressed() && KaiaUtil.getKaiaInMainHand(player) != null)
//            player.openGui(Omnipotent.instance, 7, player.world, 0, 0, 0);
//    });
//    private static final KeyBinding kaiaAntiEntitiesGui = new KeyMod(I18n.format("keykaia.antientitiesgui"), Keyboard.KEY_U, I18n.format(translateKeyOfCategory), functionAntiEntities);

    private static final KeyBinding keyReturnKaia = new KeyMod(I18n.format("keykaia.returnkaia"), IN_GAME, Keyboard.KEY_G, I18n.format(translateKeyOfCategory), (unused, unused2) -> {
        if (KeyInit.keyReturnKaia.isPressed()) {
            NetworkRegister.sendToServer(new ReturnKaiaPacket());
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaGui = new KeyMod("keykaia.config", IN_GAME, Keyboard.KEY_R, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        EntityPlayer player = (EntityPlayer) object;
        if (KeyInit.kaiaGui.isPressed() && hasKaia) {
            player.openGui(Omnipotent.instance, GuiHandler.KaiaGui, player.world, 0, 0, 0);
            return true;
        }
        return false;
    });

    private static final KeyBinding getKaiaBetweenSaves = new KeyMod("keykaia.getKaiaBetweenSaves", IN_GAME, Keyboard.KEY_NUMPAD2, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        if (KeyInit.getKaiaBetweenSaves.isPressed()) {
            NetworkRegister.sendToServer(new KaiaNbtPacket("getKaiaBetweenSaves"));
        }
        return false;
    });

    private static final KeyBinding kaiaGuiEnchantment = new KeyMod(I18n.format("keykaia.enchantmentkaia"), IN_GAME, Keyboard.KEY_L, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        EntityPlayer player = (EntityPlayer) object;
        if (KeyInit.kaiaGuiEnchantment.isPressed() && hasKaia) {
            player.openGui(Omnipotent.instance, KaiaGuiEnchantment, player.world, 0, 0, 0);
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaGuiBackpack = new KeyMod(I18n.format("keykaia.backpack"), IN_GAME, Keyboard.KEY_P, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        if (KeyInit.kaiaGuiBackpack.isPressed() && hasKaia) {
            NetworkRegister.sendToServer(new KaiaContainerOpenPackte(GUIContainerKaia));
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaGuiPotion = new KeyMod(I18n.format("keykaia.potion"), Keyboard.KEY_O, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        EntityPlayer player = (EntityPlayer) object;
        if (KeyInit.kaiaGuiPotion.isPressed() && hasKaia) {
            player.openGui(Omnipotent.instance, KaiaGuiPotionAddedAndRemove, player.world, 0, 0, 0);
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaGuiDimension = new KeyMod(I18n.format("keykaia.dimension"), Keyboard.KEY_K, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        EntityPlayer player = (EntityPlayer) object;
        if (KeyInit.kaiaGuiDimension.isPressed() && hasKaia) {
            player.openGui(Omnipotent.instance, KaiaGuiDimension, player.world, 0, 0, 0);
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaPlayerGui = new KeyMod(I18n.format("keykaia.player"), Keyboard.KEY_H, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        EntityPlayer player = (EntityPlayer) object;
        if (KeyInit.kaiaPlayerGui.isPressed() && hasKaia) {
            player.openGui(Omnipotent.instance, KaiaPlayerGui, player.world, 0, 0, 0);
            return true;
        }
        return false;
    });
    private static final KeyBinding BlockSpectator = new KeyMod(I18n.format("keykaia.spectate"), Keyboard.KEY_J, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        if (KeyInit.BlockSpectator.isPressed()) {
            NetworkRegister.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.kaiaBlockSpectator));
            return true;
        }
        return false;
    });
    private static final KeyBinding BlockCreative = new KeyMod(I18n.format("keykaia.creative"), Keyboard.KEY_F, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        if (KeyInit.BlockCreative.isPressed()) {
            NetworkRegister.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.kaiaBlockCreative));
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaShowOrHideInfo = new KeyMod(I18n.format("keykaia.kaiashowinfo"), Keyboard.KEY_MULTIPLY, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        EntityPlayer player = (EntityPlayer) object;
        if (KeyInit.kaiaShowOrHideInfo.isPressed() && hasKaia) {
            NetworkRegister.sendToServer(new KaiaNbtPacket(showInfo.getValue(), !KaiaUtil.getKaiaInMainHand(player).get().getBoolean(showInfo)));
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaSummonSwords = new KeyMod(I18n.format("keykaia.kaiasummonswords"), Keyboard.KEY_ADD, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        if (KeyInit.kaiaSummonSwords.isPressed() && hasKaia) {
            NetworkRegister.sendToServer(new KaiaNbtPacket("kaiasummonswords"));
            return true;
        }
        return false;
    });
    private static final KeyBinding kaiaAttackShow = new KeyMod(I18n.format("keykaia.kaiaattackshow"), Keyboard.KEY_SUBTRACT, I18n.format(translateKeyOfCategory), (object, hasKaia) -> {
        if (KeyInit.kaiaAttackShow.isPressed()) {
            Entity entityHit = rayTraceClient(Minecraft.getMinecraft(), KaiaEntity.class, 300, 0).entityHit;
            EntityPlayer player = (EntityPlayer) object;
            if (entityHit instanceof EntityLivingBase) {
                UtilityHelper.getKaiaCap(player).ifPresent(cap -> {
                    List<String> kaiaSwordsSummoned = cap.getKaiaSwordsSummoned();
                    player.getEntityWorld().getLoadedEntityList().stream().filter(e ->
                                    e instanceof KaiaEntity && kaiaSwordsSummoned.contains(e.getPersistentID().toString())).map(KaiaEntity.class::cast)
                            .forEach(e -> e.setAttackTarget((EntityLivingBase) entityHit));
                    NetworkRegister.sendToServer(new KaiaNbtPacket("kaiaattackshow", entityHit.getEntityId()));
                });
            }
            return true;
        }
        return false;
    });

    public static void initKeys() {
        keyBindingRequireKaiaInMainHandList.forEach(ClientRegistry::registerKeyBinding);
        othersKeyBindingList.forEach(ClientRegistry::registerKeyBinding);
    }

    static {
        Field[] fields = KeyInit.class.getDeclaredFields();
        Stream<Field> stream = Arrays.stream(fields);
        keyBindingRequireKaiaInMainHandList.addAll(stream.peek(field -> field.setAccessible(true)).filter(field -> field.getType() == KeyBinding.class && field.getName().startsWith("kaia")).map(field -> {
            try {
                return (KeyMod) field.get(null);
            } catch (IllegalAccessException e) {
                return null;
            }
        }).collect(Collectors.toList()));
        stream = Arrays.stream(fields);
        othersKeyBindingList.addAll(stream.peek(field -> field.setAccessible(true)).filter(field -> field.getType() == KeyBinding.class && !field.getName().startsWith("kaia")).map(field -> {
            try {
                return (KeyMod) field.get(null);
            } catch (IllegalAccessException e) {
                return null;
            }
        }).collect(Collectors.toList()));
    }
}
