package com.chatty.underworld.entity;

import com.chatty.underworld.ModBlocks;
import com.chatty.underworld.ModSounds;
import com.chatty.underworld.worldgen.ModWorldgen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractCowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public final class GloomgrazerEntity extends AbstractCowEntity {
    public GloomgrazerEntity(EntityType<? extends AbstractCowEntity> type, World world) {
        super(type, world);
    }

    @Override
    public GloomgrazerEntity createChild(ServerWorld world, PassiveEntity other) {
        return ModEntities.GLOOMGRAZER.create(world, SpawnReason.BREEDING);
    }

    /**
     * Gloomgrazers live in a dark dimension, so vanilla animal light checks are intentionally
     * not used. They may naturally spawn on Groil in Ashlands and Trunks, never Islands.
     */
    public static boolean canSpawn(
            EntityType<GloomgrazerEntity> type,
            ServerWorldAccess world,
            SpawnReason reason,
            BlockPos pos,
            Random random
    ) {
        boolean allowedBiome = world.getBiome(pos).matchesKey(ModWorldgen.ASHLANDS)
                || world.getBiome(pos).matchesKey(ModWorldgen.TRUNKS);

        if (!allowedBiome) return false;

        return world.getBlockState(pos.down()).isOf(ModBlocks.GROIL)
                && world.getBlockState(pos).isAir()
                && world.getBlockState(pos.up()).isAir();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GLOOMGRAZER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GLOOMGRAZER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GLOOMGRAZER_DEATH;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Gloomgrazers are "milked" with bottles instead of buckets.
        if (!this.isBaby() && stack.isOf(Items.GLASS_BOTTLE)) {
            if (!this.getEntityWorld().isClient()) {
                player.setStackInHand(
                        hand,
                        ItemUsage.exchangeStack(stack, player, new ItemStack(Items.DRAGON_BREATH))
                );
                this.playSound(ModSounds.GLOOMGRAZER_BOTTLE, 0.9F, 0.92F + this.random.nextFloat() * 0.16F);
            }
            return ActionResult.SUCCESS;
        }

        // Don't let the inherited cow interaction produce normal milk buckets.
        if (stack.isOf(Items.BUCKET)) {
            return ActionResult.PASS;
        }

        // Keeps the normal cow-like wheat breeding / interaction behavior.
        return super.interactMob(player, hand);
    }
}
