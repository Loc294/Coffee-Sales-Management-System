package org.example.entity;

import java.time.LocalDateTime;

public class Account {

    private int id;
    private String username;
    private String password;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private String role;
    private boolean active;

    public Account() {
    }

    // Full constructor
    public Account(int id, String username, String password,
                   LocalDateTime createdTime, LocalDateTime updateTime, String role,
                   boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdTime = createdTime;
        this.updateTime = updateTime;
        this.role = role;
        this.active = active;
    }

    // Constructor khi insert mới (chỉ cần username + password + active)
    public Account(String username, String password, String role, boolean active) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getRole() {return role;}

    public void setRole(String role) {this.role = role;}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}

