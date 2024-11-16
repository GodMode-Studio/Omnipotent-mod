package com.omnipotent.common.mixin;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.omnipotent.constant.NbtBooleanValues.interactLiquid;
import static com.omnipotent.util.UtilityHelper.isPlayer;

@Mixin(Entity.class)
public abstract class MixinEntity implements ICommandSender, net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound> {
    /**
     * @author
     * @reason
     */
    @Shadow
    protected abstract Vec3d getPositionEyes(float partialTicks);

    @Shadow
    protected abstract Vec3d getLook(float partialTicks);

    @Shadow
    protected World world;
    @Shadow
    public boolean isDead;

    @Shadow
    public abstract String getName();

    @Unique
    public boolean absoluteDead;

    /**
     * @author
     * @reason
     */
    @Final
    public final void setAbsoluteDead() {
        this.absoluteDead = true;
        this.isDead = true;
    }

    @Inject(method = "setUniqueId", at = @At("HEAD"), cancellable = true)
    public void setUniqueId(UUID uniqueIdIn, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof EntityPlayer && entity.getUniqueID() != null && !UtilityHelper.injectMixinIsCallerMinecraftOrForgeClass())
            ci.cancel();
    }

    @Inject(method = "setPositionAndUpdate", at = @At("HEAD"), cancellable = true)
    public void setPositionAndUpdate(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (y < -10 && KaiaUtil.hasInInventoryKaia(entity) && !UtilityHelper.injectMixinIsCallerMinecraftOrForgeClass()) {
            ci.cancel();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Nullable
    @SideOnly(Side.CLIENT)
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks) {
        AtomicBoolean stopOnLiquid = new AtomicBoolean(false);
        EntityPlayer player = null;
        if (isPlayer((Entity) (Object) this)) {
            player = (EntityPlayer) (Object) this;
            KaiaUtil.findKaiaInMainHand(player).ifPresent(kaia -> stopOnLiquid.set(kaia.getBoolean(interactLiquid)));
        }
        Vec3d vec3d = this.getPositionEyes(partialTicks);
        Vec3d vec3d1 = this.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        double value = player != null ? player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : -1;
        return UtilityHelper.rayTraceBlocks(this.world, value, vec3d, vec3d2, stopOnLiquid.get(), false, true);
    }
}
