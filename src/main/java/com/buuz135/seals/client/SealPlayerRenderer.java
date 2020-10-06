package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SealPlayerRenderer extends PlayerRenderer {

    public SealPlayerRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public SealPlayerRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
        super(renderManager, useSmallArms);
    }

    @Override
    protected void renderName(AbstractClientPlayerEntity entity, ITextComponent displayNameIn, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn) {
        if (this.renderManager.squareDistanceTo(entity) < 1000.0D && ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUniqueID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())) != null) {
            super.renderName(entity, new TranslationTextComponent("seal." + Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())).getSealLangKey()).mergeStyle(TextFormatting.LIGHT_PURPLE, TextFormatting.ITALIC), matrixStack, buffer, packedLightIn);
            matrixStack.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
        }
        super.renderName(entity, displayNameIn, matrixStack, buffer, packedLightIn);
        matrixStack.translate(0.0D, -(double) (9.0F * 1.15F * 0.025F), 0.0D);
    }

}
