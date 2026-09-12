package com.chatty.underworld.portal;

import java.util.ArrayList;
import com.chatty.underworld.ModBlocks;
import com.chatty.underworld.UnderworldMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Set;

public final class PortalManager {
    public static final RegistryKey<World> UNDERWORLD_WORLD =
            RegistryKey.of(RegistryKeys.WORLD, UnderworldMod.id("the_underworld"));

    public static void initialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.getStackInHand(hand).isOf(Items.FLINT_AND_STEEL)) {
                return ActionResult.PASS;
            }

            if (!world.getRegistryKey().equals(World.OVERWORLD)) {
                return ActionResult.PASS;
            }

            BlockPos clicked = hitResult.getBlockPos();
            if (!world.getBlockState(clicked).isOf(ModBlocks.UNDIUM)) {
                return ActionResult.PASS;
            }

            // Try each air block directly adjacent to the clicked frame block.
            for (Direction direction : Direction.values()) {
                BlockPos candidate = clicked.offset(direction);
                var shape = PortalShape.find(world, candidate);
                if (shape.isPresent()) {
                    if (!world.isClient()) {
                        shape.get().fill(world);
                        player.getStackInHand(hand).damage(1, player,
                                hand == Hand.MAIN_HAND
                                        ? net.minecraft.entity.EquipmentSlot.MAINHAND
                                        : net.minecraft.entity.EquipmentSlot.OFFHAND);
                        world.playSound(null, clicked, SoundEvents.ITEM_FLINTANDSTEEL_USE,
                                SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : new ArrayList<>(world.getPlayers())) {
                if (player.hasPortalCooldown()) continue;
                if (!touchingPortal(world, player)) continue;

                if (world.getRegistryKey().equals(World.OVERWORLD)) {
                    ServerWorld target = world.getServer().getWorld(UNDERWORLD_WORLD);
                    if (target != null) teleportThrough(player, target);
                } else if (world.getRegistryKey().equals(UNDERWORLD_WORLD)) {
                    ServerWorld target = world.getServer().getWorld(World.OVERWORLD);
                    if (target != null) teleportThrough(player, target);
                }
            }
        });
    }

    private static boolean touchingPortal(ServerWorld world, ServerPlayerEntity player) {
        BlockPos base = player.getBlockPos();
        return world.getBlockState(base).isOf(ModBlocks.UNDERWORLD_PORTAL)
                || world.getBlockState(base.up()).isOf(ModBlocks.UNDERWORLD_PORTAL);
    }

    private static void teleportThrough(ServerPlayerEntity player, ServerWorld target) {
        int x = (int)Math.floor(player.getX());
        int z = (int)Math.floor(player.getZ());

        BlockPos existing = findNearbyPortal(target, x, z, 20);
        BlockPos exit = existing != null ? existing : buildExitPortal(target, x, z);

        player.resetPortalCooldown();
        player.teleport(
                target,
                exit.getX() + 0.5,
                exit.getY() + 0.05,
                exit.getZ() + 0.5,
                Set.<PositionFlag>of(),
                player.getYaw(),
                player.getPitch(),
                false
        );
    }

    private static BlockPos findNearbyPortal(ServerWorld world, int x, int z, int radius) {
        int centerY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        int minY = Math.max(world.getBottomY(), centerY - 40);
        int maxY = Math.min(world.getTopYInclusive(), centerY + 60);

        for (int r = 0; r <= radius; r += 2) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz : new int[]{-r, r}) {
                    BlockPos found = scanColumn(world, x + dx, z + dz, minY, maxY);
                    if (found != null) return found;
                }
            }
            for (int dz = -r + 1; dz < r; dz++) {
                for (int dx : new int[]{-r, r}) {
                    BlockPos found = scanColumn(world, x + dx, z + dz, minY, maxY);
                    if (found != null) return found;
                }
            }
        }
        return null;
    }

    private static BlockPos scanColumn(ServerWorld world, int x, int z, int minY, int maxY) {
        for (int y = minY; y <= maxY; y++) {
            BlockPos p = new BlockPos(x, y, z);
            if (world.getBlockState(p).isOf(ModBlocks.UNDERWORLD_PORTAL)) return p;
        }
        return null;
    }

    private static BlockPos buildExitPortal(ServerWorld world, int x, int z) {
        int surface = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        int y;

        if (world.getRegistryKey().equals(UNDERWORLD_WORLD)) {
            // Islands can be void at the exact coordinate. Create a safe ledge if needed.
            y = Math.max(58, Math.min(150, surface + 1));
            if (surface <= world.getBottomY() + 3) y = 84;
        } else {
            y = Math.max(world.getBottomY() + 4, Math.min(world.getTopYInclusive() - 8, surface + 1));
        }

        BlockPos interiorBottom = new BlockPos(x, y, z);

        // 4 wide x 5 tall frame in the X direction, 2x3 interior.
        for (int dx = -1; dx <= 2; dx++) {
            setFrame(world, interiorBottom.add(dx, -1, 0));
            setFrame(world, interiorBottom.add(dx, 3, 0));
        }
        for (int dy = 0; dy <= 2; dy++) {
            setFrame(world, interiorBottom.add(-1, dy, 0));
            setFrame(world, interiorBottom.add(2, dy, 0));
        }

        // Safe floor around the portal.
        for (int dx = -2; dx <= 3; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                setFrame(world, interiorBottom.add(dx, -1, dz));
            }
        }

        // Clear breathing/exit room.
        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos p = interiorBottom.add(dx, dy, dz);
                    if (!world.getBlockState(p).isOf(ModBlocks.UNDIUM)) {
                        world.setBlockState(p, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
            }
        }

        BlockState portalState = ModBlocks.UNDERWORLD_PORTAL.getDefaultState()
                .with(UnderworldPortalBlock.AXIS, Direction.Axis.X);

        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 3; dy++) {
                world.setBlockState(interiorBottom.add(dx, dy, 0), portalState, Block.NOTIFY_ALL);
            }
        }

        return interiorBottom;
    }

    private static void setFrame(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, ModBlocks.UNDIUM.getDefaultState(), Block.NOTIFY_ALL);
    }

    private PortalManager() {}
}
