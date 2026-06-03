package org.example.controller;

import org.example.entity.Recipe;
import org.example.service.RecipeService;
import org.example.service.impl.RecipeServiceImpl;

import java.util.List;

public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController() {
        this.recipeService = new RecipeServiceImpl();
    }

    public List<Recipe> getRecipeByProduct(int productId) {
        return recipeService.findByProductId(productId);
    }

    public void createRecipe(int productId,
                             String ingredientName,
                             double amount,
                             String unit) {

        Recipe recipe = new Recipe(
                productId,
                ingredientName,
                amount,
                unit
        );

        recipeService.create(recipe);
    }

    public void updateRecipe(Recipe recipe) {
        recipeService.update(recipe);
    }

    public void deleteRecipe(int id) {
        recipeService.deleteById(id);
    }

    public void deleteRecipeByProduct(int productId) {
        recipeService.deleteByProductId(productId);
    }
}
