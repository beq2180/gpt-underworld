package com.chatty.underworld;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    public static final SoundEvent GLOOMGRAZER_AMBIENT = register("entity.gloomgrazer.ambient");
    public static final SoundEvent GLOOMGRAZER_HURT = register("entity.gloomgrazer.hurt");
    public static final SoundEvent GLOOMGRAZER_DEATH = register("entity.gloomgrazer.death");
    public static final SoundEvent GLOOMGRAZER_BOTTLE = register("entity.gloomgrazer.bottle");

    private static SoundEvent register(String path) {
        Identifier id = UnderworldMod.id(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {
        // Class loading performs the registrations above.
    }

    private ModSounds() {}
}
