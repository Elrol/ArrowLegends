package dev.elrol.arrow.legends.registries;

import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.legends.ArrowLegends;
import dev.elrol.arrow.legends.properties.MinPerfectIVsProperty;
import dev.elrol.arrow.legends.properties.ShinyChanceProperty;

public class PokemonPropertyRegistry {

    private static int registrations = 0;

    public static void register() {
        if(registrations == 0) {
            CustomPokemonProperty.Companion.register(new MinPerfectIVsProperty());
            CustomPokemonProperty.Companion.register(new ShinyChanceProperty());
        }
        registrations++;
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("Pokemon Properties Registered {} times", registrations);
    }

}
