package com.buuz135.seals.client.icon;


import net.minecraft.client.gui.GuiGraphics;

public interface IIcon {

    String getName();

    void drawIcon(GuiGraphics guiGraphics, int posX, int posY);

}
