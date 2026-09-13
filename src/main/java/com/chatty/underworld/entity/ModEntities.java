package com.chatty.underworld.entity;

import com.chatty.underworld.UnderworldMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractCowEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.Heightmap;

public final class ModEntities {
    public static final EntityType<GloomgrazerEntity> GLOOMGRAZER = registerGloomgrazer();

    private static EntityType<GloomgrazerEntity> registerGloomgrazer() {
        RegistryKey<EntityType<?>> key = RegistryKey.of(
                RegistryKeys.ENTITY_TYPE,
                UnderworldMod.id("gloomgrazer")
        );

        // Fabric's mob builder lets us register a spawn restriction for this custom entity.
        // We use a custom predicate because the Underworld has no skylight, while vanilla
        // animals normally require bright light to spawn.
        EntityType<GloomgrazerEntity> type = FabricEntityTypeBuilder.<GloomgrazerEntity>createMob()
                .spawnGroup(SpawnGroup.CREATURE)
                .entityFactory(GloomgrazerEntity::new)
                .dimensions(EntityDimensions.fixed(0.9F, 1.4F))
                .trackRangeBlocks(10)
                .spawnRestriction(
                        SpawnLocationTypes.ON_GROUND,
                        Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                        GloomgrazerEntity::canSpawn
                )
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
