package com.matibi.gaiasbreath.client.modmenu;

import com.matibi.gaiasbreath.client.modmenu.config.GaiasBreathConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GaiasBreathModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GaiasBreathConfigScreen::new;
    }
}
