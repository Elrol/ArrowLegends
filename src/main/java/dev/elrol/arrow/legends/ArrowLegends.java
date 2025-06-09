package dev.elrol.arrow.legends;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
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
import dev.elrol.arrow.libs.ModTranslations;
import dev.elrol.arrow.libs.ModUtils;
import kotlin.Unit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            CONFIG.legendarySettings.specificSettings.put("moltres", new PokemonSettings());
            CONFIG.legendarySettings.specificSettings.put("articuno", new PokemonSettings());
            CONFIG.legendarySettings.specificSettings.put("zapdos", new PokemonSettings());

            CONFIG.save();
        }

        registerEvents();
    }

    private void registerEvents() {
        ArrowLegends.LOGGER.error("Registering Events");
        //PokemonPropertyRegistry.register();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            ItemStack handItem = player.getStackInHand(hand);

            boolean isBeacon = state.isOf(Blocks.BEACON);
            boolean isHoldingOrb = handItem.isOf(ItemRegistry.SHOCK_ORB_FULL) || handItem.isOf(ItemRegistry.FLAME_ORB_FULL) || handItem.isOf(ItemRegistry.FROST_ORB_FULL);
            boolean isCrouching = player.isSneaking();

            Item unfilledOrb = ItemRegistry.orbMap.get(handItem.getItem());
            if(unfilledOrb != null) {
                ItemRegistry.OrbSettings settings = ItemRegistry.orbTypeMap.get(unfilledOrb);

                if(settings == null) return ActionResult.PASS;

                boolean isValidBeacon = true;

                for(int x = -1; x <= 1; x++) {
                    for(int z = -1; z <= 1; z++) {
                        BlockState baseState = world.getBlockState(pos.add(x, -1, z));
                        if(!baseState.isOf(settings.beaconBlock)) {
                            isValidBeacon = false;
                            break;
                        }
                    }
                }

                if(isBeacon && isHoldingOrb && isValidBeacon && isCrouching) {
                    PokemonEntity pokemon = settings.getPokemon().createEntity(world);
                    pokemon.setPosition(Vec3d.of(pos));
                    world.spawnEntity(pokemon);
                    player.setStackInHand(hand, ItemStack.EMPTY);
                    world.removeBlock(pos, true);
                    for(int x = -1; x <= 1; x++) {
                        for(int z = -1; z <= 1; z++) {
                            world.removeBlock(pos.add(x, -1, z), true);
                        }
                    }
                }
            }

            return ActionResult.PASS;
        });

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, (event) -> {
            Map<ElementalType, Integer> typeMap = new HashMap<>();

            if(!event.getBattle().isPvP()) {
                List<ServerPlayerEntity> winningPlayers = new ArrayList<>();
                for (BattleActor winner : event.getWinners()) {
                    if(winner instanceof PlayerBattleActor player) {
                        if(player.getEntity() == null) break;
                        ItemStack stack = player.getEntity().getOffHandStack();
                        boolean isOrb = ItemRegistry.isOfTag(stack, "arrowlegends", "orbs");
                        if(isOrb) winningPlayers.add(player.getEntity());
                    }
                }
                if(winningPlayers.isEmpty()) return Unit.INSTANCE;

                List<PokemonBattleActor> pokemonDefeated = new ArrayList<>();
                for (BattleActor loser : event.getLosers()) {
                    if(loser instanceof PokemonBattleActor pokemonBattleActor) pokemonDefeated.add(pokemonBattleActor);
                }
                for (PokemonBattleActor pokemonActor : pokemonDefeated) {
                    for (ElementalType type : pokemonActor.getPokemon().getOriginalPokemon().getTypes()) {
                        int elementAmount = typeMap.getOrDefault(type, 0);
                        elementAmount++;
                        typeMap.put(type, elementAmount);
                    }
                }
                winningPlayers.forEach(player -> {
                    ItemStack offhand = player.getOffHandStack();
                    if(offhand.isEmpty()) return;
                    if(!ItemRegistry.orbTypeMap.containsKey(offhand.getItem())) return;
                    ItemRegistry.OrbSettings settings = ItemRegistry.orbTypeMap.get(offhand.getItem());
                    ElementalType type = settings.type;

                    if(type != null) {
                        int numberDefeated = typeMap.getOrDefault(type, 0);
                        ItemStack orb = ItemRegistry.increaseOrbCount(offhand, numberDefeated * (ArrowCore.CONFIG.isDebug ? 25 : 1));

                        if(orb.getDamage() == 0) {
                            if(orb.isOf(ItemRegistry.SHADOW_ORB)) {
                                player.sendMessage(ModTranslations.msg("shadow_orb_full"));
                            } else {
                                player.sendMessage(ModTranslations.msg("orb_full", settings.beaconBlock.getName().getString()));
                            }
                            player.getInventory().setStack(PlayerInventory.OFF_HAND_SLOT, orb.withItem(settings.fullOrb));

                        } else {
                            String percent = (orb.getMaxDamage() - orb.getDamage()) + "%";
                            player.sendMessage(ModTranslations.msg("orb_filled", percent));
                        }
                    }
                });
            }

            return Unit.INSTANCE;
        });



        CobblemonEvents.POKEMON_PROPERTY_INITIALISED.subscribe(Priority.NORMAL, unit -> {
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
