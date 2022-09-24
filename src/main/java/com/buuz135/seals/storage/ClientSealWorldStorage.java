package com.buuz135.seals.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ClientSealWorldStorage {

    public static final ClientSealWorldStorage SEALS = new ClientSealWorldStorage();

    private final HashMap<String, ResourceLocation> clientSeals;

    public ClientSealWorldStorage() {
        this.clientSeals = new HashMap<>();
    }

    public HashMap<String, ResourceLocation> getClientSeals() {
        return clientSeals;
    }

    public void deserialize(CompoundTag nbt) {
        clientSeals.clear();
        for (String name : nbt.getAllKeys()) {
            clientSeals.put(name, new ResourceLocation(nbt.getString(name)));
        }
    }
}
