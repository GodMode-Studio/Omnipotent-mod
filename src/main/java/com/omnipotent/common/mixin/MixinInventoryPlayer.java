package com.omnipotent.common.mixin;

import com.omnipotent.common.tool.Kaia;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayer implements IInventory {

    @Shadow
    public EntityPlayer player;

    @Shadow
    @Final
    private List<NonNullList<ItemStack>> allInventories;

    @Accessor("allInventories")
    abstract List<NonNullList<ItemStack>> getAllInventories();

    @Inject(method = "clearMatchingItems", at = @At("HEAD"), cancellable = true)
    public void execute(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT, CallbackInfoReturnable<Integer> cir) {
        if (KaiaUtil.hasInInventoryKaia(player)) {
            String currentLanguage = FMLCommonHandler.instance().getCurrentLanguage();
            if (currentLanguage.equals("pt_br")) {
                UtilityHelper.sendMessageToAllPlayers(TextFormatting.DARK_RED + "O JOGADOR " + player.getName() + " ESTA ALEM DOS COMANDOS");
            } else {
                UtilityHelper.sendMessageToAllPlayers(TextFormatting.DARK_RED + "THE PLAYER " + player.getName() + " IS BEYOND THE COMMANDS");
            }
            cir.cancel();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void clear() {
        if (this.player != null && KaiaUtil.hasInInventoryKaia(this.player))
            return;
        for (List<ItemStack> list : this.getAllInventories()) {
            list.clear();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Final
    public void setInventorySlotContents(int index, ItemStack stack) {
        NonNullList<ItemStack> nonnulllist = null;

        for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
            if (index < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }

            index -= nonnulllist1.size();
        }

        if (nonnulllist != null) {
            if (nonnulllist.get(index).getItem() instanceof Kaia) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace.length > 2) {
                    StackTraceElement stackTraceElement = stackTrace[2];
                    String className = stackTraceElement.getClassName();
                    if (!className.startsWith("net.minecraft") && !className.startsWith("net.minecraftforge") &&
                            !className.startsWith("com.omnipotent"))
                        return;
                }
            }
            nonnulllist.set(index, stack);
        }
    }

    @Inject(method = "dropAllItems", at = @At("HEAD"), cancellable = true)
    public void dropAllItemsInject(CallbackInfo ci) {
        if (this.player != null && KaiaUtil.hasInInventoryKaia(this.player))
            ci.cancel();
    }

    @Inject(method = "deleteStack", at = @At("HEAD"), cancellable = true)
    public void deleteStack(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof Kaia) {
            if (!stack.getTagCompound().hasKey("noowner"))
                ci.cancel();
            else
                stack.getTagCompound().removeTag("noowner");
        }
    }
}
