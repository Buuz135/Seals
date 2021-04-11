package com.buuz135.seals.mixin;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/renderer/entity/PlayerRenderer;renderName(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lnet/minecraft/util/text/ITextComponent;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", cancellable = true)
    private void renderName(AbstractClientPlayerEntity entity, ITextComponent displayNameIn, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, CallbackInfo info) {
        if (Minecraft.getInstance().getRenderManager().squareDistanceTo(entity) < 1000.0D && ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUniqueID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())) != null) {
            this.realRenderName(Minecraft.getInstance().getRenderManager(), entity, new TranslationTextComponent("seal." + Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUniqueID().toString())).getSealLangKey()).mergeStyle(TextFormatting.LIGHT_PURPLE, TextFormatting.ITALIC), matrixStack, buffer, packedLightIn);
            matrixStack.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
            realRenderName(Minecraft.getInstance().getRenderManager(), entity, displayNameIn, matrixStack, buffer, packedLightIn);
            matrixStack.translate(0.0D, -(double) (9.0F * 1.15F * 0.025F), 0.0D);
            info.cancel();
        }
    }

    protected void realRenderName(EntityRendererManager renderManager, AbstractClientPlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        double d0 = renderManager.squareDistanceTo(entityIn);
        matrixStackIn.push();
        if (d0 < 100.0D) {
            Scoreboard scoreboard = entityIn.getWorldScoreboard();
            ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
            if (scoreobjective != null) {
                Score score = scoreboard.getOrCreateScore(entityIn.getScoreboardName(), scoreobjective);
                this.renderNameParent(renderManager, entityIn, (new StringTextComponent(Integer.toString(score.getScorePoints()))).appendString(" ").append(scoreobjective.getDisplayName()), matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
            }
        }

        this.renderNameParent(renderManager, entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }

    protected void renderNameParent(EntityRendererManager renderManager, PlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        double d0 = renderManager.squareDistanceTo(entityIn);
        if (!(d0 > 4096.0D)) {
            boolean flag = !entityIn.isDiscrete();
            float f = entityIn.getHeight() + 0.5F;
            int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, (double) f, 0.0D);
            matrixStackIn.rotate(renderManager.getCameraOrientation());
            matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            FontRenderer fontrenderer = renderManager.getFontRenderer();
            float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
            fontrenderer.func_243247_a(displayNameIn, f2, (float) i, 553648127, false, matrix4f, bufferIn, flag, j, packedLightIn);
            if (flag) {
                fontrenderer.func_243247_a(displayNameIn, f2, (float) i, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
            }

            matrixStackIn.pop();
        }
    }

}
