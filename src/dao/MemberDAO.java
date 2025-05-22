package dao;

import database.DBConnection;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberDAO {

    private final Connection connection;
    private final PromotionDAO promotionDAO = new PromotionDAO();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO(promotionDAO);
    private TrainerDAO trainerDAO ;


    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }
    public MemberDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void create(Member member) {
        // 1. Inserare în users
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement userStmt = connection.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            userStmt.setString(1, member.getName());
            userStmt.setString(2, member.getUsername());
            userStmt.setString(3, member.getEmail());
            userStmt.setString(4, member.getPhoneNumber());
            userStmt.setString(5, member.getPassword());

            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                member.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Nu s-a generat ID-ul pentru user.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în users: " + e.getMessage());
            return;
        }

        // 2. Inserare în members
        String memberSql = "INSERT INTO members (user_id, registration_date, weight, height, experience_level, trainer_id, is_student) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement memberStmt = connection.prepareStatement(memberSql)) {
            memberStmt.setInt(1, member.getId());
            memberStmt.setString(2, member.getRegistrationDate().toString());
            memberStmt.setFloat(3, member.getWeight());
            memberStmt.setFloat(4, member.getHeight());
            memberStmt.setString(5, member.getExperienceLevel());

            if (member.getTrainer() != null) {
                memberStmt.setInt(6, member.getTrainer().getId());
            } else {
                memberStmt.setNull(6, Types.INTEGER);
            }

            memberStmt.setInt(7, member.isStudent() ? 1 : 0);
            memberStmt.executeUpdate();

            System.out.println("Membrul a fost adăugat în baza de date.");

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în members: " + e.getMessage());
        }
    }

    public boolean updateTrainer(int memberId, Integer trainerId) {
        String sql = "UPDATE members SET trainer_id = ? WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (trainerId != null) {
                stmt.setInt(1, trainerId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, memberId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Eroare la actualizarea trainerului membrului: " + e.getMessage());
            return false;
        }
    }

    public List<Member> readAll() {
        List<Member> members = new ArrayList<>();
        Map<Member, Integer> trainerMap = new HashMap<>();  // pentru a lega trainerId-ul fără să-l punem în model

        String sql = "SELECT * FROM users u JOIN members m ON u.id = m.user_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // PAS 1: construim membrii fără a apela alte DAO-uri în timp ce rs e deschis
            while (rs.next()) {
                int memberId = rs.getInt("user_id");
                int trainerId = rs.getInt("trainer_id");

                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("registration_date")),
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null, // trainer
                        null, // subscription
                        rs.getBoolean("is_student")
                );
                member.setId(memberId);

                if (!rs.wasNull()) {
                    trainerMap.put(member, trainerId);
                }

                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // PAS 2: apelăm alte DAO-uri după ce rs și stmt sunt închise
        for (Member member : members) {
            try {
                // setăm trainerul dacă există
                Integer trainerId = trainerMap.get(member);
                if (trainerId != null) {
                    Trainer trainer = trainerDAO.findById(trainerId);
                    member.setTrainer(trainer);
                }

                // setăm abonamentul activ, dacă există
                Subscription subscription = subscriptionDAO.findActiveByMemberId(member.getId());
                member.setSubscription(subscription);

            } catch (Exception e) {
                System.out.println("⚠️ Eroare la completarea trainerului/subscriptiei pentru membrul: " + member.getUsername());
            }
        }

        return members;
    }



//    public List<Member> readAll() {
//        List<Member> members = new ArrayList<>();
//        String sql = "SELECT * FROM users u JOIN members m ON u.id = m.user_id";
//
//        try (Connection conn = DBConnection.getInstance().getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                LocalDate registrationDate = LocalDate.parse(rs.getString("registration_date"));
//
//                // Inițializăm cu null — completăm imediat
//                Trainer trainer = null;
//                Subscription subscription = null;
//
//                int memberId = rs.getInt("user_id");
//                int trainerId = rs.getInt("trainer_id");
//                if (!rs.wasNull()) {
//                    trainer = trainerDAO.findById(trainerId); // presupunem că ai metoda asta
//                }
//
//                subscription = subscriptionDAO.findActiveByMemberId(memberId); // sau una adaptată
//
//                Member member = new Member(
//                        rs.getString("name"),
//                        rs.getString("username"),
//                        rs.getString("email"),
//                        rs.getString("phone"),
//                        rs.getString("password"),
//                        registrationDate,
//                        rs.getFloat("weight"),
//                        rs.getFloat("height"),
//                        rs.getString("experience_level"),
//                        trainer,
//                        subscription,
//                        rs.getBoolean("is_student")
//                );
//                member.setId(memberId);
//                members.add(member);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return members;
//    }



    public Member readById(int id) {
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "m.registration_date, m.weight, m.height, m.experience_level, m.trainer_id, m.is_student, " +
                "t.name AS trainer_name " +
                "FROM users u " +
                "JOIN members m ON u.id = m.user_id " +
                "LEFT JOIN users t ON m.trainer_id = t.id " +
                "WHERE u.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("registration_date")),
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null, // trainer setat imediat mai jos
                        null, // subscription - poți seta separat
                        rs.getInt("is_student") == 1
                );
                member.setId(rs.getInt("id"));
                member.setPayments(new ArrayList<>());

                int trainerId = rs.getInt("trainer_id");
                if (!rs.wasNull()) {
                    Trainer trainer = new Trainer();
                    trainer.setId(trainerId);
                    trainer.setName(rs.getString("trainer_name"));
                    member.setTrainer(trainer);
                }
                Subscription subscription = subscriptionDAO.readByMemberId(member.getId());
                member.setSubscription(subscription);


                return member;
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea membrului: " + e.getMessage());
        }

        return null; // dacă nu găsește
    }


    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea membrului: " + e.getMessage());
        }

        return false;
    }

//    public List<Member> getMembersByTrainerId(int trainerId) {
//        List<Member> members = new ArrayList<>();
//        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
//                "m.registration_date, m.weight, m.height, m.experience_level, m.is_student " +
//                "FROM users u JOIN members m ON u.id = m.user_id " +
//                "WHERE m.trainer_id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, trainerId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Member member = new Member(
//                        rs.getString("name"),
//                        rs.getString("username"),
//                        rs.getString("email"),
//                        rs.getString("phone"),
//                        rs.getString("password"),
//                        LocalDate.parse(rs.getString("registration_date")),
//                        rs.getFloat("weight"),
//                        rs.getFloat("height"),
//                        rs.getString("experience_level"),
//                        null,
//                        null,
//                        rs.getInt("is_student") == 1
//                );
//                member.setId(rs.getInt("id"));
//                member.setPayments(new ArrayList<>());
//                members.add(member);
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Eroare la citirea membrilor antrenați: " + e.getMessage());
//        }
//
//        return members;
//    }

    public List<Member> getMembersByTrainerId(int trainerId) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "m.registration_date, m.weight, m.height, m.experience_level, m.is_student " +
                "FROM users u JOIN members m ON u.id = m.user_id " +
                "WHERE m.trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("registration_date")),
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null,
                        null,
                        rs.getInt("is_student") == 1
                );
                member.setId(rs.getInt("id"));
                member.setPayments(new ArrayList<>());
                members.add(member);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea membrilor antrenați: " + e.getMessage());
        }

        return members;
    }


    public Member findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users u JOIN members m ON u.id = m.user_id WHERE u.username = ? AND u.password = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Convertim registrationDate din String în LocalDate
                LocalDate registrationDate = LocalDate.parse(rs.getString("registration_date"));

                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        registrationDate,
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null, // trainer îl putem seta separat după JOIN dacă vrei
                        null, // subscription îl putem încărca din SubscriptionDAO separat
                        rs.getBoolean("is_student")
                );

                return member;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addMember(Member member) {
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        String memberSql = "INSERT INTO members (user_id, registration_date, weight, height, experience_level, trainer_id, is_student) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement memberStmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // tranzacție!

            userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, member.getName());
            userStmt.setString(2, member.getUsername());
            userStmt.setString(3, member.getEmail());
            userStmt.setString(4, member.getPhoneNumber());
            userStmt.setString(5, member.getPassword());
            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);

                memberStmt = conn.prepareStatement(memberSql);
                memberStmt.setInt(1, userId);
                memberStmt.setString(2, member.getRegistrationDate().toString());
                memberStmt.setFloat(3, member.getWeight());
                memberStmt.setFloat(4, member.getHeight());
                memberStmt.setString(5, member.getExperienceLevel());
                memberStmt.setObject(6, member.getTrainer() != null ? member.getTrainer().getId() : null); // poate fi null
                memberStmt.setBoolean(7, member.isStudent());
                memberStmt.executeUpdate();

                conn.commit();
            } else {
                throw new SQLException("User ID not generated.");
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
                if (memberStmt != null) memberStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void assignTrainer(int memberId, int trainerId) {
        String sql = "UPDATE members SET trainer_id = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getTrainerIdForMember(int memberId) {
        String sql = "SELECT trainer_id FROM members WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("trainer_id");
                return rs.wasNull() ? null : id;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeTrainerFromMember(int memberId) {
        String sql = "UPDATE members SET trainer_id = NULL WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateTrainerForMember(int memberId, int trainerId) {
        String sql = "UPDATE members SET trainer_id = ? WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error updating trainer for member: " + e.getMessage());
        }
    }




}

