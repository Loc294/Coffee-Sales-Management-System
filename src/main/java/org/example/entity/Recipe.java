package org.example.entity;

import java.time.LocalDateTime;

public class Recipe {

    private int id;
    private int productId;

    private String ingredientName;
    private double amount;
    private String unit;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Recipe() {}

    public Recipe(int productId,
                  String ingredientName,
                  double amount,
                  String unit) {
        this.productId = productId;
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }

    // ===== GET SET =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
