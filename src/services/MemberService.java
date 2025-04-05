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


    public MemberService(List<Member> membri) {
        this.members = membri;
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

        if (confirmation.equalsIgnoreCase("yes")) {
            // Ștergerea abonamentului
            if (member.getSubscription() != null) {
                member.setSubscription(null); // Ștergem abonamentul
                System.out.println("Your subscription has been deleted.");
            }

            // Ștergerea plăților - presupunând că există o listă de plăți asociate membrului
            if (member.getPayments() != null) {
                member.setPayments(new ArrayList<>()); // Golește lista de plăți
                System.out.println("Your payments have been deleted.");
            }

            /***
            // Ștergerea rezervărilor la clase de fitness - presupunând că există o listă de rezervări la clase
            if (member.getFitnessClassReservations() != null) {
                member.setFitnessClassReservations(new ArrayList<>()); // Golește lista de rezervări la clase de fitness
                System.out.println("Your fitness class reservations have been deleted.");
            }

            // Ștergerea rezervărilor la antrenori - presupunând că există o listă de rezervări la antrenori
            if (member.getTrainerReservations() != null) {
                member.setTrainerReservations(new ArrayList<>()); // Golește lista de rezervări la antrenori
                System.out.println("Your trainer reservations have been deleted.");
            }
            ***/

            // Ștergerea membrului din lista de membri
            if (members.contains(member)) {
                members.remove(member); // Ștergem membrul din lista de membri
                System.out.println("Your account has been deleted successfully.");
            } else {
                System.out.println("Member not found.");
            }
        } else {
            System.out.println("Account deletion has been canceled.");
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
    public void addNewSubscription(Member member) {
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

        System.out.println("Do you have a promotion code? (yes/no)");
        String hasPromotion = scanner.nextLine();
        Promotion promotion = null;

        if (hasPromotion.equalsIgnoreCase("yes")) {
            System.out.print("Enter your promotion code: ");
            String promoCode = scanner.nextLine();
            // Exemplu de verificare promoție (poți să implementezi mai multe logici pentru promoții)
            if (promoCode.equals("DISCOUNT10")) {
                promotion = new Promotion("DISCOUNT10", "10% discount at any subscription",10.0f, LocalDate.now(), LocalDate.now().plusMonths(1));
            } else {
                System.out.println("Invalid promotion code.");
            }
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
                System.out.println("Enter new subscription type (monthly, 6 months, yearly): ");
                String newType = scanner.nextLine();
                member.getSubscription().setType(newType);
                System.out.println("Subscription type updated to: " + newType);

                System.out.println("Select payment method (CARD, CASH, ONLINE):");
                PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());

                Payment payment1 = new Payment(
                        member.getSubscription().getPrice(),
                        LocalDate.now(),
                        method,
                        member,
                        "Changed subscription type to: " + newType
                );
                member.addPayment(payment1);
                break;
            case 2:
                System.out.println("Enter the number of months to extend: ");
                int months = scanner.nextInt();
                Subscription currentSubscription = member.getSubscription();


                float monthlyPrice = currentSubscription.getPrice() / ChronoUnit.MONTHS.between(currentSubscription.getStartDate(), currentSubscription.getEndDate());
                float additionalCost = months * monthlyPrice;

                if (currentSubscription.getPromotion() != null && currentSubscription.getPromotion().isValidNow()) {
                    float discount = currentSubscription.getPromotion().getDiscountPercent();
                    additionalCost -= additionalCost * (discount / 100);
                    System.out.println("A promo code has been applied: " + discount + "% off.");
                }

                float newTotalPrice = currentSubscription.getPrice() + additionalCost;

                currentSubscription.setStartDate(currentSubscription.getStartDate().plusMonths(months));
                currentSubscription.setPrice(newTotalPrice);

                System.out.println("Subscription extended for " + months + " months.");


                System.out.println("Select payment method (CARD, CASH, ONLINE):");
                PaymentMethod method1 = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());

                Payment payment2 = new Payment(
                        additionalCost,
                        LocalDate.now(),
                        method1,
                        member,
                        "Extended subscription by " + months + " months"
                );
                member.addPayment(payment2);
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