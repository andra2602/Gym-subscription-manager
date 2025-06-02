package dao;

import database.DBConnection;
import models.Trainer;

import java.sql.*;

import java.util.*;

public class TrainerDAO {

    private final Connection connection;
    private MemberDAO memberDAO;
    private final ReviewDAO reviewDAO;


    public void setMemberDAO(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    public TrainerDAO() {
        this.connection = DBConnection.getInstance().getConnection();
        this.reviewDAO = new ReviewDAO();
    }

    public void create(Trainer trainer) {
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement userStmt = connection.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            userStmt.setString(1, trainer.getName());
            userStmt.setString(2, trainer.getUsername());
            userStmt.setString(3, trainer.getEmail());
            userStmt.setString(4, trainer.getPhoneNumber());
            userStmt.setString(5, trainer.getPassword());

            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                trainer.setId(generatedId);
            } else {
                throw new SQLException("Crearea userului a eșuat, nu s-a returnat niciun ID.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în users: " + e.getMessage());
            return;
        }

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
            conn.setAutoCommit(false);

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
                trainer.setId(userId);

                trainerStmt = conn.prepareStatement(trainerSql);
                trainerStmt.setInt(1, userId);
                trainerStmt.setString(2, trainer.getSpecialization());
                trainerStmt.setDouble(3, trainer.getYearsOfExperience());
                trainerStmt.setDouble(4, trainer.getPricePerHour());
                trainerStmt.executeUpdate();

                conn.commit();
            } else {
                throw new SQLException("Failed to retrieve user ID after insert.");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
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
        Map<Trainer, Integer> trainerIdMap = new HashMap<>();

        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "t.specialization, t.years_of_experience, t.price_per_hour " +
                "FROM users u JOIN trainers t ON u.id = t.user_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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
                        new HashSet<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                );

                int trainerId = rs.getInt("id");
                trainer.setId(trainerId);
                trainer.setCoordinatedClasses(new HashMap<>());
                trainers.add(trainer);
                trainerIdMap.put(trainer, trainerId);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea trainerilor: " + e.getMessage());
        }

        for (Trainer trainer : trainers) {
            int trainerId = trainerIdMap.get(trainer);
            trainer.setTrainedMembers(new HashSet<>(memberDAO.getMembersByTrainerId(trainerId)));
            trainer.setReviewScores(reviewDAO.readRatingsByTrainerId(trainerId));
        }

        return trainers;
    }

    public Trainer readById(int id) {
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "t.specialization, t.years_of_experience, t.price_per_hour " +
                "FROM users u JOIN trainers t ON u.id = t.user_id " +
                "WHERE u.id = ?";

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

        return null;
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
                        rs.getFloat("price_per_hour"),
                        new HashSet<>(),        // trainedMembers
                        new ArrayList<>(),      // availableSlots
                        new ArrayList<>(),      // bookings
                        new ArrayList<>()       // reviewScores
                );
                trainer.setId(id);

                List<Integer> reviews = reviewDAO.readRatingsByTrainerId(id);
                trainer.setReviewScores(reviews);

                return trainer;
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea trainerului: " + e.getMessage());
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
                Trainer trainer = new Trainer(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("specialization"),
                        rs.getDouble("years_of_experience"),
                        rs.getDouble("price_per_hour"),
                        new HashSet<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                );

                trainer.setId(rs.getInt("user_id"));

                trainer.setReviewScores(reviewDAO.readRatingsByTrainerId(trainer.getId()));

                if (memberDAO != null) {
                    trainer.setTrainedMembers(new HashSet<>(memberDAO.getMembersByTrainerId(trainer.getId())));
                }

                trainer.setCoordinatedClasses(new HashMap<>());

                return trainer;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updatePrice(int trainerId, double newPrice) {
        String sql = "UPDATE trainers SET price_per_hour = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newPrice);
            stmt.setInt(2, trainerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update trainer price: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea trainerului: " + e.getMessage());
        }

        return false;
    }

}
