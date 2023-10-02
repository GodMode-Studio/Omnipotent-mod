package com.omnipotent.server.network.nbtpackets;

import com.omnipotent.server.capability.BlockModeProvider;
import com.omnipotent.server.capability.IBlockMode;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.NbtListUtil;
import com.omnipotent.util.Teleporte;
import com.omnipotent.util.UtilityHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.omnipotent.util.KaiaConstantsNbt.*;
import static com.omnipotent.util.NbtListUtil.divisionUUIDAndName;

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
        try {
            intValue = buf.readInt();
        } catch (Exception e) {
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = type.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.writeBoolean(booleanValue);
        if (text != null) {
            byte[] bytes2 = text.getBytes(StandardCharsets.UTF_8);
            buf.writeInt(bytes2.length);
            buf.writeBytes(bytes2);
        }
        buf.writeInt(intValue);
    }

    public static class KaiaNbtPacketHandler implements IMessageHandler<KaiaNbtPacket, IMessage> {
        @Override
        public IMessage onMessage(KaiaNbtPacket message, MessageContext ctx) {
            MinecraftServer server = ctx.getServerHandler().player.getServer();
            if (!server.isCallingFromMinecraftThread())
                ctx.getServerHandler().player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
            else {
                EntityPlayer player = ctx.getServerHandler().player;
                switch (message.type) {
                    case blockReachDistance:
                        UtilityHelper.modifyBlockReachDistance(ctx.getServerHandler().player, message.intValue);
                        break;
                    case kaiaEnchant:
                        functionManageEnchantments(message, player);
                        break;
                    case kaiaPotion:
                        functionManagePotions(message, player);
                        break;
                    case kaiaDimension:
                        functionTeletransportDimension(message, player);
                        break;
                    case playersDontKill:
                        functionManagePlayersCantOfKaia(player, message);
                        break;
                    case entitiesCantKill:
                        functionManageEntitiesCantKillOfKaia(message, player);
                        break;
                    case kaiaBlockSpectator:
                        IBlockMode capability = player.getCapability(BlockModeProvider.blockMode, null);
                        capability.setBlockNoEditMode(!capability.getBlockNoEditMode());
                        break;
                    case kaiaBlockCreative:
                        IBlockMode capability1 = player.getCapability(BlockModeProvider.blockMode, null);
                        capability1.setBlockCreativeMode(!capability1.getBlockCreativeMode());
                        break;
                    case effectsBlockeds:
                        functionManageEffectsBlocked(player, message);
                        break;
                    default:
                        functionManageBooleansAndIntegersNbt(player, message);
                        break;
                }
            }
            return null;
        }

        private void functionManageEffectsBlocked(EntityPlayer player, KaiaNbtPacket message) {
            ItemStack kaia = KaiaUtil.getKaiaInMainHand(player);
            NBTTagList tagList = kaia.getTagCompound().getTagList(effectsBlockeds, 8);
            if (message.intValue == 0) {
                tagList.appendTag(new NBTTagString(message.text));
            } else {
                if (NbtListUtil.isElementAlreadyExists(tagList, message.text))
                    NbtListUtil.removeElement(tagList, message.text);
            }
        }

        private static void functionManageBooleansAndIntegersNbt(EntityPlayer player, KaiaNbtPacket message) {
            ArrayList<String> listNBTBoolean = new ArrayList<>();
            listNBTBoolean.addAll(Arrays.asList(counterAttack, killAllEntities, killFriendEntities, attackYourWolf, interactLiquid, noBreakTileEntity, autoBackPack, autoBackPackEntities, playersCantRespawn, playersWhoShouldNotKilledInCounterAttack, playerDontKillInDirectAttack, chargeItemsInInventory, summonLightBoltsInKill, banEntitiesAttacked, autoKill));
            for (String nbt : listNBTBoolean) {
                if (message.type.equals(nbt)) {
                    KaiaUtil.getKaiaInMainHand(player).getTagCompound().setBoolean(nbt, message.booleanValue);
                    return;
                }
            }
            ArrayList<String> listNBTInt = new ArrayList<>();
            listNBTInt.addAll(Arrays.asList(blockBreakArea, rangeAttack, maxCountSlot, rangeAutoKill));
            for (String nbt : listNBTInt) {
                if (message.type.equals(nbt)) {
                    KaiaUtil.getKaiaInMainHand(player).getTagCompound().setInteger(nbt, message.intValue);
                    return;
                }
            }
        }

        private static void functionManageEntitiesCantKillOfKaia(KaiaNbtPacket message, EntityPlayer player) {
            ItemStack kaia = KaiaUtil.getKaiaInMainHand(player) == null ? KaiaUtil.getKaiaInInventory(player) : KaiaUtil.getKaiaInMainHand(player);
            if (message.intValue == 0) {
                NBTTagList tagList = kaia.getTagCompound().getTagList(entitiesCantKill, 8);

                tagList.appendTag(new NBTTagString(message.text));
            } else
                NbtListUtil.removeString(kaia.getTagCompound().getTagList(entitiesCantKill, 8), message.text, 0);
        }

        private void functionManagePlayersCantOfKaia(EntityPlayer player, KaiaNbtPacket message) {
            NBTTagList tagList = KaiaUtil.getKaiaInMainHand(player).getTagCompound().getTagList(playersDontKill, 8);
            Iterator<NBTBase> iterator = tagList.iterator();
            ArrayList<String> names = new ArrayList<>();
            String tagForRemoved = null;
            int pos = -1;
            if (message.intValue == 1) {
                while (iterator.hasNext()) {
                    pos++;
                    String string = iterator.next().toString();
                    string = string.substring(1, string.length() - 1);
                    String[] split = string.split(divisionUUIDAndName);
                    if (split[1].equals(message.text)) {
                        tagForRemoved = string;
                        break;
                    }
                }
                if (tagForRemoved != null)
                    tagList.removeTag(pos);
            } else {
                EntityPlayerMP playerTarget = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(message.text);
                if (playerTarget == null)
                    return;
                message.text = playerTarget.getUniqueID().toString() + divisionUUIDAndName + message.text;
                while (iterator.hasNext()) {
                    String string = iterator.next().toString();
                    string = string.substring(1, string.length() - 1);
                    names.add(string);
                }
                if (!names.contains(message.text))
                    tagList.appendTag(new NBTTagString(message.text));
            }
        }

        private static void functionManageEnchantments(KaiaNbtPacket message, EntityPlayer player) {
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
        }

        private static void functionManagePotions(KaiaNbtPacket message, EntityPlayer player) {
            Potion potionFromResourceLocation = Potion.getPotionFromResourceLocation(message.text);
            if (message.intValue == 0) player.removePotionEffect(potionFromResourceLocation);
            else
                player.addPotionEffect(new PotionEffect(potionFromResourceLocation, Integer.MAX_VALUE / 5, message.intValue, false, false));
        }

        private static void functionTeletransportDimension(KaiaNbtPacket message, EntityPlayer player) {
            int i = message.text.indexOf(',');
            int posX = Integer.parseInt(message.text.substring(0, i));
            int posY = Integer.parseInt(message.text.substring(i + 1).split(",")[0].trim());
            int posZ = Integer.parseInt(message.text.substring(i + 1).split(",")[1].trim());
            if (player.dimension == message.intValue) {
                player.dismountRidingEntity();
                player.setPositionAndUpdate(posX, posY, posZ);
            } else player.changeDimension(message.intValue, new Teleporte(posX, posY, posZ));
        }
    }
}
