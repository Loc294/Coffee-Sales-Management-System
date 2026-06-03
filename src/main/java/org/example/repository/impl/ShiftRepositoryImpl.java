package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Shift;
import org.example.repository.ShiftRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftRepositoryImpl implements ShiftRepository {

    @Override
    public List<Shift> findAll() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT * FROM shift";

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Shift s = new Shift();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setStartTime(rs.getTime("start_time").toLocalTime());
                s.setEndTime(rs.getTime("end_time").toLocalTime());

                Timestamp ct = rs.getTimestamp("createdTime");
                Timestamp ut = rs.getTimestamp("updateTime");
                if (ct != null) s.setCreatedTime(ct.toLocalDateTime());
                if (ut != null) s.setUpdateTime(ut.toLocalDateTime());

                list.add(s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void save(Shift s) {
        String sql = "INSERT INTO shift(name,start_time,end_time) VALUES(?,?,?)";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setTime(2, Time.valueOf(s.getStartTime()));
            ps.setTime(3, Time.valueOf(s.getEndTime()));
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Shift s) {
        String sql = """
            UPDATE shift
            SET name=?, start_time=?, end_time=?, updateTime=NOW()
            WHERE id=?
        """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setTime(2, Time.valueOf(s.getStartTime()));
            ps.setTime(3, Time.valueOf(s.getEndTime()));
            ps.setInt(4, s.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM shift WHERE id=?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
