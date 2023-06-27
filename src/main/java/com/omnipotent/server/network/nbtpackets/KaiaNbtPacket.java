package com.omnipotent.server.network.nbtpackets;

import com.omnipotent.util.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.Teleporte;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.omnipotent.util.KaiaConstantsNbt.*;

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
            MinecraftServer server = ctx.getServerHandler().player.getServer();
            if (!server.isCallingFromMinecraftThread()) {
                ctx.getServerHandler().player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
            }
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
            } else if (type.equals(kaiaPotion)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                Potion potionFromResourceLocation = Potion.getPotionFromResourceLocation(text);
                if (intValue == 0) {
                    player.removePotionEffect(potionFromResourceLocation);
                } else {
                    player.addPotionEffect(new PotionEffect(potionFromResourceLocation, Integer.MAX_VALUE / 5, intValue, false, false));
                }
            } else if (type.equals(maxCountSlot)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                kaiaItem.getTagCompound().setInteger(maxCountSlot, intValue);
            } else if (type.equals(autoBackPack)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                kaiaItem.getTagCompound().setBoolean(autoBackPack, booleanValue);
            } else if (type.equals(autoBackPackEntities)) {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack kaiaItem = player.getHeldItemMainhand();
                kaiaItem.getTagCompound().setBoolean(autoBackPackEntities, booleanValue);
            } else if (type.equals(kaiaDimension)) {
                EntityPlayer player = ctx.getServerHandler().player;
                int i = text.indexOf(',');
                int posX = Integer.parseInt(text.substring(0, i));
                int posY = Integer.parseInt(text.substring(i + 1).split(",")[0].trim());
                int posZ = Integer.parseInt(text.substring(i + 1).split(",")[1].trim());
                if (player.dimension == intValue) {
                    player.dismountRidingEntity();
                    player.setPositionAndUpdate(posX, posY, posZ);
                } else {
                    player.changeDimension(intValue, new Teleporte(posX, posY, posZ));
                    return null;
                }
            }
            return null;
        }
    }
}
