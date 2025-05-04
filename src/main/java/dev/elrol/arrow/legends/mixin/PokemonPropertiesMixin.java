package dev.elrol.arrow.legends.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.legends.ArrowLegends;
import dev.elrol.arrow.legends.properties.MinPerfectIVsProperty;
import dev.elrol.arrow.legends.properties.ShinyChanceProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = PokemonProperties.class, remap = false)
public abstract class PokemonPropertiesMixin {
    @Unique
    private CustomPokemonProperty shinyChanceProperty;
    @Unique
    private CustomPokemonProperty minPerfectIVProperty;

    @Inject(
            method = "setCustomProperties",
            at = @At(
                    value = "TAIL"
            )
    )
    public void arrowLegends$getAndRemoveShinyChance(List<CustomPokemonProperty> properties, CallbackInfo ci) {
        properties.removeIf((prop) -> {
            if (prop.asString().startsWith(ShinyChanceProperty.key)) {
                if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Property: {}", prop.asString());
                this.shinyChanceProperty = prop;
                return true;
            } else if(prop.asString().startsWith(MinPerfectIVsProperty.key)) {
                if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Property: {}", prop.asString());
                this.minPerfectIVProperty = prop;
                return true;
            } else {
                return false;
            }
        });
    }

    @Inject(
            method = "roll(Lcom/cobblemon/mod/common/pokemon/Pokemon;)V",
            at = @At(
                    value = "TAIL"
            )

    )
    public void test(Pokemon pokemon, CallbackInfo ci) {
        if (this.shinyChanceProperty != null) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Applying ShinyChance Property in Roll");
            this.shinyChanceProperty.apply(pokemon);
        }
        if(this.minPerfectIVProperty != null) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Applying MinPerfectIVs Property in Roll");
            this.minPerfectIVProperty.apply(pokemon);
        }
    }

    @Inject(
            method = "apply(Lcom/cobblemon/mod/common/pokemon/Pokemon;)V",
            at = @At(
                    value = "TAIL"
            )
    )
    public void forceCustomPropertiesLast(Pokemon pokemon, CallbackInfo ci) {
        if (this.shinyChanceProperty != null) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Applying ShinyChance Property 1");
            this.shinyChanceProperty.apply(pokemon);
        }
        if(this.minPerfectIVProperty != null) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Applying MinPerfectIVs Property 1");
            this.minPerfectIVProperty.apply(pokemon);
        }
    }

    @Inject(
            method = "apply(Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;)V",
            at = @At(
                    value = "TAIL"
            )
    )
    public void forceCustomPropertiesLast(PokemonEntity pokemon, CallbackInfo ci) {
        if (this.shinyChanceProperty != null) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Applying ShinyChance Property");
            this.shinyChanceProperty.apply(pokemon);
        }
        if(this.minPerfectIVProperty != null) {
            if(ArrowCore.CONFIG.isDebug) ArrowLegends.LOGGER.warn("Applying MinPerfectIVs Property");
            this.minPerfectIVProperty.apply(pokemon);
        }
    }

    @Inject(
            method = "matches(Lcom/cobblemon/mod/common/pokemon/Pokemon;)Z",
            at = @At(
                    value = "TAIL"
            ),
            cancellable = true
    )
    public void matchesProperties(Pokemon pokemon, CallbackInfoReturnable<Boolean> cir) {
        boolean isShiny = shinyChanceProperty == null || shinyChanceProperty.matches(pokemon);
        boolean minIVs = minPerfectIVProperty == null || minPerfectIVProperty.matches(pokemon);

        if (isShiny && minIVs) {
            //cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "matches(Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;)Z",
            at = @At(
                    value = "TAIL"
            ),
            cancellable = true
    )
    public void neoDaycare$matchesEggEntity(PokemonEntity pokemon, CallbackInfoReturnable<Boolean> cir) {
        boolean isShiny = shinyChanceProperty == null || shinyChanceProperty.matches(pokemon);
        boolean minIVs = minPerfectIVProperty == null || minPerfectIVProperty.matches(pokemon);

        if (isShiny && minIVs) {
            //cir.setReturnValue(true);
        }
    }
}
