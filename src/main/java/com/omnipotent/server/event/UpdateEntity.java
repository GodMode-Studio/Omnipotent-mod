package com.omnipotent.server.event;

import com.omnipotent.server.tool.Kaia;
import com.omnipotent.server.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.*;
import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

public class UpdateEntity {
    public static final Set<String> entitiesWithKaia = new HashSet<>();
    public static final Set<String> entitiesFlightKaia = new HashSet<>();
    public static ArrayList<EntityLivingBase> mobsNamedMkll = new ArrayList<>();
    private static Map<EntityLivingBase, Integer> timeTeleportation = new HashMap<>();

    @SubscribeEvent
    public void updateAbilities(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            defineTimeAndListEasterEggMkll(event);
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        String keyUID = player.getCachedUniqueIdString() + "|" + player.world.isRemote;
        boolean hasKaia = hasInInventoryKaia(player);
        if (hasKaia && player.getHealth() < 5) {
            player.isDead = false;
            player.setHealth(Float.MAX_VALUE);
        }
        if (KaiaUtil.theLastAttackIsKaia(player) && !hasKaia && !player.isDead) {
            player.isDead = true;
            player.deathTime = 99999;
            player.onDeath(new AbsoluteOfCreatorDamage(player));
        }
        if (hasKaia) {
            if (player.isBurning()) {
                player.extinguish();
            }
            entitiesWithKaia.add(keyUID);
            handleKaiaStateChange(player, true);
        }
        if (!hasKaia) {
            entitiesWithKaia.remove(keyUID);
            handleKaiaStateChange(player, false);
        }
    }


    private static void defineTimeAndListEasterEggMkll(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.hasCustomName() && entity.getCustomNameTag().equals("mkll") && !mobsNamedMkll.contains(entity) && !entity.world.isRemote) {
            mobsNamedMkll.add(entity);
            timeTeleportation.put(entity, 1);
        } else if (mobsNamedMkll.contains(entity) && !entity.getCustomNameTag().equals("mkll")) {
            timeTeleportation.remove(entity);
            mobsNamedMkll.remove(entity);
        }
    }

    private static void handleKaiaStateChange(EntityLivingBase entity, boolean isNew) {
        String keyUID = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) entity);
            if (isNew) {
                player.capabilities.allowFlying = true;
                player.capabilities.setFlySpeed(0.15f);
                entitiesFlightKaia.add(keyUID);
            } else {
                if (!player.capabilities.isCreativeMode && entitiesFlightKaia.contains(keyUID)) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    entitiesFlightKaia.remove(keyUID);
                }
            }
        }
    }

    @SubscribeEvent
    public void cancelTimeItem(ItemExpireEvent event) {
        if (event.getEntityItem().getItem().getItem() instanceof Kaia) {
            if(event.getEntityItem().getItem().hasTagCompound()){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent()
    public void entityPickupKaia(EntityItemPickupEvent event) {
        if (event.getEntityPlayer().world.isRemote || event.getItem().world.isRemote)
            return;
        if (!(event.getEntity() instanceof EntityPlayer) || !(event.getItem().getItem().getItem() instanceof Kaia))
            return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = event.getItem().getItem();
        if (!item.hasTagCompound())
            return;
        if (!KaiaUtil.isOwnerOfKaia(item, player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityItemJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() != null && event.getEntity() instanceof EntityItem && !event.getEntity().getEntityWorld().isRemote) {
            EntityItem entityItem = (EntityItem) event.getEntity();
            if (!entityItem.getItem().isEmpty() && entityItem.getItem().getItem() instanceof Kaia) {
                entityItem.setEntityInvulnerable(true);
                entityItem.setNoPickupDelay();
                if (!entityItem.getItem().hasTagCompound()) {
                    entityItem.setDead();
                }
            }
        }
        if (KaiaUtil.antiEntity.contains(event.getEntity().getClass())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickUpdate(TickEvent.WorldTickEvent event) {
        easterEggFunctionMkllVerify();
    }

    private static void easterEggFunctionMkllVerify() {
        if (!mobsNamedMkll.isEmpty()) {
            for (EntityLivingBase entity : mobsNamedMkll) {
                if (timeTeleportation.get(entity) % 100 == 0) {
                    if (entity.isDead) {
                        mobsNamedMkll.remove(entity);
                        timeTeleportation.remove(entity);
                        return;
                    }
                    entity.attemptTeleport(entity.posX + 10, entity.posY + 3, entity.posZ + 10);
                    entity.world.spawnAlwaysVisibleParticle(EnumParticleTypes.PORTAL.getParticleID(), entity.posX, entity.posY, entity.posZ, 0, 0, 0, new int[0]);
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 5.0f, 1.0f);
                    int timeTeleportationEntity = timeTeleportation.get(entity);
                    timeTeleportationEntity = 1;
                    timeTeleportation.put(entity, timeTeleportationEntity);
                } else {
                    int timeTeleportationEntity = timeTeleportation.get(entity);
                    timeTeleportationEntity++;
                    timeTeleportation.put(entity, timeTeleportationEntity);
                }
            }
        }
    }
}