package dao;

import database.DBConnection;
import models.Payment;
import models.PaymentMethod;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDAO extends BaseDAO<Payment, Integer> {

    private static PaymentDAO instance;
    private final Connection connection;

    private PaymentDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public static PaymentDAO getInstance() {
        if (instance == null) {
            instance = new PaymentDAO();
        }
        return instance;
    }
    @Override
    public void create(Payment payment) {
        String sql = "INSERT INTO payments (amount, payment_date, payment_method, member_id, purpose) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, payment.getAmount());
            stmt.setString(2, payment.getPaymentDate().toString());
            stmt.setString(3, payment.getPaymentMethod().name());
            stmt.setInt(4, payment.getMember().getId());
            stmt.setString(5, payment.getPurpose());

            stmt.executeUpdate();
            System.out.println("Payment recorded successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting payment: " + e.getMessage());
        }
    }
    @Override
    public Optional<Payment> read(Integer id) {
        String sql = "SELECT * FROM payments WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Payment p = new Payment(
                        rs.getFloat("amount"),
                        LocalDate.parse(rs.getString("payment_date")),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        null,
                        rs.getString("purpose")
                );
                return Optional.of(p);
            }

        } catch (SQLException e) {
            System.out.println("Error reading payment: " + e.getMessage());
        }

        return Optional.empty();
    }

    public void addPayment(Payment payment) {
        String sql = "INSERT INTO payments (amount, payment_date, payment_method, member_id, purpose) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, payment.getAmount());
            stmt.setString(2, payment.getPaymentDate().toString());
            stmt.setString(3, payment.getPaymentMethod().toString());
            stmt.setInt(4, payment.getMember().getId());
            stmt.setString(5, payment.getPurpose());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Payment> getPaymentsByMemberId(int memberId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE member_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment p = new Payment(
                        rs.getFloat("amount"),
                        LocalDate.parse(rs.getString("payment_date")),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        null,
                        rs.getString("purpose")
                );
                payments.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public List<Payment> getPaymentsForMember(int memberId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE member_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment p = new Payment(
                        rs.getFloat("amount"),
                        LocalDate.parse(rs.getString("payment_date")),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        null, // îl poți lăsa null aici, dacă nu ai nevoie de membru complet
                        rs.getString("purpose")
                );
                payments.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public List<Payment> getPaymentsBetweenDates(LocalDate start, LocalDate end) {
        List<Payment> payments = new ArrayList<>();

        String sql = "SELECT * FROM payments WHERE payment_date BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, start.toString());
            stmt.setString(2, end.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getFloat("amount"),
                        LocalDate.parse(rs.getString("payment_date")),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        null,
                        rs.getString("purpose")
                );
                payments.add(payment);
            }

        } catch (SQLException e) {
            System.out.println("Error loading payments: " + e.getMessage());
        }

        return payments;
    }
    public Payment getLastSubscriptionPaymentForMember(int memberId) {
        String sql = "SELECT * FROM payments WHERE member_id = ? AND purpose LIKE 'New Subscription%' ORDER BY payment_date DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Payment(
                        rs.getFloat("amount"),
                        LocalDate.parse(rs.getString("payment_date")),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        null,
                        rs.getString("purpose")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving last subscription payment: " + e.getMessage());
        }

        return null;
    }



}
