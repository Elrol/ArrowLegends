package dev.elrol.arrow.legends.properties;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.IntProperty;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.legends.ArrowLegends;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MinPerfectIVsProperty implements CustomPokemonPropertyType<IntProperty> {

    public static final String key = "min_perfect_ivs";

    public MinPerfectIVsProperty() {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("Property Created");
    }

    IntProperty minPerfectIVs(int min) {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Creating new MinPerfectIVs instance");
        return new IntProperty(key, min,
                (pokemon, integer) -> {
                    if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Applying minPerfectIVs to pokemon");
                    if(integer > 0 && !hasEnoughIVs(pokemon, integer)) {
                        IVs ivs = IVs.createRandomIVs(integer);
                        ivs.forEach((entry) -> pokemon.setIV(entry.getKey(), entry.getValue()));
                    }
                    return Unit.INSTANCE;
                },
                (pokemonEntity, integer) -> {
                    if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Applying minPerfectIVs to pokemon entity");
                    Pokemon pokemon = pokemonEntity.getPokemon();
                    if(integer > 0 && !hasEnoughIVs(pokemon, integer)) {
                        IVs ivs = IVs.createRandomIVs(integer);
                        ivs.forEach((entry) -> pokemon.setIV(entry.getKey(), entry.getValue()));
                        pokemonEntity.setPokemon(pokemon);
                    }
                    return Unit.INSTANCE;
                },
                this::hasEnoughIVs,
                (pokemonEntity, integer) -> hasEnoughIVs(pokemonEntity.getPokemon(), integer));
    }

    private boolean hasEnoughIVs(Pokemon pokemon, int min) {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Checking if pokemon has enough perfect IVs");
        IVs ivs = pokemon.getIvs();
        AtomicInteger total = new AtomicInteger();
        Stats.Companion.getPERMANENT().forEach(stat -> {
            Integer iv = ivs.get(stat);
            if(iv != null && iv >= 31) total.addAndGet(1);
        });
        return total.get() >= min;
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
        return List.of("min_perfect_ivs=1", "min_perfect_ivs=0", "min_perfect_ivs=6");
    }

    @Override
    public @Nullable IntProperty fromString(@Nullable String s) {
        if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Parsing MinPerfectIVs from string: {}", s);
        int min = 0;
        if(s != null && !s.isEmpty()) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: String was not null or empty");
            try {
                min = Integer.parseInt(s);
                if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: Parsed string into int");
            } catch (NumberFormatException ignored) {
                if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: String couldn't be parsed to int");
            }
        } else {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.error("esftghbnjlkl: String was null or empty");
        }
        return minPerfectIVs(min);
    }
}
