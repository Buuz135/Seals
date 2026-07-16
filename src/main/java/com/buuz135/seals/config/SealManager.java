package com.buuz135.seals.config;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ItemStackIcon;
import com.buuz135.seals.datapack.SealInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SealManager {

    private List<SealInfo> seals;

    public SealManager() {
        this.seals = new ArrayList<>();
    }

    public List<SealInfo> getSeals() {
        return seals;
    }

    public void setSeals(Level level, List<SealInfo> seals) {
        this.seals.clear();
        this.seals.addAll(seals);
        SealInfo info = new SealInfo(Identifier.parse("seals:machinist")) {
            @Override
            public boolean hasAchievedSeal(ServerPlayer entity) {
                return entity.getUUID().toString().equals("d28b7061-fb92-4064-90fb-7e02b95a72a6");
            }

            @Override
            public boolean hasAchievedSealClient(LocalPlayer entity) {
                return entity.getUUID().toString().equals("d28b7061-fb92-4064-90fb-7e02b95a72a6");
            }
        };
        info.setSealLangKey("machinist");
        //info.setRequisites(new Identifier[]{new Identifier("minecraft:story/root")});
        info.setInvisible();
        info.setIcon(new ItemStackIcon(level.registryAccess().lookupOrThrow(Registries.BLOCK).getKey(Blocks.FURNACE)));
        this.seals.add(info);
        info = new SealInfo(Identifier.parse("seals:patreon")) {
            @Override
            public boolean hasAchievedSeal(ServerPlayer entity) {
                return Seals.PATREONS.stream().anyMatch(uuid -> uuid.equals(entity.getUUID()));
            }

            @Override
            public boolean hasAchievedSealClient(LocalPlayer entity) {
                return Seals.PATREONS.stream().anyMatch(uuid -> uuid.equals(entity.getUUID()));
            }
        };
        //info.setRequisites(new Identifier[]{new Identifier("minecraft:story/root")});
        info.setSealLangKey("munificent");
        info.setInvisible();
        info.setIcon(new ItemStackIcon(level.registryAccess().lookupOrThrow(Registries.ITEM).getKey(Items.NETHER_STAR)));
        this.seals.add(info);
        for (SealInfo seal : this.seals) {
            System.out.println(seal.getSealLangKey());
        }
    }

    @Nullable
    public SealInfo getSeal(Identifier resourceLocation) {
        for (SealInfo seal : seals) {
            if (seal.getSealID().equals(resourceLocation)) return seal;
        }
        return null;
    }
}
