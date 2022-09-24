package com.buuz135.seals.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage {

    IMessage fromBytes(FriendlyByteBuf buf);

    void toBytes(FriendlyByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
