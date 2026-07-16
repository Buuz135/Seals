package com.buuz135.seals.datapack;

import com.buuz135.seals.client.icon.ItemStackIcon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class SealInfoSerializer {

    private static final MapCodec<SealInfo> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(seal -> seal.getSealID().toString()),
            Codec.STRING.fieldOf("lang_key").forGetter(SealInfo::getSealLangKey),
            Codec.STRING.listOf().fieldOf("requisites").forGetter(seal -> seal.getRequisites().stream().map(Identifier::toString).toList()),
            Codec.STRING.fieldOf("icon").forGetter(seal -> seal.getIcon().getStack().toString()),
            Codec.BOOL.fieldOf("invisible").forGetter(SealInfo::isInvisible)
    ).apply(instance, (id, lang, requisites, icon, invisible) -> new SealInfo(
            Identifier.parse(id), lang, requisites.stream().map(Identifier::parse).toList(), new ItemStackIcon(Identifier.parse(icon)), invisible
    )));
    private static final StreamCodec<RegistryFriendlyByteBuf, SealInfo> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SealInfo decode(RegistryFriendlyByteBuf buffer) {
            SealInfo seal = new SealInfo(Identifier.parse(buffer.readUtf()));
            seal.setSealLangKey(buffer.readUtf());
            seal.setIcon(new ItemStackIcon(Identifier.parse(buffer.readUtf())));
            seal.setInvisible(buffer.readBoolean());
            int count = buffer.readVarInt();
            seal.setRequisites(java.util.stream.IntStream.range(0, count).mapToObj(ignored -> Identifier.parse(buffer.readUtf())).toList());
            return seal;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, SealInfo seal) {
            buffer.writeUtf(seal.getSealID().toString());
            buffer.writeUtf(seal.getSealLangKey());
            buffer.writeUtf(seal.getIcon().getStack().toString());
            buffer.writeBoolean(seal.isInvisible());
            buffer.writeVarInt(seal.getRequisites().size());
            seal.getRequisites().forEach(id -> buffer.writeUtf(id.toString()));
        }
    };

    private SealInfoSerializer() {
    }

    public static RecipeSerializer<SealInfo> create() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
