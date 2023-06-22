package com.omnipotent.server.mixin;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.command.CommandClearInventory;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.command.CommandBase.getCommandSenderAsPlayer;
import static net.minecraft.command.CommandBase.getPlayer;

@Mixin(CommandClearInventory.class)
public abstract class MixinClearControl {

    @Inject(method = "execute", at = @At("HEAD"))
    public void execute(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) throws CommandException {
        EntityPlayerMP entityPlayerMp = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(server, sender, args[0]);
        if( KaiaUtil.hasInInventoryKaia(entityPlayerMp) ) {
            String currentLanguage = FMLCommonHandler.instance().getCurrentLanguage();
            if(currentLanguage.equals("pt_br")){
                UtilityHelper.sendMessageToAllPlayers(TextFormatting.DARK_RED+"O JOGADOR "+entityPlayerMp.getName()+" ESTA ALEM DOS COMANDOS");
            }else{
                UtilityHelper.sendMessageToAllPlayers(TextFormatting.DARK_RED+"THE PLAYER "+entityPlayerMp.getName()+" IS BEYOND THE COMMANDS");
            }
            ci.cancel();
        }
    }
}
