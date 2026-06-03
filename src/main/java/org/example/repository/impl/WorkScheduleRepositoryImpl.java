package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.WorkSchedule;
import org.example.repository.WorkScheduleRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WorkScheduleRepositoryImpl implements WorkScheduleRepository {

    @Override
    public List<WorkSchedule> findAll() {
        List<WorkSchedule> list = new ArrayList<>();

        String sql = """
    SELECT
        es.id,
        e.id AS employee_id,
        e.name AS employee_name,
        s.id AS shift_id,
        s.name AS shift_name,
        s.start_time,
        s.end_time,
        es.work_date,
        es.register_date
    FROM employee_shift es
    JOIN employee e ON es.employee_id = e.id
    JOIN shift s ON es.shift_id = s.id
    ORDER BY es.work_date DESC, es.register_date DESC
""";


        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                WorkSchedule ws = new WorkSchedule();

                ws.setEmployeeId(rs.getInt("employee_id"));
                ws.setShiftId(rs.getInt("shift_id"));
                ws.setEmployeeName(rs.getString("employee_name"));
                ws.setShiftName(rs.getString("shift_name"));
                ws.setStartTime(rs.getTime("start_time").toLocalTime());
                ws.setEndTime(rs.getTime("end_time").toLocalTime());
                ws.setWorkDate(rs.getDate("work_date").toLocalDate());

                Timestamp reg = rs.getTimestamp("register_date");
                if (reg != null)
                    ws.setRegisterDate(reg.toLocalDateTime());

                list.add(ws);
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi load lịch làm việc", e);
        }

        return list;
    }
    @Override
    public void update(int employeeId, int oldShiftId, int newShiftId, String workDate) {

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
            ps.setInt(2, employeeId);
            ps.setInt(3, oldShiftId);
            ps.setDate(4, Date.valueOf(workDate));

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật lịch làm việc", e);
        }
    }


    @Override
    public void delete(int employeeId, int shiftId, String workDate) {

        String sql = """
            DELETE FROM employee_shift
            WHERE employee_id=? AND shift_id=? AND work_date=?
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, shiftId);
            ps.setDate(3, Date.valueOf(workDate));
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xóa lịch làm việc", e);
        }
    }
}
