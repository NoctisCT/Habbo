/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CraftingResultComposer
extends MessageComposer {
    private final CraftingRecipe recipe;
    private final boolean succes;

    public CraftingResultComposer(CraftingRecipe recipe) {
        this.recipe = recipe;
        this.succes = this.recipe != null;
    }

    public CraftingResultComposer(CraftingRecipe recipe, boolean success) {
        this.recipe = recipe;
        this.succes = success;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(618);
        this.response.appendBoolean(this.succes);
        if (this.recipe != null) {
            this.response.appendString(this.recipe.getName());
            this.response.appendString(this.recipe.getReward().getName());
        }
        return this.response;
    }

    public CraftingRecipe getRecipe() {
        return this.recipe;
    }

    public boolean isSucces() {
        return this.succes;
    }
}

