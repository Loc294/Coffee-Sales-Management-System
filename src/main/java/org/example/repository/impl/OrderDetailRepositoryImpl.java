package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.OrderDetail;
import org.example.repository.OrderDetailRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    @Override
    public void saveAll(List<OrderDetail> details) {
        String sql = """
            INSERT INTO order_detail
            (order_id, product_id, product_name, unit_price, quantity, subtotal)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (OrderDetail d : details) {
                ps.setInt(1, d.getOrderId());
                ps.setInt(2, d.getProductId());
                ps.setString(3, d.getProductName());
                ps.setDouble(4, d.getUnitPrice());
                ps.setInt(5, d.getQuantity());
                ps.setDouble(6, d.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu order details", e);
        }
    }

    @Override
    public List<OrderDetail> findByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM order_detail WHERE order_id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setId(rs.getInt("id"));
                    d.setOrderId(rs.getInt("order_id"));
                    d.setProductId(rs.getInt("product_id"));
                    d.setProductName(rs.getString("product_name"));
                    d.setUnitPrice(rs.getDouble("unit_price"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy order details", e);
        }
        return list;
    }

    @Override
    public void deleteByOrderId(int orderId) {
        String sql = "DELETE FROM order_detail WHERE order_id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa order details", e);
        }
    }
}