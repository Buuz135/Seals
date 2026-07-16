package com.buuz135.seals.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class ClientSealWorldStorage {

    public static final ClientSealWorldStorage SEALS = new ClientSealWorldStorage();

    private final HashMap<String, Identifier> clientSeals;

    public ClientSealWorldStorage() {
        this.clientSeals = new HashMap<>();
    }

    public HashMap<String, Identifier> getClientSeals() {
        return clientSeals;
    }

    public void deserialize(CompoundTag nbt) {
        clientSeals.clear();
        for (String name : nbt.keySet()) {
            clientSeals.put(name, Identifier.parse(nbt.getStringOr(name, "")));
        }
    }
}
