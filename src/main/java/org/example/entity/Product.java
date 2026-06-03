package org.example.entity;

import java.time.LocalDateTime;

public class Product {

    private int id;
    private String name;
    private double price;

    private boolean active;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Product() {
    }

    // Full constructor (dùng khi lấy dữ liệu từ DB)
    public Product(int id, String name, double price,
                   boolean active,
                   LocalDateTime createdTime,
                   LocalDateTime updatedTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.active = active;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // Constructor khi insert mới (không cần id, time)
    public Product(String name, double price, boolean active) {
        this.name = name;
        this.price = price;
        this.active = active;
    }

    // ===== GETTER & SETTER =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return this.name; // hoặc this.getName()
    }

}