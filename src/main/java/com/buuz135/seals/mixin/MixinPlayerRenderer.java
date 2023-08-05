package com.buuz135.seals.mixin;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    @Inject(at = @At("HEAD"), method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    private void renderName(AbstractClientPlayer entity, Component displayNameIn, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn, CallbackInfo info) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity) < 100.0D && ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUUID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString())) != null) {
            renderCustomNameTag(entity, Component.translatable("seal." + Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString())).getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC), matrixStack, buffer, packedLightIn);
            matrixStack.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
            renderCustomNameTag(entity, displayNameIn, matrixStack, buffer, packedLightIn);
            matrixStack.translate(0.0D, -(double) (9.0F * 1.15F * 0.025F), 0.0D);
            info.cancel();
        }
    }

    protected void renderCustomNameTag(AbstractClientPlayer p_117808_, Component p_117809_, PoseStack p_117810_, MultiBufferSource p_117811_, int p_117812_) {
        double d0 = Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(p_117808_);
        p_117810_.pushPose();
        if (d0 < 100.0D) {
            Scoreboard scoreboard = p_117808_.getScoreboard();
            Objective objective = scoreboard.getDisplayObjective(2);
            if (objective != null) {
                Score score = scoreboard.getOrCreatePlayerScore(p_117808_.getScoreboardName(), objective);
                this.renderNameTagParent(p_117808_, Component.literal(Integer.toString(score.getScore())).append(" ").append(objective.getDisplayName()), p_117810_, p_117811_, p_117812_);
                p_117810_.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
            }
        }

        this.renderNameTagParent(p_117808_, p_117809_, p_117810_, p_117811_, p_117812_);
        p_117810_.popPose();
    }

    protected void renderNameTagParent(AbstractClientPlayer p_114498_, Component p_114499_, PoseStack p_114500_, MultiBufferSource p_114501_, int p_114502_) {
        double d0 = Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(p_114498_);
        if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_114498_, d0)) {
            boolean flag = !p_114498_.isDiscrete();
            float f = p_114498_.getBbHeight() + 0.5F;
            int i = "deadmau5".equals(p_114499_.getString()) ? -10 : 0;
            p_114500_.pushPose();
            p_114500_.translate(0.0D, (double) f, 0.0D);
            p_114500_.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            p_114500_.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = p_114500_.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            Font font = Minecraft.getInstance().font;
            float f2 = (float) (-font.width(p_114499_) / 2);
            font.drawInBatch(p_114499_, f2, (float) i, 553648127, false, matrix4f, p_114501_, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, p_114502_);
            if (flag) {
                font.drawInBatch(p_114499_, f2, (float) i, -1, false, matrix4f, p_114501_, Font.DisplayMode.NORMAL, 0, p_114502_);
            }

            p_114500_.popPose();
        }
    }

}
