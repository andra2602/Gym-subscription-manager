package dao;

import database.DBConnection;
import models.Trainer;

import java.sql.*;

import java.util.*;

public class TrainerDAO {

    private final Connection connection;
    private final MemberDAO memberDAO;
    private final ReviewDAO reviewDAO;

    public TrainerDAO() {
        this.connection = DBConnection.getInstance().getConnection();
        this.memberDAO = new MemberDAO();
        this.reviewDAO = new ReviewDAO();
    }

    public void create(Trainer trainer) {
        // 1. Inserăm în users
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement userStmt = connection.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            userStmt.setString(1, trainer.getName());
            userStmt.setString(2, trainer.getUsername());
            userStmt.setString(3, trainer.getEmail());
            userStmt.setString(4, trainer.getPhoneNumber());
            userStmt.setString(5, trainer.getPassword());

            userStmt.executeUpdate();

            // 2. Obținem ID-ul generat
            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                trainer.setId(generatedId); // setăm în obiectul Trainer
            } else {
                throw new SQLException("Crearea userului a eșuat, nu s-a returnat niciun ID.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în users: " + e.getMessage());
            return;
        }

        // 3. Inserăm în trainers
        String trainerSql = "INSERT INTO trainers (user_id, specialization, years_of_experience, price_per_hour) VALUES (?, ?, ?, ?)";
        try (PreparedStatement trainerStmt = connection.prepareStatement(trainerSql)) {
            trainerStmt.setInt(1, trainer.getId());
            trainerStmt.setString(2, trainer.getSpecialization());
            trainerStmt.setDouble(3, trainer.getYearsOfExperience());
            trainerStmt.setDouble(4, trainer.getPricePerHour());

            trainerStmt.executeUpdate();
            System.out.println("Trainerul a fost salvat în baza de date.");

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în trainers: " + e.getMessage());
        }
    }

    public void addTrainer(Trainer trainer) {
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        String trainerSql = "INSERT INTO trainers (user_id, specialization, years_of_experience, price_per_hour) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement trainerStmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // pornim tranzacția

            // Inserăm în users
            userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, trainer.getName());
            userStmt.setString(2, trainer.getUsername());
            userStmt.setString(3, trainer.getEmail());
            userStmt.setString(4, trainer.getPhoneNumber());
            userStmt.setString(5, trainer.getPassword());
            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                trainer.setId(userId); // salvăm ID-ul în obiectul Java

                // Inserăm în trainers
                trainerStmt = conn.prepareStatement(trainerSql);
                trainerStmt.setInt(1, userId);
                trainerStmt.setString(2, trainer.getSpecialization());
                trainerStmt.setDouble(3, trainer.getYearsOfExperience());
                trainerStmt.setDouble(4, trainer.getPricePerHour());
                trainerStmt.executeUpdate();

                conn.commit(); // totul a mers OK!
            } else {
                throw new SQLException("Failed to retrieve user ID after insert.");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // rollback dacă ceva merge prost
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (userStmt != null) userStmt.close();
                if (trainerStmt != null) trainerStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Trainer> readAll() {
        List<Trainer> trainers = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "t.specialization, t.years_of_experience, t.price_per_hour " +
                "FROM users u JOIN trainers t ON u.id = t.user_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Trainer trainer = new Trainer(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("specialization"),
                        rs.getDouble("years_of_experience"),
                        rs.getDouble("price_per_hour"),
                        new HashSet<>(),       // trainedMembers
                        new ArrayList<>(),     // availableSlots
                        new ArrayList<>(),     // bookings
                        new ArrayList<>()      // reviewScores temporar
                );
                int trainerId = rs.getInt("id");
                trainer.setId(trainerId);
                trainer.setCoordinatedClasses(new HashMap<>());

                // Populăm membrii
                trainer.setTrainedMembers(new HashSet<>(memberDAO.getMembersByTrainerId(trainerId)));

                // Populăm review-urile
                List<Integer> ratings = reviewDAO.readRatingsByTrainerId(trainerId);
                trainer.setReviewScores(ratings);

                trainers.add(trainer);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea trainerilor: " + e.getMessage());
        }

        return trainers;
    }


    public Trainer readById(int id) {
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "t.specialization, t.years_of_experience, t.price_per_hour " +
                "FROM users u JOIN trainers t ON u.id = t.user_id " +
                "WHERE u.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Trainer trainer = new Trainer(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("specialization"),
                        rs.getDouble("years_of_experience"),
                        rs.getDouble("price_per_hour"),
                        new HashSet<>(),       // trainedMembers
                        new ArrayList<>(),     // availableSlots
                        new ArrayList<>(),     // bookings
                        new ArrayList<>()      // reviewScores temporar
                );
                trainer.setId(rs.getInt("id"));
                trainer.setTrainedMembers(new HashSet<>(memberDAO.getMembersByTrainerId(trainer.getId())));
                trainer.setCoordinatedClasses(new HashMap<>());

                List<Integer> ratings = reviewDAO.readRatingsByTrainerId(trainer.getId());
                trainer.setReviewScores(ratings);

                return trainer;
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea trainerului: " + e.getMessage());
        }

        return null; // dacă nu a fost găsit
    }

    public Trainer findById(int id) {
        String sql = "SELECT * FROM users u JOIN trainers t ON u.id = t.user_id WHERE u.id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Trainer trainer = new Trainer(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("specialization"),
                        rs.getFloat("years_of_experience"),
                        rs.getFloat("price_per_hour")
                );
                trainer.setId(id);
                return trainer;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Trainer findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users u JOIN trainers t ON u.id = t.user_id WHERE u.username = ? AND u.password = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Trainer(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("specialization"),
                        rs.getDouble("years_of_experience"),
                        rs.getDouble("price_per_hour"),
                        new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
