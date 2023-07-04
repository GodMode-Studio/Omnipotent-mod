package com.omnipotent.server.network.nbtpackets;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.Teleporte;
import com.omnipotent.util.UtilityHelper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.omnipotent.util.KaiaConstantsNbt.*;

public class KaiaNbtPacket implements IMessage {
    private String type;
    private String text;
    private boolean booleanValue;
    private int intValue;

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
            if (!server.isCallingFromMinecraftThread()) ctx.getServerHandler().player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
            else {
                EntityPlayer player = ctx.getServerHandler().player;
                if (message.type.equals(blockReachDistance)) UtilityHelper.modifyBlockReachDistance(ctx.getServerHandler().player, message.intValue);
                 else if (message.type.equals(kaiaEnchant)) {
                    ItemStack kaiaItem = KaiaUtil.getKaiaInMainHand(player);
                    if (message.intValue == 0) {
                        Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(message.text);
                        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(kaiaItem);
                        enchantments.remove(enchantmentByLocation);
                        EnchantmentHelper.setEnchantments(enchantments, kaiaItem);
                    } else {
                        Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(message.text);
                        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(kaiaItem);
                        enchantments.put(enchantmentByLocation, message.intValue);
                        EnchantmentHelper.setEnchantments(enchantments, kaiaItem);
                    }
                } else if (message.type.equals(kaiaPotion)) {
                    Potion potionFromResourceLocation = Potion.getPotionFromResourceLocation(message.text);
                    if (message.intValue == 0) player.removePotionEffect(potionFromResourceLocation);
                    else player.addPotionEffect(new PotionEffect(potionFromResourceLocation, Integer.MAX_VALUE / 5, message.intValue, false, false));
                } else if (message.type.equals(kaiaDimension)) {
                    int i = message.text.indexOf(',');
                    int posX = Integer.parseInt(message.text.substring(0, i));
                    int posY = Integer.parseInt(message.text.substring(i + 1).split(",")[0].trim());
                    int posZ = Integer.parseInt(message.text.substring(i + 1).split(",")[1].trim());
                    if (player.dimension == message.intValue) {
                        player.dismountRidingEntity();
                        player.setPositionAndUpdate(posX, posY, posZ);
                    } else player.changeDimension(message.intValue, new Teleporte(posX, posY, posZ));
                } else {
                    ArrayList<String> listNBTBoolean = new ArrayList<>();
                    listNBTBoolean.addAll(Arrays.asList(counterAttack, killAllEntities, killFriendEntities, attackYourWolf, interactLiquid, noBreakTileEntity, autoBackPack, autoBackPackEntities, playersCantRespawn));
                    for (String nbt : listNBTBoolean) {
                        if (message.type.equals(nbt)) {
                            KaiaUtil.getKaiaInMainHand(player).getTagCompound().setBoolean(nbt, message.booleanValue);
                            return null;
                        }
                    }
                    ArrayList<String> listNBTInt = new ArrayList<>();
                    listNBTInt.addAll(Arrays.asList(blockBreakArea, rangeAttack, maxCountSlot));
                    for (String nbt : listNBTInt) {
                        if (message.type.equals(nbt)) {
                            KaiaUtil.getKaiaInMainHand(player).getTagCompound().setInteger(nbt, message.intValue);
                            return null;
                        }
                    }
                }
            }
            return null;
        }
    }
}
