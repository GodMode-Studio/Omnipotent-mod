package com.omnipotent.common.mixin;

import com.omnipotent.util.KaiaUtil;
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

import javax.annotation.Nullable;
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Nullable
    @SideOnly(Side.CLIENT)
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks) {
        AtomicBoolean stopOnLiquid = new AtomicBoolean(false);
        if (isPlayer((Entity) (Object) this)) {
            EntityPlayer player = (EntityPlayer) (Object) this;
            KaiaUtil.getKaiaInMainHand(player).ifPresent(kaia -> {
                NBTTagCompound tagCompound = kaia.getTagCompound();
                if (tagCompound != null)
                    stopOnLiquid.set(tagCompound.getBoolean(interactLiquid.getValue()));
            });
        }
        Vec3d vec3d = this.getPositionEyes(partialTicks);
        Vec3d vec3d1 = this.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return this.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid.get(), false, true);
    }
}
