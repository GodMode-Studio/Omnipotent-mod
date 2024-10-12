package com.omnipotent.common.mixin;

import com.mojang.authlib.GameProfile;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.KaiaWrapper;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;
import static com.omnipotent.util.KaiaUtil.returnKaiaOfOwner;


@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMp extends EntityPlayer implements IContainerListener {
    public MixinEntityPlayerMp(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Shadow
    public NetHandlerPlayServer connection;

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void onDeath(DamageSource cause) {
        EntityPlayerMP playerMP = (EntityPlayerMP) (Object) this;
        UtilityHelper.getKaiaCap(playerMP).ifPresent(cap -> {
            for (ItemStack kaia : cap.returnList()) {
                if (new KaiaWrapper(kaia).getBoolean(NbtBooleanValues.rescueBeforeDeath)) {
                    returnKaiaOfOwner(playerMP);
                    break;
                }
            }
        });
        if (hasInInventoryKaia(this)) {
            this.setHealth(Integer.MAX_VALUE);
            this.isDead = false;
            return;
        }
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, cause))
            return;
        boolean flag = this.world.getGameRules().getBoolean("showDeathMessages");
        this.connection.sendPacket(new SPacketCombatEvent(this.getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED, flag));

        if (flag) {
            Team team = this.getTeam();

            if (team != null && team.getDeathMessageVisibility() != Team.EnumVisible.ALWAYS) {
                if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
                    playerMP.server.getPlayerList().sendMessageToAllTeamMembers(this, this.getCombatTracker().getDeathMessage());
                } else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
                    playerMP.server.getPlayerList().sendMessageToTeamOrAllPlayers(this, this.getCombatTracker().getDeathMessage());
                }
            } else {
                playerMP.server.getPlayerList().sendMessage(this.getCombatTracker().getDeathMessage());
            }
        }

        this.spawnShoulderEntities();

        if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
            captureDrops = true;
            capturedDrops.clear();
            this.destroyVanishingCursedItems();
            this.inventory.dropAllItems();

            captureDrops = false;
            net.minecraftforge.event.entity.player.PlayerDropsEvent event = new net.minecraftforge.event.entity.player.PlayerDropsEvent(this, cause, capturedDrops, recentlyHit > 0);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
                for (net.minecraft.entity.item.EntityItem item : capturedDrops) {
                    this.world.spawnEntity(item);
                }
            }
        }

        for (ScoreObjective scoreobjective : this.world.getScoreboard().getObjectivesFromCriteria(IScoreCriteria.DEATH_COUNT)) {
            Score score = this.getWorldScoreboard().getOrCreateScore(this.getName(), scoreobjective);
            score.incrementScore();
        }

        EntityLivingBase entitylivingbase = this.getAttackingEntity();

        if (entitylivingbase != null) {
            EntityList.EntityEggInfo entitylist$entityegginfo = EntityList.ENTITY_EGGS.get(EntityList.getKey(entitylivingbase));

            if (entitylist$entityegginfo != null) {
                this.addStat(entitylist$entityegginfo.entityKilledByStat);
            }

            entitylivingbase.awardKillScore(this, this.scoreValue, cause);
        }

        this.addStat(StatList.DEATHS);
        this.takeStat(StatList.TIME_SINCE_DEATH);
        this.extinguish();
        this.setFlag(0, false);
        this.getCombatTracker().reset();
    }
}
