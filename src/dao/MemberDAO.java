package dao;

import database.DBConnection;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MemberDAO {

    private final Connection connection;
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();


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
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "m.registration_date, m.weight, m.height, m.experience_level, m.trainer_id, m.is_student, " +
                "t.name AS trainer_name " +
                "FROM users u " +
                "JOIN members m ON u.id = m.user_id " +
                "LEFT JOIN users t ON m.trainer_id = t.id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
                        null, // trainer se va seta separat
                        null, // subscription – se va seta mai târziu
                        rs.getInt("is_student") == 1
                );
                member.setId(rs.getInt("id"));
                member.setPayments(new ArrayList<>()); // ca să nu crape aplicația

                int trainerId = rs.getInt("trainer_id");
                if (!rs.wasNull()) {
                    Trainer trainer = new Trainer();
                    trainer.setId(trainerId);
                    trainer.setName(rs.getString("trainer_name"));  // setăm și numele trainerului
                    member.setTrainer(trainer);
                }
                Subscription subscription = subscriptionDAO.readByMemberId(member.getId());
                member.setSubscription(subscription);


                members.add(member);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea membrilor: " + e.getMessage());
        }

        return members;
    }

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

    public List<Member> getMembersByTrainerId(int trainerId) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "m.registration_date, m.weight, m.height, m.experience_level, m.is_student " +
                "FROM users u JOIN members m ON u.id = m.user_id " +
                "WHERE m.trainer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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


}

