package org.example.entity;


import java.time.LocalDateTime;

public class Employee {

    private int id;
    private String name;
    private String phone;
    private String position;
    private Integer accountId; // CÓ THỂ NULL
    private String username;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;


    public Employee() {}

    public Employee(String name, String phone, String position, Integer accountId) {
        this.name = name;
        this.phone = phone;
        this.position = position;
        this.accountId = accountId;
    }

    // ===== GETTER & SETTER =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
