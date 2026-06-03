package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.repository.EmployeeShiftRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class EmployeeShiftRepositoryImpl implements EmployeeShiftRepository {

    @Override
    public void assign(int empId, int shiftId, LocalDate date) {

        String sql = """
            INSERT INTO employee_shift(employee_id, shift_id, work_date)
            VALUES (?, ?, ?)
        """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ps.setInt(2, shiftId);
            ps.setDate(3, Date.valueOf(date));
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi gán ca", e);
        }
    }

    @Override
    public boolean exists(int empId, int shiftId, LocalDate date) {

        String sql = """
            SELECT 1 FROM employee_shift
            WHERE employee_id=? AND shift_id=? AND work_date=?
        """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ps.setInt(2, shiftId);
            ps.setDate(3, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi kiểm tra trùng ca", e);
        }
    }
    @Override
    public void update(int empId, int oldShiftId, int newShiftId, String workDate) {

        String sql = """
        UPDATE employee_shift
        SET shift_id = ?
        WHERE employee_id = ?
          AND shift_id = ?
          AND work_date = ?
    """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newShiftId);
            ps.setInt(2, empId);
            ps.setInt(3, oldShiftId);
            ps.setDate(4, Date.valueOf(workDate));

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật ca làm", e);
        }
    }

}
