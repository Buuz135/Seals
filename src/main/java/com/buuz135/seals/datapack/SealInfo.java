package com.buuz135.seals.datapack;

import com.buuz135.seals.Seals;
import com.buuz135.seals.client.icon.ItemStackIcon;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SealInfo extends CustomRecipe {

    private Identifier sealID;
    private String sealLangKey;
    private List<Identifier> requisites;
    private ItemStackIcon icon;
    private boolean invisible;

    public SealInfo(Identifier sealID) {
        super();
        this.sealID = sealID;
        this.requisites = new ArrayList<>();
        this.invisible = false;
    }

    public SealInfo(Identifier sealID, String sealLangKey, List<Identifier> requisites, ItemStackIcon icon, boolean invisible) {
        super();
        this.sealID = sealID;
        this.sealLangKey = sealLangKey;
        this.requisites = requisites;
        this.icon = icon;
        this.invisible = invisible;
    }

    public List<Identifier> getRequisites() {
        return requisites;
    }

    public void setRequisites(List<Identifier> requisites) {
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

    public Identifier getSealID() {
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
        for (Identifier requisite : this.getRequisites()) {
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
    public ItemStack assemble(CraftingInput craftingInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<SealInfo> getSerializer() {
        return Seals.EMOJI_RECIPE_SERIALIZER.get();
    }

}
