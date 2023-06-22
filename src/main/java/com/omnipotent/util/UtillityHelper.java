package com.omnipotent.util;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

/**
 * @Author <a href="gamerYToffi:"
 * este método retorna verdadeiro caso a string contenha apenas numeros inteiros (O que incluir o sinal de menos) e falso caso contrario.
 */
public class UtillityHelper {
    public static boolean isJustNumber(String text) {
        return text.matches("[-]?\\d+");
    }

    /**
     * @Author <a href="gamerYToffi:"
     * Este método com base no valor recebido e na largura da tela retorna um valor equivalente para qualquer monitor.
     */
    public static int getEquivalentValueOfscreenHeight(int value, int height) {
        double ratio = (double) value / height;
        int equivalentValue = (int) (height * ratio);
        return equivalentValue;
    }

    /**
     * @Author <a href="gamerYToffi:"
     * Este método com base no valor recebido e na largura da tela retorna um valor equivalente para qualquer monitor.
     */
    public static int getEquivalentValueOfscreenWidth(int value, int width) {
        double ratio = (double) value / width;
        int equivalentValue = (int) (width * ratio);
        return equivalentValue;
    }

    /**
     * @Author <a href="gamerYToffi:"
     * Este método envia uma mensagem apenas para o player que lançou a própria mensagem, util quando se quer enviar informações que apenas um player deve ver.
     */
    public static void sendmessageToPlayer(String message) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    /**
     * @Author <a href="gamerYToffi:"
     * Este método envia uma mensagem para todos os players.
     */
    public static void sendMessageToAllPlayers(String message) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(player -> player.sendMessage(new TextComponentString(message)));
    }
}
