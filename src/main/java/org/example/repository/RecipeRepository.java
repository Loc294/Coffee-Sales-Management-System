package org.example.repository;

import org.example.entity.Recipe;

import java.util.List;

public interface RecipeRepository {

    List<Recipe> findByProductId(int productId);

    void save(Recipe recipe);

    void update(Recipe recipe);

    void deleteById(int id);

    void deleteByProductId(int productId);
}
