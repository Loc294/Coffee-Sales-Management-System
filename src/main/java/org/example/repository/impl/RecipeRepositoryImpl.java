package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Recipe;
import org.example.repository.RecipeRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeRepositoryImpl implements RecipeRepository {

    @Override
    public List<Recipe> findByProductId(int productId) {
        List<Recipe> list = new ArrayList<>();

        String sql = "SELECT * FROM recipe WHERE product_id = ?";

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy recipe", e);
        }
        return list;
    }

    @Override
    public void save(Recipe r) {
        String sql = """
            INSERT INTO recipe
            (product_id, ingredient_name, amount, unit, createdTime)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getProductId());
            ps.setString(2, r.getIngredientName());
            ps.setDouble(3, r.getAmount());
            ps.setString(4, r.getUnit());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Recipe r) {
        String sql = """
            UPDATE recipe
            SET ingredient_name=?, amount=?, unit=?, updatedTime=?
            WHERE id=?
        """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getIngredientName());
            ps.setDouble(2, r.getAmount());
            ps.setString(3, r.getUnit());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("DELETE FROM recipe WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByProductId(int productId) {
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("DELETE FROM recipe WHERE product_id=?")) {

            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe mapRow(ResultSet rs) throws SQLException {
        Recipe r = new Recipe();
        r.setId(rs.getInt("id"));
        r.setProductId(rs.getInt("product_id"));
        r.setIngredientName(rs.getString("ingredient_name"));
        r.setAmount(rs.getDouble("amount"));
        r.setUnit(rs.getString("unit"));

        Timestamp c = rs.getTimestamp("createdTime");
        Timestamp u = rs.getTimestamp("updatedTime");

        if (c != null) r.setCreatedTime(c.toLocalDateTime());
        if (u != null) r.setUpdatedTime(u.toLocalDateTime());

        return r;
    }
}
