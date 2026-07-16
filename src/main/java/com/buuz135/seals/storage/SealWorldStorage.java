package com.buuz135.seals.storage;


import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.UUID;

public class SealWorldStorage extends SavedData {

    private static final SavedDataType<SealWorldStorage> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("seals", "seals"),
            SealWorldStorage::new,
            Codec.unboundedMap(Codec.STRING, Identifier.CODEC).xmap(SealWorldStorage::new, storage -> storage.seals)
    );
    private HashMap<String, Identifier> seals;

    public SealWorldStorage() {
        this.seals = new HashMap<>();
    }

    private SealWorldStorage(java.util.Map<String, Identifier> seals) {
        this.seals = new HashMap<>(seals);
    }

    public static SealWorldStorage get(ServerLevel serverWorld) {
        return serverWorld.getDataStorage().computeIfAbsent(TYPE);
    }

    public void put(UUID uuid, Identifier resourceLocation) {
        if (this.seals.containsKey(uuid.toString()) && this.seals.get(uuid.toString()).equals(resourceLocation)) {
            this.seals.remove(uuid.toString());
        } else {
            this.seals.put(uuid.toString(), resourceLocation);
        }
        this.setDirty();
    }

    public HashMap<String, Identifier> getSeals() {
        return seals;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        seals.forEach((uuid, seal) -> tag.putString(uuid, seal.toString()));
        return tag;
    }

}
