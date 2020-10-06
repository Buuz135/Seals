package com.buuz135.seals.client;

import com.buuz135.seals.SealInfo;
import com.buuz135.seals.Seals;
import com.buuz135.seals.network.SealRequestMessage;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SealButton extends Widget {

    private static Color SELECTED = new Color(0xffcc00);
    private static Color COMPLETED = new Color(0x00ff15);

    private SealInfo info;

    public SealButton(SealInfo info, int xIn, int yIn) {
        super(xIn, yIn, 22, 22, new StringTextComponent(""));
        this.info = info;
        this.width = 22;
        this.height = 22;
        this.active = true;
    }

    @Override
    public void render(MatrixStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(stack, p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void renderButton(MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.disableLighting();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(Minecraft.getInstance().player.getUniqueID().toString()) && ClientSealWorldStorage.SEALS.getClientSeals().get(Minecraft.getInstance().player.getUniqueID().toString()).equals(info.getSealID())) {
            RenderSystem.color4f(SELECTED.getRed() / 255f, SELECTED.getGreen() / 255f, SELECTED.getBlue() / 255f, this.alpha);
        }
        this.blit(stack, this.x, this.y, 24, 23, 22, 22);
        if (info.getIcon() != null) info.getIcon().drawIcon(Minecraft.getInstance().currentScreen, x,y);
        if (isHovered()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(TextFormatting.LIGHT_PURPLE + new TranslationTextComponent("seal." + info.getSealLangKey()).getString());
            for (ResourceLocation requisite : info.getRequisites()) {
                Advancement advancement = minecraft.player.connection.getAdvancementManager().getAdvancementList().getAdvancement(requisite);
                if (advancement != null) {
                    boolean completed = false;
                    if (minecraft.player.connection.getAdvancementManager().advancementToProgress.containsKey(advancement)) {
                        completed = minecraft.player.connection.getAdvancementManager().advancementToProgress.get(advancement).isDone();
                    }
                    tooltip.add(TextFormatting.GOLD + "- " + (completed ? TextFormatting.GREEN : TextFormatting.RED) + advancement.getDisplay().getTitle().getString());
                } else {
                    tooltip.add(TextFormatting.GOLD + "- " + TextFormatting.RED + "??????");
                }
            }
            stack.translate(0, 0, 300);
            minecraft.currentScreen.func_243308_b(stack, tooltip.stream().map(StringTextComponent::new).collect(Collectors.toList()), x + 18, y + (tooltip.size() / 2) + fontrenderer.FONT_HEIGHT);
            stack.translate(0, 0, -300);
        }
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        super.onClick(p_onClick_1_, p_onClick_3_);
        Seals.NETWORK.sendToServer(new SealRequestMessage(info.getSealID()));
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderToolTip(matrixStack, mouseX, mouseY);
    }

}
