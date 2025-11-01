package com.matibi.gaiasbreath.system;

import com.matibi.gaiasbreath.GaiasBreath;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SoilWearSystem {

    private static final Map<Long, Boolean> blockedMap = new HashMap<>();
    private static final Map<UUID, BlockPos> lastPlayerPositions = new HashMap<>();
    private static long tickCounter = 0;

    public static void tick(ServerWorld world) {
        tickCounter++;
        var cfg = GaiasBreath.CONFIG;

        MinecraftServer server = world.getServer();
        SoilWearData data = SoilWearData.getServerState(server);
        if (data == null) return;
        Map<Long, Integer> wearMap = data.getWearData();

        for (PlayerEntity player : world.getPlayers()) {
            BlockPos posUnder = player.getBlockPos().down();
            BlockPos lastPos = lastPlayerPositions.get(player.getUuid());

            long key = posUnder.asLong();

            if (lastPos != null && lastPos.equals(posUnder)) {
                blockedMap.put(key, true);
                continue;
            }

            lastPlayerPositions.put(player.getUuid(), posUnder.toImmutable());
            Block block = world.getBlockState(posUnder).getBlock();

            if (block == Blocks.GRASS_BLOCK || block == Blocks.COARSE_DIRT || block == Blocks.DIRT) {
                blockedMap.put(key, true);
                wearMap.merge(key, 1, Integer::sum);

                int count = wearMap.get(key);
                if (block == Blocks.GRASS_BLOCK && count >= cfg.STEP_TO_COARSE)
                    world.setBlockState(posUnder, Blocks.COARSE_DIRT.getDefaultState());
                else if (block == Blocks.COARSE_DIRT && count >= cfg.STEP_TO_PATH)
                    world.setBlockState(posUnder, Blocks.DIRT_PATH.getDefaultState());
            }
        }

        if (tickCounter % cfg.DECAY_INTERVAL != 0) {
            data.markDirty();
            return;
        }

        Iterator<Map.Entry<Long, Integer>> iterator = wearMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            long key = entry.getKey();

            if (blockedMap.getOrDefault(key, false)) continue;

            BlockPos pos = BlockPos.fromLong(key);
            Block block = world.getBlockState(pos).getBlock();
            int count = Math.max(0, entry.getValue() - cfg.RECOVERY_RATE);
            entry.setValue(count);

            if (block == Blocks.DIRT_PATH && count <= cfg.STEP_TO_COARSE)
                world.setBlockState(pos, Blocks.COARSE_DIRT.getDefaultState());
            else if (block == Blocks.COARSE_DIRT && count == 0) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                iterator.remove();
            } else if (block != Blocks.DIRT_PATH && block != Blocks.COARSE_DIRT)
                iterator.remove();
        }

        blockedMap.clear();
        data.markDirty();
    }
}