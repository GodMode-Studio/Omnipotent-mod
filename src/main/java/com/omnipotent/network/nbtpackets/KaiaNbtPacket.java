package com.omnipotent.network.nbtpackets;

import com.omnipotent.tools.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.omnipotent.tools.KaiaConstantsNbt.*;

public class KaiaNbtPacket implements IMessage {
    public static String type;
    public static String text;
    public static boolean booleanValue;
    public static int intValue;

    public KaiaNbtPacket() {
    }

    public KaiaNbtPacket(String type) {
        this.type = type;
    }

    public KaiaNbtPacket(String type, boolean b) {
        this.type = type;
        this.booleanValue = b;
        this.text = "";
    }

    public KaiaNbtPacket(String type, int i) {
        this.type = type;
        this.intValue = i;
        this.text = "";
    }

    public KaiaNbtPacket(String type, String text, int i) {
        this.type = type;
        this.text = text;
        this.intValue = i;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        type = new String(bytes, StandardCharsets.UTF_8);
        booleanValue = buf.readBoolean();
        int length2 = buf.readInt();
        byte[] bytes2 = new byte[length2];
        buf.readBytes(bytes2);
        text = new String(bytes2, StandardCharsets.UTF_8);
        intValue = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = type.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.writeBoolean(booleanValue);
        byte[] bytes2 = text.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes2.length);
        buf.writeBytes(bytes2);
        buf.writeInt(intValue);
    }

    public static class KaiaNbtPacketHandler implements IMessageHandler<KaiaNbtPacket, IMessage> {
        @Override
        public IMessage onMessage(KaiaNbtPacket message, MessageContext ctx) {
            if (type.equals(blockBreakArea)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                int areaBloco = intValue;
                kaiaItem.getTagCompound().setInteger(blockBreakArea, areaBloco);
            } else if (type.equals(counterAttack)) {
                EntityPlayer player = ctx.getServerHandler().player;
                boolean value = KaiaNbtPacket.booleanValue;
                KaiaUtil.getKaiaInMainHand(player).getTagCompound().setBoolean(counterAttack, value);
            } else if (type.equals(killAllEntities)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                boolean killAllEntitiesPacket = booleanValue;
                kaiaItem.getTagCompound().setBoolean(killAllEntities, killAllEntitiesPacket);
            } else if (type.equals(killFriendEntities)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                boolean killFriendEntities = booleanValue;
                kaiaItem.getTagCompound().setBoolean(KaiaConstantsNbt.killFriendEntities, killFriendEntities);
            } else if (type.equals(rangeAttack)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                int killRangeAttack = intValue;
                kaiaItem.getTagCompound().setInteger(rangeAttack, killRangeAttack);
            } else if (type.equals(attackYourWolf)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                boolean attackYourWolf = booleanValue;
                kaiaItem.getTagCompound().setBoolean(KaiaConstantsNbt.attackYourWolf, attackYourWolf);
            } else if (type.equals("blockReachDistance")) {
                KaiaUtil.modifyBlockReachDistance(ctx.getServerHandler().player, intValue);
            } else if (type.equals(interactLiquid)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                kaiaItem.getTagCompound().setBoolean(interactLiquid, booleanValue);
            } else if (type.equals(noBreakTileEntity)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                kaiaItem.getTagCompound().setBoolean(noBreakTileEntity, booleanValue);
            } else if (type.equals(kaiaEnchant)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                if (intValue == 0) {
                    Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(text);
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(kaiaItem);
                    enchantments.remove(enchantmentByLocation);
                    EnchantmentHelper.setEnchantments(enchantments, kaiaItem);
                } else {
                    Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(text);
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(kaiaItem);
                    enchantments.put(enchantmentByLocation, intValue);
                    EnchantmentHelper.setEnchantments(enchantments, kaiaItem);
                }
            }
            return null;
        }
    }
}
