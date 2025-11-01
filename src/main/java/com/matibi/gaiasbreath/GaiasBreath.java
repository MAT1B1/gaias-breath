package com.matibi.gaiasbreath;

import com.matibi.gaiasbreath.client.modmenu.config.GaiasBreathConfig;
import com.matibi.gaiasbreath.event.WorldEvolutionHandler;
import com.matibi.gaiasbreath.util.ChunkTracker;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GaiasBreath implements ModInitializer {
	public static final String MOD_ID = "gaias-breath";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static GaiasBreathConfig CONFIG;

	@Override
	public void onInitialize() {
        CONFIG = GaiasBreathConfig.load();
        ChunkTracker.register();
        WorldEvolutionHandler.register();

	}
}