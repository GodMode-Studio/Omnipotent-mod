package com.omnipotent.common.tool;

import com.omnipotent.common.event.EventInitItems;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DevTool extends ItemTool {
    public DevTool() {
        super(ToolMaterial.DIAMOND, null);
        EventInitItems.itemsInit.add(this);
        setRegistryName("tooltestdev");
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.getName().equals("gamerYToffi"))
            return false;
        if (player.world.isRemote) {
//            try {
            NetworkRegister.sendToServer(new KaiaNbtPacket("", "", 1000));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        } else {
//            EntityPlayerMP entity1 = (EntityPlayerMP) player;
//            entity1.getActivePotionEffects().clear();
//            entity1.getActivePotionMap().clear();
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Just to Test Devs");
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 300f;
    }
}
