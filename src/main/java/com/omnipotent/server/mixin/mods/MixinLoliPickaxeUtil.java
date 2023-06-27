package com.omnipotent.server.mixin.mods;

import com.anotherstar.common.config.ConfigLoader;
import com.anotherstar.common.event.LoliTickEvent;
import com.anotherstar.network.LoliDeadPacket;
import com.anotherstar.network.LoliKillEntityPacket;
import com.anotherstar.network.NetworkHandler;
import com.anotherstar.util.LoliPickaxeUtil;
//import com.fantasticsource.fantasticlib.CmdGive;
//import com.fantasticsource.fantasticlib.FantasticLib;
//import com.fantasticsource.mctools.MCTools;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.*;

import static com.anotherstar.util.LoliPickaxeUtil.getLoliPickaxe;
import static com.anotherstar.util.LoliPickaxeUtil.invHaveLoliPickaxe;

@Optional.Interface(iface = "com.anotherstar.util.LoliPickaxeUtil", modid = "lolipickaxe")
@Mixin(LoliPickaxeUtil.class)
public abstract class MixinLoliPickaxeUtil {

    /**
     * @author
     * @reason
     */
    @Overwrite @Final
    public static void killPlayer(EntityPlayer player, EntityLivingBase source) {
        if(KaiaUtil.hasInInventoryKaia(player)){
            return;
        }
        if (invHaveLoliPickaxe(player) || player.loliDead || player instanceof FakePlayer) {
            return;
        }
        ItemStack stack = getLoliPickaxe(source);
        if (ConfigLoader.getBoolean(stack, "loliPickaxeClearInventory")) {
            player.inventory.clearMatchingItems(null, -1, -1, null);
            InventoryEnderChest ec = player.getInventoryEnderChest();
            for (int i = 0; i < ec.getSizeInventory(); i++) {
                ec.removeStackFromSlot(i);
            }
        }
        if (ConfigLoader.getBoolean(stack, "loliPickaxeDropItems")) {
            player.inventory.dropAllItems();
        }
        DamageSource ds = source == null ? new DamageSource("loli") : new EntityDamageSource("loli", source);
        player.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
        player.setHealth(0.0F);
        player.onDeath(ds);
        boolean remove = ConfigLoader.getBoolean(stack, "loliPickaxeCompulsoryRemove");
        if (remove) {
            player.loliDead = true;
            delayKill(player);
        }
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            NetworkHandler.INSTANCE.sendMessageToPlayer(new LoliDeadPacket(remove, ConfigLoader.getBoolean(stack, "loliPickaxeBlueScreenAttack"), ConfigLoader.getBoolean(stack, "loliPickaxeExitAttack"), ConfigLoader.getBoolean(stack, "loliPickaxeFailRespondAttack")), playerMP);
            if (ConfigLoader.getBoolean(stack, "loliPickaxeBeyondRedemption")) {
                ConfigLoader.addPlayerToBeyondRedemption(playerMP);
            }
            if (ConfigLoader.getBoolean(stack, "loliPickaxeKickPlayer")) {
                playerMP.connection.disconnect(new TextComponentString(ConfigLoader.getString(stack, "loliPickaxeKickMessage")));
            }
            if (ConfigLoader.getBoolean(stack, "loliPickaxeReincarnation")) {
                ConfigLoader.addPlayerToReincarnation(playerMP);
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite @Final
    private static void delayKill(EntityLivingBase entity) {
        int tick = 21;
        if (!(entity instanceof EntityPlayer)) {
            ResourceLocation id = EntityList.getKey(entity);
            if (ConfigLoader.loliPickaxeDelayRemoveList.containsKey(id.toString())) {
                tick = ConfigLoader.loliPickaxeDelayRemoveList.get(id.toString());
            } else if (ConfigLoader.loliPickaxeDelayRemoveList.containsKey(id.getResourcePath())) {
                tick = ConfigLoader.loliPickaxeDelayRemoveList.get(id.getResourcePath());
            }
        }
        LoliTickEvent.addTask(new LoliTickEvent.TickStartTask(tick, () -> {
            entity.loliCool = true;
            entity.isDead = true;
            NetworkHandler.INSTANCE.sendMessageToAll(new LoliKillEntityPacket(entity.dimension, entity.getEntityId()));
        }), TickEvent.Phase.START);
    }
}
