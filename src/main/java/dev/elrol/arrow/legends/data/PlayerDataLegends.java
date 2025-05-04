package dev.elrol.arrow.legends.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.api.data.IPlayerData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class PlayerDataLegends implements IPlayerData {
    public static final Codec<PlayerDataLegends> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("uuid").forGetter(data -> data.uuid.toString())
        ).apply(instance, (uuid) -> {
            PlayerDataLegends data = new PlayerDataLegends(UUID.fromString(uuid));
            return data;
        }));
    }

    public final UUID uuid;

    public PlayerDataLegends(ServerPlayerEntity player) {
        this.uuid = player.getUuid();
    }

    public PlayerDataLegends(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getDataID() {
        return "legends";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IPlayerData> Codec<T> getCodec() {
        return (Codec<T>) CODEC;
    }
}
