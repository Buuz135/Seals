package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.network.SealRequestMessage;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
        }, Button.DEFAULT_NARRATION::createNarrationMessage);
        this.info = info;
        this.width = 22;
        this.height = 22;
        this.active = true;
        this.left = left;
    }

    @Override
    public void render(GuiGraphics stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(stack, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
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
        guiGraphics.blit(WIDGETS_LOCATION, this.getX(), this.getY(), 24, 23, 22, 22);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
        if (info.getIcon() != null) info.getIcon().drawIcon(guiGraphics, this.getX(), this.getY());
        if (isHovered()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(ChatFormatting.LIGHT_PURPLE + Component.translatable("seal." + info.getSealLangKey()).getString());
            var clientAdvancements = minecraft.player.connection.getAdvancements();
            for (ResourceLocation requisite : info.getRequisites()) {
                Advancement advancement = minecraft.player.connection.getAdvancements().getAdvancements().get(requisite);
                if (advancement != null) {
                    boolean completed = false;
                    if (clientAdvancements.progress.containsKey(advancement) && clientAdvancements.progress.get(advancement).isDone()) {
                        completed = true;
                    }
                    tooltip.add(ChatFormatting.GOLD + "- " + (completed ? ChatFormatting.GREEN : ChatFormatting.RED) + advancement.getDisplay().getTitle().getString());
                } else {
                    tooltip.add(ChatFormatting.GOLD + "- " + ChatFormatting.RED + "??????");
                }
            }
            guiGraphics.pose().translate(0, 0, 300);
            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip.stream().map(s -> Component.literal(s)).collect(Collectors.toList()), Optional.empty(), left ? this.getX() + 18 : this.getX() + 7, this.getY() + (tooltip.size() / 2) + fontrenderer.lineHeight);
            guiGraphics.pose().translate(0, 0, -300);
        }
    }


}
