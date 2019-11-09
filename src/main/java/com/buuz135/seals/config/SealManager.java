package com.buuz135.seals.config;

import com.buuz135.seals.SealInfo;
import com.buuz135.seals.client.icon.ItemStackIcon;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SealManager {

    private List<SealInfo> seals;

    public SealManager() {
        this.seals = new ArrayList<>();
        SealInfo sealInfo = new SealInfo(new ResourceLocation("seals:alchemist"), "alchemist");
        sealInfo.setRequisites(new ResourceLocation[]{new ResourceLocation("minecraft:nether/all_effects"), new ResourceLocation("minecraft:nether/all_potions")});
        sealInfo.setIcon(new ItemStackIcon(Items.BREWING_STAND.getRegistryName()));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:hero"), "hero");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:adventure/hero_of_the_village"),
                new ResourceLocation("minecraft:adventure/kill_all_mobs"),
                new ResourceLocation("minecraft:adventure/arbalistic")
        });
        sealInfo.setIcon(new ItemStackIcon(Items.SHIELD.getRegistryName()));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:explorer"), "explorer");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:adventure/adventuring_time"),
                new ResourceLocation("minecraft:nether/fast_travel")
        });
        sealInfo.setIcon(new ItemStackIcon(Items.OAK_BOAT.getRegistryName()));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:cultist"), "cultist");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:end/levitate"),
                new ResourceLocation("minecraft:end/respawn_dragon"),
                new ResourceLocation("minecraft:end/enter_end_gateway")
        });
        sealInfo.setIcon(new ItemStackIcon(Items.ENDER_EYE.getRegistryName()));
        seals.add(sealInfo);
        sealInfo = new SealInfo(new ResourceLocation("seals:pacifist"), "pacifist");
        sealInfo.setRequisites(new ResourceLocation[]{
                new ResourceLocation("minecraft:husbandry/bred_all_animals"),
                new ResourceLocation("minecraft:husbandry/complete_catalogue"),
                new ResourceLocation("minecraft:husbandry/balanced_diet"),
                new ResourceLocation("minecraft:husbandry/break_diamond_hoe"),
                new ResourceLocation("minecraft:story/cure_zombie_villager")
        });
        sealInfo.setIcon(new ItemStackIcon(Items.EMERALD.getRegistryName()));
        seals.add(sealInfo);
    }

    public List<SealInfo> getSeals() {
        return seals;
    }

    public void setSeals(List<SealInfo> seals) {
        this.seals.clear();
        this.seals.addAll(seals);
    }

    @Nullable
    public SealInfo getSeal(ResourceLocation resourceLocation) {
        for (SealInfo seal : seals) {
            if (seal.getSealID().equals(resourceLocation)) return seal;
        }
        return null;
    }
}
