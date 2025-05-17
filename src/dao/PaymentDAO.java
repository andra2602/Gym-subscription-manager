package dao;

import database.DBConnection;
import models.Member;
import models.Payment;
import models.PaymentMethod;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private final Connection connection;

    public PaymentDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void create(Payment payment) {
        String sql = "INSERT INTO payments (amount, payment_date, payment_method, member_id, purpose) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    public List<Payment> readByMemberId(int memberId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE member_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getFloat("amount"),
                        LocalDate.parse(rs.getString("payment_date")),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        null, // Poți seta member aici dacă vrei, dar nu e obligatoriu
                        rs.getString("purpose")
                );
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.out.println("Error reading payments: " + e.getMessage());
        }

        return payments;
    }

}
