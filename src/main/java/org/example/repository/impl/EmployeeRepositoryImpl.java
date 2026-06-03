package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Override
    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();

        String sql = """
            SELECT
                id,
                name,
                phone,
                position,
                createdTime,
                updateTime
            FROM employee
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi lấy danh sách employee", ex);
        }

        return list;
    }

    @Override
    public Employee findById(int id) {
        String sql = """
            SELECT
                id,
                name,
                phone,
                position,
                createdTime,
                updateTime
            FROM employee
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm employee theo id", e);
        }

        return null;
    }

    @Override
    public void save(Employee emp) {
        String sql = """
            INSERT INTO employee (name, phone, position)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPhone());
            ps.setString(3, emp.getPosition());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu employee", e);
        }
    }

    @Override
    public void update(Employee emp) {
        String sql = """
            UPDATE employee
            SET name = ?, phone = ?, position = ?, updateTime = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPhone());
            ps.setString(3, emp.getPosition());
            ps.setInt(4, emp.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật employee", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM employee WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa employee", e);
        }
    }

    // =======================
    // MAP ROW
    // =======================
    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();

        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setPhone(rs.getString("phone"));
        e.setPosition(rs.getString("position"));

        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updateTime");

        if (created != null) e.setCreatedTime(created.toLocalDateTime());
        if (updated != null) e.setUpdateTime(updated.toLocalDateTime());

        return e;
    }
}
