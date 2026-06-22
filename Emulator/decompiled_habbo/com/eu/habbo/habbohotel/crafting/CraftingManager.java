/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingAltar;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CraftingManager.class);
    private final THashMap<Item, CraftingAltar> altars = new THashMap();

    public CraftingManager() {
        this.reload();
    }

    public void reload() {
        this.dispose();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM crafting_altars_recipes INNER JOIN crafting_recipes ON crafting_altars_recipes.recipe_id = crafting_recipes.id INNER JOIN crafting_recipes_ingredients ON crafting_recipes.id = crafting_recipes_ingredients.recipe_id WHERE crafting_recipes.enabled = ? ORDER BY altar_id ASC");){
            statement.setString(1, "1");
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    Item ingredientItem;
                    CraftingAltar altar;
                    Item item = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("altar_id"));
                    if (item == null) continue;
                    if (!this.altars.containsKey(item)) {
                        this.altars.put(item, new CraftingAltar(item));
                    }
                    if ((altar = this.altars.get(item)) == null) continue;
                    CraftingRecipe recipe = altar.getRecipe(set.getInt("crafting_recipes_ingredients.recipe_id"));
                    if (recipe == null) {
                        recipe = new CraftingRecipe(set);
                        altar.addRecipe(recipe);
                    }
                    if ((ingredientItem = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("crafting_recipes_ingredients.item_id"))) != null) {
                        recipe.addIngredient(ingredientItem, set.getInt("crafting_recipes_ingredients.amount"));
                        altar.addIngredient(ingredientItem);
                        continue;
                    }
                    LOGGER.error("Unknown ingredient item {}", (Object)set.getInt("crafting_recipes_ingredients.item_id"));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRecipesWithItemCount(final Item item) {
        final int[] i = new int[]{0};
        THashMap<Item, CraftingAltar> tHashMap = this.altars;
        synchronized (tHashMap) {
            this.altars.forEachValue(new TObjectProcedure<CraftingAltar>(){
                final /* synthetic */ CraftingManager this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(CraftingAltar altar) {
                    if (altar.hasIngredient(item)) {
                        i[0] = i[0] + 1;
                    }
                    return true;
                }
            });
        }
        return i[0];
    }

    public CraftingRecipe getRecipe(String recipeName) {
        for (CraftingAltar altar : this.altars.values()) {
            CraftingRecipe recipe = altar.getRecipe(recipeName);
            if (recipe == null) continue;
            return recipe;
        }
        return null;
    }

    public CraftingAltar getAltar(Item item) {
        return this.altars.get(item);
    }

    public void dispose() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE crafting_recipes SET remaining = ? WHERE id = ? LIMIT 1");){
            for (CraftingAltar altar : this.altars.values()) {
                for (CraftingRecipe recipe : altar.getRecipes()) {
                    if (!recipe.isLimited()) continue;
                    statement.setInt(1, recipe.getRemaining());
                    statement.setInt(2, recipe.getId());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        this.altars.clear();
    }
}

