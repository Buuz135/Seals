package com.buuz135.seals.client.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStackIcon implements IIcon {

    private ResourceLocation stack;

    public ItemStackIcon(ResourceLocation stack) {
        this.stack = stack;
    }

    @Override
    public String getName() {
        return "item";
    }

    @Override
    public void drawIcon(GuiGraphics guiGraphics, int posX, int posY) {
        //RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.enableDepthTest();
        guiGraphics.renderItem(new ItemStack(ForgeRegistries.ITEMS.getValue(stack)), posX + 3, posY + 3);
        //RenderHelper.disableStandardItemLighting();
    }

    public ResourceLocation getStack() {
        return stack;
    }
}
