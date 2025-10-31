package com.matibi.gaiasbreath.system;

import com.matibi.gaiasbreath.GaiasBreath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.datafixer.DataFixTypes;

import java.util.HashMap;
import java.util.Map;

public class SoilWearData extends PersistentState {

    private final Map<Long, Integer> wearData = new HashMap<>();

    public static final Codec<SoilWearData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT)
                            .xmap(
                                    // String -> Long (lecture)
                                    map -> {
                                        Map<Long, Integer> converted = new HashMap<>();
                                        for (var e : map.entrySet()) {
                                            try {
                                                converted.put(Long.parseLong(e.getKey()), e.getValue());
                                            } catch (NumberFormatException ignored) {}
                                        }
                                        return converted;
                                    },
                                    // Long -> String (écriture)
                                    map -> {
                                        Map<String, Integer> converted = new HashMap<>();
                                        for (var e : map.entrySet()) {
                                            converted.put(Long.toString(e.getKey()), e.getValue());
                                        }
                                        return converted;
                                    }
                            )
                            .fieldOf("wearData")
                            .forGetter(SoilWearData::getWearData)
            ).apply(instance, SoilWearData::new)
    );

    public static final PersistentStateType<SoilWearData> TYPE = new PersistentStateType<>(
            GaiasBreath.MOD_ID + ".soil_wear",
            SoilWearData::new,
            context -> CODEC,
            DataFixTypes.LEVEL
    );

    public SoilWearData(Map<Long, Integer> data) {
        this.wearData.putAll(data);
    }

    public SoilWearData(Context context) {

    }

    public Map<Long, Integer> getWearData() {
        return wearData;
    }

    public static SoilWearData getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) return null;

        PersistentStateManager manager = world.getPersistentStateManager();
        SoilWearData state = manager.getOrCreate(TYPE);
        state.markDirty();
        return state;
    }
}
