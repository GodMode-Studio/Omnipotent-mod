package com.omnipotent.tools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class KaiaEntity extends EntityItem implements IForgeRegistryEntry {
    public KaiaEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public KaiaEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public KaiaEntity(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void setDead() {
        World entityWorld = this.getEntityWorld();
        ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> persistentChunksFor = ForgeChunkManager.getPersistentChunksFor(entityWorld);
        Chunk chunk = entityWorld.getChunkFromBlockCoords(this.getPosition());
        ChunkPos chunkPos = entityWorld.getChunkFromBlockCoords(this.getPosition()).getPos();
        ImmutableSet<ForgeChunkManager.Ticket> tickets = persistentChunksFor.get(chunkPos);
        List<ForgeChunkManager.Ticket> collect = tickets.stream().collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(chunk.getEntityLists()).forEach(classIn -> i.addAndGet(classIn.stream().filter(entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof Kaia).collect(Collectors.toList()).size()));
        if(collect!=null && collect.size()>0 && i.get()<=0) {
            ForgeChunkManager.Ticket ticket = collect.get(0);
            ForgeChunkManager.releaseTicket(ticket);
            ForgeChunkManager.unforceChunk(ticket, chunkPos);
        }
        super.setDead();
    }

    @Override
    public Object setRegistryName(ResourceLocation name) {
        return new ResourceLocation("entity2226");
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation("entity2226");
    }

    @Override
    public Class getRegistryType() {
        return null;
    }
}
