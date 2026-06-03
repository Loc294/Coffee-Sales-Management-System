package org.example.service.impl;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.repository.OrderDetailRepository;
import org.example.repository.OrderRepository;
import org.example.repository.impl.OrderDetailRepositoryImpl;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.OrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo = new OrderRepositoryImpl();
    private final OrderDetailRepository detailRepo = new OrderDetailRepositoryImpl();

    @Override
    public Order createOrder(List<OrderDetail> details, String note) {
        if (details == null || details.isEmpty())
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 sản phẩm");

        // Tính tổng tiền
        double total = details.stream()
                .mapToDouble(OrderDetail::getSubtotal)
                .sum();

        // Tạo mã đơn hàng tự động
        String code = generateOrderCode();

        Order order = new Order(code, note);
        order.setTotalAmount(total);
        order.setStatus("PENDING");

        // Lưu order (lấy được generated id)
        orderRepo.save(order);

        // Gắn order_id cho từng detail rồi lưu
        for (OrderDetail d : details) {
            d.setOrderId(order.getId());
        }
        detailRepo.saveAll(details);

        order.setDetails(details);
        return order;
    }

    @Override
    public void cancelOrder(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order == null)
            throw new IllegalArgumentException("Không tìm thấy đơn hàng ID = " + orderId);
        if ("COMPLETED".equals(order.getStatus()))
            throw new IllegalStateException("Không thể hủy đơn đã hoàn thành");
        orderRepo.updateStatus(orderId, "CANCELLED");
    }

    @Override
    public void completeOrder(int orderId) {
        orderRepo.updateStatus(orderId, "COMPLETED");
    }

    @Override
    public Order getOrderWithDetails(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order != null) {
            order.setDetails(detailRepo.findByOrderId(orderId));
        }
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public List<OrderDetail> getOrderDetails(int orderId) {
        return detailRepo.findByOrderId(orderId);
    }

    @Override
    public void delete(int orderId) {
        orderRepo.delete(orderId);
    }



    // ===== Tạo mã đơn hàng =====
    private String generateOrderCode() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return "ORD-" + timestamp;
    }
}