package dev.elrol.arrow.legends.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PokemonSettings {

    public static final Codec<PokemonSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("shinyMultiplier").forGetter(data -> data.shinyMultiplier),
            Codec.INT.fieldOf("minPerfectIVs").forGetter(data -> data.minPerfectIVs)
    ).apply(instance, (shinyMultiplier, minPerfectIVs) -> {
        PokemonSettings data = new PokemonSettings();
        data.shinyMultiplier = shinyMultiplier;
        data.minPerfectIVs = minPerfectIVs;
        return data;
    }));

    public float shinyMultiplier = 1.0f;
    public int minPerfectIVs = 0;

}
