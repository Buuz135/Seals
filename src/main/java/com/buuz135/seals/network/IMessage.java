package com.buuz135.seals.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IMessage {

    void handle(IPayloadContext context);
}
