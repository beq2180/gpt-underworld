package com.chatty.underworld.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public final class IslandsTerrainFeature extends Feature<DefaultFeatureConfig> {
    public IslandsTerrainFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        int startX = origin.getX() & ~15;
        int startZ = origin.getZ() & ~15;
        long seed = world.getSeed();

        BlockPos.Mutable pos = new BlockPos.Mutable();
        boolean changed = false;

        // Remove the Overworld-style base terrain only where the Islands biome owns
        // the column. Then rebuild it as floating island masses.
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = startX + lx;
                int z = startZ + lz;

                if (!world.getBiome(pos.set(x, 80, z)).matchesKey(ModWorldgen.ISLANDS)) continue;

                for (int y = -63; y <= 319; y++) {
                    pos.set(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (!state.isAir() && !state.isOf(Blocks.BEDROCK)) {
                        setBlockState(world, pos, Blocks.AIR.getDefaultState());
                        changed = true;
                    }
                }

                for (int y = 32; y <= 178; y++) {
                    double density = islandDensity(seed, x, y, z);
                    if (density > 0.0) {
                        long h = mix(seed, x, y, z);
                        BlockState state = (h & 3L) == 0L
                                ? Blocks.DEEPSLATE.getDefaultState()
                                : Blocks.COBBLED_DEEPSLATE.getDefaultState();
                        setBlockState(world, pos.set(x, y, z), state);
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    private static double islandDensity(long seed, int x, int y, int z) {
        double best = -1.0;

        // Large islands, one candidate per 64x64 cell.
        int cellX = Math.floorDiv(x, 64);
        int cellZ = Math.floorDiv(z, 64);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int cx = cellX + dx;
                int cz = cellZ + dz;
                long h = mix(seed ^ 0x61C8864680B583EBL, cx, 0, cz);

                double centerX = cx * 64.0 + 12.0 + positiveUnit(h) * 40.0;
                double centerZ = cz * 64.0 + 12.0 + positiveUnit(h >>> 11) * 40.0;
                double centerY = 74.0 + positiveUnit(h >>> 22) * 54.0;
                double rx = 22.0 + positiveUnit(h >>> 33) * 22.0;
                double rz = 22.0 + positiveUnit(h >>> 44) * 22.0;
                double thickness = 9.0 + positiveUnit(h >>> 52) * 10.0;

                best = Math.max(best, ellipsoidDensity(x, y, z, centerX, centerY, centerZ, rx, rz, thickness));
            }
        }

        // Smaller satellites, more common and more vertical variation.
        int smallX = Math.floorDiv(x, 32);
        int smallZ = Math.floorDiv(z, 32);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int cx = smallX + dx;
                int cz = smallZ + dz;
                long h = mix(seed ^ 0xD1B54A32D192ED03L, cx, 7, cz);

                // Keep only ~58% of small-cell candidates.
                if (positiveUnit(h >>> 5) > 0.58) continue;

                double centerX = cx * 32.0 + 5.0 + positiveUnit(h) * 22.0;
                double centerZ = cz * 32.0 + 5.0 + positiveUnit(h >>> 13) * 22.0;
                double centerY = 58.0 + positiveUnit(h >>> 26) * 92.0;
                double rx = 7.0 + positiveUnit(h >>> 39) * 9.0;
                double rz = 7.0 + positiveUnit(h >>> 48) * 9.0;
                double thickness = 4.0 + positiveUnit(h >>> 56) * 6.0;

                best = Math.max(best, ellipsoidDensity(x, y, z, centerX, centerY, centerZ, rx, rz, thickness));
            }
        }

        return best;
    }

    private static double ellipsoidDensity(
            double x, double y, double z,
            double cx, double cy, double cz,
            double rx, double rz, double thickness
    ) {
        double nx = (x - cx) / rx;
        double nz = (z - cz) / rz;
        double horizontal = nx * nx + nz * nz;
        if (horizontal >= 1.0) return -1.0;

        // Slightly flatter top and tapered underside: End-ish rather than a sphere.
        double verticalRadius = thickness * Math.pow(1.0 - horizontal, 0.62);
        double dy = y - cy;
        double lowerScale = dy < 0.0 ? 1.45 : 0.75;
        double normalizedY = Math.abs(dy) / (verticalRadius * lowerScale + 0.001);

        return 1.0 - normalizedY;
    }

    private static long mix(long seed, int x, int y, int z) {
        long h = seed;
        h ^= (long)x * 0x9E3779B97F4A7C15L;
        h = Long.rotateLeft(h, 27);
        h ^= (long)y * 0xC2B2AE3D27D4EB4FL;
        h = Long.rotateLeft(h, 31);
        h ^= (long)z * 0x165667B19E3779F9L;
        h ^= h >>> 30;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 27;
        h *= 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }

    private static double positiveUnit(long value) {
        return (double)(value & 0xFFFFL) / 65535.0;
    }
}
