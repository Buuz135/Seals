package com.buuz135.seals.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage {

    IMessage fromBytes(ByteBuf buf);

    void toBytes(ByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
