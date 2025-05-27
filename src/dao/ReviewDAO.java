package dao;

import database.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private final Connection connection;

    public ReviewDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Adaugă review în baza de date
    public boolean create(int memberId, int trainerId, int rating) {
        String sql = "INSERT INTO reviews (member_id, trainer_id, rating, review_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, rating);
            stmt.setString(4, LocalDate.now().toString());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error inserting review: " + e.getMessage());
            return false;
        }
    }

    public void addReview(int memberId, int trainerId, int rating, LocalDate date) {
        String checkSql = "SELECT COUNT(*) FROM reviews WHERE member_id = ? AND trainer_id = ?";
        String insertSql = "INSERT INTO reviews (member_id, trainer_id, rating, review_date) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE reviews SET rating = ?, review_date = ? WHERE member_id = ? AND trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, memberId);
            checkStmt.setInt(2, trainerId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean reviewExists = rs.getInt(1) > 0;

            if (reviewExists) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, rating);
                    updateStmt.setString(2, date.toString());
                    updateStmt.setInt(3, memberId);
                    updateStmt.setInt(4, trainerId);
                    updateStmt.executeUpdate();
                    System.out.println("✅ Review updated successfully.");
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, memberId);
                    insertStmt.setInt(2, trainerId);
                    insertStmt.setInt(3, rating);
                    insertStmt.setString(4, date.toString());
                    insertStmt.executeUpdate();
                    System.out.println("✅ Review inserted successfully.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error inserting/updating review: " + e.getMessage());
        }
    }


    // Ia toate scorurile review pentru un trainer (anonim, doar rating-uri)
//    public List<Integer> readRatingsByTrainerId(int trainerId) {
//        List<Integer> ratings = new ArrayList<>();
//        String sql = "SELECT rating FROM reviews WHERE trainer_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, trainerId);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                ratings.add(rs.getInt("rating"));
//            }
//        } catch (SQLException e) {
//            System.out.println("Error reading reviews: " + e.getMessage());
//        }
//        return ratings;
//    }
    public List<Integer> readRatingsByTrainerId(int trainerId) {
        List<Integer> ratings = new ArrayList<>();
        String sql = "SELECT rating FROM reviews WHERE trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(rs.getInt("rating"));
            }

        } catch (SQLException e) {
            System.out.println("Error reading reviews: " + e.getMessage());
        }

        return ratings;
    }


    public List<Integer> getReviewsForTrainer(int trainerId) {
        List<Integer> scores = new ArrayList<>();
        String sql = "SELECT rating FROM reviews WHERE trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                scores.add(rs.getInt("rating"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading reviews: " + e.getMessage());
        }

        return scores;
    }


}
