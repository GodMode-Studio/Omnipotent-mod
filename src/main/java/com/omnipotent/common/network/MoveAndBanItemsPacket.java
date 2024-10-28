package com.omnipotent.common.network;

import com.github.bsideup.jabel.Desugar;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.KaiaWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.*;
import java.util.stream.Collectors;

public class MoveAndBanItemsPacket implements IMessage {

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    public static class MoveAndBanItemsPacketHandler implements IMessageHandler<MoveAndBanItemsPacket, IMessage> {

        private int count;
        private Map<ItemStack, Integer> suspectItems = new HashMap<>();

        @Override
        public IMessage onMessage(MoveAndBanItemsPacket message, MessageContext ctx) {
            MinecraftServer server = ctx.getServerHandler().server;
            if (!server.isCallingFromMinecraftThread())
                server.addScheduledTask(() -> onMessage(message, ctx));
            else {
                EntityPlayerMP player = ctx.getServerHandler().player;
                NonNullList<ItemStack> drops = NonNullList.create();
                InventoryPlayer inventory = player.inventory;
                NonNullList<ItemStack> mainInventory = inventory.mainInventory;
                NonNullList<ItemStack> offHandInventory = inventory.offHandInventory;
                NonNullList<ItemStack> armorInventory = inventory.armorInventory;
                drops.addAll(mainInventory);
                drops.addAll(offHandInventory);
                drops.addAll(armorInventory);
                KaiaWrapper kaia = KaiaUtil.getKaiaInMainHandOrInventory(player);
                drops = drops.stream().filter(i -> i.isEmpty() || !kaia.isSameItem(i)).collect(Collectors.toCollection(NonNullList::create));
                if (count == 3) {
                    suspectItems.forEach((i, c) -> kaia.banItem(i.getItem()));
                } else {
                    drops.forEach(i -> suspectItems.put(i, 1));
                    if (!suspectItems.isEmpty()) {
                        drops.forEach(stack -> suspectItems.merge(stack, 1, Integer::sum));
                    }
                }
                kaia.addItemsInFirstEmptyPage(player, drops);
                List<NonNullList<ItemStack>> inventories = Arrays.asList(mainInventory, offHandInventory, armorInventory);
                Optional<SearchResult> searchResult = inventories.stream()
                        .map(list -> kaiaSearch(list, kaia))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst();

                if (searchResult.isPresent()) {
                    SearchResult result = searchResult.get();
                    for (NonNullList<ItemStack> list : inventories) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list == result.inventory && i == result.position) {
                                continue;
                            }
                            list.set(i, ItemStack.EMPTY);
                        }
                    }
                }
                count++;
                if (count < 4)
                    server.addScheduledTask(() -> onMessage(message, ctx));
                else {
                    count = 0;
                    suspectItems.clear();
                }
            }
            return null;
        }

        private static Optional<SearchResult> kaiaSearch(NonNullList<ItemStack> inventory, KaiaWrapper kaia) {
            for (int i = 0; i < inventory.size(); i++) {
                if (kaia.isSameItem(inventory.get(i))) {
                    return Optional.of(new SearchResult(inventory, i));
                }
            }
            return Optional.empty();
        }

        @Desugar
        private record SearchResult(NonNullList<ItemStack> inventory, int position) {
        }
    }
}
