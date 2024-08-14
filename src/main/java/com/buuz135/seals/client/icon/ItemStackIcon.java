package com.buuz135.seals.client.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class ItemStackIcon implements IIcon {

    private ResourceLocation stack;

    public ItemStackIcon(ResourceLocation stack) {
        this.stack = stack;
    }

    @Override
    public String getName() {
        return "item";
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawIcon(GuiGraphics guiGraphics, int posX, int posY) {
        //RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.enableDepthTest();
        guiGraphics.renderItem(getCachedStack(), posX + 3, posY + 3);
        //RenderHelper.disableStandardItemLighting();
    }

    public ResourceLocation getStack() {
        return stack;
    }

    private ItemStack cached = ItemStack.EMPTY;

    @OnlyIn(Dist.CLIENT)
    public ItemStack getCachedStack() {
        if (cached.isEmpty()) {
            cached = new ItemStack(Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).get(stack));
        }

        return cached;
    }
}
