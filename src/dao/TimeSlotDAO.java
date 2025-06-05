package dao;

import database.DBConnection;
import models.TimeSlot;
import models.Trainer;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimeSlotDAO extends BaseDAO<TimeSlot, Integer> {

    private static TimeSlotDAO instance;

    private TimeSlotDAO() {
    }

    public static TimeSlotDAO getInstance() {
        if (instance == null) {
            instance = new TimeSlotDAO();
        }
        return instance;
    }

    // Creează un nou time slot pentru un trainer
    @Override
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
            System.out.println("Error inserting time slot: " + e.getMessage());
        }
    }

    @Override
    public Optional<TimeSlot> read(Integer id) {
        String sql = "SELECT * FROM time_slots WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TimeSlot slot = new TimeSlot(
                        LocalTime.parse(rs.getString("start_time")),
                        LocalTime.parse(rs.getString("end_time")),
                        DayOfWeek.valueOf(rs.getString("day")),
                        new Trainer(rs.getInt("trainer_id"))
                );
                return Optional.of(slot);
            }

        } catch (SQLException e) {
            System.out.println("Error reading time slot: " + e.getMessage());
        }

        return Optional.empty();
    }
    // Șterge un slot după ID
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

    public void addTimeSlot(TimeSlot slot) {
        String sql = "INSERT INTO time_slots (start_time, end_time, day, trainer_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, slot.getStartTime().toString());
            stmt.setString(2, slot.getEndTime().toString());
            stmt.setString(3, slot.getDay().toString());
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
                        new Trainer(rs.getInt("trainer_id"))
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
            System.out.println("Error checking slot existence: " + e.getMessage());
        }

        return false;
    }

}
