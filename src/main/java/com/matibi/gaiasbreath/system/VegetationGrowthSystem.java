package com.matibi.gaiasbreath.system;

import com.matibi.gaiasbreath.util.ChunkTracker;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

import java.util.*;
import java.util.function.Predicate;

public class VegetationGrowthSystem {

    private static final int MAX_CHUNK_PER_TICK = 20;
    private static final int BLOCKS_PER_CHUNK = 100;

    private static final float SHORT_GRASS_GROWTH_CHANCE = 0.005f;
    private static final float SHORT_TO_TALL_CHANCE = 0.005f;
    private static final float FLOWER_SPREAD_CHANCE = 0.005f;
    private static final float SAPLING_SPREAD_CHANCE = 0.002f;
    private static final float MUSHROOM_SPREAD_CHANCE = 0.01f;
    private static final float BUSH_SPREAD_CHANCE = 0.01f;

    public static void tick(ServerWorld world) {

        Random random = world.random;
        Set<ChunkPos> loadedChunks = ChunkTracker.getLoadedChunks();
        if (loadedChunks.isEmpty()) return;

        List<ChunkPos> chunkList = new ArrayList<>(loadedChunks);
        Collections.shuffle(chunkList, new java.util.Random(System.nanoTime()));

        int processed = 0;
        for (ChunkPos chunkPos : chunkList) {
            if (processed++ >= MAX_CHUNK_PER_TICK) break;

            for (int i = 0; i < BLOCKS_PER_CHUNK; i++) {
                int x = chunkPos.getStartX() + random.nextInt(16);
                int z = chunkPos.getStartZ() + random.nextInt(16);
                BlockPos pos = world
                        .getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));

                growVegetation(world, pos, random);
            }
        }
    }

    private static void growVegetation(ServerWorld world, BlockPos pos, Random random) {
        BlockPos ground = pos.down();
        Block groundBlock = world.getBlockState(ground).getBlock();

        if (world.getLightLevel(pos.up()) < 8)
            return;

        if (groundBlock == Blocks.GRASS_BLOCK && random.nextFloat() < SHORT_GRASS_GROWTH_CHANCE) {
            world.setBlockState(pos, Blocks.SHORT_GRASS.getDefaultState());
            return;
        }

        if (groundBlock == Blocks.GRASS_BLOCK
                && world.getBlockState(pos).getBlock() == Blocks.SHORT_GRASS
                && random.nextFloat() < SHORT_TO_TALL_CHANCE) {
            BlockPos above = pos.up();
            if (world.isAir(above))
                TallPlantBlock.placeAt(world, Blocks.TALL_GRASS.getDefaultState(), pos, Block.NOTIFY_ALL);
            return;
        }

        if (random.nextFloat() < FLOWER_SPREAD_CHANCE) {
            Optional<Block> nearbyFlower = findNearbyBlock(world, pos, 4,
                    b -> b instanceof FlowerBlock);

            if (nearbyFlower.isPresent() && world.isAir(pos)) {
                Block flower = nearbyFlower.get();

                if (validGround(groundBlock) && flower.getDefaultState().canPlaceAt(world, pos))
                    world.setBlockState(pos, flower.getDefaultState());
                return;
            }
        }

        if (random.nextFloat() < BUSH_SPREAD_CHANCE) {
            Optional<Block> nearbyBush = findNearbyBlock(world, pos, 3,
                    b -> b == Blocks.SWEET_BERRY_BUSH || b == Blocks.BUSH || b == Blocks.FIREFLY_BUSH);

            if (nearbyBush.isPresent() && world.isAir(pos)) {
                Block bush = nearbyBush.get();

                if (validGround(groundBlock) && bush.getDefaultState().canPlaceAt(world, pos))
                    world.setBlockState(pos, bush.getDefaultState());
                return;
            }
        }

        if (random.nextFloat() < SAPLING_SPREAD_CHANCE) {
            Optional<Block> nearbyLog = findNearbyBlock(world, pos, 5,
                    b -> b.getDefaultState().isIn(BlockTags.LOGS));

            if (nearbyLog.isPresent()
                    && groundBlock == Blocks.GRASS_BLOCK
                    && world.isAir(pos)
                    && !world.getBlockState(pos.down()).isAir()) {

                Block sapling = pickSaplingForLog(nearbyLog.get());
                if (sapling != null && sapling.getDefaultState().canPlaceAt(world, pos))
                    world.setBlockState(pos, sapling.getDefaultState());
                return;
            }
        }

        if (random.nextFloat() < MUSHROOM_SPREAD_CHANCE) {
            if ((world.getLightLevel(pos) <= 7 || isUnderTree(world, pos))
                    && world.getBlockState(ground).isFullCube(world, ground)) {

                Optional<Block> nearbyMushroom = findNearbyBlock(world, pos, 4,
                        b -> b == Blocks.RED_MUSHROOM || b == Blocks.BROWN_MUSHROOM);

                if (nearbyMushroom.isPresent() && world.isAir(pos)) {
                    world.setBlockState(pos, nearbyMushroom.get().getDefaultState());
                }
            }
        }
    }

    private static Optional<Block> findNearbyBlock(ServerWorld world, BlockPos center, int radius, Predicate<Block> condition) {
        List<Block> matches = new ArrayList<>();
        for (BlockPos pos : BlockPos.iterateOutwards(center, radius, 1, radius)) {
            Block b = world.getBlockState(pos).getBlock();
            if (condition.test(b)) matches.add(b);
        }
        if (matches.isEmpty()) return Optional.empty();
        return Optional.of(matches.get(world.random.nextInt(matches.size())));
    }

    private static Block pickSaplingForLog(Block log) {
        if (log.getDefaultState().isIn(BlockTags.BIRCH_LOGS)) return Blocks.BIRCH_SAPLING;
        if (log.getDefaultState().isIn(BlockTags.SPRUCE_LOGS)) return Blocks.SPRUCE_SAPLING;
        if (log.getDefaultState().isIn(BlockTags.JUNGLE_LOGS)) return Blocks.JUNGLE_SAPLING;
        if (log.getDefaultState().isIn(BlockTags.ACACIA_LOGS)) return Blocks.ACACIA_SAPLING;
        if (log.getDefaultState().isIn(BlockTags.DARK_OAK_LOGS)) return Blocks.DARK_OAK_SAPLING;
        if (log.getDefaultState().isIn(BlockTags.PALE_OAK_LOGS)) return Blocks.PALE_OAK_SAPLING;
        return Blocks.OAK_SAPLING;
    }

    private static boolean isUnderTree(ServerWorld world, BlockPos pos) {
        for (int dy = 1; dy <= 8; dy++) {
            Block b = world.getBlockState(pos.up(dy)).getBlock();
            if (b instanceof LeavesBlock) return true;
        }
        return false;
    }

    private static boolean validGround(Block groundBlock) {
        return groundBlock == Blocks.GRASS_BLOCK
                || groundBlock == Blocks.PODZOL;
    }
}