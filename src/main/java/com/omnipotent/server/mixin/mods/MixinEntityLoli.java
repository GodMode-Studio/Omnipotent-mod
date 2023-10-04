package com.omnipotent.server.mixin.mods;

import com.anotherstar.common.entity.EntityLoli;
import com.anotherstar.common.entity.IEntityLoli;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Optional.Interface(iface = "com.anotherstar.common.entity.EntityLoli", modid = "lolipickaxe")

@Mixin(value = EntityLoli.class, remap = false)
public abstract class MixinEntityLoli extends EntityCreature implements IEntityLoli {
    @Shadow
    private boolean dispersal;

    public MixinEntityLoli(World worldIn) {
        super(worldIn);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void onRemovedFromWorld() {
        if (!dispersal && !world.isRemote) {
            EntityLoli loli = new EntityLoli(world);
            loli.copyLocationAndAnglesFrom(this);
            NBTTagCompound tagCompound = new NBTTagCompound();
            this.writeToNBT(tagCompound);
            loli.readFromNBT(tagCompound);
            world.spawnEntity(loli);
            dispersal = true;
            world.spawnEntity(new EntityItem(world, this.posX, this.posY, this.posZ, Item.REGISTRY.getObject(new ResourceLocation("lolipickaxe", "loli_pickaxe")).getDefaultInstance()));
        }
        super.onRemovedFromWorld();
    }

    /**
     * @author
     * @reason
     */
    @Override
    @Final
    public void setDead() {
        this.isDead = true;
        onRemovedFromWorld();
    }
}
