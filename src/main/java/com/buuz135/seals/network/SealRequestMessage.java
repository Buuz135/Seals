package com.buuz135.seals.network;

import com.buuz135.seals.Seals;
import com.buuz135.seals.config.SealManager;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.storage.SealWorldStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SealRequestMessage implements CustomPacketPayload, IMessage {

    public static CustomPacketPayload.Type<SealRequestMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Seals.MOD_ID, "seal_request"));
    public static StreamCodec<? super RegistryFriendlyByteBuf, SealRequestMessage> CODEC = new StreamCodec<>() {
        @Override
        public SealRequestMessage decode(RegistryFriendlyByteBuf object) {
            return new SealRequestMessage(Identifier.parse(object.readUtf()));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, SealRequestMessage sealRequestMessage) {
            registryFriendlyByteBuf.writeUtf(sealRequestMessage.seal.toString());
        }
    };

    private Identifier seal;

    public SealRequestMessage(Identifier seal) {
        this.seal = seal;
    }

    public SealRequestMessage() {

    }

    @Override
    public void handle(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(() -> {
            Player entity = contextSupplier.player();
            if (entity instanceof ServerPlayer serverPlayer && entity.level() instanceof ServerLevel serverLevel) {
                var manager = new SealManager();
                manager.setSeals(contextSupplier.player().level(), Seals.getSealRecipes(serverLevel.getServer().getRecipeManager().recipeMap()));
                SealInfo sealInfo = manager.getSeal(this.seal);
                if (sealInfo != null && sealInfo.hasAchievedSeal(serverPlayer)) {
                    SealWorldStorage.get(serverLevel).put(entity.getUUID(), seal);
                    CompoundTag data = SealWorldStorage.get(serverLevel).toTag();
                    serverLevel.getPlayers(serverPlayer2 -> true).forEach(entity1 -> entity1.connection.send(new ClientSyncSealsMessage(data)));
                }
            }

        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
