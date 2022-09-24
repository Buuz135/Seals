package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.mixin.ClientAdvancementsAccessor;
import com.buuz135.seals.network.SealRequestMessage;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SealButton extends Button {

    private static Color SELECTED = new Color(0xffcc00);
    private static Color COMPLETED = new Color(0x00ff15);

    private SealInfo info;
    private boolean left;

    public SealButton(SealInfo info, int xIn, int yIn, boolean left) {
        super(xIn, yIn, 22, 22, Component.literal(""), press -> {
            Seals.NETWORK.sendToServer(new SealRequestMessage(info.getSealID()));
        });
        this.info = info;
        this.width = 22;
        this.height = 22;
        this.active = true;
        this.left = left;
    }

    @Override
    public void render(PoseStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(stack, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void renderButton(PoseStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        //RenderSystem.disableLighting();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(Minecraft.getInstance().player.getUUID().toString()) && ClientSealWorldStorage.SEALS.getClientSeals().get(Minecraft.getInstance().player.getUUID().toString()).equals(info.getSealID())) {
            RenderSystem.setShaderColor(SELECTED.getRed() / 255f, SELECTED.getGreen() / 255f, SELECTED.getBlue() / 255f, this.alpha);
        }
        this.blit(stack, this.x, this.y, 24, 23, 22, 22);
        if (info.getIcon() != null) info.getIcon().drawIcon(Minecraft.getInstance().screen, x, y);
        if (isHoveredOrFocused()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(ChatFormatting.LIGHT_PURPLE + Component.translatable("seal." + info.getSealLangKey()).getString());
            var clientAdvancements = minecraft.player.connection.getAdvancements();
            var advAccessor = (ClientAdvancementsAccessor) clientAdvancements;
            for (ResourceLocation requisite : info.getRequisites()) {
                Advancement advancement = minecraft.player.connection.getAdvancements().getAdvancements().get(requisite);
                if (advancement != null) {
                    boolean completed = false;
                    if (advAccessor.getProgress().containsKey(advancement) && advAccessor.getProgress().get(advancement).isDone()) {
                        completed = true;
                    }
                    tooltip.add(ChatFormatting.GOLD + "- " + (completed ? ChatFormatting.GREEN : ChatFormatting.RED) + advancement.getDisplay().getTitle().getString());
                } else {
                    tooltip.add(ChatFormatting.GOLD + "- " + ChatFormatting.RED + "??????");
                }
            }
            stack.translate(0, 0, 300);
            minecraft.screen.renderTooltip(stack, tooltip.stream().map(s -> Component.literal(s)).collect(Collectors.toList()), Optional.empty(), left ? x + 18 : x + 7, y + (tooltip.size() / 2) + fontrenderer.lineHeight);
            stack.translate(0, 0, -300);
        }
    }


}
