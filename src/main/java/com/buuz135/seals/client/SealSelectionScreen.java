package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SealSelectionScreen extends Screen {

    private static final int PANEL_WIDTH = 280;
    private static final int BUTTON_SPACING = 28;
    private static final int PANEL_PADDING = 18;

    private final Screen parent;
    private int panelLeft;
    private int panelTop;
    private int panelHeight;

    public SealSelectionScreen(Screen parent) {
        super(Component.translatable("seals.select"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        List<SealInfo> seals = new ArrayList<>(Seals.SEAL_MANAGER.getSeals());
        seals.removeIf(seal -> seal.isInvisible() && !seal.hasAchievedSealClient(Minecraft.getInstance().player));

        int columns = Math.max(1, Math.min(8, (PANEL_WIDTH - PANEL_PADDING * 2) / BUTTON_SPACING));
        int rows = Math.max(1, (int) Math.ceil((double) seals.size() / columns));
        this.panelHeight = 78 + rows * BUTTON_SPACING;
        this.panelLeft = (this.width - PANEL_WIDTH) / 2;
        this.panelTop = (this.height - this.panelHeight) / 2;

        int gridWidth = columns * BUTTON_SPACING - (BUTTON_SPACING - 22);
        int gridLeft = this.panelLeft + (PANEL_WIDTH - gridWidth) / 2;
        int gridTop = this.panelTop + 42;
        for (int i = 0; i < seals.size(); i++) {
            SealInfo seal = seals.get(i);
            this.addRenderableWidget(new SealButton(seal, gridLeft + i % columns * BUTTON_SPACING, gridTop + i / columns * BUTTON_SPACING, false, () -> {
            }));
        }

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> this.onClose())
                .pos(this.panelLeft + (PANEL_WIDTH - 80) / 2, this.panelTop + this.panelHeight - 28)
                .size(80, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.panelLeft - 1, this.panelTop - 1, this.panelLeft + PANEL_WIDTH + 1, this.panelTop + this.panelHeight + 1, 0xFFC6A25A);
        guiGraphics.fill(this.panelLeft, this.panelTop, this.panelLeft + PANEL_WIDTH, this.panelTop + this.panelHeight, 0xE0181420);
        this.renderEquippedSeal(guiGraphics);
        var text = guiGraphics.textRenderer();
        text.accept(TextAlignment.CENTER, this.width / 2, this.panelTop + 12, this.title.copy().withStyle(ChatFormatting.WHITE));
        text.accept(TextAlignment.CENTER, this.width / 2, this.panelTop + 27, Component.translatable("seals.select_hint").withStyle(ChatFormatting.GRAY));
        for (Renderable renderable : this.renderables) {
            renderable.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderEquippedSeal(GuiGraphicsExtractor guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        var selectedSealId = ClientSealWorldStorage.SEALS.getClientSeals().get(minecraft.player.getUUID().toString());
        SealInfo selectedSeal = selectedSealId == null ? null : Seals.SEAL_MANAGER.getSeal(selectedSealId);
        if (selectedSeal != null) {
            Component sealName = Component.translatable("seal." + selectedSeal.getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE);
            guiGraphics.textRenderer().accept(TextAlignment.CENTER, this.width / 2, this.panelTop - this.font.lineHeight, sealName);
        }
        guiGraphics.textRenderer().accept(TextAlignment.CENTER, this.width / 2, this.panelTop - this.font.lineHeight * 2, minecraft.player.getName().copy().withStyle(ChatFormatting.WHITE));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
