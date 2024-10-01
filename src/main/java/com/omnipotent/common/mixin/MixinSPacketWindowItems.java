package com.omnipotent.common.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Mixin(value = SPacketWindowItems.class)
public abstract class MixinSPacketWindowItems implements Packet<INetHandlerPlayClient> {

    @Shadow
    private int windowId;
    @Shadow
    private List<ItemStack> itemStacks;


    /**
     * @author
     * @reason
     */
    @Overwrite
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readUnsignedByte();
        int i = buf.readShort();
        this.itemStacks = NonNullList.<ItemStack>withSize(i, ItemStack.EMPTY);
        for (int j = 0; j < i; ++j) {
            this.itemStacks.set(j, readItemStack(buf));
//            this.itemStacks.set(j, buf.readItemStack());
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.itemStacks.size());
        for (ItemStack itemstack : this.itemStacks) {
            writeItemStack(buf, itemstack);
//            buf.writeItemStack(itemstack);
        }
    }

    public void writeItemStack(PacketBuffer buf, ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getIdFromItem(stack.getItem()));
            int count = stack.getCount();
            boolean largestThanByte = count > 127;
            buf.writeBoolean(largestThanByte);
            if (largestThanByte)
                buf.writeInt(count);
            else
                buf.writeByte(count);
            buf.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
                nbttagcompound = stack.getItem().getNBTShareTag(stack);
            }

            buf.writeCompoundTag(nbttagcompound);
        }
    }

    public ItemStack readItemStack(PacketBuffer buf) throws IOException {
        int i = buf.readShort();

        if (i < 0) {
            return ItemStack.EMPTY;
        } else {
            int j = buf.readBoolean() ? buf.readInt() : buf.readByte();
            int k = buf.readShort();
            ItemStack itemstack = new ItemStack(Item.getItemById(i), j, k);
            itemstack.getItem().readNBTShareTag(itemstack, buf.readCompoundTag());
            return itemstack;
        }
    }
}
