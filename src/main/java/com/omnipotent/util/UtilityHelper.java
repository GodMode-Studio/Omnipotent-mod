package com.omnipotent.util;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UtilityHelper {

    /**
     * Este método retorna verdadeiro caso a string contenha apenas numeros inteiros e falso caso contrario.
     * @Author gamerYToffi
     */
    public static boolean isJustNumber(String text) {
        return text.matches("[-]?\\d+");
    }

    /**
     * Este método com base no valor recebido e na altura da tela retorna um valor equivalente para qualquer monitor.
     * @Author gamerYToffi
     */
    public static int getEquivalentValueOfscreenHeight(int value, int height) {
        double ratio = (double) value / height;
        int equivalentValue = (int) (height * ratio);
        return equivalentValue;
    }

    /**
     * Este método com base no valor recebido e na largura da tela retorna um valor equivalente para qualquer monitor.
     * @Author gamerYToffi
     */
    public static int getEquivalentValueOfscreenWidth(int value, int width) {
        double ratio = (double) value / width;
        int equivalentValue = (int) (width * ratio);
        return equivalentValue;
    }

    /**
     * Este método envia uma mensagem apenas para o player que lançou a própria mensagem, util quando se quer enviar informações que apenas um player deve ver.
     * @Author gamerYToffi
     */
    public static void sendmessageToPlayer(String message) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    /**
     * Este método envia uma mensagem para todos os players.
     * @Author gamerYToffi
     */
    public static void sendMessageToAllPlayers(String message) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(player -> player.sendMessage(new TextComponentString(message)));
    }
    /**
     * Este método envia verifica se os itemStack são iguais e suas tags, ele ignora a tag de contagem.
     * @Author gamerYToffi
     **/
    public static boolean itemsAreEquals(ItemStack item1, ItemStack item2) {
        return ItemStack.areItemStackTagsEqual(item1, item2) && item1.isItemEqual(item2);
    }
    /**
     * Este método tenta compactar todos os itemStack que são iguais e que tenham as mesmas tags ignorando a durabilidade em um unico itemStack, ele modifica a lista que recebe.
     * @Author gamerYToffi
     **/
    public static void compactListItemStacks(List<ItemStack> drops) {
        ItemStack prevStack = null;
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((ItemStack) o1).getUnlocalizedName().compareTo(((ItemStack) o2).getUnlocalizedName());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };
        drops.sort(comparator);
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int c = 0; c < drops.size(); c++) {
            ItemStack stack = drops.get(c);
            if (prevStack == null) {
                prevStack = stack;
                continue;
            }
            if (stack.isItemEqual(prevStack) && ItemStack.areItemStackTagsEqual(prevStack, stack)) {
                prevStack.setCount(prevStack.getCount() + stack.getCount());
                if (c == drops.size() - 1) {
                    itemStacks.add(prevStack);
                }
            } else {
                itemStacks.add(prevStack);
                prevStack = stack;
                if (c == drops.size() - 1) {
                    itemStacks.add(prevStack);
                }
            }
        }
        if (drops.size() == 1) {
            ItemStack e = drops.get(0);
            drops.clear();
            drops.add(e);
        } else {
            drops.clear();
            drops.addAll(itemStacks);
        }
    }

    /**
     * Este método verifica se a entidade é é uma instancia de EntityPlayer
     * @Author gamerYToffi
     */
    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    /**
     * Este método modifica a distancia de interação dos blocos do player recebido
     * @Author gamerYToffi
     */

    public static void modifyBlockReachDistance(EntityPlayerMP player, int distance) {
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(distance);
    }
}
