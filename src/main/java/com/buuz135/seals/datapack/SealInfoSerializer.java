package com.buuz135.seals.datapack;

import com.buuz135.seals.client.icon.ItemStackIcon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Arrays;

public class SealInfoSerializer implements RecipeSerializer<SealInfo> {

    private final StreamCodec<RegistryFriendlyByteBuf, SealInfo> codec;
    private final MapCodec<SealInfo> mapCodec;

    public SealInfoSerializer() {
        this.codec = new StreamCodec<RegistryFriendlyByteBuf, SealInfo>() {
            @Override
            public SealInfo decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
                var seal = new SealInfo(registryFriendlyByteBuf.readResourceLocation());
                seal.setSealLangKey(registryFriendlyByteBuf.readUtf());
                seal.setIcon(new ItemStackIcon(registryFriendlyByteBuf.readResourceLocation()));
                seal.setInvisible(registryFriendlyByteBuf.readBoolean());
                seal.setRequisites(Arrays.stream(registryFriendlyByteBuf.readArray(ResourceLocation[]::new, FriendlyByteBuf::readResourceLocation)).toList());
                return seal;
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buff, SealInfo sealInfo) {
                buff.writeResourceLocation(sealInfo.getSealID());
                buff.writeUtf(sealInfo.getSealLangKey());
                buff.writeResourceLocation(((ItemStackIcon) sealInfo.getIcon()).getStack());
                buff.writeBoolean(sealInfo.isInvisible());
                buff.writeArray(sealInfo.getRequisites().toArray(new ResourceLocation[0]), FriendlyByteBuf::writeResourceLocation);
            }
        };
        this.mapCodec = RecordCodecBuilder.mapCodec(instance -> {
            var test = instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::getGroup),
                    Codec.STRING.fieldOf("id").forGetter(sealInfo -> sealInfo.getSealID().toString()),
                    Codec.STRING.fieldOf("lang_key").forGetter(SealInfo::getSealLangKey),
                    Codec.STRING.listOf().fieldOf("requisites").forGetter(o -> o.getRequisites().stream().map(ResourceLocation::toString).toList()),
                    Codec.STRING.fieldOf("icon").forGetter(sealInfo -> sealInfo.getIcon().getStack().toString()),
                    Codec.BOOL.fieldOf("invisible").forGetter(SealInfo::isInvisible)
            );
            return test.apply(instance, (s, id, lang, requisites, icon, invisible) -> new SealInfo(ResourceLocation.parse(id),
                    lang,
                    requisites.stream().map(ResourceLocation::parse).toList(),
                    new ItemStackIcon(ResourceLocation.parse(icon)),
                    invisible
            ));
        });
    }

    @Override
    public MapCodec<SealInfo> codec() {
        return mapCodec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SealInfo> streamCodec() {
        return this.codec;
    }

}
