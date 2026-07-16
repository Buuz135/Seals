package com.buuz135.seals.mixins;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    private void seals$addSealNameplate(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo callback) {
        var sealId = ClientSealWorldStorage.SEALS.getClientSeals().get(avatar.getUUID().toString());
        if (sealId == null) {
            return;
        }

        var seal = Seals.SEAL_MANAGER.getSeal(sealId);
        if (seal != null) {
            state.scoreText = Component.translatable("seal." + seal.getSealLangKey())
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC);
        }
    }
}
