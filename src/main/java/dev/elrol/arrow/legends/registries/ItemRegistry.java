package dev.elrol.arrow.legends.registries;

import com.cobblemon.mod.common.api.fossil.Fossil;
import com.cobblemon.mod.common.pokemon.evolution.predicate.NbtItemPredicate;
import dev.elrol.arrow.legends.ArrowLegends;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class ItemRegistry {

    public static Item ANCIENT_DNA;
    public static Item DULL_ANCIENT_DNA;
    public static Item BROKEN_ANCIENT_DNA;

    public static Item EMPTY_ORB;
    public static Item FLAME_ORB;
    public static Item FROST_ORB;
    public static Item SHOCK_ORB;
    public static Item SHADOW_ORB;

    public static Item AMULET_COIN;



    public static void register() {
        ANCIENT_DNA = getLegend("ancient_dna");
        DULL_ANCIENT_DNA = getLegend("dull_ancient_dna");
        BROKEN_ANCIENT_DNA = getLegend("broken_ancient_dna");

        EMPTY_ORB = getLegend("empty_orb");
        FLAME_ORB = getLegend("flame_orb");
        FROST_ORB = getLegend("frost_orb");
        SHOCK_ORB = getLegend("shock_orb");
        SHADOW_ORB = getLegend("shadow_orb");

        AMULET_COIN = getLegend("amulet_coin");
    }

    public static Item getLegend(String id) {
        return get("arrowlegends:" + id);
    }

    public static Item get(String id) {
        return Registries.ITEM.get(Identifier.of(id));
    }

}
