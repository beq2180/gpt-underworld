package com.chatty.underworld.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AbstractCowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public final class GloomgrazerEntity extends AbstractCowEntity {
    public GloomgrazerEntity(EntityType<? extends AbstractCowEntity> type, World world) {
        super(type, world);
    }

    @Override
    public GloomgrazerEntity createChild(ServerWorld world, PassiveEntity other) {
        return ModEntities.GLOOMGRAZER.create(world, SpawnReason.BREEDING);
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
                this.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 0.82F + this.random.nextFloat() * 0.18F);
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
