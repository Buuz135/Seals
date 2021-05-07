package com.buuz135.seals.client;

import com.buuz135.seals.SealInfo;
import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class InventorySealRender {

    @SubscribeEvent
    public static void background(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (event.getGui() instanceof InventoryScreen) {
            MatrixStack stack = event.getMatrixStack();
            if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(Minecraft.getInstance().player.getUniqueID().toString())) {
                SealInfo seal = Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(Minecraft.getInstance().player.getUniqueID().toString()));
                if (seal != null) {
                    String sealName = new TranslationTextComponent("seal." + seal.getSealLangKey()).getString();
                    String playerName = Minecraft.getInstance().player.getName().getString();
                    Minecraft.getInstance().fontRenderer.drawString(stack, TextFormatting.LIGHT_PURPLE + sealName,
                            ((InventoryScreen) event.getGui()).getGuiLeft() + ((InventoryScreen) event.getGui()).getXSize() / 2F - Minecraft.getInstance().fontRenderer.getStringWidth(sealName) / 2F,
                            ((InventoryScreen) event.getGui()).getGuiTop() - Minecraft.getInstance().fontRenderer.FONT_HEIGHT, 0xFFFFFF);
                    Minecraft.getInstance().fontRenderer.drawString(stack, playerName,
                            ((InventoryScreen) event.getGui()).getGuiLeft() + ((InventoryScreen) event.getGui()).getXSize() / 2F - Minecraft.getInstance().fontRenderer.getStringWidth(playerName) / 2F,
                            ((InventoryScreen) event.getGui()).getGuiTop() - Minecraft.getInstance().fontRenderer.FONT_HEIGHT * 2, 0xFFFFFF);
                }
            }
        }
    }
}
