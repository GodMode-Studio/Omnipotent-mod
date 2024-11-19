package com.omnipotent.common.mixin;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;
import com.omnipotent.common.capability.kaiacap.KaiaProvider;
import com.omnipotent.common.entity.CustomLightningBolt;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.KaiaWrapper;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.omnipotent.Omnipotent.log;
import static com.omnipotent.util.KaiaConstantsNbt.ownerID;

@Mixin(World.class)
public abstract class MixinWorld implements IBlockAccess, net.minecraftforge.common.capabilities.ICapabilityProvider {
    @Shadow
    private boolean processingLoadedTiles;

    @Accessor("addedTileEntityList")
    abstract List<TileEntity> getaddedTileEntityList();

    @Accessor("loadedTileEntityList")
    abstract List<TileEntity> getloadedTileEntityList();

    @Accessor("tickableTileEntities")
    abstract List<TileEntity> gettickableTileEntities();

    @Shadow
    public abstract boolean spawnEntity(Entity entityIn);

    @Shadow
    public abstract Chunk getChunk(BlockPos pos);

    @Shadow
    public abstract void updateComparatorOutputLevel(BlockPos pos, Block blockIn);

    @Shadow
    public boolean isRemote;

    @Shadow
    public abstract <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter);

    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow
    @Nullable
    public abstract TileEntity getTileEntity(BlockPos pos);

    @Shadow
    public abstract ISaveHandler getSaveHandler();

    @Shadow
    public abstract ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> getPersistentChunks();

    @Shadow
    protected abstract boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty);

    @Shadow
    @Final
    public Profiler profiler;

    @Shadow
    protected abstract boolean isChunkLoaded(int x, int z, boolean allowEmpty);

    @Shadow
    public abstract Chunk getChunk(int chunkX, int chunkZ);

    @Shadow
    public abstract void updateEntity(Entity ent);

    @Inject(method = "removeEntityDangerously", at = @At("HEAD"), cancellable = true)
    public void removeEntityDangerously(Entity entityIn, CallbackInfo ci) {
        if (KaiaUtil.hasInInventoryKaia(entityIn))
            ci.cancel();
    }

    @Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"))
    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate, CallbackInfo ci) {
        if (KaiaUtil.hasInInventoryKaia(entityIn))
            entityIn.addedToChunk = true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity2 = this.getTileEntity(pos);
        if (tileentity2 instanceof IInventory && !isRemote) {
            IInventory container = (IInventory) tileentity2;
            for (int index = 0; index < container.getSizeInventory(); index++) {
                checkInventoryAndKaiaAndManager(pos, container, index, tileentity2);
            }
        }

        if (tileentity2 != null && this.processingLoadedTiles) {
            tileentity2.invalidate();
            this.getaddedTileEntityList().remove(tileentity2);
            if (!(tileentity2 instanceof ITickable)) //Forge: If they are not tickable they wont be removed in the update loop.
                this.getloadedTileEntityList().remove(tileentity2);
        } else {
            if (tileentity2 != null) {
                this.getaddedTileEntityList().remove(tileentity2);
                this.getloadedTileEntityList().remove(tileentity2);
                this.gettickableTileEntities().remove(tileentity2);
            }

            this.getChunk(pos).removeTileEntity(pos);
        }
        this.updateComparatorOutputLevel(pos, getBlockState(pos).getBlock()); //Notify neighbors of changes
    }

    private void checkInventoryAndKaiaAndManager(BlockPos pos, IInventory container, int index, TileEntity tileentity2) {
        ItemStack stackInSlot = container.getStackInSlot(index);
        KaiaWrapper.wrapIfKaia(stackInSlot).ifPresent(kaia -> {
            BlockPos pos1 = tileentity2.getPos();
            ItemStack itemStack = new ItemStack(this.getTileEntity(pos).getBlockType());
            List<EntityItem> entitiesWithinAABB = this.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos1, pos1.add(5, 5, 5)), e -> e.getItem().isItemEqualIgnoreDurability(itemStack));
            kaia.getOwner().ifPresentOrElse(playerOwner -> managerKaiaInContainerAndSaveInKaiaBrand(pos, playerOwner, stackInSlot, entitiesWithinAABB), () -> {
                if (!kaia.hasOwner())
                    return;
                UUID uuid = kaia.getOwnerUUID().get();
                managerKaiaInContainerAndSaveInPlayerData(uuid, stackInSlot, entitiesWithinAABB);
            });
        });
    }

    private void managerKaiaInContainerAndSaveInPlayerData(UUID uuid, ItemStack stackInSlot, List<EntityItem> entitiesWithinAABB) {
        UtilityHelper.getPlayerDataFileOfPlayer(uuid).ifPresent(playerData -> {
            try {
                String replace = playerData.getAbsolutePath();
                NBTTagCompound playerNbt = CompressedStreamTools.readCompressed(new FileInputStream(replace));
                if (this.getSaveHandler() instanceof SaveHandler) {
                    try {
                        NBTTagList inventory = playerNbt.getTagList("Inventory", 10);
                        final ArrayList<Byte> slots = new ArrayList<>();
                        for (NBTBase nbt : inventory) {
                            if (nbt instanceof NBTTagCompound nbt1 && nbt1.hasKey("Slot")) {
                                byte slot = nbt1.getByte("Slot");
                                boolean noArmorSlot = slot != 100 && slot != 101 && slot != 102 && slot != 103;
                                if (noArmorSlot) {
                                    slots.add(slot);
                                }
                            }
                        }
                        if (slots.size() <= 36) {
                            NBTTagCompound nbt = new NBTTagCompound();
                            if (!slots.contains((byte) -106)) {
                                stackInSlot.writeToNBT(nbt);
                                nbt.setByte("Slot", (byte) -106);
                                inventory.appendTag(nbt);
                            } else {
                                for (byte i = 0; i <= 35; i++) {
                                    if (!slots.contains(i)) {
                                        stackInSlot.writeToNBT(nbt);
                                        nbt.setByte("Slot", i);
                                        inventory.appendTag(nbt);
                                        break;
                                    }
                                }
                            }
                        }
                        CompressedStreamTools.writeCompressed(playerNbt, new FileOutputStream(replace));
                    } catch (IOException e) {
                    }
                    CompletableFuture.runAsync(() -> {
                        NBTTagCompound nbtEntity = new NBTTagCompound();
                        for (EntityItem entityItem : entitiesWithinAABB) {
                            try {
                                entityItem.writeToNBTAtomically(nbtEntity);
                                NBTTagList items = nbtEntity.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("BlockEntityTag").getTagList("Items", 10);
                                removeKaiaOfInventory(items);
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("error in extern try catch of managerKaia", e);
            }
        });
    }

    private void managerKaiaInContainerAndSaveInKaiaBrand(BlockPos pos, EntityPlayer player, ItemStack stackInSlot, List<EntityItem> entitiesWithinAABB) {
        player.sendMessage(new TextComponentString(TextFormatting.AQUA + "Press G for return Kaia"));
        List<ItemStack> kaiaItems = player.getCapability(KaiaProvider.KaiaBrand, null).returnList();
        kaiaItems.add(stackInSlot);
        CompletableFuture.runAsync(() -> {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            for (EntityItem entityItem : entitiesWithinAABB) {
                try {
                    entityItem.writeToNBTAtomically(nbtTagCompound);
                    NBTTagList items = nbtTagCompound.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("BlockEntityTag").getTagList("Items", 10);
                    removeKaiaOfInventory(items);
                } catch (Exception e) {
                }
            }
        });
        spawnEntity(new CustomLightningBolt((World) (Object) this, pos.getX(), pos.getY(), pos.getZ(), true));
    }

    private void removeKaiaOfInventory(NBTTagList items) {
        ArrayList<Integer> removeIndexs = new ArrayList<>();
        Iterator<NBTBase> iterator = items.iterator();
        int c = -1;
        while (iterator.hasNext()) {
            c++;
            NBTBase next = iterator.next();
            if (next instanceof NBTTagCompound) {
                NBTTagCompound next1 = (NBTTagCompound) next;
                if (next1.hasKey("id") && next1.getString("id").equals("omnipotent:kaia")) {
                    removeIndexs.add(c);
                }
            }
        }
        for (Integer index : removeIndexs) {
            items.removeTag(index);
        }
    }
}
