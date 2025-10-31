package com.matibi.gaiasbreath.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ChunkTracker {
    private static final Set<ChunkPos> loadedChunks = new HashSet<>();

    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (world.getRegistryKey() == World.OVERWORLD)
                loadedChunks.add(chunk.getPos());
        });

        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            if (world.getRegistryKey() == World.OVERWORLD)
                loadedChunks.remove(chunk.getPos());
        });
    }

    public static Set<ChunkPos> getLoadedChunks() {
        return loadedChunks;
    }
}
