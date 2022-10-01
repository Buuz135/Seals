package com.buuz135.seals.network;

import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientSyncSealsMessage implements IMessage {

    private CompoundTag sync;

    public ClientSyncSealsMessage(CompoundTag sync) {
        this.sync = sync;
    }

    public ClientSyncSealsMessage() {
    }

    @Override
    public ClientSyncSealsMessage fromBytes(FriendlyByteBuf buf) {
        sync = buf.readNbt();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(sync);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
                ClientSealWorldStorage.SEALS.deserialize(sync);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
