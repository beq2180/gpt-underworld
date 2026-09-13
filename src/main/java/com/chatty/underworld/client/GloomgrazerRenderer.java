package com.chatty.underworld.client;

import com.chatty.underworld.UnderworldMod;
import com.chatty.underworld.entity.GloomgrazerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public final class GloomgrazerRenderer extends MobEntityRenderer<GloomgrazerEntity, LivingEntityRenderState, CowEntityModel> {
    private static final Identifier TEXTURE = UnderworldMod.id("textures/entity/gloomgrazer.png");

    public GloomgrazerRenderer(EntityRendererFactory.Context context) {
        super(context, new CowEntityModel(context.getPart(EntityModelLayers.COW)), 0.7F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
