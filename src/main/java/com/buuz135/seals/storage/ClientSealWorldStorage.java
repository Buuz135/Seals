package com.buuz135.seals.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.UUID;

public class ClientSealWorldStorage {

    public static final ClientSealWorldStorage SEALS = new ClientSealWorldStorage();

    private final HashMap<String, ResourceLocation> clientSeals;

    public ClientSealWorldStorage() {
        this.clientSeals = new HashMap<>();
    }

    public HashMap<String, ResourceLocation> getClientSeals() {
        return clientSeals;
    }

    public void deserialize(CompoundNBT nbt) {
        clientSeals.clear();
        for (String name : nbt.keySet()) {
            clientSeals.put(name, new ResourceLocation(nbt.getString(name)));
        }
    }
}
