package com.chatty.underworld;

import com.chatty.underworld.client.GloomgrazerRenderer;
import com.chatty.underworld.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.BlockRenderLayer;

public final class UnderworldClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.UNDERWORLD_PORTAL, BlockRenderLayer.TRANSLUCENT);
        EntityRendererRegistry.register(ModEntities.GLOOMGRAZER, GloomgrazerRenderer::new);
    }
}
