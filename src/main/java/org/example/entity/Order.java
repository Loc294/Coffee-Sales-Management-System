package org.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int id;
    private String orderCode;
    private double totalAmount;
    private String status;          // PENDING | COMPLETED | CANCELLED
    private String note;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // Danh sách chi tiết (dùng khi hiển thị hóa đơn, không persist riêng)
    private List<OrderDetail> details = new ArrayList<>();

    public Order() {}

    public Order(String orderCode, String note) {
        this.orderCode = orderCode;
        this.note = note;
        this.status = "PENDING";
    }

    // ===== GETTERS & SETTERS =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> details) { this.details = details; }
}