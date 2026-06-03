package org.example.service;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import java.util.List;

public interface OrderService {
    Order createOrder(List<OrderDetail> details, String note);
    void cancelOrder(int orderId);
    void completeOrder(int orderId);
    Order getOrderWithDetails(int orderId);
    List<Order> getAllOrders();
    List<OrderDetail> getOrderDetails(int orderId);
    void delete(int orderId);
}