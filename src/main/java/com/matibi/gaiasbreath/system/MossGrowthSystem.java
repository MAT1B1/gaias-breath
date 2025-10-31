package com.matibi.gaiasbreath.system;

import com.matibi.gaiasbreath.util.ChunkTracker;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MossGrowthSystem {

    private static final float STONE_TO_MOSSY_CHANCE = 0.02f;
    private static final float MOSSY_SPREAD_CHANCE = 0.01f;
    private static final float MOSSY_TO_MOSS_CHANCE = 0.01f;

    private static final int MAX_CHUNK_PER_TICK = 10;
    private static final int BLOCK_PER_CHUNK = 10;
    private static final int Y_RANGE = 20;

    public static void tick(ServerWorld world) {
        Random random = world.random;
        Set<ChunkPos> loadedChunks = ChunkTracker.getLoadedChunks();

        if (loadedChunks.isEmpty()) return;

        List<ChunkPos> chunkList = new ArrayList<>(loadedChunks);
        Collections.shuffle(chunkList);
        int processed = 0;

        for (ChunkPos chunkPos : chunkList) {
            if (processed++ >= MAX_CHUNK_PER_TICK) break;
            for (int i = 0; i < BLOCK_PER_CHUNK; i++) {
                int x = chunkPos.getStartX() + random.nextInt(16);
                int z = chunkPos.getStartZ() + random.nextInt(16);

                BlockPos surfacePos = world.getTopPosition(
                        net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        new BlockPos(x, 0, z)
                );

                for (int dy = 0; dy < Y_RANGE; dy++) {
                    BlockPos targetPos = surfacePos.down(dy);
                    Block block = world.getBlockState(targetPos).getBlock();
                    if (block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.MOSSY_COBBLESTONE)
                        tryGrow(world, targetPos, random);

                    if (block == Blocks.BEDROCK) break;
                }
            }
        }
    }

    public static void tryGrow(ServerWorld world, BlockPos pos, Random random) {
        Block block = world.getBlockState(pos).getBlock();

        if ((block == Blocks.STONE || block == Blocks.COBBLESTONE) && isTouchingWater(world, pos)) {
            if (random.nextFloat() < STONE_TO_MOSSY_CHANCE)
                world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
            return;
        }

        if (block == Blocks.MOSSY_COBBLESTONE && isTouchingWater(world, pos)) {
            if (random.nextFloat() < MOSSY_TO_MOSS_CHANCE)
                world.setBlockState(pos, Blocks.MOSS_BLOCK.getDefaultState());
            return;
        }

        if (block == Blocks.MOSSY_COBBLESTONE)
            if (random.nextFloat() < MOSSY_SPREAD_CHANCE)
                spreadToNeighbors(world, pos, random);
    }

    private static boolean isTouchingWater(ServerWorld world, BlockPos pos) {
        for (Direction dir : Direction.values())
            if (world.getFluidState(pos.offset(dir)).isIn(FluidTags.WATER))
                    return true;
        return false;
    }

    private static void spreadToNeighbors(ServerWorld world, BlockPos pos, Random random) {
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.offset(dir);
            Block neighbor = world.getBlockState(target).getBlock();

            if ((neighbor == Blocks.STONE || neighbor == Blocks.COBBLESTONE)
                    && random.nextFloat() < 0.25f)
                world.setBlockState(target, Blocks.MOSSY_COBBLESTONE.getDefaultState());
        }
    }
}
