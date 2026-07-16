package com.buuz135.seals.client.icon;

import net.minecraft.resources.Identifier;


public class ItemStackIcon implements IIcon {
    private final Identifier stack;

    public ItemStackIcon(Identifier stack) {
        this.stack = stack;
    }

    @Override
    public String getName() {
        return "item";
    }

    public Identifier getStack() {
        return stack;
    }
}
