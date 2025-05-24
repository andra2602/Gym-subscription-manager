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

    public void removeParticipantsForClass(int classId) {
        String sql = "DELETE FROM class_participants WHERE fitness_class_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Member> getParticipantsForClass(int classId) {
        List<Member> participants = new ArrayList<>();
        String sql = "SELECT u.* FROM class_participants cp " +
                "JOIN members m ON cp.member_id = m.user_id " +
                "JOIN users u ON m.user_id = u.id " +
                "WHERE cp.fitness_class_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Member m = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        null, 0f, 0f, "beginner", null, null, false
                );
                m.setId(rs.getInt("id"));
                participants.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participants;
    }

    public int countParticipantsForClass(int classId) {
        String sql = "SELECT COUNT(*) FROM class_participants WHERE fitness_class_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Eroare la numărarea participanților: " + e.getMessage());
        }
        return 0;
    }

    public boolean isMemberAlreadyEnrolled(int memberId, int classId) {
        String sql = "SELECT COUNT(*) FROM class_participants WHERE member_id = ? AND fitness_class_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, classId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Eroare la verificarea participării la clasă: " + e.getMessage());
        }

        return false;
    }

    public boolean addParticipantToClass(int memberId, int classId) {
        String sql = "INSERT INTO class_participants (member_id, fitness_class_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, classId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la adăugarea participantului la clasă: " + e.getMessage());
        }

        return false;
    }

}
