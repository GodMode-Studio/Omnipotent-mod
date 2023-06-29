package com.omnipotent.server.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.client.render.RenderTextures.texturesItemsInit;
import static com.omnipotent.server.event.EventBlockItems.blocksInit;
import static com.omnipotent.server.event.EventInitItems.itemsInit;

public class BlockOre extends BlockBasic {

    Item itemToDrop;
    int minDropAmount;
    int maxDropAmount;

    public BlockOre(String name, Material material, float hardness, SoundType soundType, Item itemToDrop, int minDropAmount, int maxDropAmount) {
        super(name, material);
        setCreativeTab(omnipotentTab);
        setHardness(hardness);
        setSoundType(soundType);
        this.itemToDrop = itemToDrop;
        this.minDropAmount = minDropAmount;
        this.maxDropAmount = maxDropAmount;
    }

    @Override
    public int quantityDropped(Random random) {
        if (this.minDropAmount > this.maxDropAmount) {
            int i = this.minDropAmount;
            this.minDropAmount = this.maxDropAmount;
            this.maxDropAmount = i;
        }
        return this.minDropAmount + random.nextInt(this.maxDropAmount - this.minDropAmount + 1);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(this.getDefaultState(), random, fortune)) {
            int i = random.nextInt(fortune + 2) - 1;
            if (i < 0) { i = 0; }
            return quantityDropped(random) * (i + 1);
        } else {
            return quantityDropped(random);
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        Random rand = new Random();

        int dropAmount = minDropAmount + rand.nextInt(maxDropAmount - minDropAmount + 1);
        for (int i = 0; i < dropAmount; i++) {
            drops.add(new ItemStack(itemToDrop));
        }

        return drops;
    }

}
