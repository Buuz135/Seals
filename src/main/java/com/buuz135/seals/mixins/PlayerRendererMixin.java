package com.buuz135.seals.mixins;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ItemStackIcon;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final double translateY = (double) (9.0F * 1.15F * 0.025F);

    public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "HEAD"))
    public void renderNameTagHead(AbstractClientPlayer entity, Component component, PoseStack pose, MultiBufferSource multiBufferSource, int packedLight, float partialTick, CallbackInfo callbackInfo) {
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUUID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString())) != null) {
            var seal = Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString()));
            super.renderNameTag(entity, Component.translatable("seal." + seal.getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC), pose, multiBufferSource, packedLight, partialTick);
            if (seal.getIcon() instanceof ItemStackIcon icon) {
                Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
                pose.pushPose();

                pose.translate(vec3.x, vec3.y - 0.099f + 0.5f, vec3.z);
                //pose.mulPose(Axis.YN.rotation(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
                //pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

                pose.mulPose(Axis.YP.rotation(Minecraft.getInstance().player.yRotO * -0.017453292F));
                //pose.mulPose(Axis.YP.rotation(180));

                pose.translate(Minecraft.getInstance().font.width(Component.translatable("seal." + seal.getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)) * 0.01f + 0.3, 0, 0);
                var scale = 0.5f;
                BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(icon.getCachedStack(), Minecraft.getInstance().level, null, 0);
                if (!model.isGui3d()) {
                    scale = 0.3f;
                }
                pose.scale(scale, scale, 0.01F);
                Minecraft.getInstance().getItemRenderer().render(icon.getCachedStack(), ItemDisplayContext.FIXED, false, pose, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);
                pose.popPose();
            }
            pose.translate(0, translateY, 0);
        }
    }


    @Inject(method = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "TAIL"))
    public void renderNameTagBottom(AbstractClientPlayer entity, Component component, PoseStack pose, MultiBufferSource multiBufferSource, int packedLight, float partialTick, CallbackInfo callbackInfo) {
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUUID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString())) != null) {
            pose.translate(0, -translateY, 0);
        }
    }
}
