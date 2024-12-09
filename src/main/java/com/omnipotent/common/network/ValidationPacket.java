package com.omnipotent.common.network;

import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.player.PlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.omnipotent.Omnipotent.log;
import static com.omnipotent.common.event.EventInitItems.kaia;
import static com.omnipotent.constant.NbtNumberValues.rangeAttack;

public class ValidationPacket implements IMessage {

    private String challenge = "";
    private String type = "";
    private byte[] response = {-1};

    public ValidationPacket() {
    }

    public ValidationPacket(String type, String s) {
        this.type = type;
        challenge = s;
    }

    public ValidationPacket(String type, byte[] sign) {
        this.type = type;
        this.response = sign;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, challenge);
        ByteBufUtils.writeUTF8String(buf, type);
        buf.writeInt(response.length);
        buf.writeBytes(response);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        challenge = ByteBufUtils.readUTF8String(buf);
        type = ByteBufUtils.readUTF8String(buf);
        response = new byte[buf.readInt()];
        buf.readBytes(response);
    }

    public static class ValidationPacketHandler implements IMessageHandler<ValidationPacket, IMessage> {

        private static final PublicKey pKey = constructKey();

        private static PublicKey constructKey() {
            try {
                return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode("MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQAHq9DZo+KvYmTrrPJNTx1DPuoOUuSYDmgcVov4ocNw8F3FU+wRdiwvcAeAyDb3jrJjN1HokzxBKa8rgLtqXO5ozkAWCI5C54xa0/hYtoDSWfuVJem33quiSNxXT8Ijtk7GLJAbzo7G9sodLwJMEnJhJMJuZSQrKC+2iFDpuCRGgia5Do=")));
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                log.error("Fatal error in construct public key", e);
                return null;
            }
        }

        @Override
        public IMessage onMessage(ValidationPacket message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                executeClient(message, ctx);
            } else {
                try {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    MinecraftServer server = player.server;

                    if (!server.isCallingFromMinecraftThread())
                        server.addScheduledTask(() -> this.onMessage(message, ctx));
                    else {
                        Signature verifier = Signature.getInstance("SHA512withECDSA");
                        verifier.initVerify(pKey);
                        verifier.update(PlayerData.getPlayerData(player).getChallenge().getBytes());
                        if (!verifier.verify(message.response))
                            return null;
                        specificAction(message, player);
                    }

                } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                }
            }
            return null;
        }

        private static void specificAction(ValidationPacket msg, EntityPlayerMP player) {
            if (msg.type.equals("remove")) {
                if (player.getHeldItemMainhand().getItem() instanceof Kaia)
                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            } else if (msg.type.equals("give")) {
                double d0 = player.posY - 0.30000001192092896D + (double) player.getEyeHeight();
                EntityItem entityitem = new EntityItem(player.world, player.posX, d0, player.posZ, new ItemStack(Item.getByNameOrId(kaia.getRegistryName().toString())));
                entityitem.setThrower(player.getName());
                entityitem.onCollideWithPlayer(player);
            }
        }

        @SideOnly(Side.CLIENT)
        private void executeClient(ValidationPacket message, MessageContext ctx) {
            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread())
                Minecraft.getMinecraft().addScheduledTask(() -> this.onMessage(message, ctx));
            else {
                try {
                    Signature sig = Signature.getInstance("SHA512withECDSA");
                    Path resolve = Paths.get(System.getProperty("user.dir")).resolve("gamerKey.txt");
                    if (!Files.exists(resolve))
                        return;
                    byte[] decode = Base64.getDecoder().decode(Files.readAllBytes(resolve));
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
                    KeyFactory keyFactory = KeyFactory.getInstance("EC");
                    PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                    sig.initSign(privateKey);
                    sig.update(message.challenge.getBytes());
                    byte[] sign = sig.sign();
                    NetworkRegister.sendToServer(new ValidationPacket(message.type, sign));
                } catch (Exception e) {
                }
            }
        }
    }
}