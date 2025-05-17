package dao;

import database.DBConnection;
import models.Promotion;
import models.Subscription;

import java.sql.*;
import java.time.LocalDate;

public class SubscriptionDAO {
    private final Connection connection;

    public SubscriptionDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Creează un abonament nou pentru un membru
    public void create(Subscription subscription, int memberId) {
        String sql = "INSERT INTO subscriptions (member_id, type, start_date, price, is_active, promotion_id, extended_months) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, memberId);
            stmt.setString(2, subscription.getType());
            stmt.setString(3, subscription.getStartDate().toString());
            stmt.setFloat(4, subscription.getPrice());
            stmt.setInt(5, subscription.isActive() ? 1 : 0);

            if (subscription.getPromotion() != null) {
                stmt.setInt(6, subscription.getPromotion().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setInt(7, subscription.getExtendedMonths());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                // Dacă vrei să salvezi id-ul în Subscription, adaugă un field id în model
                // subscription.setId(id);
            }
            System.out.println("Subscription created successfully for member ID " + memberId);

        } catch (SQLException e) {
            System.out.println("Error creating subscription: " + e.getMessage());
        }
    }

    // Citește abonamentul activ al unui membru după ID-ul membrului
    public Subscription readByMemberId(int memberId) {
        String sql = "SELECT s.*, p.name AS promotion_name, p.description, p.discount_percent, p.start_date AS promo_start, p.end_date AS promo_end, p.active " +
                "FROM subscriptions s " +
                "LEFT JOIN promotions p ON s.promotion_id = p.id " +
                "WHERE s.member_id = ? AND s.is_active = 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Promotion promotion = null;
                int promoId = rs.getInt("promotion_id");
                if (!rs.wasNull()) {
                    promotion = new Promotion(
                            promoId,
                            rs.getString("promotion_name"),
                            rs.getString("description"),          // adaugă și descrierea
                            rs.getFloat("discount_percent"),
                            LocalDate.parse(rs.getString("promo_start")),
                            LocalDate.parse(rs.getString("promo_end")),
                            rs.getInt("active") == 1              // presupunem că active e stocat ca int 0/1
                    );
                }


                Subscription subscription = new Subscription(
                        rs.getString("type"),
                        LocalDate.parse(rs.getString("start_date")),
                        rs.getFloat("price"),
                        rs.getInt("is_active") == 1,
                        promotion
                );

                // setează extendedMonths dacă ai getter/setter în Subscription
                // subscription.setExtendedMonths(rs.getInt("extended_months"));

                return subscription;
            }

        } catch (SQLException e) {
            System.out.println("Error reading subscription: " + e.getMessage());
        }

        return null;
    }

    // Actualizează abonamentul
    public boolean update(Subscription subscription, int memberId) {
        String sql = "UPDATE subscriptions SET type = ?, start_date = ?, price = ?, is_active = ?, promotion_id = ?, extended_months = ? " +
                "WHERE member_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, subscription.getType());
            stmt.setString(2, subscription.getStartDate().toString());
            stmt.setFloat(3, subscription.getPrice());
            stmt.setInt(4, subscription.isActive() ? 1 : 0);

            if (subscription.getPromotion() != null) {
                stmt.setInt(5, subscription.getPromotion().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, subscription.getExtendedMonths());

            stmt.setInt(7, memberId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error updating subscription: " + e.getMessage());
            return false;
        }
    }

    // Șterge abonamentul după memberId
    public boolean deleteByMemberId(int memberId) {
        String sql = "DELETE FROM subscriptions WHERE member_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting subscription: " + e.getMessage());
            return false;
        }
    }

}
