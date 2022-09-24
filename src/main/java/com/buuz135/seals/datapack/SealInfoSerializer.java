package com.buuz135.seals.datapack;

import com.buuz135.seals.client.icon.ItemStackIcon;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class SealInfoSerializer implements RecipeSerializer<SealInfo> {

    @Override
    public SealInfo fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        var seal = new SealInfo(resourceLocation);
        seal.setSealLangKey(jsonObject.get("lang_key").getAsString());
        var reqs = new ArrayList<ResourceLocation>();
        jsonObject.getAsJsonArray("requisites").forEach(jsonElement -> {
            reqs.add(new ResourceLocation(jsonElement.getAsString()));
        });
        seal.setRequisites(reqs);

        //TODO IMPROVE
        seal.setIcon(new ItemStackIcon(new ResourceLocation(jsonObject.getAsJsonObject("icon").getAsJsonObject("value").getAsJsonPrimitive("stack").getAsString())));

        if (jsonObject.get("invisible").getAsBoolean()) seal.setInvisible();
        return seal;
    }

    @Override
    public @Nullable SealInfo fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
        var seal = new SealInfo(resourceLocation);
        for (int i = 0; i < buf.readInt(); i++) {
            seal.getRequisites().add(buf.readResourceLocation());
        }
        //TODO IMPROVE
        seal.setIcon(new ItemStackIcon(buf.readResourceLocation()));

        if (buf.readBoolean()) seal.setInvisible();

        return seal;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, SealInfo sealInfo) {
        buf.writeCharSequence(sealInfo.getSealLangKey(), Charset.defaultCharset());
        buf.writeInt(sealInfo.getRequisites().size());
        sealInfo.getRequisites().forEach(buf::writeResourceLocation);

        //TODO IMPROVE
        buf.writeResourceLocation(((ItemStackIcon) sealInfo.getIcon()).getStack());

        buf.writeBoolean(sealInfo.isInvisible());

    }
}
