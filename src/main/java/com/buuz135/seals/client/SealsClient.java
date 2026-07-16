package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ItemStackIcon;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.WeakHashMap;


public final class SealsClient {
    private static final Map<Screen, Button> ADVANCEMENT_SEAL_BUTTONS = new WeakHashMap<>();
    private static final float NAME_TAG_SCALE = 0.025F;
    private static final float NAME_TAG_LINE_OFFSET = 9.0F * 1.15F * NAME_TAG_SCALE;
    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXT_PADDING = 2;
    private static final float ICON_DEPTH = 0.01F;
    private static final int FULL_BRIGHT_LIGHT = 15728880;

    private SealsClient() {
    }

    public static void init() {
        NeoForge.EVENT_BUS.register(new SealsClient());
    }

    @SubscribeEvent
    public void onRecipesUpdated(RecipesReceivedEvent event) {
        Seals.SEAL_MANAGER.setSeals(Minecraft.getInstance().level, Seals.getSealRecipes(event.getRecipeMap()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(ScreenEvent.Init.Pre event) {
        if (event.getScreen() instanceof AdvancementsScreen || event.getScreen().getClass().getName().contains("BetterAdvancementsScreen")) {
            Button button = Button.builder(Component.translatable("seals.open_selection"), press -> Minecraft.getInstance().setScreen(new SealSelectionScreen(event.getScreen())))
                    .pos(4, event.getScreen().height - 24)
                    .size(58, 20)
                    .build();
            ADVANCEMENT_SEAL_BUTTONS.put(event.getScreen(), button);
            event.addListener(button);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAdvancementsRender(ScreenEvent.Render.Post event) {
        Button button = ADVANCEMENT_SEAL_BUTTONS.get(event.getScreen());
        if (button != null) button.extractRenderState(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderNameTag(RenderNameTagEvent.DoRender event) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !(event.getEntityRenderState() instanceof AvatarRenderState state) || state.scoreText == null || state.nameTagAttachment == null) {
            return;
        }

        var entity = minecraft.level.getEntity(state.id);
        if (!(entity instanceof Avatar avatar)) {
            return;
        }

        if (!avatar.getDisplayName().contains(event.getContent())) {
            return;
        }

        var sealId = ClientSealWorldStorage.SEALS.getClientSeals().get(avatar.getUUID().toString());
        if (sealId == null) {
            return;
        }

        var seal = Seals.SEAL_MANAGER.getSeal(sealId);
        if (seal == null || !(seal.getIcon() instanceof ItemStackIcon icon)) {
            return;
        }

        var item = minecraft.level.registryAccess().lookupOrThrow(Registries.ITEM).getValue(icon.getStack());
        if (item == null) {
            return;
        }

        ItemStackRenderState itemState = new ItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(itemState, new ItemStack(item), ItemDisplayContext.GUI, minecraft.level, minecraft.player, state.id);

        int yOffset = state.showExtraEars ? -10 : 0;
        float textLeft = -minecraft.font.width(state.scoreText) / 2.0F;
        float iconLeft = textLeft - ICON_SIZE - ICON_TEXT_PADDING;
        var pose = event.getPoseStack();

        pose.pushPose();
        pose.translate(0.0F, -NAME_TAG_LINE_OFFSET, 0.0F);
        pose.translate(state.nameTagAttachment.x, state.nameTagAttachment.y + 0.5, state.nameTagAttachment.z);
        pose.mulPose(event.getCameraRenderState().orientation);
        pose.scale(NAME_TAG_SCALE, -NAME_TAG_SCALE, NAME_TAG_SCALE);
        pose.translate(iconLeft + ICON_SIZE / 2.0F, yOffset + 4.0F, 0.0F);
        pose.scale(ICON_SIZE, -ICON_SIZE, ICON_DEPTH);
        itemState.submit(pose, event.getSubmitNodeCollector(), FULL_BRIGHT_LIGHT, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }
}
