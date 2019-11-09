package com.buuz135.seals.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.UUID;

public class SealWorldStorage extends WorldSavedData {

    public static String NAME = "Seals";
    private HashMap<String, ResourceLocation> seals;

    public SealWorldStorage() {
        this(NAME);
    }

    public SealWorldStorage(String name) {
        super(NAME);
        this.seals = new HashMap<>();
    }

    public static SealWorldStorage get(ServerWorld serverWorld) {
        return serverWorld.getSavedData().getOrCreate(SealWorldStorage::new, NAME);
    }

    public void put(UUID uuid, ResourceLocation resourceLocation) {
        if (this.seals.containsKey(uuid.toString()) && this.seals.get(uuid.toString()).equals(resourceLocation)){
            this.seals.remove(uuid.toString());
        } else {
            this.seals.put(uuid.toString(), resourceLocation);
        }
        markDirty();
    }

    public HashMap<String, ResourceLocation> getSeals() {
        return seals;
    }

    @Override
    public void read(CompoundNBT nbt) {
        seals.clear();
        for (String name : nbt.keySet()) {
            seals.put(name, new ResourceLocation(nbt.getString(name)));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        seals.forEach((uuid, resourceLocation) -> compound.putString(uuid, resourceLocation.toString()));
        return compound;
    }
}
