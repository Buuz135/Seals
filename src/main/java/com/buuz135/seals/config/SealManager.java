package com.buuz135.seals.config;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ItemStackIcon;
import com.buuz135.seals.datapack.SealInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SealManager {

    private List<SealInfo> seals;

    public SealManager() {
        this.seals = new ArrayList<>();
        /*
        SealInfo sealInfo = new SealInfo(new ResourceLocation("seals:alchemist"), "alchemist");
        sealInfo.setRequisites(new ResourceLocation[]{new ResourceLocation("minecraft:nether/all_effects"), new ResourceLocation("minecraft:nether/all_potions")});
        sealInfo.setIcon(new ItemStackIcon(ForgeRegistries.ITEMS.getKey(Items.BREWING_STAND)));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:hero"), "hero");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:adventure/hero_of_the_village"),
                new ResourceLocation("minecraft:adventure/kill_all_mobs"),
                new ResourceLocation("minecraft:adventure/arbalistic")
        });
        sealInfo.setIcon(new ItemStackIcon(ForgeRegistries.ITEMS.getKey(Items.SHIELD)));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:explorer"), "explorer");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:adventure/adventuring_time"),
                new ResourceLocation("minecraft:nether/fast_travel")
        });
        sealInfo.setIcon(new ItemStackIcon(ForgeRegistries.ITEMS.getKey(Items.OAK_BOAT)));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:cultist.json"), "cultist.json");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:end/levitate"),
                new ResourceLocation("minecraft:end/respawn_dragon"),
                new ResourceLocation("minecraft:end/enter_end_gateway")
        });
        sealInfo.setIcon(new ItemStackIcon(ForgeRegistries.ITEMS.getKey(Items.ENDER_EYE)));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:pacifist"), "pacifist");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:husbandry/bred_all_animals"),
                new ResourceLocation("minecraft:husbandry/complete_catalogue"),
                new ResourceLocation("minecraft:husbandry/balanced_diet"),
                new ResourceLocation("minecraft:story/cure_zombie_villager")
        });
        sealInfo.setIcon(new ItemStackIcon(ForgeRegistries.ITEMS.getKey(Items.EMERALD)));
        seals.add(sealInfo);*/
    }

    public List<SealInfo> getSeals() {
        return seals;
    }

    public void setSeals(List<SealInfo> seals) {
        this.seals.clear();
        this.seals.addAll(seals);
        SealInfo info = new SealInfo(new ResourceLocation("seals:machinist")) {
            @Override
            public boolean hasAchievedSeal(ServerPlayer entity) {
                return entity.getAdvancements().toString().equals("d28b7061-fb92-4064-90fb-7e02b95a72a6");
            }

            @Override
            public boolean hasAchievedSealClient(LocalPlayer entity) {
                return entity.getUUID().toString().equals("d28b7061-fb92-4064-90fb-7e02b95a72a6");
            }
        };
        info.setSealLangKey("machinist");
        //info.setRequisites(new ResourceLocation[]{new ResourceLocation("minecraft:story/root")});
        info.setInvisible();
        info.setIcon(new ItemStackIcon(ForgeRegistries.BLOCKS.getKey(Blocks.FURNACE)));
        this.seals.add(info);
        info = new SealInfo(new ResourceLocation("seals:patreon")) {
            @Override
            public boolean hasAchievedSeal(ServerPlayer entity) {
                return Seals.PATREONS.stream().anyMatch(uuid -> uuid.equals(entity.getUUID()));
            }

            @Override
            public boolean hasAchievedSealClient(LocalPlayer entity) {
                return Seals.PATREONS.stream().anyMatch(uuid -> uuid.equals(entity.getUUID()));
            }
        };
        //info.setRequisites(new ResourceLocation[]{new ResourceLocation("minecraft:story/root")});
        info.setSealLangKey("munificent");
        info.setInvisible();
        info.setIcon(new ItemStackIcon(ForgeRegistries.ITEMS.getKey(Items.NETHER_STAR)));
        this.seals.add(info);
    }

    @Nullable
    public SealInfo getSeal(ResourceLocation resourceLocation) {
        for (SealInfo seal : seals) {
            if (seal.getSealID().equals(resourceLocation)) return seal;
        }
        return null;
    }
}
