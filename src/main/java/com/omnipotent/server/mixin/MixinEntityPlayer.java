package com.omnipotent.server.mixin;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.NbtListUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Iterator;

import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.KaiaUtil.*;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {


    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Shadow
    protected abstract void destroyVanishingCursedItems();

    @Shadow
    protected int flyToggleTimer;
    @Shadow
    protected FoodStats foodStats;
    @Shadow
    protected float speedInAir = 0.02F;
    @Shadow
    public InventoryPlayer inventory;
    @Shadow
    public float prevCameraYaw;
    @Shadow
    public float cameraYaw;
    @Shadow
    public PlayerCapabilities capabilities;

    @Shadow
    protected abstract void spawnShoulderEntities();

    @Shadow
    protected abstract void collideWithPlayer(Entity entityIn);

    @Shadow
    protected abstract void playShoulderEntityAmbientSound(@Nullable NBTTagCompound p_192028_1_);

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void onDeath(DamageSource cause) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        if (hasInInventoryKaia(player) && player.isDead) {
            isDead = false;
            return;
        }
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, cause))
            return;
        super.onDeath(cause);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.10000000149011612D;

        captureDrops = true;
        capturedDrops.clear();

        if ("Notch".equals(this.getName())) {
            player.dropItem(new ItemStack(Items.APPLE, 1), true, false);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory") && !player.isSpectator()) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAllItems();
        }

        captureDrops = false;
        if (!world.isRemote)
            net.minecraftforge.event.ForgeEventFactory.onPlayerDrops(player, cause, capturedDrops, recentlyHit > 0);

        if (cause != null) {
            this.motionX = (double) (-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * 0.017453292F) * 0.1F);
            this.motionZ = (double) (-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * 0.017453292F) * 0.1F);
        } else {
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
        }

        player.addStat(StatList.DEATHS);
        player.takeStat(StatList.TIME_SINCE_DEATH);
        this.extinguish();
        this.setFlag(0, false);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        if (hasInInventoryKaia(player)) {
            ItemStack kaia = getKaiaInMainHand(player) == null ? getKaiaInInventory(player) : getKaiaInMainHand(player);
            Entity enemie;
            if (source != null && source.getTrueSource() != null) {
                enemie = source.getTrueSource();
                if (UtilityHelper.isPlayer(enemie) && kaia.getTagCompound().getBoolean(playersWhoShouldNotKilledInCounterAttack)) {
                    Iterator<NBTBase> iterator = kaia.getTagCompound().getTagList(playersDontKill, 8).iterator();
                    while (iterator.hasNext()) {
                        String string = iterator.next().toString();
                        if (string.startsWith("\"") && string.endsWith("\""))
                            string = string.substring(1, string.length() - 1);
                        if (string.split(NbtListUtil.divisionUUIDAndName)[0].equals(enemie.getUniqueID().toString())) {
                            cir.cancel();
                            return;
                        }
                    }
                }
//                NBTTagList tagList = kaia.getTagCompound().getTagList(entitiesCantKill, 8);
//                if (tagList.tagCount() > 0)
//                    for (String uuid : NbtListUtil.getUUIDOfNbtList(tagList)) {
//                        if (uuid.equals(source.getTrueSource().getUniqueID().toString())) {
//                            cir.cancel();
//                            return;
//                        }
//                    }
                if (kaia.getTagCompound().getBoolean(counterAttack)) {
                    if (entityIsFriendEntity(source.getTrueSource())) {
                        if (entityFriendCanKilledByKaia(kaia.getTagCompound(), source.getTrueSource()))
                            KaiaUtil.killChoice(source.getTrueSource(), player, kaia.getTagCompound().getBoolean(killAllEntities));
                    } else if (entityNoIsNormalAndCanKilledByKaia(kaia.getTagCompound(), source.getTrueSource()))
                        KaiaUtil.killChoice(source.getTrueSource(), player, kaia.getTagCompound().getBoolean(killAllEntities));
                    else if (entityIsPlayerAndKaiaCanKillPlayer(kaia.getTagCompound(), false, source.getTrueSource()))
                        KaiaUtil.killChoice(source.getTrueSource(), player, kaia.getTagCompound().getBoolean(killAllEntities));
                    else
                        KaiaUtil.killChoice(source.getTrueSource(), player, kaia.getTagCompound().getBoolean(killAllEntities));
                }
            }
            cir.cancel();
        }
    }
}
