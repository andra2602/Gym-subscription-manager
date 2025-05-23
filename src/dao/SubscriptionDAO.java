package dao;

import database.DBConnection;
import models.Promotion;
import models.Subscription;

import java.sql.*;
import java.time.LocalDate;

public class SubscriptionDAO {
    //private final Connection connection;
    private PromotionDAO promotionDAO;

    public SubscriptionDAO(PromotionDAO promotionDAO) {
        //this.connection = DBConnection.getInstance().getConnection();
        this.promotionDAO = promotionDAO;
    }

    // Creează un abonament nou pentru un membru
    public void create(Subscription subscription, int memberId) {
        String sql = "INSERT INTO subscriptions (member_id, type, start_date, price, is_active, promotion_id, extended_months) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    public void addSubscription(Subscription sub, int memberId) {
        String sql = "INSERT INTO subscriptions (member_id, type, start_date, price, is_active, promotion_id, extended_months) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setString(2, sub.getType());
            stmt.setString(3, sub.getStartDate().toString());
            stmt.setFloat(4, sub.getPrice());
            stmt.setBoolean(5, sub.isActive());

            if (sub.getPromotion() != null) {
                stmt.setInt(6, sub.getPromotion().getId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.setInt(7, sub.getExtendedMonths());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Citește abonamentul activ al unui membru după ID-ul membrului
    public Subscription readByMemberId(int memberId) {
        String sql = "SELECT s.*, p.name AS promotion_name, p.description, p.discount_percent, p.start_date AS promo_start, p.end_date AS promo_end, p.active " +
                "FROM subscriptions s " +
                "LEFT JOIN promotions p ON s.promotion_id = p.id " +
                "WHERE s.member_id = ? AND s.is_active = 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public Subscription findActiveByMemberId(int memberId) {
        String sql = "SELECT * FROM subscriptions WHERE member_id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Subscription subscription = new Subscription(
                        rs.getString("type"),
                        LocalDate.parse(rs.getString("start_date")),
                        rs.getFloat("price"),
                        rs.getBoolean("is_active"),
                        null );// promotion — poți seta ulterior dacă e cazul

                subscription.setId(rs.getInt("id")); // ⚠️ FOARTE IMPORTANT
                subscription.setExtendedMonths(rs.getInt("extended_months"));

                return subscription;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Subscription findMostRecentByMemberId(int memberId) {
        String sql = "SELECT * FROM subscriptions WHERE member_id = ? ORDER BY start_date DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Promotion promo = null;
                int promoId = rs.getInt("promotion_id");
                if (!rs.wasNull()) {
                    promo = promotionDAO.findById(promoId);
                }

                return new Subscription(
                        rs.getInt("id"),
                        rs.getString("type"),
                        LocalDate.parse(rs.getString("start_date")),
                        rs.getFloat("price"),
                        rs.getBoolean("is_active"),
                        promo,
                        rs.getInt("extended_months")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteSubscription(int subscriptionId) {
        String sql = "DELETE FROM subscriptions WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subscriptionId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSubscription(Subscription subscription) {
        String sql = "UPDATE subscriptions SET type = ?, start_date = ?, price = ?, is_active = ?, extended_months = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subscription.getType());
            stmt.setString(2, subscription.getStartDate().toString());
            stmt.setFloat(3, subscription.getPrice());
            stmt.setBoolean(4, subscription.isActive());
            stmt.setInt(5, subscription.getExtendedMonths());
            stmt.setInt(6, subscription.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
