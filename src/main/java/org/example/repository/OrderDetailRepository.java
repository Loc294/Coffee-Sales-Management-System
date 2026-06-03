package org.example.repository;

import org.example.entity.OrderDetail;
import java.util.List;

public interface OrderDetailRepository {
    void saveAll(List<OrderDetail> details);           // batch insert
    List<OrderDetail> findByOrderId(int orderId);
    void deleteByOrderId(int orderId);
}