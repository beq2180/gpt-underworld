package com.chatty.underworld.worldgen;

import com.chatty.underworld.UnderworldMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;

public final class ModWorldgen {
    public static final RegistryKey<Biome> ASHLANDS =
            RegistryKey.of(RegistryKeys.BIOME, UnderworldMod.id("ashlands"));
    public static final RegistryKey<Biome> TRUNKS =
            RegistryKey.of(RegistryKeys.BIOME, UnderworldMod.id("trunks"));
    public static final RegistryKey<Biome> ISLANDS =
            RegistryKey.of(RegistryKeys.BIOME, UnderworldMod.id("islands"));

    public static final Feature<net.minecraft.world.gen.feature.DefaultFeatureConfig> ASHLANDS_TERRAIN =
            new SurfaceTerrainFeature(ASHLANDS);
    public static final Feature<net.minecraft.world.gen.feature.DefaultFeatureConfig> TRUNKS_TERRAIN =
            new SurfaceTerrainFeature(TRUNKS);
    public static final Feature<net.minecraft.world.gen.feature.DefaultFeatureConfig> ISLANDS_TERRAIN =
            new IslandsTerrainFeature();

    public static final RegistryKey<PlacedFeature> UNDIUM_ORE =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, UnderworldMod.id("undium_ore"));

    public static void initialize() {
        Registry.register(net.minecraft.registry.Registries.FEATURE,
                UnderworldMod.id("ashlands_terrain"), ASHLANDS_TERRAIN);
        Registry.register(net.minecraft.registry.Registries.FEATURE,
                UnderworldMod.id("trunks_terrain"), TRUNKS_TERRAIN);
        Registry.register(net.minecraft.registry.Registries.FEATURE,
                UnderworldMod.id("islands_terrain"), ISLANDS_TERRAIN);

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.NETHER_WASTES, BiomeKeys.BASALT_DELTAS),
                GenerationStep.Feature.UNDERGROUND_ORES,
                UNDIUM_ORE
        );
    }

    private ModWorldgen() {}
}
