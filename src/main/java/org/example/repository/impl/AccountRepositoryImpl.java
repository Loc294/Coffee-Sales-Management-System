
package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Account;
import org.example.repository.AccountRepository;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {

    /* ================== FIND ALL ================== */
    @Override
    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();

        String sql = """
        SELECT id, username, password, role, is_active, createdTime, updateTime
        FROM account
    """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Account acc = new Account();

                acc.setId(rs.getInt("id"));
                acc.setUsername(rs.getString("username"));
                acc.setPassword(rs.getString("password"));
                acc.setRole(rs.getString("role"));
                acc.setActive(rs.getInt("is_active") == 1);

                //createdTime (không null)
                Timestamp created = rs.getTimestamp("createdTime");
                if (created != null) {
                    acc.setCreatedTime(created.toLocalDateTime());
                }

                //updateTime (CÓ THỂ NULL)
                Timestamp updated = rs.getTimestamp("updateTime");
                if (updated != null) {
                    acc.setUpdateTime(updated.toLocalDateTime());
                } else {
                    acc.setUpdateTime(null);
                }

                list.add(acc);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách account", e);
        }
        return list;
    }


    /* ================== FIND BY USERNAME ================== */
    @Override
    public Account findByUsername(String username) {
        String sql = "SELECT * FROM account WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm account", e);
        }
        return null;
    }

    /* ================== SAVE (REGISTER) ================== */
    @Override
    public void save(Account account) {
        String sql = """
            INSERT INTO account (username, password, createdTime, updateTime, is_active, role)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            ps.setTimestamp(3, Timestamp.valueOf(
                    account.getCreatedTime() != null
                            ? account.getCreatedTime()
                            : LocalDateTime.now()
            ));

            ps.setNull(4, Types.TIMESTAMP); // updateTime
            ps.setBoolean(5, true);         // is_active
            ps.setString(6, account.getRole());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu account", e);
        }
    }


    @Override
    public Optional<Account> findById(Long id) {

        String sql = "SELECT * FROM account WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm account theo id", e);
        }

        return Optional.empty();
    }


    /* ================== DELETE ================== */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM account WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa account", e);
        }
    }

    /* ================== EXISTS BY USERNAME ================== */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM account WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true nếu tồn tại
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra username", e);
        }
    }



    /* ================== UPDATE ================== */
    @Override
    public void update(Account account) {
        String sql = """
        UPDATE account
        SET username = ?,  password = ?,  role = ?, is_active = ?, updateTime = ?
        WHERE id = ?
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getRole());
            ps.setBoolean(4, account.isActive());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, account.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật account", e);
        }
    }


    /* ================== MAP RESULT ================== */
    private Account mapRow(ResultSet rs) throws SQLException {
        Account acc = new Account();

        acc.setId(rs.getInt("id"));
        acc.setUsername(rs.getString("username"));
        acc.setPassword(rs.getString("password"));

        Timestamp created = rs.getTimestamp("createdTime");
        Timestamp updated = rs.getTimestamp("updateTime");

        if (created != null)
            acc.setCreatedTime(created.toLocalDateTime());

        if (updated != null)
            acc.setUpdateTime(updated.toLocalDateTime());

        acc.setActive(rs.getBoolean("is_active"));
        acc.setRole(rs.getString("role"));

        return acc;
    }
}


