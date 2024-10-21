package com.omnipotent.common.event;

import com.omnipotent.common.capability.BlockModeProvider;
import com.omnipotent.common.capability.kaiacap.IKaiaBrand;
import com.omnipotent.common.capability.kaiacap.KaiaProvider;
import com.omnipotent.common.damage.AbsoluteOfCreatorDamage;
import com.omnipotent.common.entity.CustomLightningBolt;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.KaiaWrapper;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.util.*;

import static com.omnipotent.Omnipotent.BLOCK_MODES_OF_PLAYER;
import static com.omnipotent.Omnipotent.KAIACAP;
import static com.omnipotent.util.KaiaUtil.*;
import static com.omnipotent.util.UtilityHelper.getPlayerDataFileOfPlayer;

public class EntityEvent {
    public static final Set<String> entitiesWithKaia = new HashSet<>();
    public static final Set<String> entitiesFlightKaia = new HashSet<>();
    private static final ArrayList<EntityLivingBase> mobsNamedMkll = new ArrayList<>();
    private static final Map<EntityLivingBase, Integer> timeTeleportation = new HashMap<>();

    @SubscribeEvent
    public void updateAbilities(LivingEvent.LivingUpdateEvent event) {
        if (!UtilityHelper.isPlayer(event.getEntityLiving())) {
            defineTimeAndListEasterEggMkll(event);
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        boolean isRemote = player.world.isRemote;
        String keyUID = player.getCachedUniqueIdString() + "|" + isRemote;
        boolean hasKaia = hasInInventoryKaia(player);
        if (hasKaia && player.getHealth() < 5) {
            player.isDead = false;
            player.setHealth(Float.MAX_VALUE);
        }
        if (KaiaUtil.theLastAttackOfKaia(player) && !hasKaia && !player.isDead) {
            player.isDead = true;
            player.deathTime = 99999;
            player.onDeath(new AbsoluteOfCreatorDamage(player));
        }
        if (hasKaia) {
            if (player.isBurning())
                player.extinguish();
            entitiesWithKaia.add(keyUID);
            if (!isRemote)
                player.hasKaia = true;
            if (!player.renderSpecialName && hasInInventoryKaia(player)) {
                player.renderSpecialName = true;
            }
            handleKaiaStateChange(player, true);
        }
        if (!hasKaia) {
            entitiesWithKaia.remove(keyUID);
            if (!isRemote && !hasInInventoryKaia(player))
                if (player.renderSpecialName) {
                    player.hasKaia = false;
                    player.renderSpecialName = false;
                }
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
        if (UtilityHelper.isPlayer(entity)) {
            EntityPlayer player = ((EntityPlayer) entity);
            if (isNew) {
                player.capabilities.allowFlying = true;
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
            if (event.getEntityItem().getItem().hasTagCompound()) {
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
        ItemStack kaia = event.getItem().getItem();
        if (!kaia.hasTagCompound())
            return;
        if (kaia.getTagCompound().hasKey(KaiaConstantsNbt.ownerID) && kaia.getTagCompound().hasKey(KaiaConstantsNbt.ownerName) && !KaiaUtil.isOwnerOfKaia(kaia, player))
            event.setCanceled(true);
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void onEntityItemJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityItem && !entity.getEntityWorld().isRemote) {
            EntityItem entityItem = (EntityItem) entity;
            ItemStack kaia = entityItem.getItem();
            if (!kaia.isEmpty() && kaia.getItem() instanceof Kaia) {
                createTagCompoundStatusIfNecessary(kaia);
                if (!kaia.getTagCompound().hasKey(KaiaConstantsNbt.ownerID) || !kaia.getTagCompound().hasKey(KaiaConstantsNbt.ownerName)) {
                    entityItem.setEntityInvulnerable(true);
                    entityItem.setNoPickupDelay();
                    return;
                }
                UUID uuid = UUID.fromString(kaia.getTagCompound().getString(KaiaConstantsNbt.ownerID));
                EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
                if (player != null && isOwnerOfKaia(kaia, player)) {
                    player.sendMessage(new TextComponentString(TextFormatting.AQUA + "Press G for return Kaia"));
                    IKaiaBrand capability = player.getCapability(KaiaProvider.KaiaBrand, null);
                    capability.habilityBrand(Collections.singletonList(kaia));
                    entityItem.world.spawnEntity(new CustomLightningBolt(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ, true));
                    entityItem.setDead();
                } else if (player == null) {
                    try {
                        addKaiaAndManagerInPlayarDataFile(new KaiaWrapper(kaia), getPlayerDataFileOfPlayer(uuid).getAbsolutePath());
                        entityItem.setDead();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        for (Class<? extends Entity> clazz : antiEntity) {
            if (clazz.isInstance(entity)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer))
            return;
        event.addCapability(KAIACAP, new KaiaProvider());
        event.addCapability(BLOCK_MODES_OF_PLAYER, new BlockModeProvider());
    }

    @SubscribeEvent
    public void PlayerClone(PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        IKaiaBrand kaiaBrand = player.getCapability(KaiaProvider.KaiaBrand, null);
        IKaiaBrand oldKaiaBrand = event.getOriginal().getCapability(KaiaProvider.KaiaBrand, null);
        kaiaBrand.habilityBrand(oldKaiaBrand.returnList());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickUpdate(TickEvent.WorldTickEvent event) {
        easterEggFunctionMkllVerify();
        for (Entity entity : event.world.loadedEntityList) {
            try {
                if (entity.absoluteDead){
                    entity.isDead = true;}
            } catch (Exception e) {
            }
        }
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
                    boolean b = entity.attemptTeleport(entity.posX + 10, entity.posY + 3, entity.posZ + 10);
                    if (b) {
                        entity.world.spawnAlwaysVisibleParticle(EnumParticleTypes.PORTAL.getParticleID(), entity.posX, entity.posY, entity.posZ, 0, 0, 0, new int[0]);
                        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 5.0f, 1.0f);
                    }
                    int timeTeleportationEntity = 1;
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