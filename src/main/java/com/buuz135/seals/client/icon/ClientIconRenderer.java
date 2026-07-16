package com.buuz135.seals.client.icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;


public final class ClientIconRenderer {
    private ClientIconRenderer() {
    }

    public static void draw(ItemStackIcon icon, GuiGraphicsExtractor graphics, int x, int y) {
        ItemStack stack = new ItemStack(Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ITEM).getValue(icon.getStack()));
        graphics.item(stack, x + 3, y + 3);
    }
}
