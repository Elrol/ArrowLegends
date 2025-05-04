package dev.elrol.arrow.legends.properties;

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.FloatProperty;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.legends.ArrowLegends;
import dev.elrol.arrow.libs.ModUtils;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ShinyChanceProperty implements CustomPokemonPropertyType<FloatProperty> {

    public static final String key = "shiny_chance";

    public ShinyChanceProperty() {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("Property Created");
    }

    FloatProperty shinyChance(float min) {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Creating new ShinyChance instance");
        return new FloatProperty(key, min,
                (pokemon, chance) -> {
                    if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Applying ShinyChance to pokemon");
                    if(chance > 0 && !pokemon.getShiny()) {
                        float mult = ArrowLegends.CONFIG.legendarySettings.getShinyMultiplier(pokemon.getSpecies().getName());
                        pokemon.setShiny(ModUtils.temptFate(chance * mult, 0, 1));
                    }
                    return Unit.INSTANCE;
                },
                (pokemonEntity, chance) -> {
                    if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Applying ShinyChance to pokemon entity");
                    Pokemon pokemon = pokemonEntity.getPokemon();
                    if(chance > 0 && !pokemon.getShiny()) {
                        float mult = ArrowLegends.CONFIG.legendarySettings.getShinyMultiplier(pokemon.getSpecies().getName());
                        pokemon.setShiny(ModUtils.temptFate(chance * mult, 0, 1));
                    }
                    return Unit.INSTANCE;
                },
                (pokemon, chance) -> pokemon.getShiny(),
                (pokemonEntity, integer) -> pokemonEntity.getPokemon().getShiny());
    }

    @Override
    public @NotNull Iterable<String> getKeys() {
        return Set.of(key);
    }

    @Override
    public boolean getNeedsKey() {
        return true;
    }

    @Override
    public @NotNull Collection<String> examples() {
        return List.of("shiny_chance=0", "shiny_chance=0.0001220703125", "shiny_chance=0.1");
    }

    @Override
    public @Nullable FloatProperty fromString(@Nullable String s) {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Parsing ShinyChance from string: {}", s);
        float chance = 0;
        if(s != null && !s.isEmpty()) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: String was not null or empty");
            try {
                chance = Float.parseFloat(s);
                if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Parsed string into float");
            } catch (NumberFormatException ignored) {
                if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: String couldn't be parsed to float");
            }
        } else {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: String was null or empty");
        }
        return shinyChance(chance);
    }
}
