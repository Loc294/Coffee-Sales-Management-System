package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Order;
import org.example.repository.OrderDetailRepository;
import org.example.repository.OrderRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    private final OrderDetailRepository detailRepo = new OrderDetailRepositoryImpl();

    @Override
    public void save(Order order) {
        String sql = """
            INSERT INTO orders (order_code, total_amount, status, note, createdTime)
            VALUES (?, ?, ?, ?, NOW())
        """;
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, order.getOrderCode());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getStatus());
            ps.setString(4, order.getNote());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) order.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu order", e);
        }
    }

    @Override
    public void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái order", e);
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm order", e);
        }
        return null;
    }

    @Override
    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY createdTime DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách order", e);
        }
        return list;
    }

    @Override
    public List<Order> findByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY createdTime DESC";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lọc order theo status", e);
        }
        return list;
    }

    @Override
    public void delete(int id) {
        // Xóa chi tiết đơn hàng trước (ràng buộc khóa ngoại)
        detailRepo.deleteByOrderId(id);

        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa order", e);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setOrderCode(rs.getString("order_code"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setNote(rs.getString("note"));
        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updatedTime");
        if (created != null) o.setCreatedTime(created.toLocalDateTime());
        if (updated != null) o.setUpdatedTime(updated.toLocalDateTime());
        return o;
    }
}
