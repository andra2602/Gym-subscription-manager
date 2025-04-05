package services;
import models.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class PromotionService {
    private List<Promotion> promotions;

    public PromotionService(List<Promotion> promotions) {
        this.promotions = promotions;
    }
    public void checkPromotionValidityOnStartup() {
        for (Promotion p : promotions) {
            if (p.getEndDate().isBefore(LocalDate.now())) {
                p.setActive(false);
            }
        }
    }

    public void listActivePromotions() {
        boolean foundActivePromotion = false;
        for (Promotion promotion : promotions) {
            if (promotion.isValidNow()) {
                System.out.println("Promotion: " + promotion.getName());
                System.out.println("Description: " + promotion.getDescription());
                System.out.println("Discount: " + promotion.getDiscountPercent() + "%");
                System.out.println("Start Date: " + promotion.getStartDate());
                System.out.println("End Date: " + promotion.getEndDate());
                System.out.println("------------------------------------");
                foundActivePromotion = true;
            }
        }
        if (!foundActivePromotion) {
            System.out.println("No active promotions available.");
        }
    }
    public void listAllPromotions() {
        if (promotions.isEmpty()) {
            System.out.println("⚠ No promotions available.");
            return;
        }

        // Separăm activele de inactive
        List<Promotion> activePromos = promotions.stream()
                .filter(Promotion::isActive)
                .sorted(Comparator.comparing(Promotion::getStartDate))
                .toList();

        List<Promotion> inactivePromos = promotions.stream()
                .filter(p -> !p.isActive())
                .sorted(Comparator.comparing(Promotion::getEndDate).reversed())
                .toList();

        System.out.println("\n--- All Promotions ---");
        int counter = 1;

        if (!activePromos.isEmpty()) {
            System.out.println("🟢 Active Promotions:");
            for (Promotion p : activePromos) {
                System.out.printf("%d. %s (%.1f%% OFF)\n", counter++, p.getName(), p.getDiscountPercent());
                System.out.printf("   Valid: %s → %s\n", p.getStartDate(), p.getEndDate());
                System.out.println("   Description: " + p.getDescription());
                System.out.println("   Status: ✅ Active now");
                System.out.println("--------------------------------------");
            }
        }

        if (!inactivePromos.isEmpty()) {
            System.out.println("🔴 Inactive Promotions:");
            for (Promotion p : inactivePromos) {
                System.out.printf("%d. %s (%.1f%% OFF)\n", counter++, p.getName(), p.getDiscountPercent());
                System.out.printf("   Valid: %s → %s\n", p.getStartDate(), p.getEndDate());
                System.out.println("   Description: " + p.getDescription());
                System.out.println("   Status: ❌ Inactive");
                System.out.println("--------------------------------------");
            }
        }
    }


    public void addPromotion(Scanner scanner) {
        try {
            System.out.println("Enter promotion name:");
            String name = scanner.nextLine();

            System.out.println("Enter description (10-200 characters):");
            String description = scanner.nextLine();

            System.out.println("Enter discount percentage (0–100):");
            float discount = scanner.nextFloat();
            scanner.nextLine();

            System.out.println("Enter start date (YYYY-MM-DD):");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.println("Enter end date (YYYY-MM-DD):");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            Promotion promo = new Promotion(name, description, discount, startDate, endDate);
            promotions.add(promo);

            System.out.println("Promotion added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding promotion: " + e.getMessage());
        }
    }

    public void editPromotion(Scanner scanner) {
        List<Promotion> activePromotions = promotions.stream()
                .filter(Promotion::isValidNow)
                .toList();

        if (activePromotions.isEmpty()) {
            System.out.println("No active promotions to edit.");
            return;
        }

        System.out.println("\n--- Active Promotions ---");
        for (int i = 0; i < activePromotions.size(); i++) {
            Promotion p = activePromotions.get(i);
            System.out.printf("%d. %s (%.1f%%) valid until %s\n",
                    i + 1, p.getName(), p.getDiscountPercent(), p.getEndDate());
        }

        System.out.println("Enter the number of the promotion to edit:");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > activePromotions.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Promotion promo = activePromotions.get(index - 1);
        boolean done = false;

        System.out.println("Editing promotion: " + promo.getName());

        while (!done) {
            System.out.println("\nWhat would you like to edit?");
            System.out.println("1. Name");
            System.out.println("2. Description");
            System.out.println("3. Discount percentage");
            System.out.println("4. Start date");
            System.out.println("5. End date");
            System.out.println("6. Finish editing");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.println("Enter new name:");
                        promo.setName(scanner.nextLine());
                        System.out.println("✔ Name updated!");
                        break;
                    case 2:
                        System.out.println("Enter new description:");
                        promo.setDescription(scanner.nextLine());
                        System.out.println("✔ Description updated!");
                        break;
                    case 3:
                        System.out.println("Enter new discount percentage:");
                        promo.setDiscountPercent(scanner.nextFloat());
                        scanner.nextLine();
                        System.out.println("✔ Discount updated!");
                        break;
                    case 4:
                        System.out.println("Enter new start date (YYYY-MM-DD):");
                        promo.setStartDate(LocalDate.parse(scanner.nextLine()));
                        System.out.println("✔ Start date updated!");
                        break;
                    case 5:
                        System.out.println("Enter new end date (YYYY-MM-DD):");
                        promo.setEndDate(LocalDate.parse(scanner.nextLine()));
                        System.out.println("✔ End date updated!");
                        break;
                    case 6:
                        done = true;
                        System.out.println("✔ Promotion saved and editing finished.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }

            } catch (Exception e) {
                System.out.println("⚠ Error: " + e.getMessage());
            }
        }
    }

    public void removePromotion(Scanner scanner) {
        if (promotions.isEmpty()) {
            System.out.println("No promotions to remove.");
            return;
        }

        listAllPromotions();
        System.out.println("Enter the number of the promotion to remove:");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > promotions.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Promotion removed = promotions.remove(index - 1);
        System.out.println("Removed promotion: " + removed.getName());
    }


    public void deactivatePromotion(Scanner scanner) {
        List<Promotion> activePromos = promotions.stream()
                .filter(Promotion::isActive)
                .toList();

        if (activePromos.isEmpty()) {
            System.out.println("No active promotions to deactivate.");
            return;
        }

        System.out.println("\n--- Active Promotions ---");
        for (int i = 0; i < activePromos.size(); i++) {
            Promotion p = activePromos.get(i);
            System.out.printf("%d. %s (%.1f%% OFF) valid until %s\n", i + 1, p.getName(), p.getDiscountPercent(), p.getEndDate());
        }

        System.out.println("Enter the number of the promotion to deactivate:");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > activePromos.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Promotion selected = activePromos.get(index - 1);
        selected.setActive(false);
        System.out.println("✅ Promotion \"" + selected.getName() + "\" has been deactivated.");
    }

    public void reactivatePromotion(Scanner scanner) {
        List<Promotion> inactivePromos = promotions.stream()
                .filter(p -> !p.isValidNow())
                .toList();

        if (inactivePromos.isEmpty()) {
            System.out.println("All promotions are currently active. Nothing to reactivate.");
            return;
        }

        System.out.println("\n--- Inactive Promotions ---");
        for (int i = 0; i < inactivePromos.size(); i++) {
            Promotion p = inactivePromos.get(i);
            System.out.printf("%d. %s (%.1f%% OFF) | Expired on: %s\n", i + 1, p.getName(), p.getDiscountPercent(), p.getEndDate());
        }

        System.out.println("Enter the number of the promotion to reactivate:");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > inactivePromos.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Promotion selected = inactivePromos.get(choice - 1);

        try {
            System.out.println("Enter new start date (YYYY-MM-DD):");
            LocalDate newStart = LocalDate.parse(scanner.nextLine());

            System.out.println("Enter new end date (YYYY-MM-DD):");
            LocalDate newEnd = LocalDate.parse(scanner.nextLine());

            selected.setStartDate(newStart);
            selected.setEndDate(newEnd);
            selected.setActive(true);

            System.out.println("✅ Promotion \"" + selected.getName() + "\" has been reactivated!");
        } catch (Exception e) {
            System.out.println("⚠ Error while updating dates: " + e.getMessage());
        }
    }









}
