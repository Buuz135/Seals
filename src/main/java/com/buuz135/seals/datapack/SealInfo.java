package com.buuz135.seals.datapack;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ItemStackIcon;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SealInfo extends CustomRecipe {

    private ResourceLocation sealID;
    private String sealLangKey;
    private List<ResourceLocation> requisites;
    private ItemStackIcon icon;
    private boolean invisible;

    public SealInfo(ResourceLocation sealID) {
        super(CraftingBookCategory.MISC);
        this.sealID = sealID;
        this.requisites = new ArrayList<>();
        this.invisible = false;
    }

    public SealInfo(ResourceLocation sealID, String sealLangKey, List<ResourceLocation> requisites, ItemStackIcon icon, boolean invisible) {
        super(CraftingBookCategory.MISC);
        this.sealID = sealID;
        this.sealLangKey = sealLangKey;
        this.requisites = requisites;
        this.icon = icon;
        this.invisible = invisible;
    }

    public List<ResourceLocation> getRequisites() {
        return requisites;
    }

    public void setRequisites(List<ResourceLocation> requisites) {
        this.requisites = requisites;
    }

    public ItemStackIcon getIcon() {
        return icon;
    }

    public void setIcon(ItemStackIcon icon) {
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

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean hasAchievedSealClient(LocalPlayer entity) {
        return true;
    }

    public boolean hasAchievedSeal(ServerPlayer entity) {
        int completed = 0;
        for (ResourceLocation requisite : this.getRequisites()) {
            var advancement = entity.level().getServer().getAdvancements().get(requisite);
            if (advancement != null && entity.getAdvancements().getOrStartProgress(advancement).isDone()) {
                ++completed;
            }
        }
        return completed == this.getRequisites().size();
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return ItemStack.EMPTY;
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
