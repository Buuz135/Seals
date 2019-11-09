package com.buuz135.seals.network;

import com.buuz135.seals.storage.ClientSealWorldStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientSyncSealsMessage implements IMessage {

    private CompoundNBT sync;

    public ClientSyncSealsMessage(CompoundNBT sync) {
        this.sync = sync;
    }

    public ClientSyncSealsMessage() {
    }

    @Override
    public ClientSyncSealsMessage fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        sync = packetBuffer.readCompoundTag();
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeCompoundTag(sync);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ClientSealWorldStorage.SEALS.deserialize(sync);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
