package org.example.controller;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.service.OrderService;
import org.example.service.impl.OrderServiceImpl;

import java.util.List;

public class OrderController {

    private final OrderService orderService = new OrderServiceImpl();

    /** Tạo đơn hàng mới từ danh sách chi tiết */
    public Order createOrder(List<OrderDetail> details, String note) {
        return orderService.createOrder(details, note);
    }

    /** Lấy đơn hàng kèm chi tiết để in hóa đơn */
    public Order getOrderWithDetails(int orderId) {
        return orderService.getOrderWithDetails(orderId);
    }

    /** Lấy toàn bộ đơn hàng */
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /** Hủy đơn */
    public void cancelOrder(int orderId) {
        orderService.cancelOrder(orderId);
    }

    /** Hoàn thành đơn (chuyển sang thanh toán) */
    public void completeOrder(int orderId) {
        orderService.completeOrder(orderId);
    }

    public Order getOrderById(int id) {return orderService.getOrderWithDetails(id);}

    public List<OrderDetail> getOrderDetails(int id) {
        return orderService.getOrderDetails(id);
    }

    /** Xóa đơn hàng */
    public void deleteOrder(int orderId) {
        orderService.delete(orderId);
    }
}