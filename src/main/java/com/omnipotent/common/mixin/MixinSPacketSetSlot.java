package com.omnipotent.common.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketSetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(SPacketSetSlot.class)
public abstract class MixinSPacketSetSlot implements Packet<INetHandlerPlayClient> {

    @Shadow
    private int windowId;
    @Shadow
    private int slot;
    @Shadow
    private ItemStack item;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.slot = buf.readShort();
        this.item = readItemStack(buf);
//        this.item = buf.readItemStack();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slot);
        writeItemStack(buf, this.item);
//        buf.writeItemStack(this.item);
    }

    public void writeItemStack(PacketBuffer buf, ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getIdFromItem(stack.getItem()));
            buf.writeInt(stack.getCount());
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
            int j = buf.readInt();
            int k = buf.readShort();
            ItemStack itemstack = new ItemStack(Item.getItemById(i), j, k);
            itemstack.getItem().readNBTShareTag(itemstack, buf.readCompoundTag());
            return itemstack;
        }
    }
}
