package dao;

import database.DBConnection;
import models.Promotion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromotionDAO extends BaseDAO<Promotion, Integer> {

    private static PromotionDAO instance;


    private PromotionDAO() {
        // singleton private constructor
    }

    public static PromotionDAO getInstance() {
        if (instance == null) {
            instance = new PromotionDAO();
        }
        return instance;
    }
    public boolean existsByNameAndStartDate(String name, LocalDate startDate) {
        String sql = "SELECT COUNT(*) FROM promotions WHERE name = ? AND start_date = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, startDate.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void create(Promotion promotion) {
        String sql = "INSERT INTO promotions (name, description, discount_percent, start_date, end_date, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, promotion.getName());
            stmt.setString(2, promotion.getDescription());
            stmt.setFloat(3, promotion.getDiscountPercent());
            stmt.setString(4, promotion.getStartDate().toString());
            stmt.setString(5, promotion.getEndDate().toString());
            stmt.setBoolean(6, promotion.isActive());

            stmt.executeUpdate();
            System.out.println("Promotion added: " + promotion.getName());

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea promoției: " + e.getMessage());
        }
    }

    @Override
    public Optional<Promotion> read(Integer id) {
        String sql = "SELECT * FROM promotions WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Promotion promotion = new Promotion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("discount_percent"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        rs.getBoolean("active")
                );
                return Optional.of(promotion);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error reading promotion: " + e.getMessage());
        }

        return Optional.empty();
    }

    public List<Promotion> readAll() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Promotion promotion = new Promotion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("discount_percent"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        rs.getInt("active") == 1
                );
                promotions.add(promotion);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea promoțiilor: " + e.getMessage());
        }

        return promotions;
    }

    public boolean update(Promotion promotion) {
        String sql = "UPDATE promotions SET name = ?, description = ?, discount_percent = ?, start_date = ?, end_date = ?, active = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, promotion.getName());
            stmt.setString(2, promotion.getDescription());
            stmt.setFloat(3, promotion.getDiscountPercent());
            stmt.setString(4, promotion.getStartDate().toString());
            stmt.setString(5, promotion.getEndDate().toString());
            stmt.setInt(6, promotion.isActive() ? 1 : 0);
            stmt.setInt(7, promotion.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error when trying to update promotion with ID " + promotion.getId() + ": " + e.getMessage());
        }

        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM promotions WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting promotion: " + e.getMessage());
        }

        return false;
    }

    public Promotion findById(int id) {
        String sql = "SELECT * FROM promotions WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Promotion(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("discount_percent"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        rs.getBoolean("active")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
