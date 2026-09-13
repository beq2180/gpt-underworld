package com.chatty.underworld.entity;

import com.chatty.underworld.UnderworldMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractCowEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModEntities {
    public static final EntityType<GloomgrazerEntity> GLOOMGRAZER = registerGloomgrazer();

    private static EntityType<GloomgrazerEntity> registerGloomgrazer() {
        RegistryKey<EntityType<?>> key = RegistryKey.of(
                RegistryKeys.ENTITY_TYPE,
                UnderworldMod.id("gloomgrazer")
        );

        EntityType<GloomgrazerEntity> type = EntityType.Builder
                .create(GloomgrazerEntity::new, SpawnGroup.CREATURE)
                .dimensions(0.9F, 1.4F)
                .maxTrackingRange(10)
                .build(key);

        return Registry.register(Registries.ENTITY_TYPE, UnderworldMod.id("gloomgrazer"), type);
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(
                GLOOMGRAZER,
                AbstractCowEntity.createCowAttributes()
                        .add(EntityAttributes.MAX_HEALTH, 15.0D)
        );
    }

    private ModEntities() {}
}
