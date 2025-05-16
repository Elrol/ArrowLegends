package dev.elrol.arrow.legends;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.events.ArrowEvents;
import dev.elrol.arrow.api.events.FinishBrushingCallback;
import dev.elrol.arrow.api.events.RefreshCallback;
import dev.elrol.arrow.legends.data.AncientDNAConfig;
import dev.elrol.arrow.legends.data.PokemonSettings;
import dev.elrol.arrow.legends.libs.LegendsConstants;
import dev.elrol.arrow.legends.registries.FossilRegistry;
import dev.elrol.arrow.legends.registries.ItemRegistry;
import dev.elrol.arrow.legends.registries.PokemonPropertyRegistry;
import dev.elrol.arrow.libs.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ArrowLegends implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(LegendsConstants.MODID);
    public static LegendsConfig CONFIG = new LegendsConfig();

    static {
        ArrowCore.registerMod(LegendsConstants.MODID);
    }

    @Override
    public void onInitialize() {
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) return;
        CONFIG = CONFIG.load();
        if(CONFIG.legendarySettings.specificSettings.isEmpty()) {
            CONFIG.legendarySettings.specificSettings.put("mew", new PokemonSettings());
            CONFIG.legendarySettings.specificSettings.put("mewtwo", new PokemonSettings());
            CONFIG.legendarySettings.specificSettings.put("ditto", new PokemonSettings());
            CONFIG.save();
        }

        registerEvents();
    }

    private void registerEvents() {
        ArrowLegends.LOGGER.error("Registering Events");
        //PokemonPropertyRegistry.register();

        CobblemonEvents.POKEMON_PROPERTY_INITIALISED.subscribe(Priority.NORMAL, unit -> {
            ArrowLegends.LOGGER.error("esftghbnjlkl: Pokemon Property Initialized");
            PokemonPropertyRegistry.register();

            return unit;
        });

        //ServerLifecycleEvents.SERVER_STARTED.register((server) -> PokemonPropertyRegistry.register());

        ArrowEvents.SERVER_DATA_LOADED_EVENT.register(modServerDataRegistry -> {
            ItemRegistry.register();
            FossilRegistry.register();
            PokemonPropertyRegistry.register();
        });

        RefreshCallback.EVENT.register((server) -> {
            CONFIG = CONFIG.load();
            return ActionResult.PASS;
        });

        FinishBrushingCallback.EVENT.register(player -> {
            AncientDNAConfig ancientDNA = CONFIG.customItemSettings.ancientDNA;
            if(ModUtils.temptFate(ancientDNA.dropChance, 0, 100)) {
                float selected = (new Random()).nextFloat(0, 100);

                Item dna;

                if(selected < ancientDNA.cloneDittoChance) {
                    dna = Registries.ITEM.get(Identifier.of("arrowlegends", "broken_ancient_dna"));
                } else {
                    selected -= ancientDNA.cloneDittoChance;
                    if(selected < ancientDNA.cloneMewTwoChance) {
                        dna = Registries.ITEM.get(Identifier.of("arrowlegends", "dull_ancient_dna"));
                    } else {
                        dna = Registries.ITEM.get(Identifier.of("arrowlegends", "ancient_dna"));
                    }
                }

                player.giveItemStack(new ItemStack(dna, 1));
            }
            return ActionResult.PASS;
        });
    }

}
