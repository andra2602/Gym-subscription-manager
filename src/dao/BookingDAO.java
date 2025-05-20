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

    public BookingDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void create(Booking booking) {
        String sql = "INSERT INTO bookings (member_id, trainer_id, fitness_class_id, date, time, purpose) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            // member_id (poate fi null)
            if (booking.getMember() != null) {
                stmt.setInt(1, booking.getMember().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            // trainer_id
            stmt.setInt(2, booking.getTrainer().getId());

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
            System.out.println("❌ Error inserting booking: " + e.getMessage());
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

    public List<Booking> readByMember(int memberId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE member_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(extractBooking(rs));
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea rezervărilor membrului: " + e.getMessage());
        }

        return bookings;
    }

    public boolean deleteById(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea booking-ului: " + e.getMessage());
            return false;
        }
    }

    private Booking extractBooking(ResultSet rs) throws SQLException {
        // Construim obiectul Booking parțial (fără Member, Trainer complet încă)
        Booking booking = new Booking();
        booking.setDate(LocalDate.parse(rs.getString("date")));
        booking.setTimeSlot(LocalTime.parse(rs.getString("time")));
        booking.setPurpose(rs.getString("purpose"));

        // Minimalist - doar setăm ID-uri. Dacă vrei detalii complete, folosește DAO-uri pentru member/trainer
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

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    public Booking getBookingForSlot(int trainerId, LocalTime time, LocalDate date) {
        String sql = "SELECT * FROM bookings WHERE trainer_id = ? AND time = ? AND date = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, time.toString());
            stmt.setString(3, date.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int memberId = rs.getInt("member_id");
                int classId = rs.getInt("fitness_class_id");
                String purpose = rs.getString("purpose");

                Member member = null;
                if (!rs.wasNull()) {
                    member = new Member();
                    member.setId(memberId); // sau folosește memberDAO dacă vrei obiect complet
                }

                FitnessClass fitnessClass = null;
                if (classId != 0) {
                    fitnessClass = new FitnessClass();
                    fitnessClass.setId(classId); // opțional: poți folosi FitnessClassDAO
                }

                Trainer trainer = new Trainer(trainerId);

                Booking booking = new Booking();
                booking.setTrainer(trainer);
                booking.setDate(date);
                booking.setTimeSlot(time);
                booking.setPurpose(purpose);
                booking.setMember(member);
                booking.setFitnessClass(fitnessClass);

                return booking;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Booking> getBookingsForTrainerByDate(int trainerId, LocalDate date) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE trainer_id = ? AND date = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setString(2, date.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setTrainer(new Trainer(trainerId));
                booking.setDate(date);
                booking.setTimeSlot(LocalTime.parse(rs.getString("time")));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bookings: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> getBookingsForTrainerByMember(int trainerId, int memberId) {
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM bookings WHERE trainer_id = ? AND member_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setInt(2, memberId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                LocalTime time = LocalTime.parse(rs.getString("time"));

                // Pentru review ne interesează doar data și ora
                Booking booking = new Booking(null, null, date, time);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println("❌ Eroare la getBookingsForTrainerByMember: " + e.getMessage());
        }

        return bookings;
    }


    public void deleteBookingsForClass(int classId) {
        String sql = "DELETE FROM bookings WHERE fitness_class_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
