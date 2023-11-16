//package com.omnipotent.server.mixin;
//
//import com.omnipotent.server.tool.Kaia;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.ClickType;
//import net.minecraft.inventory.Container;
//import net.minecraft.inventory.ContainerPlayer;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.math.BlockPos;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.List;
//import java.util.Set;
//
//import static com.omnipotent.util.KaiaConstantsNbt.listOfCoordenatesKaia;
//
//@Mixin(Container.class)
//public abstract class MixinContainer {
//    @Shadow
//    public List<Slot> inventorySlots;
//
//    @Shadow
//    @Final
//    private Set<EntityPlayer> playerList;
//
//    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
//    public void slotClickInject(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
//        if (!player.world.isRemote) {
//            Slot slot = this.inventorySlots.get(slotId);
//            if (slot != null) {
//                ItemStack itemstack8 = slot.getStack();
//                if (itemstack8.getItem() instanceof Kaia) {
//                    NBTTagCompound tagCompound = itemstack8.getTagCompound();
//                    int[] intArray = tagCompound.getIntArray(listOfCoordenatesKaia);
//                    double x = player.getLook(0).x;
//                    double y = player.getLook(0).y;
//                    int z = (int) player.getLook(0).z;
//                    IBlockState blockState = player.world.getBlockState(new BlockPos(x, y, z));
//                    if (!blockState.getBlock().hasTileEntity(blockState))
//                        return;
//                    intArray[0] = (int) x;
//                    intArray[1] = (int) y;
//                    intArray[2] = z;
//                    tagCompound.setIntArray(listOfCoordenatesKaia, intArray);
//                }
//            }
//        }
//    }
//}
