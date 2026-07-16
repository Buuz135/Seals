package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ClientIconRenderer;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.network.SealRequestMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class SealButton extends Button {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Seals.MOD_ID, "textures/gui/seal_button.png");
    private final SealInfo info;
    private final boolean left;
    private final Runnable onSelected;

    public SealButton(SealInfo info, int x, int y, boolean left, Runnable onSelected) {
        super(x, y, 22, 22, Component.empty(), button -> {
        }, Button.DEFAULT_NARRATION);
        this.info = info;
        this.left = left;
        this.onSelected = onSelected;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        ClientPacketDistributor.sendToServer(new SealRequestMessage(info.getSealID()));
        onSelected.run();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, 0, 22, 22, 22, 22);
        if (info.getIcon() != null)
            ClientIconRenderer.draw((com.buuz135.seals.client.icon.ItemStackIcon) info.getIcon(), graphics, getX(), getY());
        if (isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("seal." + info.getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE));
            var advancements = minecraft.player.connection.getAdvancements();
            for (Identifier requisite : info.getRequisites()) {
                var advancement = advancements.getTree().get(requisite);
                boolean complete = advancement != null && advancements.progress.containsKey(advancement.holder()) && advancements.progress.get(advancement.holder()).isDone();
                tooltip.add(advancement == null ? Component.literal("- ??????").withStyle(ChatFormatting.GOLD, ChatFormatting.RED) : advancement.advancement().display().get().getTitle().copy().withStyle(ChatFormatting.GOLD, complete ? ChatFormatting.GREEN : ChatFormatting.RED));
            }
            graphics.setTooltipForNextFrame(minecraft.font, tooltip, java.util.Optional.empty(), left ? getX() + 18 : getX() + 7, getY() + minecraft.font.lineHeight);
        }
    }
}
