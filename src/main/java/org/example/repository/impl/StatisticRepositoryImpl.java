package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.repository.StatisticRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticRepositoryImpl implements StatisticRepository {

    @Override
    public int countCustomers() {
        String sql = "SELECT COUNT(*) FROM customer";
        return getCount(sql);
    }

    @Override
    public int countEmployees() {
        String sql = "SELECT COUNT(*) FROM employee";
        return getCount(sql);
    }

    @Override
    public double getMonthlyRevenue() {
        // Lấy doanh thu từ các hóa đơn (đơn hàng) hoàn thành trong tháng hiện tại
        String sql = """
            SELECT IFNULL(SUM(total_amount), 0)
            FROM orders
            WHERE status = 'COMPLETED'
              AND MONTH(createdTime) = MONTH(CURRENT_DATE())
              AND YEAR(createdTime) = YEAR(CURRENT_DATE())
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int getTotalInventory() {
        String sql = "SELECT IFNULL(SUM(quantity), 0) FROM inventory";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getTotalExported() {
        String sql = """
        SELECT IFNULL(SUM(ABS(quantity_change)), 0)
        FROM inventory_history
        WHERE action = 'EXPORT'
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getExportedTotal() {
        return getTotalExported();
    }

    @Override
    public Map<Integer, Double> getRevenueByMonths() {
        Map<Integer, Double> revenueMap = new LinkedHashMap<>();
        
        // Khởi tạo map với 10 tháng gần nhất (hoặc 12 tháng) với giá trị 0
        // Cách đơn giản nhất là lấy 12 tháng của năm hiện tại
        for (int i = 1; i <= 12; i++) {
            revenueMap.put(i, 0.0);
        }

        String sql = """
            SELECT MONTH(createdTime) AS month, SUM(total_amount) AS revenue
            FROM orders
            WHERE status = 'COMPLETED'
              AND YEAR(createdTime) = YEAR(CURRENT_DATE())
            GROUP BY MONTH(createdTime)
            ORDER BY MONTH(createdTime)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int month = rs.getInt("month");
                double revenue = rs.getDouble("revenue");
                revenueMap.put(month, revenue);
            }

        } catch (Exception e) {
            System.err.println("Lỗi lấy doanh thu theo tháng: " + e.getMessage());
            e.printStackTrace();
        }
        return revenueMap;
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            System.err.println("Lỗi thực thi SQL: " + sql);
            e.printStackTrace();
        }
        return 0;
    }
}