package com.chatty.underworld;

import com.chatty.underworld.entity.ModEntities;
import com.chatty.underworld.portal.PortalManager;
import com.chatty.underworld.worldgen.ModWorldgen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UnderworldMod implements ModInitializer {
    public static final String MOD_ID = "underworld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModEntities.initialize();
        ModWorldgen.initialize();
        PortalManager.initialize();
        LOGGER.info("The Underworld initialized.");
    }
}
