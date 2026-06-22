/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.Map;

public class CraftingRecipeComposer
extends MessageComposer {
    private final CraftingRecipe recipe;

    public CraftingRecipeComposer(CraftingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2774);
        this.response.appendInt(this.recipe.getIngredients().size());
        for (Map.Entry<Item, Integer> ingredient : this.recipe.getIngredients().entrySet()) {
            this.response.appendInt(ingredient.getValue());
            this.response.appendString(ingredient.getKey().getName());
        }
        return this.response;
    }

    public CraftingRecipe getRecipe() {
        return this.recipe;
    }
}

