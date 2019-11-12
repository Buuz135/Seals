package com.buuz135.seals.config;

import com.buuz135.seals.SealInfo;
import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.IIcon;
import com.buuz135.seals.client.icon.ItemStackIcon;
import com.google.gson.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

public class JSONConfigLoader {

    private static final JsonDeserializer<ResourceLocation> RESOURCE_LOCATION_DESERIALIZER = (json, typeOfT, context) -> new ResourceLocation(json.getAsString());
    private static final JsonSerializer<ResourceLocation> RESOURCE_LOCATION_SERIALIZER = (src, typeOfSrc, context) -> new JsonPrimitive(src.toString());
    private static final JsonDeserializer<IIcon> ICON_DESERIALIZER = new JsonDeserializer<IIcon>() {
        @Override
        public IIcon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return GSON.fromJson(json.getAsJsonObject().get("value"), ItemStackIcon.class);
        }
    };
    private static final JsonSerializer<IIcon> ICON_SERIALIZER = JSONConfigLoader::serialize;
    public static Gson GSON = new GsonBuilder().
            registerTypeAdapter(ResourceLocation.class, RESOURCE_LOCATION_DESERIALIZER).registerTypeAdapter(ResourceLocation.class, RESOURCE_LOCATION_SERIALIZER).
            registerTypeAdapter(IIcon.class, ICON_DESERIALIZER).registerTypeAdapter(IIcon.class, ICON_SERIALIZER)
            .setPrettyPrinting().create();
    private File file;

    public JSONConfigLoader() {
        file = new File("./config/seals.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                GSON.toJson(Seals.SEAL_MANAGER.getSeals(), writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileReader reader = new FileReader(file);
                Seals.SEAL_MANAGER.setSeals(Arrays.asList(GSON.fromJson(reader, SealInfo[].class)));
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SealInfo info = new SealInfo(new ResourceLocation("seals:machinist"), "machinist"){
            @Override
            public boolean hasAchievedSeal(ServerPlayerEntity entity) {
                return entity.getUniqueID().toString().equals("d28b7061-fb92-4064-90fb-7e02b95a72a6");
            }

            @Override
            public boolean hasAchievedSealClient(ClientPlayerEntity entity) {
                return entity.getUniqueID().toString().equals("d28b7061-fb92-4064-90fb-7e02b95a72a6");
            }
        };
        //info.setRequisites(new ResourceLocation[]{new ResourceLocation("minecraft:story/root")});
        info.setInvisible();
        info.setIcon(new ItemStackIcon(Blocks.FURNACE.getRegistryName()));
        Seals.SEAL_MANAGER.getSeals().add(info);
        info = new SealInfo(new ResourceLocation("seals:patreon"), "munificent"){
            @Override
            public boolean hasAchievedSeal(ServerPlayerEntity entity) {
                return Seals.PATREONS.stream().anyMatch(uuid -> uuid.equals(entity.getUniqueID()));
            }

            @Override
            public boolean hasAchievedSealClient(ClientPlayerEntity entity) {
                return Seals.PATREONS.stream().anyMatch(uuid -> uuid.equals(entity.getUniqueID()));
            }
        };
        //info.setRequisites(new ResourceLocation[]{new ResourceLocation("minecraft:story/root")});
        info.setInvisible();
        info.setIcon(new ItemStackIcon(Items.NETHER_STAR.getRegistryName()));
        Seals.SEAL_MANAGER.getSeals().add(info);
    }

    private static JsonElement serialize(IIcon src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", src.getName());
        object.add("value", GSON.toJsonTree(src));
        return object;
    }
}
