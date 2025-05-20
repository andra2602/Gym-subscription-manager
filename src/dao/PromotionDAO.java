package dao;

import database.DBConnection;
import models.Promotion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    private final Connection connection;

    public PromotionDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void create(Promotion promotion) {
        String sql = "INSERT INTO promotions (name, description, discount_percent, start_date, end_date, active) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, promotion.getName());
            stmt.setString(2, promotion.getDescription());
            stmt.setFloat(3, promotion.getDiscountPercent());
            stmt.setString(4, promotion.getStartDate().toString());
            stmt.setString(5, promotion.getEndDate().toString());
            stmt.setInt(6, promotion.isActive() ? 1 : 0);

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                promotion.setId(keys.getInt(1));
            }

            System.out.println("Promotion added successfully.");
        } catch (SQLException e) {
            System.out.println("Erorr at creating the promotion: " + e.getMessage());
        }
    }

    public List<Promotion> readAll() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
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

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
