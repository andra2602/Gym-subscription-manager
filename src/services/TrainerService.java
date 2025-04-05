package services;

import models.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TrainerService {

    private List<Trainer> trainers;
    public TrainerService(List<Trainer> trainers) {
        this.trainers = trainers;
    }

    public Trainer findByUsernameAndPassword(String username, String password) {
        for (Trainer trainer : trainers) {
            if (trainer.getUsername().equals(username) && trainer.getPassword().equals(password)) {
                return trainer;
            }
        }
        return null;
    }
    public boolean isUsernameTaken(String username) {
        for (Trainer trainer : trainers) {
            if (trainer.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    public void addTrainer(Trainer newTrainer) {
        if (isUsernameTaken(newTrainer.getUsername())) {
            throw new IllegalArgumentException("Username already taken! Please choose a different one.");
        }
        if(newTrainer.getAvailableSlots().isEmpty()) {
            newTrainer.setAvailableSlots(generateWeeklyTimeSlotsForTrainer(newTrainer));
        }
        trainers.add(newTrainer);
        System.out.println("Trainer successfully added!");
    }

    public void listTrainers() {
        if (trainers.isEmpty()) {
            System.out.println("No trainers available.");
            System.out.println("Try again later...");
            return;
        }

        System.out.println("\nList of trainers:");
        for (Trainer trainer : trainers) {
            System.out.println(trainer.toString());
            System.out.println("------------------------------------");
        }
    }

    public void listAllTrainersDetailed() {
        if (trainers.isEmpty()) {
            System.out.println("No trainers available.");
            return;
        }

        System.out.println("\n--- Available Trainers ---");
        for (int i = 0; i < trainers.size(); i++) {
            Trainer t = trainers.get(i);
            System.out.printf("%d. %s | Specialization: %s | Experience: %.1f yrs | Price: %.2f RON/hour | Rating: %.1f/5\n",
                    i + 1, t.getName(), t.getSpecialization(), t.getYearsOfExperience(), t.getPricePerHour(), t.getReview());
        }
    }


    public static List<TimeSlot> generateWeeklyTimeSlotsForTrainer(Trainer trainer) {
        List<TimeSlot> slots = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            for (int hour = 6; hour < 23; hour++) {
                LocalTime start = LocalTime.of(hour, 0);
                LocalTime end = start.plusHours(1);
                slots.add(new TimeSlot(start, end, day, trainer));
            }
        }

        return slots;
    }
    public Booking getBookingForSlot(TimeSlot slot, Trainer trainer, LocalDate date) {
        for (Booking booking : trainer.getBookings()) {
            if (booking.getDate().equals(date) &&
                    booking.getTimeSlot().equals(slot.getStartTime())) {
                return booking;
            }
        }
        return null;
    }
    public void showScheduleForToday(Trainer trainer) {
        System.out.println("Schedule for Trainer: " + trainer.getName());
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalDate date = LocalDate.now();

        List<TimeSlot> todaySlots = trainer.getAvailableSlots().stream()
                .filter(slot -> slot.getDay() == today)
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .collect(Collectors.toList());

        for (TimeSlot slot : todaySlots) {
            Booking booking = getBookingForSlot(slot, trainer, date);
            if (booking == null) {
                System.out.println(slot + " - FREE");
            } else if (booking.getFitnessClass() != null) {
                System.out.println(slot + " - CLASS: " + booking.getFitnessClass().getName());
            } else {
                System.out.println(slot + " - BOOKED BY: " + booking.getMember().getName());
            }
        }
    }
    public void showScheduleForDate(Trainer trainer, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        List<TimeSlot> slots = trainer.getAvailableSlots().stream()
                .filter(slot -> slot.getDay().equals(day))
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .collect(Collectors.toList());

        System.out.println("--- YOUR SCHEDULE FOR " + date + " (" + day + ") ---");

        for (TimeSlot slot : slots) {
            Booking booking = getBookingForSlot(slot, trainer, date);

            if (booking == null) {
                System.out.println(slot + " - FREE");
            } else if (booking.getFitnessClass() != null) {
                System.out.println(slot + " - CLASS: " + booking.getFitnessClass().getName());
            } else {
                System.out.println(slot + " - BOOKED BY: " + booking.getMember().getName());
            }
        }
    }

    public void listTrainedMembersFilteredByLevel(Trainer trainer, String experienceLevel) {
        Set<Member> trainedMembers = trainer.getTrainedMembers();

        if (trainedMembers == null || trainedMembers.isEmpty()) {
            System.out.println("You are not currently training any members.");
            return;
        }

        List<Member> filteredMembers = trainedMembers.stream()
                .filter(m -> experienceLevel == null || m.getExperienceLevel().equalsIgnoreCase(experienceLevel))
                .sorted(Comparator.comparing(Member::getRegistrationDate))
                .collect(Collectors.toList());

        if (filteredMembers.isEmpty()) {
            System.out.println("No members found for that experience level.");
        } else {
            System.out.println("Members you train:");
            for (Member m : filteredMembers) {
                System.out.println("- " + m.getName() +
                        " | Level: " + m.getExperienceLevel() +
                        " | Registered: " + m.getRegistrationDate());
            }
        }
    }


    private boolean overlaps(LocalTime bookedTime, LocalTime newTime, int duration) {
        LocalTime bookedEnd = bookedTime.plusHours(1);
        LocalTime newEnd = newTime.plusMinutes(duration);

        return !(newEnd.isBefore(bookedTime) || newTime.isAfter(bookedEnd));
    }


    public void showFitnessClasses(Trainer trainer) {
        Map<String, FitnessClass> classes = trainer.getCoordinatedClasses();

        if (classes.isEmpty()) {
            System.out.println("You are not coordinating any fitness classes.");
            return;
        }

        System.out.println("\n--- Your Coordinated Fitness Classes ---");
        for (Map.Entry<String, FitnessClass> entry : classes.entrySet()) {
            System.out.println("Class name: " + entry.getKey());
            System.out.println(entry.getValue());
            System.out.println("-------------------------------");
        }
    }
    public void addFitnessClass(Scanner scanner, Trainer trainer, FitnessClassService fitnessClassService) {
        System.out.println("Enter class name (must be unique):");
        String name = scanner.nextLine();

        if (trainer.getCoordinatedClasses().containsKey(name)) {
            System.out.println("You already have a class with that name.");
            return;
        }

        System.out.println("Enter duration (in minutes):");
        int duration = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter difficulty (beginner/intermediate/advanced):");
        String difficulty = scanner.nextLine();

        System.out.println("Enter price:");
        double price = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Enter date (YYYY-MM-DD):");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        System.out.println("Enter start hour (HH:mm):");
        LocalTime startHour = LocalTime.parse(scanner.nextLine());

        System.out.println("Enter max participants:");
        int maxParticipants = scanner.nextInt();
        scanner.nextLine();


        boolean hasConflict = trainer.getBookings().stream()
                .anyMatch(b -> b.getDate().equals(date) &&
                        overlaps(b.getTimeSlot(), startHour, duration));

        if (hasConflict) {
            System.out.println("❌ Conflict detected: you already have a booking during that time.");
            return;
        }

        // Dacă nu există conflicte, creăm clasa
        FitnessClass fitnessClass = new FitnessClass(name, duration, difficulty,price, trainer,
                new ArrayList<>(), date, startHour, maxParticipants);

        trainer.getCoordinatedClasses().put(name, fitnessClass);
        fitnessClassService.addFitnessClass(fitnessClass);

        // Adăugăm toate booking-urile pentru intervalul de timp
        int slotsToBlock = (int) Math.ceil(duration / 60.0);
        LocalTime slotTime = startHour;

        for (int i = 0; i < slotsToBlock; i++) {
            Booking booking = new Booking(fitnessClass, null, date, slotTime);
            trainer.getBookings().add(booking);
            slotTime = slotTime.plusHours(1);
        }

        System.out.println("Class added successfully and added to your schedule!");

    }
    public void deleteFitnessClass(Scanner scanner, Trainer trainer, FitnessClassService fitnessClassService) {
        Map<String, FitnessClass> classes = trainer.getCoordinatedClasses();

        if (classes.isEmpty()) {
            System.out.println("You don't have any classes to delete.");
            return;
        }

        System.out.println("Your current classes:");
        List<String> classNames = new ArrayList<>(classes.keySet());
        for (int i = 0; i < classNames.size(); i++) {
            System.out.println((i + 1) + ". " + classNames.get(i));
        }

        System.out.println("Enter the number of the class to delete:");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > classNames.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String classNameToRemove = classNames.get(index - 1);
        FitnessClass toDelete = classes.get(classNameToRemove);

        trainer.getBookings().removeIf(b -> {
            return b.getFitnessClass() != null &&
                    b.getFitnessClass().equals(toDelete);
        });

        classes.remove(classNameToRemove);
        fitnessClassService.removeFitnessClass(toDelete);

        System.out.println("Class \"" + classNameToRemove + "\" deleted successfully and removed from your schedule.");
    }


    public void bookPersonalTrainer(Scanner scanner, Member member) {
        if (trainers.isEmpty()) {
            System.out.println("No trainers available.");
            return;
        }

        // 1. List all trainers
        listAllTrainersDetailed();

        System.out.print("Enter the number of the trainer you want to book: ");
        int trainerIndex = scanner.nextInt();
        scanner.nextLine();

        if (trainerIndex < 1 || trainerIndex > trainers.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Trainer selectedTrainer = trainers.get(trainerIndex - 1);

        // 2. Pick a date
        System.out.print("Enter the date you want to book (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        // 3. Show trainer's schedule for that day
        showScheduleForDate(selectedTrainer, date);

        System.out.print("Enter desired hour (HH:mm): ");
        LocalTime hour = LocalTime.parse(scanner.nextLine());

        // 4. Verify if slot is available and not already booked
        boolean isAvailable = selectedTrainer.getAvailableSlots().stream()
                .anyMatch(slot ->
                        slot.getDay().equals(date.getDayOfWeek()) &&
                                slot.getStartTime().equals(hour) &&
                                slot.getTrainer().equals(selectedTrainer));

        boolean alreadyBooked = selectedTrainer.getBookings().stream()
                .anyMatch(b -> b.getDate().equals(date) &&
                        b.getTimeSlot().equals(hour));

        boolean memberAlreadyBooked = selectedTrainer.getBookings().stream()
                .anyMatch(b -> b.getDate().equals(date) &&
                        b.getTimeSlot().equals(hour) &&
                        b.getMember().equals(member));

        if (!isAvailable) {
            System.out.println("❌ This time slot is not part of the trainer's schedule.");
            return;
        }

        if (alreadyBooked) {
            System.out.println("❌ That time slot is already booked by someone else.");
            return;
        }

        if (memberAlreadyBooked) {
            System.out.println("⚠ You already have a booking with this trainer at that time.");
            return;
        }

        // 5. Create booking
        Booking booking = new Booking(selectedTrainer, member, date, hour, "Personal Training");

        selectedTrainer.getBookings().add(booking);
        selectedTrainer.getTrainedMembers().add(member); // link member to trainer


        System.out.println("Select payment method (CARD, CASH, ONLINE):");
        PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());

        Payment payment = new Payment(
                (float) selectedTrainer.getPricePerHour(),
                LocalDate.now(),
                method,
                member,
                "Personal training with " + selectedTrainer.getName()
        );

        member.getPayments().add(payment);

        System.out.println("✅ Personal training session booked successfully with " + selectedTrainer.getName() +
                " on " + date + " at " + hour);
    }


}