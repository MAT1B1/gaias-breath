package com.matibi.gaiasbreath.client.modmenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matibi.gaiasbreath.GaiasBreath;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class GaiasBreathConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("gaiasbreath.json");

    // === Config variables ===
    // World Growth
    public int GROWTH_MAX_CHUNK_PER_TICK = 20;
    public int GROWTH_BLOCKS_PER_CHUNK = 100;
    public float SHORT_GRASS_GROWTH_CHANCE = 0.005f;
    public float SHORT_TO_TALL_CHANCE = 0.005f;
    public float FLOWER_SPREAD_CHANCE = 0.005f;
    public float SAPLING_SPREAD_CHANCE = 0.002f;
    public float MUSHROOM_SPREAD_CHANCE = 0.01f;
    public float BUSH_SPREAD_CHANCE = 0.01f;
    public float RAIN_GROWTH_CHANCE = 0.25f;

    // Moss Generation
    public float STONE_TO_MOSSY_CHANCE = 0.02f;
    public float MOSSY_SPREAD_CHANCE = 0.01f;
    public float MOSSY_TO_MOSS_CHANCE = 0.01f;
    public int MOSS_MAX_CHUNK_PER_TICK = 10;
    public int MOSS_BLOCKS_PER_CHUNK = 10;
    public int Y_RANGE = 20;

    // Path Generation
    public int STEP_TO_COARSE = 30;
    public int STEP_TO_PATH   = 80;
    public int RECOVERY_RATE  = 1;
    public int DECAY_INTERVAL = 20 * 60;

    // Misc
    public float CHARCOAL_DROP_CHANCE = 0.5f;

    // === Load & save ===
    public static GaiasBreathConfig load() {
        try {
            if (Files.exists(FILE)) {
                try (FileReader reader = new FileReader(FILE.toFile())) {
                    return GSON.fromJson(reader, GaiasBreathConfig.class);
                }
            } else {
                GaiasBreathConfig config = new GaiasBreathConfig();
                config.save();
                return config;
            }
        } catch (Exception e) {
            GaiasBreath.LOGGER.error("Error: ", e);
            return new GaiasBreathConfig();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(FILE.toFile())) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            GaiasBreath.LOGGER.error("Error: ", e);
        }
    }
}