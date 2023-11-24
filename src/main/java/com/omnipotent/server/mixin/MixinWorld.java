package com.omnipotent.server.mixin;

import com.google.common.base.Predicate;
import com.omnipotent.server.capability.KaiaProvider;
import com.omnipotent.server.entity.CustomLightningBolt;
import com.omnipotent.server.tool.Kaia;
import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

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
    public abstract Chunk getChunkFromBlockCoords(BlockPos pos);

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

            this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
        }
        this.updateComparatorOutputLevel(pos, getBlockState(pos).getBlock()); //Notify neighbors of changes
    }

    private void checkInventoryAndKaiaAndManager(BlockPos pos, IInventory container, int index, TileEntity tileentity2) {
        ItemStack stackInSlot = container.getStackInSlot(index);
        if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof Kaia && stackInSlot.getTagCompound() != null) {
            NBTTagCompound kaiaTagCompund = stackInSlot.getTagCompound();
            if (kaiaTagCompund.hasKey(KaiaConstantsNbt.ownerName) && kaiaTagCompund.hasKey(ownerID)) {
                UUID uuid = UUID.fromString(kaiaTagCompund.getString(ownerID));
                EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
                ItemStack itemStack = new ItemStack(this.getTileEntity(pos).getBlockType());
                BlockPos pos1 = tileentity2.getPos();
                List<EntityItem> entitiesWithinAABB = this.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos1, pos1.add(5, 5, 5)), e -> e.getItem().isItemEqualIgnoreDurability(itemStack));
                if (player != null) {
                    if (KaiaUtil.isOwnerOfKaia(stackInSlot, player)) {
                        managerKaiaInContainerAndSaveInKaiaBrand(pos, player, stackInSlot, entitiesWithinAABB);
                    }
                } else if (uuid.toString().equals(kaiaTagCompund.getString(ownerID))) {
                    managerKaiaInContainerAndSaveInPlayerData(uuid, stackInSlot, entitiesWithinAABB);
                }
            }
        }
    }

    private void managerKaiaInContainerAndSaveInPlayerData(UUID uuid, ItemStack stackInSlot, List<EntityItem> entitiesWithinAABB) {
        File playerData = UtilityHelper.getPlayerDataFileOfPlayer(uuid);
        try {
            String replace = playerData.getAbsolutePath();
            NBTTagCompound playerNbt = CompressedStreamTools.readCompressed(new FileInputStream(replace));
            if (playerNbt != null) {
                if (this.getSaveHandler() instanceof SaveHandler) {
                    try {
                        NBTTagList inventory = playerNbt.getTagList("Inventory", 10);
                        ArrayList<Byte> slots = new ArrayList<>();
                        for (NBTBase nbt : inventory) {
                            if (nbt instanceof NBTTagCompound) {
                                NBTTagCompound nbt1 = (NBTTagCompound) nbt;
                                if (nbt1.hasKey("Slot")) {
                                    byte slot = nbt1.getByte("Slot");
                                    if (slot != 100 && slot != 101 && slot != 102 && slot != 103) {
                                        slots.add(slot);
                                    }
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
                        FileOutputStream fileOutputStream = new FileOutputStream(replace);
                        CompressedStreamTools.writeCompressed(playerNbt, fileOutputStream);
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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
            }
        } catch (Exception e) {
        }
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
