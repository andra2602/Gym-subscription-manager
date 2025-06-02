package services;

import models.Manager;
import models.Payment;
import dao.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ManagerService {

    private List<Manager> managers;
    private PaymentDAO paymentDAO = new PaymentDAO();

    public ManagerService() {
        this.managers = new ArrayList<>();
        // Manageri predefiniți
        Manager manager1 = new Manager("Andra Andruta", "andra_mihaela2602", "andra.andruta60@gmail.com", "0712345678", "Password123*");
        Manager manager2 = new Manager("Alexandru Firica", "alexx.firica", "alexfirica5@gmail.com", "0723456789", "Password456!");

        managers.add(manager1);
        managers.add(manager2);
    }

    public Manager findByUsernameAndPassword(String username, String password) {
        for (Manager manager : managers) {
            if (manager.getUsername().equals(username) && manager.getPassword().equals(password)) {
                return manager;
            }
        }
        return null;
    }

    public void calculateRevenue(Scanner scanner) {
        System.out.println("Enter start date (YYYY-MM-DD):");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.println("Enter end date (YYYY-MM-DD):");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        List<Payment> payments = paymentDAO.getPaymentsBetweenDates(start, end);

        double totalRevenue = payments.stream()
                .filter(p -> p.getAmount() > 0)
                .mapToDouble(Payment::getAmount)
                .sum();

        double totalRefunds = payments.stream()
                .filter(p -> p.getAmount() < 0)
                .mapToDouble(Payment::getAmount)
                .sum();

        double netTotal = totalRevenue + totalRefunds;

        System.out.println("\nRevenue Report from " + start + " to " + end);
        System.out.printf("Total income: %.2f RON\n", totalRevenue);
        System.out.printf("Total refunds: %.2f RON\n", totalRefunds);
        System.out.printf("Net revenue: %.2f RON\n", netTotal);
    }

    public void viewFullAuditLog() {
        System.out.println("\n📜 Full Audit Log:");
        try (BufferedReader reader = new BufferedReader(new FileReader("audit.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to read audit file: " + e.getMessage());
        }
    }

    public void viewAuditLogForDate(Scanner scanner) {
        System.out.println("Enter date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();

        System.out.println("\n📜 Audit Log for " + dateInput + ":");
        try (BufferedReader reader = new BufferedReader(new FileReader("audit.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(dateInput)) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to filter audit log: " + e.getMessage());
        }
    }


}