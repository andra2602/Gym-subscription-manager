package services;

import models.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberService {

    private List<Member> members;
    List<FitnessClass> fitnessClasses = new ArrayList<>();
    private List<Trainer> trainers;



    public MemberService(List<Member> membri,List<Trainer> trainers) {
        this.members = membri;
        this.trainers = trainers;
    }

    public Member findByUsernameAndPassword(String username, String password) {
        for (Member membru : members) {
            if (membru.getUsername().equals(username) && membru.getPassword().equals(password)) {
                return membru;
            }
        }
        return null;
    }
    public boolean isUsernameTaken(String username) {
        for (Member member : members) {
            if (member.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void addMember(Member newMember) {
        if (isUsernameTaken(newMember.getUsername())) {
            throw new IllegalArgumentException("Username already taken! Please choose a different one.");
        }
        members.add(newMember);
    }
    public void removeMember(Member member) {
        if (members.contains(member)) {
            members.remove(member);
            System.out.println("Member removed from the list.");
        } else {
            System.out.println("Member not found.");
        }
    }


    public boolean isMemberExists(Member member) {
        return members.contains(member);
    }

    public void deleteMemberAccount(Member member) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you sure you want to delete your account?");
        System.out.println("This action is permanent and cannot be undone.");
        System.out.println("Type 'yes' to confirm or 'no' to cancel.");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Account deletion has been canceled.");
            return;
        }

        // 1. Subscription
        if (member.getSubscription() != null) {
            member.setSubscription(null);
            System.out.println("✔ Your subscription has been deleted.");
        }

        // 2. Payments
        if (member.getPayments() != null) {
            member.setPayments(new ArrayList<>());
            System.out.println("✔ Your payments have been deleted.");
        }

        // 3. Bookings (trainer + fitness class)
        for (Trainer trainer : trainers) {
            // eliminăm toate bookingurile membrului din lista trainerului
            List<Booking> toRemove = new ArrayList<>();

            for (Booking b : trainer.getBookings()) {
                if (member.equals(b.getMember())) {
                    toRemove.add(b);

                    // Dacă era pentru o clasă – scădem participanții
                    if (b.getFitnessClass() != null) {
                        FitnessClass fc = b.getFitnessClass();
                        fc.getParticipants().remove(member);
                        System.out.println("✔ Removed from class: " + fc.getName());
                    }

                    // Dacă era personal training – slotul devine din nou liber
                    if (b.getTrainer() != null && b.getFitnessClass() == null) {
                        TimeSlot freedSlot = new TimeSlot(
                                b.getTimeSlot(),
                                b.getTimeSlot().plusHours(1),
                                b.getDate().getDayOfWeek(),
                                b.getTrainer()
                        );
                        b.getTrainer().getAvailableSlots().add(freedSlot);
                        System.out.println("✔ Freed slot at " + b.getTimeSlot() + " for " + b.getTrainer().getName());
                    }
                }
            }

            trainer.getBookings().removeAll(toRemove);

            // 4. Scoatem membrul de la trainerul lui (dacă e cazul)
            trainer.getTrainedMembers().remove(member);
        }

        // 5. Scoatem din lista de membri
        if (members.contains(member)) {
            members.remove(member);
            System.out.println("✅ Your account has been deleted successfully.");
        } else {
            System.out.println("❌ Member not found.");
        }
    }

    public void listMembers() {
        if (members.isEmpty()) {
            System.out.println("No members available.");
            System.out.println("Try again later...");
            return;
        }

        System.out.println("\nList of members:");
        for (Member member : members) {
            System.out.println(member.toString());
            System.out.println("------------------------------------");
        }
    }

    public void assignPersonalTrainer(Scanner scanner, Member member, List<Trainer> trainers) {
        if (member.getTrainer() != null) {
            System.out.println("⚠ You already have a personal trainer assigned: " + member.getTrainer().getName());
            System.out.println("❗ Please remove your current trainer before assigning a new one.");
            return;
        }

        if (trainers.isEmpty()) {
            System.out.println("No trainers available to assign.");
            return;
        }

        // Afișăm trainerii
        for (int i = 0; i < trainers.size(); i++) {
            Trainer t = trainers.get(i);
            System.out.println((i + 1) + ". " + t.getName() + " - " + t.getSpecialization());
        }

        System.out.print("Enter the number of the trainer you want to assign: ");
        int trainerIndex = scanner.nextInt();
        scanner.nextLine();

        if (trainerIndex < 1 || trainerIndex > trainers.size()) {
            System.out.println("Invalid trainer selected.");
            return;
        }

        Trainer selectedTrainer = trainers.get(trainerIndex - 1);

        System.out.print("Are you sure you want to assign " + selectedTrainer.getName() + " as your personal trainer? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            member.setTrainer(selectedTrainer);
            selectedTrainer.getTrainedMembers().add(member);
            System.out.println("✅ " + selectedTrainer.getName() + " is now your personal trainer!");
        } else {
            System.out.println("❌ Assignment cancelled.");
        }
    }
    public void viewCurrentPersonalTrainer(Member member){
        Trainer trainer = member.getTrainer();

        if (trainer == null) {
            System.out.println("⚠ You currently do not have a personal trainer assigned.");
        } else {
            System.out.println("👤 Your current trainer is: " + trainer.getName());
            System.out.println("   Specialization: " + trainer.getSpecialization());
            System.out.println("   Experience: " + trainer.getYearsOfExperience() + " years");
            System.out.println("   Price/hour: " + trainer.getPricePerHour() + " RON");
            List<Integer> reviews = trainer.getReviewScores();
            if (reviews.isEmpty()) {
                System.out.println("   ⭐ Rating: No reviews yet");
            } else {
                double average = reviews.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0);
                System.out.printf("   ⭐ Average Rating: %.2f / 5 (%d reviews)\n", average, reviews.size());
            }
        }
    }
    public void removePersonalTrainer(Scanner scanner, Member member){
        Trainer trainer = member.getTrainer();

        if (trainer == null) {
            System.out.println("⚠ You have no personal trainer to remove.");
            return;
        }

        System.out.print("Are you sure you want to remove " + trainer.getName() + " as your personal trainer? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            member.setTrainer(null);
            trainer.getTrainedMembers().remove(member);
            System.out.println("✅ Your trainer has been removed.");
        } else {
            System.out.println("❌ Removal cancelled.");
        }
    }

    ///  Subscription
    public void viewSubscriptionDetails(Member member) {
        if (member.getSubscription() == null) {
            System.out.println("You do not have an active subscription.");
            return;
        }

        Subscription subscription = member.getSubscription();
        System.out.println("Subscription Details:");
        System.out.println("Type: " + subscription.getType());
        System.out.println("Start Date: " + subscription.getStartDate());
        System.out.println("End Date: " + subscription.getEndDate());
        System.out.println("Status: " + (subscription.isActive() ? "Active" : "Inactive"));
        System.out.println("Final Price: " + subscription.getFinalPriceForMember(member));
        if (subscription.getPromotion() != null) {
            System.out.println("Promotion: " + subscription.getPromotion().getName() + " - " + subscription.getPromotion().getDiscountPercent() + "% off");
        }
    }
    public void addNewSubscription(Member member, PromotionService promotionService) {
        if (member.getSubscription() != null && member.getSubscription().isCurrentlyActive()) {
            System.out.println("You already have an active subscription.");
            return; // Dacă membrul are deja un abonament activ, nu îi se va crea unul nou
        }

        if (member.getSubscription() != null && !member.getSubscription().isCurrentlyActive()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("You currently have an inactive subscription.");
            System.out.println("Would you like to edit or delete your current subscription before creating a new one?");
            System.out.println("1. Edit current subscription");
            System.out.println("2. Delete current subscription and create a new one");
            System.out.println("3. Cancel and return to the menu");
            System.out.print("Enter your choice (1/2/3): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                editSubscription(member);
                return;
            } else if (choice == 2) {
                deleteSubscription(member);
            } else {
                System.out.println("Returning to the menu...");
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose your subscription type:");
        System.out.println("1. Monthly");
        System.out.println("2. 6 months");
        System.out.println("3. Annual");
        System.out.print("Enter your choice (1/2/3): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String type = "";
        float price = 0;

        switch (choice) {
            case 1:
                type = "monthly";
                price = 50.0f;
                break;
            case 2:
                type = "6 months";
                price = 270.0f;
                break;
            case 3:
                type = "annual";
                price = 480.0f;
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        List<Promotion> promotions = promotionService.getPromotions();
        Promotion promotion = null;
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
                System.out.println("Promotion applied: " + promotion.getName());
            } else if (promoChoice != 0) {
                System.out.println("❌ Invalid choice. No promotion applied.");
            }
        } else {
            System.out.println("No active promotions available at the moment.");
        }



        System.out.println("Select payment method (CARD, CASH, ONLINE):");
        PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());


        Subscription newSubscription = new Subscription(type, LocalDate.now(), price, true, promotion);

        member.setSubscription(newSubscription);

        Payment payment = new Payment(
                newSubscription.getFinalPriceForMember(member),
                LocalDate.now(),
                method,
                member,
                "New Subscription - " + newSubscription.getType()
        );
        member.addPayment(payment);

        System.out.println("Your new subscription has been created successfully!");
        System.out.println("Subscription details:");
        System.out.println("Type: " + newSubscription.getType());
        System.out.println("Start date: " + newSubscription.getStartDate());
        System.out.println("End date: " + newSubscription.getEndDate());
        System.out.println("Final price: " + newSubscription.getFinalPriceForMember(member));
    }
    public void editSubscription(Member member) {
        if (member.getSubscription() == null) {
            System.out.println("You don't have a subscription to edit.");
            return;
        }
        if (!member.getSubscription().isCurrentlyActive()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Your subscription is currently inactive.");
            System.out.println("Would you like to reactivate it?");
            System.out.println("Press 1 to reactivate or 2 to go back to the menu:");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                member.getSubscription().setActive(true);
                System.out.println("Your subscription has been reactivated.");
            } else {
                System.out.println("Returning to menu...");
                return; }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Edit your subscription:");
        System.out.println("1. Change subscription type");
        System.out.println("2. Extend subscription");
        System.out.println("3. Change subscription status (active/inactive)");
        System.out.print("Enter your choice (1/2/3): ");
        int choice = scanner.nextInt();
        scanner.nextLine();


        switch (choice) {
            case 1:
                System.out.println("Enter new subscription type (monthly, 6 months, annual): ");
                String newType = scanner.nextLine();

                Subscription currentSub = member.getSubscription();
                String oldType = currentSub.getType().toLowerCase();

                float newPrice;
                switch (newType.toLowerCase()) {
                    case "monthly" -> newPrice = 50f;
                    case "6 months" -> newPrice = 270f;
                    case "annual" -> newPrice = 480f;
                    default -> {
                        System.out.println("❌ Invalid type. No changes made.");
                        return;
                    }
                }

                float oldPrice = currentSub.getPrice();
                float difference = newPrice - oldPrice;

                if (currentSub.getPromotion() != null && currentSub.getPromotion().isValidNow()) {
                    float discount = currentSub.getPromotion().getDiscountPercent();
                    newPrice -= newPrice * (discount / 100);
                    difference = newPrice - oldPrice;
                    System.out.println("A promotion has been applied: " + discount + "% off.");
                }

                currentSub.setType(newType);
                currentSub.setStartDate(LocalDate.now());
                currentSub.setPrice(newPrice);

                System.out.println("✔ Subscription type changed to: " + newType);
                System.out.println("New total price: " + newPrice);

                String purpose = "Changed subscription type to: " + newType;
                float amountToCharge = Math.abs(difference);

                PaymentMethod method;
                if (difference != 0) {
                    method = null;
                    while (method == null) {
                        System.out.print("Select payment method (CARD, CASH, ONLINE): ");
                        String input = scanner.nextLine().trim().toUpperCase();
                        try {
                            method = PaymentMethod.valueOf(input);
                        } catch (IllegalArgumentException e) {
                            System.out.println("❌ Invalid input. Please enter CARD, CASH or ONLINE.");
                        }
                    }

                    if (difference > 0) {
                        System.out.println("You need to pay an extra: " + amountToCharge + " RON.");
                        member.addPayment(new Payment(
                                amountToCharge,
                                LocalDate.now(),
                                method,
                                member,
                                purpose + " (Upgrade)"
                        ));
                    } else {
                        System.out.println("Refund recorded: " + amountToCharge + " RON.");
                        member.addPayment(new Payment(
                                -amountToCharge, // refund = valoare negativă
                                LocalDate.now(),
                                method,
                                member,
                                purpose + " (Downgrade Refund)"
                        ));
                    }
                } else {
                    System.out.println("No price change required.");
                }
                break;

            case 2:
                LocalDate today = LocalDate.now();
                LocalDate endDate = member.getSubscription().getEndDate();
                long daysUntilEnd = ChronoUnit.DAYS.between(today, endDate);

                if (daysUntilEnd > 5) {
                    System.out.println("⏳ You can only extend your subscription if it is within 5 days of expiring.");
                    return;
                }


                int months;
                while (true) {
                    try {
                        System.out.print("Enter the number of months to extend: ");
                        months = Integer.parseInt(scanner.nextLine());
                        if (months <= 0) {
                            System.out.println("❌ Number of months must be greater than 0.");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid number. Please enter a valid number of months.");
                    }
                }

                Subscription currentSubscription = member.getSubscription();

                long currentDuration = ChronoUnit.MONTHS.between(
                        currentSubscription.getStartDate(),
                        currentSubscription.getEndDate()
                );

                float monthlyPrice = currentSubscription.getPrice() / currentDuration;
                float additionalCost = months * monthlyPrice;

                if (currentSubscription.getPromotion() != null && currentSubscription.getPromotion().isValidNow()) {
                    float discount = currentSubscription.getPromotion().getDiscountPercent();
                    additionalCost -= additionalCost * (discount / 100);
                    System.out.println("🎁 Promo code applied: " + discount + "% off.");
                }

                float newTotalPrice = currentSubscription.getPrice() + additionalCost;

                // Mutăm startDate mai devreme cu durata inițială + extinderea
                currentSubscription.addMonths(months);
                currentSubscription.setStartDate(currentSubscription.getStartDate()); // nu schimbăm, dar poți loga dacă vrei
                currentSubscription.setPrice(newTotalPrice);

                System.out.println("✅ Subscription extended by " + months + " months.");
                System.out.println("New total price: " + newTotalPrice);

                // Metoda de plată
                method = null;
                while (method == null) {
                    System.out.print("Select payment method (CARD, CASH, ONLINE): ");
                    String input = scanner.nextLine().trim().toUpperCase();
                    try {
                        method = PaymentMethod.valueOf(input);
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ Invalid input. Please enter CARD, CASH or ONLINE.");
                    }
                }

                Payment payment = new Payment(
                        additionalCost,
                        LocalDate.now(),
                        method,
                        member,
                        "Extended subscription by " + months + " months"
                );
                member.addPayment(payment);
                System.out.println("✔ Payment of " + additionalCost + " processed via " + method + ".");
                break;
            case 3:
                System.out.println("Your current subscription status is: " + (member.getSubscription().isActive() ? "Active" : "Inactive"));
                System.out.println("Do you want to toggle the subscription status? (yes/no): ");
                String toggle = scanner.nextLine();
                if (toggle.equalsIgnoreCase("yes")) {
                    // Trecerea de la activ la inactiv sau invers
                    boolean newStatus = !member.getSubscription().isActive(); // Dacă este activ, îl facem inactiv și invers
                    member.getSubscription().setActive(newStatus);
                    System.out.println("Your subscription is now " + (newStatus ? "active" : "inactive"));
                } else {
                    System.out.println("No changes made to your subscription status.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }
    public void deleteSubscription(Member member) {
        if (member.getSubscription() == null) {
            System.out.println("You don't have an active subscription to delete.");
            return;
        }

        System.out.println("Are you sure you want to delete your subscription? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            member.setSubscription(null);
            System.out.println("Your subscription has been successfully deleted.");
        } else {
            System.out.println("Your subscription was not deleted.");
        }
    }


    /// Payments
    public void viewSubscriptionPayments(Member member) {
        List<Payment> payments = member.getPayments();
        boolean found = false;

        System.out.println("\n📄 Subscription Payments:");
        for (Payment p : payments) {
            if (p.getPurpose().toLowerCase().contains("subscription")) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) {
            System.out.println("❌ No subscription payments found.");
        }
    }
    public void viewClassAndTrainerPayments(Member member) {
        List<Payment> payments = member.getPayments();
        boolean found = false;

        System.out.println("\n🏋️ Class & Trainer Payments:");
        for (Payment p : payments) {
            String purpose = p.getPurpose().toLowerCase();
            if (!purpose.contains("subscription")) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) {
            System.out.println("❌ No class or trainer payments found.");
        }
    }
    public void viewTotalPayments(Member member) {
        float totalSubscription = 0;
        float totalOthers = 0;

        for (Payment p : member.getPayments()) {
            if (p.getPurpose().toLowerCase().contains("subscription")) {
                totalSubscription += p.getAmount();
            } else {
                totalOthers += p.getAmount();
            }
        }

        System.out.printf("\n📄 Subscription total: %.2f RON\n", totalSubscription);
        System.out.printf("🏋️ Classes/Trainers total: %.2f RON\n", totalOthers);
        System.out.printf("💰 Overall total: %.2f RON\n", (totalSubscription + totalOthers));

    }


}