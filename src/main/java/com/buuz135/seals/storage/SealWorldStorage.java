package com.buuz135.seals.storage;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.UUID;

public class SealWorldStorage extends SavedData {

    public static String NAME = "Seals";
    private HashMap<String, ResourceLocation> seals;

    public SealWorldStorage() {
        this.seals = new HashMap<>();
    }

    public static SealWorldStorage get(ServerLevel serverWorld) {
        return serverWorld.getDataStorage().computeIfAbsent(compoundTag -> {
            var data = new SealWorldStorage();
            data.read(compoundTag);
            return data;
        }, SealWorldStorage::new, NAME);
    }

    public void put(UUID uuid, ResourceLocation resourceLocation) {
        if (this.seals.containsKey(uuid.toString()) && this.seals.get(uuid.toString()).equals(resourceLocation)) {
            this.seals.remove(uuid.toString());
        } else {
            this.seals.put(uuid.toString(), resourceLocation);
        }
        this.setDirty();
    }

    public HashMap<String, ResourceLocation> getSeals() {
        return seals;
    }


    public void read(CompoundTag nbt) {
        seals.clear();
        for (String name : nbt.getAllKeys()) {
            seals.put(name, new ResourceLocation(nbt.getString(name)));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        seals.forEach((uuid, resourceLocation) -> compound.putString(uuid, resourceLocation.toString()));
        return compound;
    }
}
