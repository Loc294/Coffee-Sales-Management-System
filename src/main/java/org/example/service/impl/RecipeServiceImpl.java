package org.example.service.impl;

import org.example.entity.Recipe;
import org.example.repository.RecipeRepository;
import org.example.repository.impl.RecipeRepositoryImpl;
import org.example.service.RecipeService;

import java.util.List;

public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl() {
        this.recipeRepository = new RecipeRepositoryImpl();
    }

    @Override
    public List<Recipe> findByProductId(int productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Product ID không hợp lệ");
        }
        return recipeRepository.findByProductId(productId);
    }

    @Override
    public void create(Recipe recipe) {

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe không được null");
        }

        if (recipe.getProductId() <= 0) {
            throw new IllegalArgumentException("Product ID không hợp lệ");
        }

        if (recipe.getIngredientName() == null || recipe.getIngredientName().isBlank()) {
            throw new IllegalArgumentException("Tên nguyên liệu không được trống");
        }

        if (recipe.getAmount() <= 0) {
            throw new IllegalArgumentException("Số lượng phải > 0");
        }

        recipeRepository.save(recipe);
    }

    @Override
    public void update(Recipe recipe) {

        if (recipe.getId() <= 0) {
            throw new IllegalArgumentException("Recipe ID không hợp lệ");
        }

        recipeRepository.update(recipe);
    }

    @Override
    public void deleteById(int id) {
        recipeRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(int productId) {
        recipeRepository.deleteByProductId(productId);
    }
}
