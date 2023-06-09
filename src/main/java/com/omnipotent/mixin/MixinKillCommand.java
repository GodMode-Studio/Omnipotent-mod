package com.omnipotent.mixin;

import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandKill;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CommandKill.class)
public abstract class MixinKillCommand extends CommandBase {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            EntityPlayer entityplayer = getCommandSenderAsPlayer(sender);
            entityplayer.onKillCommand();
            notifyCommandListener(sender, this, "commands.kill.successful", new Object[]{entityplayer.getDisplayName()});
        } else {
            Entity entity = getEntity(server, sender, args[0]);
            if (entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof Kaia) {
                String currentLanguage = FMLCommonHandler.instance().getCurrentLanguage();
                if(currentLanguage.equals("pt_br")){
                    KaiaUtil.sendMessageToAllPlayers(TextFormatting.DARK_PURPLE+"KAIA NAO PODE SER MORTA");
                }else{
                    KaiaUtil.sendMessageToAllPlayers(TextFormatting.DARK_PURPLE+"KAIA CANNOT BE KILLED");
                }
                return;
            }
            entity.onKillCommand();
            notifyCommandListener(sender, this, "commands.kill.successful", new Object[]{entity.getDisplayName()});
        }
    }
}
