package com.buuz135.seals.network;

import com.buuz135.seals.Seals;
import com.buuz135.seals.config.SealManager;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.storage.SealWorldStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SealRequestMessage implements IMessage {

    private ResourceLocation seal;

    public SealRequestMessage(ResourceLocation seal) {
        this.seal = seal;
    }

    public SealRequestMessage() {

    }

    @Override
    public SealRequestMessage fromBytes(FriendlyByteBuf buf) {
        seal = buf.readResourceLocation();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(seal);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer entity = contextSupplier.get().getSender();
            if (entity.level() instanceof ServerLevel serverLevel) {
                var manager = new SealManager();
                manager.setSeals(serverLevel.getRecipeManager().getAllRecipesFor(Seals.SEAL_RECIPE_TYPE.get()));
                SealInfo sealInfo = manager.getSeal(this.seal);
                if (sealInfo != null && sealInfo.hasAchievedSeal(entity)) {
                    SealWorldStorage.get(serverLevel).put(entity.getUUID(), seal);
                    CompoundTag data = SealWorldStorage.get(serverLevel).save(new CompoundTag());
                    serverLevel.getPlayers(serverPlayer -> true).forEach(entity1 -> Seals.NETWORK.sendTo(new ClientSyncSealsMessage(data), entity1.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
                }
            }

        });
        contextSupplier.get().setPacketHandled(true);
    }
}
