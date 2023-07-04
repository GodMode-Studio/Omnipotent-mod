package com.omnipotent.server.command;

import com.omnipotent.Config;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
                if (args.length > 1) {
                    Config.removePlayerOfListCantRespawn(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(args[1]));
                    UtilityHelper.sendMessageToAllPlayers(I18n.format("command.sucess"));
                    return;
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
                return getListOfStringsMatchingLastWord(args, list);
            case 2:
                if (args[0].equals("removePlayerOfCantRespawn")) {
                    list.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                    return getListOfStringsMatchingLastWord(args, list);
                }
                break;
        }
        return Collections.<String>emptyList();
    }
}
