package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
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
    protected void renderEntityName(AbstractClientPlayerEntity entity, double x, double y, double z, String p_188296_8_, double p_188296_9_) {
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUniqueID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())) != null){
            this.renderLivingLabel(entity, TextFormatting.LIGHT_PURPLE + "" + TextFormatting.ITALIC + new TranslationTextComponent("seal." + Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())).getSealLangKey()).getFormattedText(), x, y, z, 64);
            y += (double) (9.0F * 1.15F * 0.025F);
        }
        super.renderEntityName(entity, x, y, z, p_188296_8_, p_188296_9_);
    }
}
