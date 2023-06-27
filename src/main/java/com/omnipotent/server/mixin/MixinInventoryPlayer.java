package com.omnipotent.server.mixin;

import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.UtilityHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayer implements IInventory {


    @Shadow
    public EntityPlayer player;

    @Inject(method = "clearMatchingItems", at = @At("HEAD"))
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
}
