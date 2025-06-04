package dao;

import database.DBConnection;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private final Connection connection;
    private final MemberDAO memberDAO = new MemberDAO();
    public BookingDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void create(Booking booking) {
        String sql = "INSERT INTO bookings (member_id, trainer_id, fitness_class_id, date, time, purpose) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getMember().getId());

            if (booking.getTrainer() != null) {
                stmt.setInt(2, booking.getTrainer().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (booking.getFitnessClass() != null) {
                stmt.setInt(3, booking.getFitnessClass().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, booking.getDate().toString());
            stmt.setString(5, booking.getTimeSlot().toString());
            stmt.setString(6, booking.getPurpose());

            stmt.executeUpdate();
            System.out.println("Booking creat cu succes.");

        } catch (SQLException e) {
            System.out.println("Eroare la crearea booking-ului: " + e.getMessage());
        }
    }

    public void addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (member_id, trainer_id, fitness_class_id, date, time, purpose) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // member_id (poate fi null)
            if (booking.getMember() != null) {
                stmt.setInt(1, booking.getMember().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            // trainer_id
            if (booking.getTrainer() != null) {
                stmt.setInt(2, booking.getTrainer().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }


            // fitness_class_id (poate fi null)
            if (booking.getFitnessClass() != null) {
                stmt.setInt(3, booking.getFitnessClass().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            // date
            stmt.setString(4, booking.getDate().toString());

            // time
            stmt.setString(5, booking.getTimeSlot().toString());

            // purpose
            stmt.setString(6, booking.getPurpose() != null ? booking.getPurpose() : "General Booking");

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting booking: " + e.getMessage());
        }
    }

    public List<Booking> readAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = extractBooking(rs);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea booking-urilor: " + e.getMessage());
        }

        return bookings;
    }

    private Booking extractBooking(ResultSet rs) throws SQLException {
        // Construim obiectul Booking partial (fără Member, Trainer)
        Booking booking = new Booking();
        booking.setDate(LocalDate.parse(rs.getString("date")));
        booking.setTimeSlot(LocalTime.parse(rs.getString("time")));
        booking.setPurpose(rs.getString("purpose"));

        Member member = new Member();
        member.setId(rs.getInt("member_id"));
        booking.setMember(member);

        int trainerId = rs.getInt("trainer_id");
        if (!rs.wasNull()) {
            Trainer trainer = new Trainer();
            trainer.setId(trainerId);
            booking.setTrainer(trainer);
        }

        int classId = rs.getInt("fitness_class_id");
        if (!rs.wasNull()) {
            FitnessClass fc = new FitnessClass();
            fc.setId(classId);
            booking.setFitnessClass(fc);
        }

        return booking;
    }

    public boolean isSlotBooked(int trainerId, LocalTime startTime, LocalDate date) {
        String sql = "SELECT 1 FROM bookings WHERE trainer_id = ? AND time = ? AND date = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, startTime.toString());
            stmt.setString(3, date.toString());

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // dacă găsește ceva, înseamnă că e ocupat

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Booking> getBookingsForTrainerByDate(int trainerId, LocalDate date) {
        List<Booking> bookings = new ArrayList<>();
        List<Integer> memberIds = new ArrayList<>();
        List<LocalTime> times = new ArrayList<>();
        List<String> purposes = new ArrayList<>();

        String sql = "SELECT * FROM bookings WHERE trainer_id = ? AND date = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setString(2, date.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalTime time = LocalTime.parse(rs.getString("time"));
                    String purpose = rs.getString("purpose");

                    int memberId = rs.getInt("member_id");
                    if (rs.wasNull()) memberId = -1;

                    times.add(time);
                    purposes.add(purpose);
                    memberIds.add(memberId);
                }
            }

            for (int i = 0; i < times.size(); i++) {
                Member member = null;
                if (memberIds.get(i) != -1) {
                    member = memberDAO.readById(memberIds.get(i));
                }

                Booking booking = new Booking(
                        new Trainer(trainerId),
                        member,
                        date,
                        times.get(i),
                        purposes.get(i)
                );

                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> getBookingsForTrainerByMember(int trainerId, int memberId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE trainer_id = ? AND member_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, memberId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                LocalTime time = LocalTime.parse(rs.getString("time"));
                bookings.add(new Booking(null, null, date, time));
            }

        } catch (SQLException e) {
            System.out.println("Eroare la getBookingsForTrainerByMember: " + e.getMessage());
        }

        return bookings;
    }

    public void deleteBookingsForClass(FitnessClass fitnessClass) {
        String sql = "DELETE FROM bookings WHERE fitness_class_id = ? " +
                "OR (fitness_class_id = 0 AND trainer_id = ? AND date = ? AND purpose = ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fitnessClass.getId());
            stmt.setInt(2, fitnessClass.getTrainer().getId());
            stmt.setString(3, fitnessClass.getDate().toString());
            stmt.setString(4, fitnessClass.getName());

            int deleted = stmt.executeUpdate();
            System.out.println("Booking-uri șterse: " + deleted);

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea booking-urilor pentru clasă: " + e.getMessage());
        }
    }




}
