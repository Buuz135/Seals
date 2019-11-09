package com.buuz135.seals.client.icon;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
    public void drawIcon(Screen screen, int posX, int posY) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepthTest();
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(ForgeRegistries.ITEMS.getValue(stack)), posX + 3, posY +3);
    }
}
