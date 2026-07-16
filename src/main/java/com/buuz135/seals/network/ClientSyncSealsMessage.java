package com.buuz135.seals.network;

import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.ClientSealWorldStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class ClientSyncSealsMessage implements CustomPacketPayload, IMessage {

    public static CustomPacketPayload.Type<ClientSyncSealsMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Seals.MOD_ID, "sync_seals"));
    public static StreamCodec<? super RegistryFriendlyByteBuf, ClientSyncSealsMessage> CODEC = new StreamCodec<>() {
        @Override
        public ClientSyncSealsMessage decode(RegistryFriendlyByteBuf object) {
            return new ClientSyncSealsMessage(object.readNbt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, ClientSyncSealsMessage sealRequestMessage) {
            registryFriendlyByteBuf.writeNbt(sealRequestMessage.sync);
        }
    };

    private CompoundTag sync;

    public ClientSyncSealsMessage(CompoundTag sync) {
        this.sync = sync;
    }

    public ClientSyncSealsMessage() {
    }

    @Override
    public void handle(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(() -> {
            ClientSealWorldStorage.SEALS.deserialize(sync);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
