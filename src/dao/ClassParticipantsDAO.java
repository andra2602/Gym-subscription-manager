package dao;

import database.DBConnection;
import models.Member;
import models.FitnessClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassParticipantsDAO {

    private final Connection connection;

    public ClassParticipantsDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public boolean addParticipant(int fitnessClassId, int memberId) {
        String sql = "INSERT INTO class_participants (fitness_class_id, member_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fitnessClassId);
            stmt.setInt(2, memberId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Eroare la adăugarea participantului: " + e.getMessage());
            return false;
        }
    }

    public boolean removeParticipant(int fitnessClassId, int memberId) {
        String sql = "DELETE FROM class_participants WHERE fitness_class_id = ? AND member_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fitnessClassId);
            stmt.setInt(2, memberId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Eroare la eliminarea participantului: " + e.getMessage());
            return false;
        }
    }

    public List<Member> getParticipantsForClass(int fitnessClassId) {
        List<Member> participants = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password " +
                "FROM class_participants cp " +
                "JOIN members m ON cp.member_id = m.user_id " +
                "JOIN users u ON m.user_id = u.id " +
                "WHERE cp.fitness_class_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fitnessClassId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setUsername(rs.getString("username"));
                member.setEmail(rs.getString("email"));
                member.setPhoneNumber(rs.getString("phone"));
                member.setPassword(rs.getString("password"));
                // Poți completa și alte atribute dacă vrei
                participants.add(member);
            }
        } catch (SQLException e) {
            System.out.println("Eroare la citirea participanților: " + e.getMessage());
        }
        return participants;
    }

    public List<FitnessClass> getClassesForMember(int memberId) {
        List<FitnessClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM fitness_classes WHERE id IN (SELECT fitness_class_id FROM class_participants WHERE member_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FitnessClass fitnessClass = new FitnessClass();
                fitnessClass.setName(rs.getString("name"));
                fitnessClass.setDuration(rs.getInt("duration"));
                fitnessClass.setDifficulty(rs.getString("difficulty"));
                fitnessClass.setPrice(rs.getDouble("price"));
                fitnessClass.setDate(rs.getDate("date").toLocalDate());
                fitnessClass.setHour(rs.getTime("hour").toLocalTime());
                fitnessClass.setMaxParticipants(rs.getInt("max_participants"));

                // aici apelezi DAO-ul pentru participanti pt fiecare clasa
                ClassParticipantsDAO participantsDAO = new ClassParticipantsDAO();
                fitnessClass.setParticipants(participantsDAO.getParticipantsForClass(rs.getInt("id")));

                classes.add(fitnessClass);
            }
        } catch (SQLException e) {
            System.out.println("Eroare la citirea claselor pentru membru: " + e.getMessage());
        }
        return classes;
    }

}
