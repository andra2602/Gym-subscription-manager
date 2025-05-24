package dao;

import database.DBConnection;
import models.TimeSlot;
import models.Trainer;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    private final Connection connection;

    public TimeSlotDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Creează un nou time slot pentru un trainer
    public void create(TimeSlot slot) {
        String sql = "INSERT INTO time_slots (start_time, end_time, day, trainer_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, slot.getStartTime().toString());
            stmt.setString(2, slot.getEndTime().toString());
            stmt.setString(3, slot.getDay().toString());
            stmt.setInt(4, slot.getTrainer().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error inserting time slot: " + e.getMessage());
        }
    }


    // Afișează toate sloturile pentru un anumit trainer și o anumită zi
    public List<TimeSlot> readByTrainerAndDay(int trainerId, DayOfWeek day) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE trainer_id = ? AND day = ? ORDER BY start_time";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, day.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TimeSlot slot = extractTimeSlot(rs);
                slots.add(slot);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea TimeSlot-urilor: " + e.getMessage());
        }

        return slots;
    }


    // Șterge un slot după ID (dacă ai ID în DB)
    public boolean delete(int id) {
        String sql = "DELETE FROM time_slots WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea TimeSlot-ului: " + e.getMessage());
            return false;
        }
    }

    // Șterge toate sloturile unui trainer (ex: când îl scoți din sistem)
    public boolean deleteByTrainer(int trainerId) {
        String sql = "DELETE FROM time_slots WHERE trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea TimeSlot-urilor trainerului: " + e.getMessage());
            return false;
        }
    }

    // Helper pentru a construi un TimeSlot din ResultSet
    private TimeSlot extractTimeSlot(ResultSet rs) throws SQLException {
        LocalTime startTime = LocalTime.parse(rs.getString("start_time"));
        LocalTime endTime = LocalTime.parse(rs.getString("end_time"));
        DayOfWeek day = DayOfWeek.valueOf(rs.getString("day"));
        int trainerId = rs.getInt("trainer_id");

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        return new TimeSlot(startTime, endTime, day, trainer);
    }

    public void addTimeSlot(TimeSlot slot) {
        String sql = "INSERT INTO time_slots (start_time, end_time, day, trainer_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, slot.getStartTime().toString());
            stmt.setString(2, slot.getEndTime().toString());
            stmt.setString(3, slot.getDay().toString()); // DayOfWeek as String (e.g. MONDAY)
            stmt.setInt(4, slot.getTrainer().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TimeSlot> getTimeSlotsForTrainerByDay(int trainerId, DayOfWeek day) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE trainer_id = ? AND day = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, day.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                slots.add(new TimeSlot(
                        LocalTime.parse(rs.getString("start_time")),
                        LocalTime.parse(rs.getString("end_time")),
                        DayOfWeek.valueOf(rs.getString("day")),
                        new Trainer(rs.getInt("trainer_id")) // trainer dummy, doar cu id
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return slots;
    }

    public boolean exists(int trainerId, LocalTime startTime, DayOfWeek day) {
        String sql = "SELECT COUNT(*) FROM time_slots WHERE trainer_id = ? AND start_time = ? AND day = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, startTime.toString());
            stmt.setString(3, day.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error checking slot existence: " + e.getMessage());
        }

        return false;
    }


}
