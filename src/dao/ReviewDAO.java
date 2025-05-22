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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, rating);
            stmt.setString(4, LocalDate.now().toString());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting review: " + e.getMessage());
            return false;
        }
    }
    public void addReview(int memberId, int trainerId, int rating, LocalDate date) {
        String sql = "INSERT INTO reviews (member_id, trainer_id, rating, review_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, rating);
            stmt.setString(4, date.toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error inserting review: " + e.getMessage());
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

}
