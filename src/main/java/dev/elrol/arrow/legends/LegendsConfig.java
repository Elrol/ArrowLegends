package dev.elrol.arrow.legends;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.config._BaseConfig;
import dev.elrol.arrow.legends.data.AncientDNAConfig;
import dev.elrol.arrow.legends.data.PokemonSettings;

import java.util.HashMap;
import java.util.Map;

public class LegendsConfig extends _BaseConfig {

    public static Codec<LegendsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LegendarySettings.CODEC.fieldOf("legendarySettings").forGetter(data -> data.legendarySettings),
            CustomItemSettings.CODEC.fieldOf("customItemSettings").forGetter(data -> data.customItemSettings)
    ).apply(instance, (legendarySettings, customItemSettings) -> {
        LegendsConfig data = new LegendsConfig();
        data.legendarySettings = legendarySettings;
        data.customItemSettings = customItemSettings;
        return data;
    }));

    public LegendarySettings legendarySettings = new LegendarySettings();
    public CustomItemSettings customItemSettings = new CustomItemSettings();

    public LegendsConfig() {
        super("legends");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends _BaseConfig> Codec<T> getCodec() {
        return (Codec<T>) CODEC;
    }


    public static class LegendarySettings {

        public static final Codec<LegendarySettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("globalShinyMultiplier").forGetter(data -> data.globalShinyMultiplier),
                Codec.unboundedMap(Codec.STRING, PokemonSettings.CODEC).fieldOf("specificSettings").forGetter(data -> data.specificSettings)
        ).apply(instance, (globalShinyMultiplier, specificSettings) -> {
            LegendarySettings data = new LegendarySettings();
            data.globalShinyMultiplier = globalShinyMultiplier;
            data.specificSettings = new HashMap<>(specificSettings);
            return data;
        }));

        // 10% by default
        public float globalShinyMultiplier = 1f;

        public Map<String, PokemonSettings> specificSettings = new HashMap<>();

        public PokemonSettings getSettings(String species) {
            PokemonSettings settings;
            if(specificSettings.containsKey(species)) {
                settings = specificSettings.get(species);
            } else {
                settings = new PokemonSettings();
                specificSettings.put(species, settings);
            }
            return settings;
        }

        public int getMinPerfectIVs(String species) {
            return getSettings(species).minPerfectIVs;
        }

        public float getShinyMultiplier(String species) {
            return globalShinyMultiplier * getSettings(species).shinyMultiplier;
        }
    }

    public static class CustomItemSettings {
        public static final Codec<CustomItemSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AncientDNAConfig.CODEC.fieldOf("ancientDNA").forGetter(data -> data.ancientDNA)
        ).apply(instance, (ancientDNA) -> {
            CustomItemSettings data = new CustomItemSettings();
            data.ancientDNA = ancientDNA;
            return data;
        }));

        public AncientDNAConfig ancientDNA = new AncientDNAConfig();
    }
}
