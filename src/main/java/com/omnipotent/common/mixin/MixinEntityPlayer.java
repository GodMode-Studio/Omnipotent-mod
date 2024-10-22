package com.omnipotent.common.mixin;

import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.KaiaWrapper;
import com.omnipotent.util.NbtListUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.omnipotent.Omnipotent.deobfuscatedEnvironment;
import static com.omnipotent.constant.NbtBooleanValues.*;
import static com.omnipotent.util.KaiaConstantsNbt.playersDontKill;
import static com.omnipotent.util.KaiaUtil.*;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {
    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Shadow
    protected abstract void destroyVanishingCursedItems();

    @Shadow
    public InventoryPlayer inventory;
    @Shadow
    public PlayerCapabilities capabilities;

    @Shadow(remap = false)
    public abstract String getDisplayNameString();

    @Accessor(value = "prefixes", remap = false)
    abstract Collection<ITextComponent> getprefixes();

    @Accessor(value = "suffixes", remap = false)
    abstract Collection<ITextComponent> getsuffixes();

    @Shadow(remap = false)
    private String displayname;

    @Unique
    public boolean renderSpecialName = false;
    @Unique
    public boolean hasKaia = false;
    @Shadow
    public float cameraYaw;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ITextComponent getDisplayName() {
        ITextComponent itextcomponent = new TextComponentString("");
        if (!getprefixes().isEmpty()) for (ITextComponent prefix : getprefixes()) itextcomponent.appendSibling(prefix);
        itextcomponent.appendSibling(new TextComponentString(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayNameString())));
        if (!getsuffixes().isEmpty()) for (ITextComponent suffix : getsuffixes()) itextcomponent.appendSibling(suffix);
        itextcomponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
        itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
        itextcomponent.getStyle().setInsertion(this.getName());
        if (renderSpecialName) {
            displayname = null;
        }
        return itextcomponent;
    }
//
//    private ArrayList<Float> suspectPitch = new ArrayList<>();
//    private ArrayList<Float> suspectYaw = new ArrayList<>();
//
//    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
//    public void onLivingUpdate(CallbackInfo ci) {
//        if (KaiaUtil.hasInInventoryKaia(this)) {
//            if (cameraPitch > 360) {
//                suspectPitch.add(cameraPitch);
//            }
//            if (cameraYaw > 360) {
//                suspectYaw.add(cameraPitch);
//            }
//            if (suspectPitch.size() > 100) {
//                for (Float n : suspectPitch) {
//
//                }
//            }
//        }
//    }

    //
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void closeScreen() {
//        if (KaiaUtil.hasInInventoryKaia(this)) {
//            return;
//        }
//        this.openContainer = this.inventoryContainer;
//    }
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
//        if (slotIn == EntityEquipmentSlot.MAINHAND) {
//            this.playEquipSound(stack);
//            NonNullList<ItemStack> mainInventory = this.inventory.mainInventory;
//            int currentItem = this.inventory.currentItem;
//            if (mainInventory.get(currentItem).getItem() instanceof Kaia)
//                return;
//            mainInventory.set(currentItem, stack);
//        } else if (slotIn == EntityEquipmentSlot.OFFHAND) {
//            this.playEquipSound(stack);
//            NonNullList<ItemStack> offHandInventory = this.inventory.offHandInventory;
//            if (offHandInventory.get(0).getItem() instanceof Kaia)
//                return;
//            offHandInventory.set(0, stack);
//        } else if (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
//            this.playEquipSound(stack);
//            this.inventory.armorInventory.set(slotIn.getIndex(), stack);
//        }
//    }


    @Inject(method = "dropItem(Z)Lnet/minecraft/entity/item/EntityItem;", at = @At("HEAD"), cancellable = true)
    public void dropItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
        boolean hasKaia = hasInInventoryKaia(this);
        if (dropAll && hasKaia)
            cir.cancel();
        else if (hasKaia) {
            cancelExecution(cir);
        }
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/item/EntityItem;", at = @At("HEAD"), cancellable = true)
    public void dropItem(ItemStack itemStackIn, boolean unused, CallbackInfoReturnable<EntityItem> cir) {
        if (itemStackIn!=null && itemStackIn.getItem() instanceof Kaia) {
            cancelExecution(cir);
        }
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/item/EntityItem;", at = @At("HEAD"), cancellable = true)
    public void dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem, CallbackInfoReturnable<EntityItem> cir) {
        if (droppedItem.getItem() instanceof Kaia) {
            cancelExecution(cir);
        }
    }

    @Unique
    private static void cancelExecution(CallbackInfoReturnable<EntityItem> cir) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement stackTraceElement = stackTrace[4];
            if (!stackTraceElement.getClassName().startsWith("net.minecraft") && !stackTraceElement.getClassName().startsWith("net.minecraftforge"))
                cir.cancel();
        }
    }

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
        if (ForgeHooks.onLivingDeath(this, cause))
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
            ForgeEventFactory.onPlayerDrops(player, cause, capturedDrops, recentlyHit > 0);

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
            KaiaWrapper kaia = getKaiaInMainHandOrInventory(player);
            Entity enemie;
            if (source != null && source.getTrueSource() != null) {
                enemie = source.getTrueSource();
                if (UtilityHelper.isPlayer(enemie) && kaia.getBoolean(playersWhoShouldNotKilledInCounterAttack)) {
                    if (kaia.playerIsProtected(enemie.getUniqueID().toString())) {
                        cir.cancel();
                        return;
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
                if (kaia.getBoolean(counterAttack)) {
                    if (entityIsFriendEntity(source.getTrueSource())) {
                        if (entityFriendCanKilledByKaia(kaia, source.getTrueSource(), false))
                            killChoice(source.getTrueSource(), player, kaia.getBoolean(killAllEntities));
                    } else if (entityNoIsNormalAndCanKilledByKaia(kaia, source.getTrueSource()))
                        killChoice(source.getTrueSource(), player, kaia.getBoolean(killAllEntities));
                    else if (entityIsPlayerAndKaiaCanKillPlayer(kaia, false, source.getTrueSource()))
                        killChoice(source.getTrueSource(), player, kaia.getBoolean(killAllEntities));
                    else
                        killChoice(source.getTrueSource(), player, kaia.getBoolean(killAllEntities));
                }
            }
            cir.cancel();
        }
    }
}
