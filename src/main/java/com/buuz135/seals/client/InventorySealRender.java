package com.buuz135.seals.client;

import com.buuz135.seals.Seals;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class InventorySealRender {

    @SubscribeEvent
    public static void background(ScreenEvent.BackgroundRendered event) {
        if (event.getScreen() instanceof InventoryScreen) {
            PoseStack stack = event.getPoseStack();
            if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(Minecraft.getInstance().player.getUUID().toString())) {
                SealInfo seal = Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(Minecraft.getInstance().player.getUUID().toString()));
                if (seal != null) {
                    String sealName = Component.translatable("seal." + seal.getSealLangKey()).getString();
                    String playerName = Minecraft.getInstance().player.getName().getString();
                    Minecraft.getInstance().font.draw(stack, ChatFormatting.LIGHT_PURPLE + sealName,
                            ((InventoryScreen) event.getScreen()).getGuiLeft() + ((InventoryScreen) event.getScreen()).getXSize() / 2F - Minecraft.getInstance().font.width(sealName) / 2F,
                            ((InventoryScreen) event.getScreen()).getGuiTop() - Minecraft.getInstance().font.lineHeight, 0xFFFFFF);
                    Minecraft.getInstance().font.draw(stack, playerName,
                            ((InventoryScreen) event.getScreen()).getGuiLeft() + ((InventoryScreen) event.getScreen()).getXSize() / 2F - Minecraft.getInstance().font.width(playerName) / 2F,
                            ((InventoryScreen) event.getScreen()).getGuiTop() - Minecraft.getInstance().font.lineHeight * 2, 0xFFFFFF);
                }
            }
        }
    }
}
