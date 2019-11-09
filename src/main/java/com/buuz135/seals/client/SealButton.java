package com.buuz135.seals.client;

import com.buuz135.seals.SealInfo;
import com.buuz135.seals.Seals;
import com.buuz135.seals.network.SealRequestMessage;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SealButton extends Widget {

    private static Color SELECTED = new Color(0xffcc00);
    private static Color COMPLETED = new Color(0x00ff15);

    private SealInfo info;

    public SealButton(SealInfo info, int xIn, int yIn) {
        super(xIn, yIn, "");
        this.info = info;
        this.width = 22;
        this.height = 22;
        this.active = true;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(Minecraft.getInstance().player.getUniqueID().toString()) && ClientSealWorldStorage.SEALS.getClientSeals().get(Minecraft.getInstance().player.getUniqueID().toString()).equals(info.getSealID())){
            GlStateManager.color4f(SELECTED.getRed()/255f, SELECTED.getGreen()/255f, SELECTED.getBlue()/255f, this.alpha);
        }
        this.blit(this.x, this.y, 24, 23, 22, 22);
        info.getIcon().drawIcon(Minecraft.getInstance().currentScreen, x,y);
        if (isHovered()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(TextFormatting.LIGHT_PURPLE + new TranslationTextComponent("seal." + info.getSealLangKey()).getFormattedText());
            for (ResourceLocation requisite : info.getRequisites()) {
                Advancement advancement = minecraft.player.connection.getAdvancementManager().getAdvancementList().getAdvancement(requisite);
                if (advancement != null) {
                    boolean completed = false;
                    if (minecraft.player.connection.getAdvancementManager().advancementToProgress.containsKey(advancement)) {
                        completed = minecraft.player.connection.getAdvancementManager().advancementToProgress.get(advancement).isDone();
                    }
                    tooltip.add(TextFormatting.GOLD + "- " + (completed ? TextFormatting.GREEN : TextFormatting.RED) + advancement.getDisplay().getTitle().getFormattedText());
                } else {
                    tooltip.add(TextFormatting.GOLD + "- " + TextFormatting.RED + "??????");
                }
            }
            minecraft.currentScreen.renderTooltip(tooltip, x + 18, y + (tooltip.size() / 2) + fontrenderer.FONT_HEIGHT);
        }
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        super.onClick(p_onClick_1_, p_onClick_3_);
        Seals.NETWORK.sendToServer(new SealRequestMessage(info.getSealID()));
    }
}
