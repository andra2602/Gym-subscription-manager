//package services;
//import models.*;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Scanner;
//import java.util.stream.Collectors;
//
//public class FitnessClassService {
//
//    private List<FitnessClass> fitnessClasses;
//
//    public FitnessClassService(List<FitnessClass> fitnessClasses) {
//        this.fitnessClasses = fitnessClasses;
//    }
//    public void listFitnessClasses() {
//        if (fitnessClasses.isEmpty()) {
//            System.out.println("No fitness classes available.");
//            System.out.println("Try again later...");
//            return;
//        }
//
//        System.out.println("\nList of fitness classes:");
//        for (FitnessClass fitnessClass : fitnessClasses) {
//            System.out.println(fitnessClass.toString());
//            System.out.println("------------------------------------");
//        }
//    }
//    public void addFitnessClass(FitnessClass fitnessClass) {
//        this.fitnessClasses.add(fitnessClass);
//    }
//    public void removeFitnessClass(FitnessClass fitnessClass) {
//        fitnessClasses.remove(fitnessClass);
//    }
//
//
//    public void scheduleFitnessClass(Scanner scanner, Member member, PromotionService promotionService) {
//        List<FitnessClass> availableClasses = fitnessClasses.stream()
//                .filter(fc -> fc.getParticipants().size() < fc.getMaxParticipants())
//                .collect(Collectors.toList());
//
//        if (availableClasses.isEmpty()) {
//            System.out.println("There are no available classes or classes with open spots right now.");
//            return;
//        }
//
//        System.out.println("\n--- Available Fitness Classes ---");
//        for (int i = 0; i < availableClasses.size(); i++) {
//            FitnessClass fc = availableClasses.get(i);
//            System.out.printf("%d. %s (%s) on %s at %s - Trainer: %s\n", i + 1,
//                    fc.getName(), fc.getDifficulty(), fc.getDate(), fc.getHour(), fc.getTrainer().getName());
//            System.out.printf("   Participants: %d/%d\n", fc.getParticipants().size(), fc.getMaxParticipants());
//            System.out.println("-------------------------------------");
//        }
//
//        System.out.println("Enter the number of the class you want to join:");
//        int choice = scanner.nextInt();
//        scanner.nextLine();
//
//        if (choice < 1 || choice > availableClasses.size()) {
//            System.out.println("Invalid selection.");
//            return;
//        }
//
//        FitnessClass selectedClass = availableClasses.get(choice - 1);
//
//        if (selectedClass.getParticipants().contains(member)) {
//            System.out.println("⚠ You are already enrolled in this class.");
//            return;
//        }
//
//        float basePrice = (float) selectedClass.getPrice();
//        float finalPrice = basePrice;
//        Promotion promotion = null;
//
//        List<Promotion> promotions = promotionService.getPromotions();
//        List<Promotion> activePromos = promotions.stream()
//                .filter(Promotion::isValidNow)
//                .toList();
//
//        if (!activePromos.isEmpty()) {
//            System.out.println("Available promotions:");
//            for (int i = 0; i < activePromos.size(); i++) {
//                Promotion p = activePromos.get(i);
//                System.out.printf("%d. %s - %.1f%% off (%s → %s)\n",
//                        i + 1, p.getName(), p.getDiscountPercent(), p.getStartDate(), p.getEndDate());
//            }
//
//            System.out.println("Enter the number of the promotion you want to apply (or 0 for none):");
//            int promoChoice = scanner.nextInt();
//            scanner.nextLine();
//
//            if (promoChoice > 0 && promoChoice <= activePromos.size()) {
//                promotion = activePromos.get(promoChoice - 1);
//                float discount = promotion.getDiscountPercent();
//                finalPrice -= basePrice * (discount / 100);
//                System.out.println("Promotion applied: " + promotion.getName() + " (" + discount + "% off)");
//            } else if (promoChoice != 0) {
//                System.out.println("❌ Invalid choice. No promotion applied.");
//            }
//        } else {
//            System.out.println("No active promotions available at the moment.");
//        }
//
//        boolean added = selectedClass.addParticipant(member);
//        if (added) {
//            // Optional: Creăm un Booking și îl adăugăm în lista trainerului
//            Booking booking = new Booking(selectedClass, member, selectedClass.getDate(), selectedClass.getHour());
//            selectedClass.getTrainer().getBookings().add(booking);
//
//            // Adăugăm plata!
//            System.out.println("Select payment method (CARD, CASH, ONLINE):");
//            PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());
//
//            Payment payment = new Payment(
//                    finalPrice,
//                    LocalDate.now(),
//                    method,
//                    member,
//                    "Fitness class: " + selectedClass.getName()
//            );
//
//            member.getPayments().add(payment);
//
//            System.out.println("✅ You have successfully joined the class: " + selectedClass.getName());
//            System.out.println("Final price paid: " + finalPrice + " RON");
//        } else {
//            System.out.println("❌ Sorry, this class is now full.");
//        }
//    }
//
//}


package services;
import models.*;
import dao.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class FitnessClassService {

    private final FitnessClassDAO fitnessClassDAO;
    private final BookingDAO bookingDAO;
    private final PaymentDAO paymentDAO;
    private final ClassParticipantsDAO classParticipantsDAO;
    private final PromotionDAO promotionDAO;


    public FitnessClassService(FitnessClassDAO fitnessClassDAO, BookingDAO bookingDAO,
                               PaymentDAO paymentDAO, ClassParticipantsDAO classParticipantsDAO,
                               PromotionDAO promotionDAO) {
        this.fitnessClassDAO = fitnessClassDAO;
        this.bookingDAO = bookingDAO;
        this.paymentDAO = paymentDAO;
        this.classParticipantsDAO = classParticipantsDAO;
        this.promotionDAO = promotionDAO;
    }


    public void listFitnessClasses() {
        List<FitnessClass> fitnessClasses = fitnessClassDAO.readAll();
        if (fitnessClasses.isEmpty()) {
            System.out.println("No fitness classes available.");
            System.out.println("Try again later...");
            return;
        }

        System.out.println("\nList of fitness classes:");
        for (FitnessClass fitnessClass : fitnessClasses) {
            System.out.println(fitnessClass.toString());
            System.out.println("------------------------------------");
        }
    }

    public void scheduleFitnessClass(Scanner scanner, Member member, PromotionService promotionService) {
        List<FitnessClass> allClasses = fitnessClassDAO.readAllAvailable();
        List<FitnessClass> availableClasses = allClasses.stream()
                .filter(fc -> classParticipantsDAO.countParticipantsForClass(fc.getId()) < fc.getMaxParticipants())
                .toList();

        if (availableClasses.isEmpty()) {
            System.out.println("There are no available classes or classes with open spots right now.");
            return;
        }

        System.out.println("\n--- Available Fitness Classes ---");
        for (int i = 0; i < availableClasses.size(); i++) {
            FitnessClass fc = availableClasses.get(i);
            int currentCount = classParticipantsDAO.countParticipantsForClass(fc.getId());
            System.out.printf("%d. %s (%s) on %s at %s - Trainer: %s\n", i + 1,
                    fc.getName(), fc.getDifficulty(), fc.getDate(), fc.getHour(), fc.getTrainer().getName());
            System.out.printf("   Participants: %d/%d\n", currentCount, fc.getMaxParticipants());
            System.out.println("-------------------------------------");
        }

        System.out.println("Enter the number of the class you want to join:");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > availableClasses.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        FitnessClass selectedClass = availableClasses.get(choice - 1);

        boolean alreadyJoined = classParticipantsDAO.isMemberAlreadyEnrolled(member.getId(), selectedClass.getId());
        if (alreadyJoined) {
            System.out.println("⚠ You are already enrolled in this class.");
            return;
        }

        float basePrice = (float) selectedClass.getPrice();
        float finalPrice = basePrice;
        Promotion promotion = null;

        List<Promotion> promotions = promotionService.getPromotions();
        List<Promotion> activePromos = promotions.stream()
                .filter(Promotion::isValidNow)
                .toList();

        if (!activePromos.isEmpty()) {
            System.out.println("Available promotions:");
            for (int i = 0; i < activePromos.size(); i++) {
                Promotion p = activePromos.get(i);
                System.out.printf("%d. %s - %.1f%% off (%s → %s)\n",
                        i + 1, p.getName(), p.getDiscountPercent(), p.getStartDate(), p.getEndDate());
            }

            System.out.println("Enter the number of the promotion you want to apply (or 0 for none):");
            int promoChoice = scanner.nextInt();
            scanner.nextLine();

            if (promoChoice > 0 && promoChoice <= activePromos.size()) {
                promotion = activePromos.get(promoChoice - 1);
                float discount = promotion.getDiscountPercent();
                finalPrice -= basePrice * (discount / 100);
                System.out.println("Promotion applied: " + promotion.getName() + " (" + discount + "% off)");
            } else if (promoChoice != 0) {
                System.out.println("Invalid choice. No promotion applied.");
            }
        } else {
            System.out.println("No active promotions available at the moment.");
        }

        // Save participant in DB
        classParticipantsDAO.addParticipantToClass(member.getId(), selectedClass.getId());

        // Save booking
        Booking booking = new Booking(selectedClass, member, selectedClass.getDate(), selectedClass.getHour());
        bookingDAO.addBooking(booking);

        // Save payment
        System.out.println("Select payment method (CARD, CASH, ONLINE):");
        PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());

        Payment payment = new Payment(
                finalPrice,
                LocalDate.now(),
                method,
                member,
                "Fitness class: " + selectedClass.getName()
        );
        paymentDAO.addPayment(payment);

        System.out.println("You have successfully joined the class: " + selectedClass.getName());
        System.out.println("Final price paid: " + finalPrice + " RON");
    }


}

