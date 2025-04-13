package services;
import models.*;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void listActivePromotions() {
        LocalDate today = LocalDate.now();

        List<Promotion> activeNow = promotions.stream()
                .filter(Promotion::isValidNow)
                .sorted(Comparator.comparing(Promotion::getStartDate))
                .toList();

        List<Promotion> comingSoon = promotions.stream()
                .filter(p -> p.isActive() && today.isBefore(p.getStartDate()))
                .sorted(Comparator.comparing(Promotion::getStartDate))
                .toList();

        boolean shown = false;

        if (!activeNow.isEmpty()) {
            System.out.println("=== 🟢 Active Promotions Now ===");
            for (Promotion p : activeNow) {
                System.out.printf("Promotion: %s (%.1f%% OFF)\n", p.getName(), p.getDiscountPercent());
                System.out.println("Description: " + p.getDescription());
                System.out.println("Valid: " + p.getStartDate() + " → " + p.getEndDate());
                System.out.println("------------------------------------");
            }
            shown = true;
        }

        if (!comingSoon.isEmpty()) {
            System.out.println("=== 🕒 Coming Soon Promotions ===");
            for (Promotion p : comingSoon) {
                System.out.printf("Promotion: %s (%.1f%% OFF)\n", p.getName(), p.getDiscountPercent());
                System.out.println("Description: " + p.getDescription());
                System.out.println("Valid: " + p.getStartDate() + " → " + p.getEndDate());
                System.out.println("------------------------------------");
            }
            shown = true;
        }

        if (!shown) {
            System.out.println("No promotions available at the moment.");
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

            String description;
            while (true) {
                System.out.println("Enter description (10–200 characters):");
                description = scanner.nextLine();
                if (description.length() >= 10 && description.length() <= 200) break;
                System.out.println("❌ Description must be between 10 and 200 characters.");
            }

            float discount;
            while (true) {
                System.out.println("Enter discount percentage (0–100):");
                try {
                    discount = Float.parseFloat(scanner.nextLine());
                    if (discount >= 0 && discount <= 100) break;
                    else System.out.println("❌ Discount must be between 0 and 100.");
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid number. Please enter a valid float.");
                }
            }

            LocalDate startDate;
            while (true) {
                System.out.println("Enter start date (YYYY-MM-DD):");
                try {
                    startDate = LocalDate.parse(scanner.nextLine());
                    if (!startDate.isBefore(LocalDate.now())) break;
                    else System.out.println("❌ Start date can't be in the past.");
                } catch (Exception e) {
                    System.out.println("❌ Invalid date format or non-existent date. Please try again.");
                }
            }

            LocalDate endDate;
            while (true) {
                System.out.println("Enter end date (YYYY-MM-DD):");
                try {
                    endDate = LocalDate.parse(scanner.nextLine());
                    if (!endDate.isBefore(startDate)) break;
                    else System.out.println("❌ End date must be after or equal to the start date.");
                } catch (Exception e) {
                    System.out.println("❌ Invalid date format or non-existent date. Please try again.");
                }
            }

            Promotion promo = new Promotion(name, description, discount, startDate, endDate);
            promo.setActive(true); // just in case
            promotions.add(promo);

            System.out.println("✅ Promotion added successfully!");

        } catch (Exception e) {
            System.out.println("⚠ Unexpected error while adding promotion: " + e.getMessage());
        }
    }


    public void editPromotion(Scanner scanner) {
        List<Promotion> activePromotions = promotions.stream()
                .filter(Promotion::isActive)
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
                        float discount;
                        while (true) {
                            System.out.println("Enter new discount percentage (0–100):");
                            try {
                                discount = Float.parseFloat(scanner.nextLine());
                                if (discount >= 0 && discount <= 100) break;
                                System.out.println("❌ Must be between 0 and 100.");
                            } catch (NumberFormatException e) {
                                System.out.println("❌ Invalid input. Please enter a number.");
                            }
                        }
                        promo.setDiscountPercent(discount);
                        System.out.println("✔ Discount updated!");
                        break;
                    case 4:
                        LocalDate newStart;
                        while (true) {
                            System.out.println("Enter new start date (YYYY-MM-DD):");
                            try {
                                newStart = LocalDate.parse(scanner.nextLine());
                                if (!newStart.isBefore(LocalDate.now())) break;
                                System.out.println("❌ Start date can't be in the past.");
                            } catch (Exception e) {
                                System.out.println("❌ Invalid date format.");
                            }
                        }
                        promo.setStartDate(newStart);
                        System.out.println("✔ Start date updated!");
                        break;
                    case 5:
                        LocalDate newEnd;
                        while (true) {
                            System.out.println("Enter new end date (YYYY-MM-DD):");
                            try {
                                newEnd = LocalDate.parse(scanner.nextLine());
                                if (!newEnd.isBefore(promo.getStartDate())) break;
                                System.out.println("❌ End date can't be before start date (" + promo.getStartDate() + ").");
                            } catch (Exception e) {
                                System.out.println("❌ Invalid date format.");
                            }
                        }
                        promo.setEndDate(newEnd);
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

        List<Promotion> displayList = new ArrayList<>(promotions);

        System.out.println("\n--- All Promotions ---");
        for (int i = 0; i < displayList.size(); i++) {
            Promotion p = displayList.get(i);
            System.out.printf("%d. %s (%.1f%% OFF) [%s → %s]\n",
                    i + 1, p.getName(), p.getDiscountPercent(), p.getStartDate(), p.getEndDate());
        }

        System.out.println("Enter the number of the promotion to remove:");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > displayList.size()) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        Promotion selected = displayList.get(index - 1);
        promotions.remove(selected); // eliminăm din lista reală
        System.out.println("✅ Removed promotion: " + selected.getName());
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
                .filter(p -> !p.isActive()) // mai corect decât !p.isValidNow()
                .toList();

        if (inactivePromos.isEmpty()) {
            System.out.println("All promotions are currently active. Nothing to reactivate.");
            return;
        }

        System.out.println("\n--- Inactive Promotions ---");
        for (int i = 0; i < inactivePromos.size(); i++) {
            Promotion p = inactivePromos.get(i);
            System.out.printf("%d. %s (%.1f%% OFF) | Last valid until: %s\n",
                    i + 1, p.getName(), p.getDiscountPercent(), p.getEndDate());
        }

        System.out.println("Enter the number of the promotion to reactivate:");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > inactivePromos.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Promotion selected = inactivePromos.get(choice - 1);

        LocalDate newStart;
        while (true) {
            System.out.println("Enter new start date (YYYY-MM-DD):");
            try {
                newStart = LocalDate.parse(scanner.nextLine());
                if (!newStart.isBefore(LocalDate.now())) break;
                else System.out.println("❌ Start date can't be in the past.");
            } catch (Exception e) {
                System.out.println("❌ Invalid date format or nonexistent date.");
            }
        }

        LocalDate newEnd;
        while (true) {
            System.out.println("Enter new end date (YYYY-MM-DD):");
            try {
                newEnd = LocalDate.parse(scanner.nextLine());
                if (!newEnd.isBefore(newStart)) break;
                else System.out.println("❌ End date must be after or equal to the start date.");
            } catch (Exception e) {
                System.out.println("❌ Invalid date format or nonexistent date.");
            }
        }

        selected.setStartDate(newStart);
        selected.setEndDate(newEnd);
        selected.setActive(true);

        System.out.println("✅ Promotion \"" + selected.getName() + "\" has been reactivated!");
    }


}
