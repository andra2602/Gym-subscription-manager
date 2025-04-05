package services;
import models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FitnessClassService {

    private List<FitnessClass> fitnessClasses;

    public FitnessClassService(List<FitnessClass> fitnessClasses) {
        this.fitnessClasses = fitnessClasses;
    }
    public void listFitnessClasses() {
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
    public void addFitnessClass(FitnessClass fitnessClass) {
        this.fitnessClasses.add(fitnessClass);
    }
    public void removeFitnessClass(FitnessClass fitnessClass) {
        fitnessClasses.remove(fitnessClass);
    }


    public void scheduleFitnessClass(Scanner scanner, Member member) {
        List<FitnessClass> availableClasses = fitnessClasses.stream()
                .filter(fc -> fc.getParticipants().size() < fc.getMaxParticipants())
                .collect(Collectors.toList());

        if (availableClasses.isEmpty()) {
            System.out.println("There are no available classes or classes with open spots right now.");
            return;
        }

        System.out.println("\n--- Available Fitness Classes ---");
        for (int i = 0; i < availableClasses.size(); i++) {
            FitnessClass fc = availableClasses.get(i);
            System.out.printf("%d. %s (%s) on %s at %s - Trainer: %s\n", i + 1,
                    fc.getName(), fc.getDifficulty(), fc.getDate(), fc.getHour(), fc.getTrainer().getName());
            System.out.printf("   Participants: %d/%d\n", fc.getParticipants().size(), fc.getMaxParticipants());
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

        if (selectedClass.getParticipants().contains(member)) {
            System.out.println("⚠ You are already enrolled in this class.");
            return;
        }

        boolean added = selectedClass.addParticipant(member);
        if (added) {
            // Optional: Creăm un Booking și îl adăugăm în lista trainerului
            Booking booking = new Booking(selectedClass, member, selectedClass.getDate(), selectedClass.getHour());
            selectedClass.getTrainer().getBookings().add(booking);

            // Adăugăm plata!
            System.out.println("Select payment method (CARD, CASH, ONLINE):");
            PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());

            Payment payment = new Payment(
                    (float) selectedClass.getPrice(),
                    LocalDate.now(),
                    method,
                    member,
                    "Fitness class: " + selectedClass.getName()
            );

            member.getPayments().add(payment);

            System.out.println("✅ You have successfully joined the class: " + selectedClass.getName());
        } else {
            System.out.println("❌ Sorry, this class is now full.");
        }
    }

}
