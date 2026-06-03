package org.example.service;

import org.example.entity.Recipe;

import java.util.List;

public interface RecipeService {

    List<Recipe> findByProductId(int productId);

    void create(Recipe recipe);

    void update(Recipe recipe);

    void deleteById(int id);

    void deleteByProductId(int productId);
}
