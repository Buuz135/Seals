package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
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
    protected void func_225629_a_(AbstractClientPlayerEntity entity, String p_225629_2_, MatrixStack matrixStack, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        if (this.renderManager.func_229099_b_(entity) < 1000.0D && ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUniqueID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())) != null){
            super.func_225629_a_(entity, TextFormatting.LIGHT_PURPLE + "" + TextFormatting.ITALIC + new TranslationTextComponent("seal." + Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())).getSealLangKey()).getFormattedText(), matrixStack, p_225629_4_, 64);
            matrixStack.func_227861_a_(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
        }
        super.func_225629_a_(entity, p_225629_2_, matrixStack, p_225629_4_, p_225629_5_);
        matrixStack.func_227861_a_(0.0D, -(double)(9.0F * 1.15F * 0.025F), 0.0D);
    }
}
