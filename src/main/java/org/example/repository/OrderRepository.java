package org.example.repository;

import org.example.entity.Order;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    void updateStatus(int orderId, String status);
    Order findById(int id);
    List<Order> findAll();
    List<Order> findByStatus(String status);
    void delete(int id);
}
