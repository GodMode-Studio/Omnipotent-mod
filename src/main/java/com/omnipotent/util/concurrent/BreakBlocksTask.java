package com.omnipotent.util.concurrent;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import static com.omnipotent.util.KaiaUtil.fastBreakBlock;

public class BreakBlocksTask extends RecursiveTask<Long> {
    private final List<BlockPos> blocks;
    private final EntityPlayerMP player;
    private final NBTTagCompound kaiaTagCompound;

    public BreakBlocksTask(List<BlockPos> array, EntityPlayerMP player, NBTTagCompound kaia) {
        this.blocks = array;
        this.player = player;
        this.kaiaTagCompound = kaia;
    }

    @Override
    protected Long compute() {
        final int size = blocks.size();
        if (size <= 2000) {
            for (BlockPos block : blocks) {
                fastBreakBlock(player, block, kaiaTagCompound);
            }
            return (long) blocks.size();
        } else {
            int mid = size / 2;
            List<BlockPos> blocksFirstPart = blocks.subList(0, mid);
            List<BlockPos> blocksSecondPart = blocks.subList(mid, size);
            BreakBlocksTask left = new BreakBlocksTask(blocksFirstPart, player, kaiaTagCompound);
            BreakBlocksTask right = new BreakBlocksTask(blocksSecondPart, player, kaiaTagCompound);
            left.fork();
            right.compute();
            return left.join();

//                List<BlockPos> blocksFirstPart = blocks.subList(0, blocks.size() / 2 - 1);
//                List<BlockPos> blocksSecondPart = blocks.subList(blocks.size() / 2, blocks.size()-1);
//                BreakBlocksTask left = new BreakBlocksTask(blocksFirstPart, player, kaiaTagCompound);
//                BreakBlocksTask right = new BreakBlocksTask(blocksSecondPart, player, kaiaTagCompound);
//                left.fork();
//                right.fork();
//                left.join();
//                right.join();
        }
    }
}
