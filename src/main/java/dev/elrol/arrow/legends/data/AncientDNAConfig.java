package dev.elrol.arrow.legends.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AncientDNAConfig {

    public static final Codec<AncientDNAConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("dropChance").forGetter(data -> data.dropChance),
            Codec.FLOAT.fieldOf("cloneDittoChance").forGetter(data -> data.cloneDittoChance),
            Codec.FLOAT.fieldOf("cloneMewTwoChance").forGetter(data -> data.cloneMewTwoChance)
        ).apply(instance, (dropChance, dittoChance, mewTwoChance) -> {
            AncientDNAConfig data = new AncientDNAConfig();

            data.dropChance = dropChance;
            data.cloneDittoChance = dittoChance;
            data.cloneMewTwoChance = mewTwoChance;

            return data;
        }));

    public float dropChance = 0.5f;
    public float cloneDittoChance = 75.0f;
    public float cloneMewTwoChance = 20.0f;

}
