package com.matibi.gaiasbreath.event;

import com.matibi.gaiasbreath.GaiasBreath;
import com.matibi.gaiasbreath.system.MossGrowthSystem;
import com.matibi.gaiasbreath.system.SoilWearSystem;
import com.matibi.gaiasbreath.system.VegetationGrowthSystem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class WorldEvolutionHandler {

    private static int tickCounter = 0;
    private static int rouletteIndex = 0;

    private static final int TICK_INTERVAL = 10;

    private static final List<Consumer<ServerWorld>> SYSTEMS = List.of(
            MossGrowthSystem::tick,
            SoilWearSystem::tick,
            VegetationGrowthSystem::tick
    );

    public static void register() {
        GaiasBreath.LOGGER.info("Registering WorldEvolutionHandler for " + GaiasBreath.MOD_ID);
        ServerTickEvents.END_WORLD_TICK.register(WorldEvolutionHandler::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if (world.getRegistryKey() != World.OVERWORLD) return;

        tickCounter++;

        if (tickCounter % TICK_INTERVAL == 0) {
            try {
                SYSTEMS.get(rouletteIndex).accept(world);
            } catch (Exception e) {
                GaiasBreath.LOGGER.error("Error during world evolution system #{}", rouletteIndex, e);
            }
            rouletteIndex = (rouletteIndex + 1) % SYSTEMS.size();
        }
    }
}