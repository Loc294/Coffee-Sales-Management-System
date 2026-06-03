package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Voucher;
import org.example.repository.VoucherRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherRepositoryImpl implements VoucherRepository {

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) return "Còn hiệu lực";
        String s = status.trim().toUpperCase();
        if (s.contains("HIỆU LỰC") || s.equals("ACTIVE")) return "Còn hiệu lực";
        if (s.contains("HẾT HẠN") || s.equals("EXPIRED")) return "Hết hạn";
        if (s.contains("SỬ DỤNG") || s.equals("USED")) return "Đã sử dụng";
        return "Còn hiệu lực";
    }

    /**
     * Chuẩn hóa loại giảm giá sang Tiếng Việt để khớp với DB ENUM/VARCHAR
     */
    private String normalizeDiscountType(String type) {
        if (type == null) return "Phần trăm";
        String t = type.trim().toUpperCase();
        if (t.equals("PERCENT") || t.contains("PHẦN TRĂM")) return "Phần trăm";
        if (t.equals("AMOUNT") || t.contains("CỐ ĐỊNH") || t.contains("SỐ TIỀN")) return "Cố định";
        return "Phần trăm";
    }

    @Override
    public List<Voucher> findAll(String keyword, String status) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers WHERE (code LIKE ? OR note LIKE ?)";
        boolean hasStatus = status != null && !status.equalsIgnoreCase("Tất cả");
        if (hasStatus) sql += " AND status = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            if (hasStatus) ps.setString(3, normalizeStatus(status));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Voucher findByCode(String code) {
        String sql = "SELECT * FROM vouchers WHERE code = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void save(Voucher v) {
        String sql = "INSERT INTO vouchers (code, discount_type, discount_value, start_date, end_date, status, usage_limit, used_count, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, normalizeDiscountType(v.getDiscountType())); // SỬA TẠI ĐÂY
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, normalizeStatus(v.getStatus()));
            ps.setObject(7, v.getUsageLimit(), Types.INTEGER);
            ps.setObject(8, v.getUsedCount() != null ? v.getUsedCount() : 0, Types.INTEGER);
            ps.setString(9, v.getNote());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Lỗi SQL khi lưu Voucher: " + e.getMessage()); }
    }

    @Override
    public void update(Voucher v) {
        String sql = "UPDATE vouchers SET code=?, discount_type=?, discount_value=?, start_date=?, end_date=?, status=?, usage_limit=?, used_count=?, note=? WHERE id=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, normalizeDiscountType(v.getDiscountType())); // SỬA TẠI ĐÂY
            ps.setDouble(3, v.getDiscountValue());
            ps.setDate(4, Date.valueOf(v.getStartDate()));
            ps.setDate(5, Date.valueOf(v.getEndDate()));
            ps.setString(6, normalizeStatus(v.getStatus()));
            ps.setObject(7, v.getUsageLimit(), Types.INTEGER);
            ps.setObject(8, v.getUsedCount(), Types.INTEGER);
            ps.setString(9, v.getNote());
            ps.setInt(10, v.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM vouchers WHERE id = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Voucher mapResultSet(ResultSet rs) throws SQLException {
        Voucher v = new Voucher();
        v.setId(rs.getInt("id"));
        v.setCode(rs.getString("code"));
        v.setDiscountType(rs.getString("discount_type"));
        v.setDiscountValue(rs.getDouble("discount_value"));
        v.setStartDate(rs.getDate("start_date").toLocalDate());
        v.setEndDate(rs.getDate("end_date").toLocalDate());
        v.setStatus(rs.getString("status"));
        v.setUsageLimit((Integer) rs.getObject("usage_limit"));
        v.setUsedCount((Integer) rs.getObject("used_count"));
        v.setNote(rs.getString("note"));
        return v;
    }
}
