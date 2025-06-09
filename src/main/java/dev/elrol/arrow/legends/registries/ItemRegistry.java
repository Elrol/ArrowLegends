package dev.elrol.arrow.legends.registries;

import com.cobblemon.mod.common.CobblemonBlocks;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import dev.elrol.arrow.legends.ArrowLegends;
import dev.elrol.arrow.legends.data.PokemonSettings;
import dev.elrol.arrow.legends.properties.MinPerfectIVsProperty;
import dev.elrol.arrow.legends.properties.ShinyChanceProperty;
import dev.elrol.arrow.libs.ModTranslations;
import dev.elrol.arrow.libs.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {

    public static Item ANCIENT_DNA;
    public static Item DULL_ANCIENT_DNA;
    public static Item BROKEN_ANCIENT_DNA;

    public static Item EMPTY_ORB;
    public static Item FLAME_ORB;
    public static Item FLAME_ORB_FULL;
    public static Item FROST_ORB;
    public static Item FROST_ORB_FULL;
    public static Item SHOCK_ORB;
    public static Item SHOCK_ORB_FULL;

    public static Item SHADOW_ORB;
    public static Item SHADOW_ORB_FULL;

    public static Item AMULET_COIN;

    public static final Map<Item, OrbSettings> orbTypeMap = new HashMap<>();
    public static final Map<Item, Item> orbMap = new HashMap<>();

    public static void register() {
        ANCIENT_DNA = getLegend("ancient_dna");
        DULL_ANCIENT_DNA = getLegend("dull_ancient_dna");
        BROKEN_ANCIENT_DNA = getLegend("broken_ancient_dna");

        EMPTY_ORB = getLegend("empty_orb");
        FLAME_ORB = getLegend("flame_orb");
        FROST_ORB = getLegend("frost_orb");
        SHOCK_ORB = getLegend("shock_orb");
        SHADOW_ORB = getLegend("shadow_orb");
        FLAME_ORB_FULL = getLegend("flame_orb_full");
        FROST_ORB_FULL = getLegend("frost_orb_full");
        SHOCK_ORB_FULL = getLegend("shock_orb_full");
        SHADOW_ORB_FULL = getLegend("shadow_orb_full");

        AMULET_COIN = getLegend("amulet_coin");

        orbTypeMap.put(FLAME_ORB, new OrbSettings(ElementalTypes.INSTANCE.getFIRE(), CobblemonBlocks.FIRE_STONE_BLOCK, PokemonSpecies.INSTANCE.getByName("moltres"), 70, FLAME_ORB_FULL));
        orbTypeMap.put(FROST_ORB, new OrbSettings(ElementalTypes.INSTANCE.getICE(), CobblemonBlocks.ICE_STONE_BLOCK, PokemonSpecies.INSTANCE.getByName("articuno"), 70, FROST_ORB_FULL));
        orbTypeMap.put(SHOCK_ORB, new OrbSettings(ElementalTypes.INSTANCE.getELECTRIC(), CobblemonBlocks.THUNDER_STONE_BLOCK, PokemonSpecies.INSTANCE.getByName("zapdos"), 70, SHOCK_ORB_FULL));
        orbTypeMap.put(SHADOW_ORB, new OrbSettings(ElementalTypes.INSTANCE.getDARK(), null, null, 0, SHADOW_ORB_FULL));

        orbMap.put(FLAME_ORB_FULL, FLAME_ORB);
        orbMap.put(FROST_ORB_FULL, FROST_ORB);
        orbMap.put(SHOCK_ORB_FULL, SHOCK_ORB);
        orbMap.put(SHADOW_ORB_FULL, SHOCK_ORB);
    }

    public static Item getLegend(String id) {
        return get("arrowlegends:" + id);
    }

    public static Item get(String id) {
        return Registries.ITEM.get(Identifier.of(id));
    }

    public static boolean isOfTag(ItemStack stack, Identifier tag) {
        TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, tag);
        return stack.isIn(tagKey);
    }

    public static boolean isOfTag(ItemStack stack, String namespace, String tag) {
        return isOfTag(stack, Identifier.of(namespace, tag));
    }

    public static ItemStack increaseOrbCount(ItemStack orb, int amount) {
        LoreComponent loreComponent = orb.get(DataComponentTypes.LORE);
        boolean hasLoreA = loreComponent != null;
        boolean hasLore = hasLoreA && !loreComponent.lines().isEmpty();

        if(orb.getDamage() <= 0) {
            if (hasLore) {
                return orb;
            } else {
                orb.setDamage(orb.getMaxDamage());
            }
        }
        List<Text> lore = new ArrayList<>();
        int progress = Math.clamp((100 - orb.getDamage()) + amount, 0, orb.getMaxDamage());

        orb.setDamage(100-progress);
        lore.add(
                ModTranslations.literal("[").formatted(Formatting.GRAY, Formatting.BOLD)
                        .append(Text.literal(progress + "/100")
                                .formatted(getPercentColoring(((float)progress)/100.0f), Formatting.BOLD)
                        .append(Text.literal("]")
                                .formatted(Formatting.GRAY, Formatting.BOLD))));
        orb.set(DataComponentTypes.LORE, new LoreComponent(lore));
        return orb;
    }

    public static Formatting getPercentColoring(float percent) {
        if(percent >= 1.0f) return Formatting.AQUA;
        if(percent >= 0.7f) return Formatting.GREEN;
        if(percent >= 0.5f) return Formatting.YELLOW;
        if(percent >= 0.3f) return Formatting.GOLD;
        if(percent >= 0.1f) return Formatting.RED;
        return Formatting.DARK_RED;
    }

    public static int getOrbFillLevel(ItemStack orb) {
        LoreComponent loreComponent = orb.get(DataComponentTypes.LORE);
        if(loreComponent == null) return -1;

        List<Text> lore = loreComponent.lines();
        if(lore.isEmpty()) return -1;
        Text text = lore.getFirst();
        return Integer.parseInt(text.getString().substring(1).replace("/100]", ""));
    }

    public static boolean isOrbFull(ItemStack orb) {
        boolean isDurabilityFull = !orb.isDamaged();
        boolean isOrbFull = getOrbFillLevel(orb) >= 100;

        return isDurabilityFull && isOrbFull;
    }

    public static class OrbSettings {

        public final ElementalType type;
        public final Block beaconBlock;
        public final Species species;
        public final int level;
        public final Item fullOrb;

        public OrbSettings(ElementalType type, Block beaconBlock, Species species, int level, Item fullOrb) {
            this.type = type;
            this.beaconBlock = beaconBlock;
            this.species = species;
            this.level = level;
            this.fullOrb = fullOrb;
        }

        public PokemonProperties getPokemon() {
            String name = species.getName();
            PokemonProperties properties = new PokemonProperties();
            PokemonSettings settings = ArrowLegends.CONFIG.legendarySettings.getSettings(name.toLowerCase());
            boolean isShiny = ModUtils.temptFate(0.1f * ArrowLegends.CONFIG.legendarySettings.getShinyMultiplier(name), 0.0f, 1.0f);

            properties.setSpecies(name);
            properties.setIvs(IVs.createRandomIVs(settings.minPerfectIVs));
            properties.setLevel(level);
            properties.setShiny(isShiny);
            return properties;
        }

    }
}
