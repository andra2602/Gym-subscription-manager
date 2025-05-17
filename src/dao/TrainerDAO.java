package dao;

import database.DBConnection;
import models.Trainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

public class TrainerDAO {

    private final Connection connection;
    private final MemberDAO memberDAO;
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public TrainerDAO() {
        this.connection = DBConnection.getInstance().getConnection();
        this.memberDAO = new MemberDAO();
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


}
