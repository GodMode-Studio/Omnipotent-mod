package com.omnipotent.common.network.nbtpackets;

import com.omnipotent.common.capability.BlockModeProvider;
import com.omnipotent.common.capability.IBlockMode;
import com.omnipotent.common.capability.KaiaProvider;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.constant.NbtStringValues;
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
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.omnipotent.constant.NbtNumberValues.*;
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

    public static class KaiaNbtPacketHandler implements IMessageHandler<KaiaNbtPacket, IMessage> {
        @Override
        public IMessage onMessage(KaiaNbtPacket message, MessageContext ctx) {
            MinecraftServer server = ctx.getServerHandler().player.getServer();
            if (!server.isCallingFromMinecraftThread())
                ctx.getServerHandler().player.getServer().addScheduledTask(() -> this.onMessage(message, ctx));
            else {
                EntityPlayer player = ctx.getServerHandler().player;
                boolean hasKaia = KaiaUtil.hasInInventoryKaia(player);
                if (message.type.equals("getKaiaBetweenSaves"))
                    functionManageKaiaBetweenSaves(player, message);
                else if (!player.getCapability(KaiaProvider.KaiaBrand, null).returnList().isEmpty() || hasKaia)
                    blockModeHandler(message, player);
                if (!hasKaia)
                    return null;
                switch (message.type) {
                    case "blockReachDistance":
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
//                    case entitiesCantKill:
//                        functionManageEntitiesCantKillOfKaia(message, player);
//                        break;
                    case effectsBlockeds:
                        functionManageEffectsBlocked(player, message);
                        break;
                    default:
                        manageBooleansIntegersAndStringNbt(player, message);
                        break;
                }
            }
            return null;
        }

        private static void blockModeHandler(KaiaNbtPacket message, EntityPlayer player) {
            if (message.type.equals(kaiaBlockSpectator)) {
                IBlockMode capability = player.getCapability(BlockModeProvider.blockMode, null);
                capability.setBlockNoEditMode(!capability.getBlockNoEditMode());
            } else if (message.type.equals(kaiaBlockCreative)) {
                IBlockMode capability1 = player.getCapability(BlockModeProvider.blockMode, null);
                capability1.setBlockCreativeMode(!capability1.getBlockCreativeMode());
            }
        }

        private void functionManageKaiaBetweenSaves(EntityPlayer player, KaiaNbtPacket message) {
            if (!KaiaUtil.hasInInventoryKaia(player) && player.getCapability(KaiaProvider.KaiaBrand, null).returnList().isEmpty()) {
                String path = System.getProperty("user.dir").concat("\\saves");
                HashMap<Long, ItemStack> kaias = new HashMap<>();
                iteratorInSaves(player, path, kaias);
                addLastKaiaOfDataInInventory(player, kaias);
            }
        }

        private void iteratorInSaves(EntityPlayer player, String path, HashMap<Long, ItemStack> kaias) {
            File file = new File(path);
            List<File> files = Arrays.asList(file.listFiles());
            files.forEach(file2 -> verifyAndAcessSaves(player, file2, kaias));
        }

        private void verifyAndAcessSaves(EntityPlayer player, File saveFile, HashMap<Long, ItemStack> kaias) {
            if (!saveFile.isDirectory())
                return;
            String absolutePath = saveFile.getAbsolutePath();
            File file = new File(absolutePath.concat("\\playerdata"));
            List<File> files = Arrays.asList(file.listFiles()).stream().filter(file2 -> file2.getName().endsWith(".dat") && file2.getName().split(".dat")[0].equals(player.getUniqueID().toString())).collect(Collectors.toList());
            for (File file2 : files) {
                try {
                    NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    if (nbtTagCompound != null) {
                        ISaveHandler saveHandler = player.world.getSaveHandler();
                        if (saveHandler instanceof SaveHandler) {
                            ItemStack kaiaOfNbtCompound = getKaiaOfNbtCompound(nbtTagCompound);
                            if (kaiaOfNbtCompound != null)
                                kaias.put(file2.lastModified(), kaiaOfNbtCompound);
                        }
                    }
                } catch (IOException e) {
                }
            }
        }

        private void addLastKaiaOfDataInInventory(EntityPlayer player, HashMap<Long, ItemStack> kaias) {
            if (kaias.isEmpty()) return;
            ItemStack kaia = kaias.get(Collections.max(kaias.keySet()));
            player.inventory.addItemStackToInventory(kaia);
        }

        private ItemStack getKaiaOfNbtCompound(NBTTagCompound nbtTagCompound) {
            NBTTagList inventory = nbtTagCompound.getTagList("Inventory", 10);
            Iterator<NBTBase> iterator = inventory.iterator();
            while (iterator.hasNext()) {
                NBTBase next = iterator.next();
                if (next instanceof NBTTagCompound) {
                    ItemStack itemStack = new ItemStack((NBTTagCompound) next);
                    if (itemStack.getItem() instanceof Kaia) {
                        return itemStack;
                    }
                }
            }
            return null;
        }

        private void functionManageEffectsBlocked(EntityPlayer player, KaiaNbtPacket message) {
            ItemStack kaia = KaiaUtil.getKaiaInMainHand(player).get();
            NBTTagList tagList = kaia.getTagCompound().getTagList(effectsBlockeds, 8);
            if (message.intValue == 0) {
                tagList.appendTag(new NBTTagString(message.text));
            } else {
                if (NbtListUtil.isElementAlreadyExists(tagList, message.text))
                    NbtListUtil.removeElement(tagList, message.text);
            }
        }

        private static void manageBooleansIntegersAndStringNbt(EntityPlayer player, KaiaNbtPacket message) {
            NBTTagCompound tagCompound = KaiaUtil.getKaiaInMainHand(player).get().getTagCompound();
            if (managerBooleanNbt(message, tagCompound)) return;
            if (managerIntegersNbt(message, tagCompound)) return;
            managerStringNbt(message, tagCompound);
        }

        private static void managerStringNbt(KaiaNbtPacket message, NBTTagCompound tagCompound) {
            for (NbtStringValues nbt : NbtStringValues.values()) {
                if (message.type.equals(nbt.getValue())) {
                    tagCompound.setString(nbt.getValue(), message.text);
                    return;
                }
            }
        }

        private static boolean managerBooleanNbt(KaiaNbtPacket message, NBTTagCompound tagCompound) {
            for (String nbt : NbtBooleanValues.valuesNbt) {
                if (message.type.equals(nbt)) {
                    tagCompound.setBoolean(nbt, message.booleanValue);
                    return true;
                }
            }
            return false;
        }

        private static boolean managerIntegersNbt(KaiaNbtPacket message, NBTTagCompound tagCompound) {
            ArrayList<String> listNBTInt = new ArrayList<>();
            listNBTInt.addAll(Arrays.asList(blockBreakArea.getValue(), rangeAttack.getValue(), maxCountSlot.getValue(), rangeAutoKill.getValue(), chargeManaInBlocksAround.getValue(), chargeEnergyInBlocksAround.getValue(), optionOfColor.getValue()));
            for (String nbt : listNBTInt) {
                if (message.type.equals(nbt)) {
                    if (message.type.equals(optionOfColor.getValue())) {
                        if (message.intValue >= 0 && message.intValue <= 3)
                            tagCompound.setInteger(nbt, message.intValue);
                        else
                            tagCompound.setInteger(nbt, 0);
                    } else if (message.type.equals(blockBreakArea.getValue()) && message.intValue % 2 == 0)
                        tagCompound.setInteger(nbt, message.intValue - 1);
                    else
                        tagCompound.setInteger(nbt, message.intValue);
                    return true;
                }
            }
            return false;
        }

        private static void functionManageEntitiesCantKillOfKaia(KaiaNbtPacket message, EntityPlayer player) {
            ItemStack kaia = KaiaUtil.getKaiaInMainHandOrInventory(player);
            if (message.intValue == 0) {
                NBTTagList tagList = kaia.getTagCompound().getTagList(entitiesCantKill, 8);
                tagList.appendTag(new NBTTagString(message.text));
            } else
                NbtListUtil.removeString(kaia.getTagCompound().getTagList(entitiesCantKill, 8), message.text, 0);
        }

        private void functionManagePlayersCantOfKaia(EntityPlayer player, KaiaNbtPacket message) {
            NBTTagList tagList = KaiaUtil.getKaiaInMainHand(player).get().getTagCompound().getTagList(playersDontKill, 8);
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
            ItemStack kaiaItem = KaiaUtil.getKaiaInMainHand(player).get();
            int lvl = message.intValue;
            short number = NumberUtils.toShort(String.valueOf(lvl), (short) -20_000);
            if (number <= 0) {
                Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(message.text);
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(kaiaItem);
                enchantments.remove(enchantmentByLocation);
                EnchantmentHelper.setEnchantments(enchantments, kaiaItem);
            } else {
                Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(message.text);
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(kaiaItem);
                enchantments.put(enchantmentByLocation, lvl);
                EnchantmentHelper.setEnchantments(enchantments, kaiaItem);
            }
        }

        private static void functionManagePotions(KaiaNbtPacket message, EntityPlayer player) {
            Potion potionFromResourceLocation = Potion.getPotionFromResourceLocation(message.text);
            if(potionFromResourceLocation==null) return;
            int levelPotion = message.intValue;
            if (levelPotion == 0) {
                player.removePotionEffect(potionFromResourceLocation);
                if (potionFromResourceLocation == Potion.getPotionById(22))
                    player.setAbsorptionAmount(0);
            } else {
                boolean isValid = levelPotion <= 255 && levelPotion > -1;
                player.addPotionEffect(new PotionEffect(potionFromResourceLocation, Integer.MAX_VALUE / 15, isValid ? levelPotion : 1, false, false));
            }
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
