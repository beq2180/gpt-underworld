package com.chatty.underworld.worldgen;

import com.chatty.underworld.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public final class SurfaceTerrainFeature extends Feature<DefaultFeatureConfig> {
    private final RegistryKey<Biome> biomeKey;

    public SurfaceTerrainFeature(RegistryKey<Biome> biomeKey) {
        super(DefaultFeatureConfig.CODEC);
        this.biomeKey = biomeKey;
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        int startX = origin.getX() & ~15;
        int startZ = origin.getZ() & ~15;
        boolean changed = false;

        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = startX + localX;
                int z = startZ + localZ;

                int sampleY = Math.max(0, Math.min(96,
                        world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z) - 1));
                if (!world.getBiome(pos.set(x, sampleY, z)).matchesKey(biomeKey)) continue;

                int top = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z) - 1;
                if (top < -63) continue;

                int topSolid = Integer.MIN_VALUE;

                for (int y = -63; y <= top; y++) {
                    pos.set(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.isOf(Blocks.BEDROCK)) continue;

                    if (!state.getFluidState().isEmpty()) {
                        setBlockState(world, pos, Blocks.AIR.getDefaultState());
                        changed = true;
                        continue;
                    }

                    if (!state.isAir()) {
                        setBlockState(world, pos, Blocks.COBBLED_DEEPSLATE.getDefaultState());
                        topSolid = y;
                        changed = true;
                    }
                }

                if (topSolid != Integer.MIN_VALUE) {
                    pos.set(x, topSolid, z);
                    setBlockState(world, pos, ModBlocks.GROIL.getDefaultState());
                }
            }
        }

        return changed;
    }
}
