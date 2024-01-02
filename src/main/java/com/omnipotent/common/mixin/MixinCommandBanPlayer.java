package com.omnipotent.common.mixin;

import com.omnipotent.util.UtilityHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandBanPlayer.class)
public abstract class MixinCommandBanPlayer extends CommandBase {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void execute(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) throws CommandException {
        if (args.length >= 1 && args[0].length() > 0) {
            EntityPlayerMP playerByUsername = server.getPlayerList().getPlayerByUsername(args[0]);
            if (playerByUsername != null && playerByUsername.hasKaia) {
                UtilityHelper.sendMessageToAllPlayers(TextFormatting.BLACK + " The player cannot be banned because he is" + TextFormatting.OBFUSCATED + "GOD");
                ci.cancel();
            }
        }
    }
}
