package com.chatty.underworld.portal;

import com.chatty.underworld.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public record PortalShape(BlockPos lowerLeft, int width, int height, Direction.Axis axis) {
    private static final int MIN_WIDTH = 2;
    private static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    private static final int MAX_HEIGHT = 21;

    public static Optional<PortalShape> find(World world, BlockPos around) {
        Optional<PortalShape> x = find(world, around, Direction.Axis.X);
        if (x.isPresent()) return x;
        return find(world, around, Direction.Axis.Z);
    }

    private static Optional<PortalShape> find(World world, BlockPos around, Direction.Axis axis) {
        Direction positive = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction negative = positive.getOpposite();

        BlockPos cursor = around;
        for (int i = 0; i < MAX_HEIGHT + 2 && isInterior(world.getBlockState(cursor.down())); i++) {
            cursor = cursor.down();
        }

        // Walk to the negative-side frame.
        BlockPos p = cursor;
        int neg = 0;
        while (neg <= MAX_WIDTH && isInterior(world.getBlockState(p.offset(negative)))) {
            p = p.offset(negative);
            neg++;
        }

        BlockPos negativeFrame = p.offset(negative);
        if (!world.getBlockState(negativeFrame).isOf(ModBlocks.UNDIUM)) return Optional.empty();

        BlockPos lowerLeft = negativeFrame.offset(positive);

        int width = 0;
        while (width <= MAX_WIDTH && isInterior(world.getBlockState(lowerLeft.offset(positive, width)))) {
            width++;
        }

        if (width < MIN_WIDTH || width > MAX_WIDTH) return Optional.empty();
        BlockPos positiveFrame = lowerLeft.offset(positive, width);
        if (!world.getBlockState(positiveFrame).isOf(ModBlocks.UNDIUM)) return Optional.empty();

        // Bottom frame, including corners.
        for (int i = -1; i <= width; i++) {
            if (!world.getBlockState(lowerLeft.offset(positive, i).down()).isOf(ModBlocks.UNDIUM)) {
                return Optional.empty();
            }
        }

        int height = -1;
        for (int y = 0; y <= MAX_HEIGHT; y++) {
            if (!world.getBlockState(lowerLeft.up(y).offset(negative)).isOf(ModBlocks.UNDIUM)
                    || !world.getBlockState(lowerLeft.up(y).offset(positive, width)).isOf(ModBlocks.UNDIUM)) {
                return Optional.empty();
            }

            boolean wholeRowFrame = true;
            for (int x = 0; x < width; x++) {
                BlockState state = world.getBlockState(lowerLeft.up(y).offset(positive, x));
                if (!state.isOf(ModBlocks.UNDIUM)) {
                    wholeRowFrame = false;
                    if (!isInterior(state)) return Optional.empty();
                }
            }

            if (wholeRowFrame) {
                height = y;
                break;
            }
        }

        if (height < MIN_HEIGHT || height > MAX_HEIGHT) return Optional.empty();
        return Optional.of(new PortalShape(lowerLeft, width, height, axis));
    }

    private static boolean isInterior(BlockState state) {
        return state.isAir()
                || state.isOf(Blocks.FIRE)
                || state.isOf(Blocks.SOUL_FIRE)
                || state.isOf(ModBlocks.UNDERWORLD_PORTAL);
    }

    public void fill(World world) {
        Direction dir = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        BlockState portal = ModBlocks.UNDERWORLD_PORTAL.getDefaultState()
                .with(UnderworldPortalBlock.AXIS, axis);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                world.setBlockState(lowerLeft.up(y).offset(dir, x), portal, Block.NOTIFY_ALL);
            }
        }
    }
}
