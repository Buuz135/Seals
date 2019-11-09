package com.buuz135.seals.network;

import com.buuz135.seals.SealInfo;
import com.buuz135.seals.Seals;
import com.buuz135.seals.storage.SealWorldStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SealRequestMessage implements IMessage {

    private ResourceLocation seal;

    public SealRequestMessage(ResourceLocation seal) {
        this.seal = seal;
    }

    public SealRequestMessage() {

    }

    @Override
    public SealRequestMessage fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        seal = packetBuffer.readResourceLocation();
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeResourceLocation(seal);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity entity = contextSupplier.get().getSender();
            SealInfo sealInfo = Seals.SEAL_MANAGER.getSeal(seal);
            if (sealInfo != null && sealInfo.hasAchievedSeal(entity)) {
                SealWorldStorage.get(entity.getServerWorld()).put(entity.getUniqueID(), seal);
                CompoundNBT data = SealWorldStorage.get(entity.getServerWorld()).serializeNBT();
                entity.getServerWorld().getPlayers().forEach(entity1 -> Seals.NETWORK.sendTo(new ClientSyncSealsMessage(data), entity1.connection.netManager, NetworkDirection.PLAY_TO_CLIENT));
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
