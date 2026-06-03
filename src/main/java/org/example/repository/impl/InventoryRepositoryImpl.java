package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Inventory;
import org.example.repository.InventoryRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepositoryImpl implements InventoryRepository {

    /* ================== TỒN KHO HIỆN TẠI ================== */
    @Override
    public List<Inventory> findAll() {
        List<Inventory> list = new ArrayList<>();

        String sql = """
            SELECT p.id, p.name,
                   COALESCE(i.quantity, 0) AS quantity
            FROM product p
            LEFT JOIN inventory i ON p.id = i.product_id
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setProductId(rs.getInt("id"));
                inv.setProductName(rs.getString("name"));
                inv.setQuantity(rs.getInt("quantity"));
                list.add(inv);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /* ================== NHẬP KHO ================== */
    @Override
    public void importInventory(int productId, int quantity, String note) {

        String upsertInventory = """
            INSERT INTO inventory (product_id, quantity)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE quantity = quantity + ?
        """;

        String insertHistory = """
            INSERT INTO inventory_history (product_id, quantity_change, action, note)
            VALUES (?, ?, 'IMPORT', ?)
        """;

        try (Connection con = DatabaseConfig.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(upsertInventory);
                 PreparedStatement ps2 = con.prepareStatement(insertHistory)) {

                ps1.setInt(1, productId);
                ps1.setInt(2, quantity);
                ps1.setInt(3, quantity);
                ps1.executeUpdate();

                ps2.setInt(1, productId);
                ps2.setInt(2, quantity);
                ps2.setString(3, note);
                ps2.executeUpdate();

                con.commit();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ================== XUẤT KHO ================== */
    @Override
    public void exportInventory(int productId, int quantity, String note) {

        String updateInventory = """
            UPDATE inventory
            SET quantity = quantity - ?
            WHERE product_id = ? AND quantity >= ?
        """;

        String insertHistory = """
            INSERT INTO inventory_history (product_id, quantity_change, action, note)
            VALUES (?, ?, 'EXPORT', ?)
        """;

        try (Connection con = DatabaseConfig.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(updateInventory);
                 PreparedStatement ps2 = con.prepareStatement(insertHistory)) {

                ps1.setInt(1, quantity);
                ps1.setInt(2, productId);
                ps1.setInt(3, quantity);

                if (ps1.executeUpdate() == 0) {
                    throw new RuntimeException("Không đủ hàng trong kho");
                }

                ps2.setInt(1, productId);
                ps2.setInt(2, -quantity);
                ps2.setString(3, note);
                ps2.executeUpdate();

                con.commit();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ================== LỊCH SỬ ================== */
    @Override
    public List<Inventory> findHistoryByProduct(int productId) {

        List<Inventory> list = new ArrayList<>();

        String sql = """
            SELECT quantity_change, action, note, createdTime
            FROM inventory_history
            WHERE product_id = ?
            ORDER BY createdTime DESC
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Inventory inv = new Inventory();
                    inv.setQuantityChange(rs.getInt("quantity_change"));
                    inv.setAction(rs.getString("action"));
                    inv.setNote(rs.getString("note"));
                    inv.setCreatedTime(
                            rs.getTimestamp("createdTime").toLocalDateTime()
                    );
                    list.add(inv);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void deleteHistoryByProduct(int productId) {
        String sql = "DELETE FROM inventory_history WHERE product_id = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa lịch sử trong DB: " + e.getMessage());
        }
    }
}
