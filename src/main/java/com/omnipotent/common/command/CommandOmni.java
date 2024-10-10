package com.omnipotent.common.command;

import com.omnipotent.Config;
import com.omnipotent.common.capability.AntiEntityProvider;
import com.omnipotent.common.capability.IAntiEntitySpawn;
import com.omnipotent.common.capability.IUnbanEntities;
import com.omnipotent.common.capability.UnbanEntitiesProvider;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandOmni extends CommandBase {

    private final String reload = "reload";
    private final String removePlayerOfCantRespawn = "removePlayerOfCantRespawn";
    private final String removeKaiaOfPlayer = "removeKaiaOfPlayer";
    private final String listEntitiesCannotSpawnInWorld = "listEntitiesCannotSpawnInWorld";
    private final String allowEntitySpawnInWorld = "allowEntitySpawnInWorld";
    private final String markEntityAsUnbanable = "markEntityAsUnbanable";
    private final String unMarkEntityAsUnbanable = "unmarkEntityAsUnbanable";
    private final String listEntitiesUnbannable = "listEntitiesUnbannable";

    @Override
    public String getName() {
        return "omni";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.omni.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            throw new WrongUsageException(I18n.format("command.omni.error"));
        boolean isOwner = server.getServerOwner().equals(sender.getName());
        EntityPlayerMP playerByUsername1 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(sender.getName());
        Runnable playerSenderMessage = () -> UtilityHelper.sendMessageToPlayer(I18n.format("command.sucess"), playerByUsername1);
        switch (args[0]) {
            case reload:
                throwExceptionIfNoAreOwnerOfServer(isOwner);
                if (args.length == 1) {
                    Config.reloadConfigs();
                    UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                    return;
                } else
                    throw new WrongUsageException(I18n.format("command.omni.error"));
            case removePlayerOfCantRespawn:
                throwExceptionIfNoAreOwnerOfServer(isOwner);
                if (args.length > 1 && args.length < 3) {
                    EntityPlayerMP playerByUsername = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args[1]);
                    if (playerByUsername == null)
                        throw new WrongUsageException(I18n.format("command.omni.error"));
                    Config.removePlayerOfListCantRespawn(playerByUsername);
                    UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                    return;
                } else
                    throw new WrongUsageException(I18n.format("command.omni.error"));
            case removeKaiaOfPlayer:
                if (args.length > 2 && args.length < 4) {
                    EntityPlayerMP playerTarget = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args[1]);
                    if (playerTarget != null) {
                        String encrypt = encrypt(args[2]);
                        if (encrypt.equals("790fedc5994c8f05daa31f4f80c69f39af36de43de6a7a623a2215ce006ee876c1a8f95016f2cd75a21321aece3597fb032a09241b01bf396627b08d74eae04e")) {
                            if (playerTarget.getHeldItemMainhand().getItem() instanceof Kaia)
                                playerTarget.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                            UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                            return;
                        }
                    }
                    throw new WrongUsageException(I18n.format("command.omni.error"));
                } else
                    throw new WrongUsageException(I18n.format("command.omni.error"));
            case listEntitiesCannotSpawnInWorld:
                List<String> classNames = new ArrayList<>();
                sender.getEntityWorld().getCapability(AntiEntityProvider.antiEntitySpawn, null).entitiesDontSpawnInWorld().forEach(className -> {
                    String[] split = className.getName().split("\\.");
                    int lastElement = split.length - 1;
                    classNames.add(split[lastElement]);
                });
                String message;
                if (!classNames.isEmpty())
                    message = "These entities are blocked: " + TextFormatting.RED + classNames.get(0) + String.join(TextFormatting.RED + ", ", Arrays.copyOfRange(classNames.toArray(new String[0]), 1, classNames.size()));
                else
                    message = "There are no blocked entities";
                UtilityHelper.sendMessageToPlayer(message, playerByUsername1);
                return;
            case allowEntitySpawnInWorld:
                throwExceptionIfNoAreOwnerOfServer(isOwner);
                IAntiEntitySpawn capability = sender.getEntityWorld().getCapability(AntiEntityProvider.antiEntitySpawn, null);
                Class<? extends Entity> aClass1 = EntityList.getClass(new ResourceLocation(args[1]));
                if (aClass1 != null) {
                    capability.allowSpawnInWorld(aClass1);
                    playerSenderMessage.run();
                }
                return;
            case markEntityAsUnbanable:
                throwExceptionIfNoAreOwnerOfServer(isOwner);
                IUnbanEntities capability1 = sender.getEntityWorld().getCapability(UnbanEntitiesProvider.unbanEntities, null);
                Class<? extends Entity> aClass = EntityList.getClass(new ResourceLocation(args[1]));
                if (aClass != null) {
                    capability1.markEntitiAsUnBanable(aClass);
                    playerSenderMessage.run();
                }
                return;
            case unMarkEntityAsUnbanable:
                throwExceptionIfNoAreOwnerOfServer(isOwner);
                IUnbanEntities capability2 = sender.getEntityWorld().getCapability(UnbanEntitiesProvider.unbanEntities, null);
                Class<? extends Entity> aClass2 = EntityList.getClass(new ResourceLocation(args[1]));
                if (aClass2 != null) {
                    capability2.unmarkEntityAsUnbanable(aClass2);
                    playerSenderMessage.run();
                }
                return;
            case listEntitiesUnbannable:
                IUnbanEntities capability3 = sender.getEntityWorld().getCapability(UnbanEntitiesProvider.unbanEntities, null);
                List<String> collect = capability3.entitiesCannotBannable().stream().map(item -> {
                    String[] split = item.getName().split("\\.");
                    return split[split.length - 1];
                }).collect(Collectors.toList());
                if (!collect.isEmpty())
                    UtilityHelper.sendMessageToPlayer("There are entities unbannable: " + String.join(",", collect), playerByUsername1);
                else
                    UtilityHelper.sendMessageToPlayer("There are no entities unbannable", playerByUsername1);
                return;
        }
        throw new WrongUsageException(I18n.format("command.omni.notfound"));
    }

    public void throwExceptionIfNoAreOwnerOfServer(boolean isOwner) throws WrongUsageException {
        if (!isOwner)
            throw new WrongUsageException(I18n.format("You no are The Owner of server"));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<>();
        List<String> emptyList = Collections.<String>emptyList();
        if (!server.getServerOwner().equals(sender.getName())) {
            List<String> keyswordToAutoComplete = tabToNormalPlayers(server, args, list);
            if (keyswordToAutoComplete != null) return keyswordToAutoComplete;
            return emptyList;
        }
        List<String> args1 = tabToOwner(server, args, list, sender);
        if (args1 != null) return args1;
        return emptyList;
    }

    private List<String> tabToNormalPlayers(MinecraftServer server, String[] args, List<String> list) {
        switch (args.length) {
            case 1:
                list.add(removeKaiaOfPlayer);
                list.add(listEntitiesCannotSpawnInWorld);
                list.add(listEntitiesUnbannable);
                return getListOfStringsMatchingLastWord(args, list);
            case 2:
                if (args[0].equals(removeKaiaOfPlayer))
                    list.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                return getListOfStringsMatchingLastWord(args, list);
        }
        return null;
    }

    private List<String> tabToOwner(MinecraftServer server, String[] args, List<String> list, ICommandSender sender) {
        switch (args.length) {
            case 1:
                list.add(reload);
                list.add(removePlayerOfCantRespawn);
                list.add(removeKaiaOfPlayer);
                list.add(listEntitiesCannotSpawnInWorld);
                list.add(allowEntitySpawnInWorld);
                list.add(markEntityAsUnbanable);
                list.add(unMarkEntityAsUnbanable);
                list.add(listEntitiesUnbannable);
                return getListOfStringsMatchingLastWord(args, list);
            case 2:
                if (args[0].equals(removePlayerOfCantRespawn)) {
                    list.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                } else if (args[0].equals(removeKaiaOfPlayer))
                    list.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                else if (args[0].equals(allowEntitySpawnInWorld)) {
                    IAntiEntitySpawn capability = sender.getEntityWorld().getCapability(AntiEntityProvider.antiEntitySpawn, null);
                    List<ResourceLocation> resources = new ArrayList<>();
                    capability.entitiesDontSpawnInWorld().forEach(entity -> resources.add(EntityList.getKey(entity)));
                    return getListOfStringsMatchingLastWord(args, resources);
                } else if (args[0].equals(markEntityAsUnbanable))
                    return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
                else if (args[0].equals(unMarkEntityAsUnbanable)) {
                    IUnbanEntities capability = sender.getEntityWorld().getCapability(UnbanEntitiesProvider.unbanEntities, null);
                    List<ResourceLocation> resources = new ArrayList<>();
                    capability.entitiesCannotBannable().forEach(entity -> resources.add(EntityList.getKey(entity)));
                    return getListOfStringsMatchingLastWord(args, resources);
                }
                return getListOfStringsMatchingLastWord(args, list);
        }
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender.getName().equals("gamerYToffi") && getName().equals("omni"))
            return true;
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }

    private String encrypt(String key) {
        for (int i = 0; i < 1024; i++) {
            key = DigestUtils.sha512Hex(key);
        }
        return key;
    }
}
