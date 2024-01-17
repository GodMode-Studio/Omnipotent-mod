package com.omnipotent.common.tool;

import com.omnipotent.common.event.EventInitItems;
import com.omnipotent.common.network.NetworkRegister;
import com.omnipotent.common.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.KaiaConstantsNbt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ToolDevTest extends ItemTool {
    public ToolDevTest() {
        super(ToolMaterial.DIAMOND, null);
        EventInitItems.itemsInit.add(this);
        setRegistryName("tooltestdev");
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.getName().equals("gamerYToffi"))
            return false;
        if (player.world.isRemote) {
//            NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(KaiaConstantsNbt.kaiaPotion, MobEffects.ABSORPTION.getRegistryName().toString(), 1000));
        } else {
            EntityPlayerMP entity1 = (EntityPlayerMP) player;
            entity1.getActivePotionEffects().clear();
            entity1.getActivePotionMap().clear();
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
