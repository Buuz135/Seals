package com.buuz135.seals.datapack;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.IIcon;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SealInfo implements Recipe<Container> {

    private ResourceLocation sealID;
    private String sealLangKey;
    private List<ResourceLocation> requisites;
    private IIcon icon;
    private boolean invisible;

    public SealInfo(ResourceLocation sealID) {
        this.sealID = sealID;
        this.requisites = new ArrayList<>();
        this.invisible = false;
    }

    public SealInfo() {

    }

    public List<ResourceLocation> getRequisites() {
        return requisites;
    }

    public void setRequisites(List<ResourceLocation> requisites) {
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

    public void setSealLangKey(String sealLangKey) {
        this.sealLangKey = sealLangKey;
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

    public boolean hasAchievedSealClient(LocalPlayer entity) {
        return true;
    }

    public boolean hasAchievedSeal(ServerPlayer entity) {
        int completed = 0;
        for (ResourceLocation requisite : this.getRequisites()) {
            Advancement advancement = entity.getLevel().getServer().getAdvancements().getAdvancement(requisite);
            if (advancement != null && entity.getAdvancements().getOrStartProgress(advancement).isDone()) {
                ++completed;
            }
        }
        return completed == this.getRequisites().size();
    }

    @Override
    public boolean matches(Container p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(Container p_44001_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return sealID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Seals.EMOJI_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Seals.SEAL_RECIPE_TYPE.get();
    }
}
