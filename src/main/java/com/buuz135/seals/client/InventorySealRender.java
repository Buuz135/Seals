package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;


@EventBusSubscriber(value = Dist.CLIENT)
public class InventorySealRender {

    @SubscribeEvent
    public static void background(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof InventoryScreen) {
            var guiGraphics = event.getGuiGraphics();
            if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(Minecraft.getInstance().player.getUUID().toString())) {
                SealInfo seal = Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(Minecraft.getInstance().player.getUUID().toString()));
                if (seal != null) {
                    String sealName = Component.translatable("seal." + seal.getSealLangKey()).getString();
                    String playerName = Minecraft.getInstance().player.getName().getString();
                    guiGraphics.text(Minecraft.getInstance().font, ChatFormatting.LIGHT_PURPLE + sealName,
                            (int) (((InventoryScreen) event.getScreen()).getGuiLeft() + ((InventoryScreen) event.getScreen()).getXSize() / 2F - Minecraft.getInstance().font.width(sealName) / 2F),
                            ((InventoryScreen) event.getScreen()).getGuiTop() - Minecraft.getInstance().font.lineHeight, 0xFFFFFF, false);
                    guiGraphics.text(Minecraft.getInstance().font, playerName,
                            (int) (((InventoryScreen) event.getScreen()).getGuiLeft() + ((InventoryScreen) event.getScreen()).getXSize() / 2F - Minecraft.getInstance().font.width(playerName) / 2F),
                            ((InventoryScreen) event.getScreen()).getGuiTop() - Minecraft.getInstance().font.lineHeight * 2, 0xFFFFFF, false);
                }
            }
        }
    }
}
