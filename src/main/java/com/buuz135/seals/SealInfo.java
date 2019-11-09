package com.buuz135.seals;

import com.buuz135.seals.client.icon.IIcon;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class SealInfo {

    private ResourceLocation sealID;
    private String sealLangKey;
    private ResourceLocation[] requisites;
    private IIcon icon;
    private boolean invisible;

    public SealInfo(ResourceLocation sealID, String sealLangKey) {
        this.sealID = sealID;
        this.sealLangKey = sealLangKey;
        this.requisites = new ResourceLocation[]{};
        this.invisible = false;
    }

    public SealInfo() {

    }

    public ResourceLocation[] getRequisites() {
        return requisites;
    }

    public void setRequisites(ResourceLocation[] requisites) {
        this.requisites = requisites;
    }

    public IIcon getIcon() {
        return icon;
    }

    public void setIcon(IIcon icon) {
        this.icon = icon;
    }

    public String getSealLangKey() {
        return sealLangKey;
    }

    public ResourceLocation getSealID() {
        return sealID;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible() {
        this.invisible = true;
    }

    public boolean hasAchievedSealClient(ClientPlayerEntity entity) {
        return true;
    }

    public boolean hasAchievedSeal(ServerPlayerEntity entity) {
        int completed = 0;
        for (ResourceLocation requisite : this.getRequisites()) {
            Advancement advancement = entity.getServerWorld().getServer().getAdvancementManager().getAdvancement(requisite);
            if (advancement != null && entity.getAdvancements().getProgress(advancement).isDone()) {
                ++completed;
            }
        }
        return completed == this.getRequisites().length;
    }
}
