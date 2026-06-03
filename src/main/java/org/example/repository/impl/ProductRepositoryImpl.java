package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Product;
import org.example.repository.ProductRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    /* ================== FIND ALL ================== */
    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();

        String sql = """
            SELECT id, name, price, is_active, createdTime, updatedTime
            FROM product
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách product", e);
        }
        return list;
    }

    /* ================== FIND BY ID ================== */
    @Override
    public Product findById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm product theo id", e);
        }
        return null;
    }

    /* ================== FIND BY NAME ================== */
    @Override
    public List<Product> findByName(String name) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE name LIKE ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm product theo tên", e);
        }
        return list;
    }

    /* ================== SAVE ================== */
    @Override
    public void save(Product product) {
        String sql = """
            INSERT INTO product (name, price, is_active, createdTime)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setBoolean(3, product.isActive());
            ps.setTimestamp(4, Timestamp.valueOf(
                    product.getCreatedTime() != null
                            ? product.getCreatedTime()
                            : LocalDateTime.now()
            ));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu product", e);
        }
    }

    /* ================== UPDATE ================== */
    @Override
    public void update(Product product) {
        String sql = """
            UPDATE product
            SET name = ?, price = ?, is_active = ?, updatedTime = ?
            WHERE id = ?
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setBoolean(3, product.isActive());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, product.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật product", e);
        }
    }

    /* ================== DELETE ================== */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM product WHERE id = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa product", e);
        }
    }

    /* ================== MAP ROW ================== */
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();

        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setActive(rs.getBoolean("is_active"));

        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updatedTime");

        if (created != null)
            p.setCreatedTime(created.toLocalDateTime());

        if (updated != null)
            p.setUpdatedTime(updated.toLocalDateTime());

        return p;
    }
}
