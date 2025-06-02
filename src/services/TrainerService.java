//package services;
//
//import models.*;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class TrainerService {
//
//    private List<Trainer> trainers;
//    public TrainerService(List<Trainer> trainers) {
//        this.trainers = trainers;
//    }
//
//    public Trainer findByUsernameAndPassword(String username, String password) {
//        for (Trainer trainer : trainers) {
//            if (trainer.getUsername().equals(username) && trainer.getPassword().equals(password)) {
//                return trainer;
//            }
//        }
//        return null;
//    }
//    public boolean isUsernameTaken(String username) {
//        for (Trainer trainer : trainers) {
//            if (trainer.getUsername().equals(username)) {
//                return true;
//            }
//        }
//        return false;
//    }
//    public void addTrainer(Trainer newTrainer) {
//        if (isUsernameTaken(newTrainer.getUsername())) {
//            throw new IllegalArgumentException("Username already taken! Please choose a different one.");
//        }
//        if(newTrainer.getAvailableSlots().isEmpty()) {
//            newTrainer.setAvailableSlots(generateWeeklyTimeSlotsForTrainer(newTrainer));
//        }
//        trainers.add(newTrainer);
//        System.out.println("Trainer successfully added!");
//    }
//
//    public List<Trainer> getTrainers() {
//        return trainers;
//    }
//    public void listTrainers() {
//        if (trainers.isEmpty()) {
//            System.out.println("No trainers available.");
//            System.out.println("Try again later...");
//            return;
//        }
//
//        System.out.println("\nList of trainers:");
//        for (Trainer trainer : trainers) {
//            System.out.println(trainer.toString());
//            System.out.println("------------------------------------");
//        }
//    }
//
//    public void listAllTrainersDetailed() {
//        if (trainers.isEmpty()) {
//            System.out.println("No trainers available.");
//            return;
//        }
//
//        System.out.println("\n--- Available Trainers ---");
//        for (int i = 0; i < trainers.size(); i++) {
//            Trainer t = trainers.get(i);
//            System.out.printf("%d. %s | Specialization: %s | Experience: %.1f yrs | Price: %.2f RON/hour | Rating: %.1f/5\n",
//                    i + 1, t.getName(), t.getSpecialization(), t.getYearsOfExperience(), t.getPricePerHour(), t.getReview());
//        }
//    }
//
//
//    public static List<TimeSlot> generateWeeklyTimeSlotsForTrainer(Trainer trainer) {
//        List<TimeSlot> slots = new ArrayList<>();
//
//        for (DayOfWeek day : DayOfWeek.values()) {
//            for (int hour = 6; hour < 23; hour++) {
//                LocalTime start = LocalTime.of(hour, 0);
//                LocalTime end = start.plusHours(1);
//                slots.add(new TimeSlot(start, end, day, trainer));
//            }
//        }
//
//        return slots;
//    }
//    public Booking getBookingForSlot(TimeSlot slot, Trainer trainer, LocalDate date) {
//        for (Booking booking : trainer.getBookings()) {
//            if (booking.getDate().equals(date) &&
//                    booking.getTimeSlot().equals(slot.getStartTime())) {
//                return booking;
//            }
//        }
//        return null;
//    }
//    public void showScheduleForToday(Trainer trainer) {
//        System.out.println("Schedule for Trainer: " + trainer.getName());
//        DayOfWeek today = LocalDate.now().getDayOfWeek();
//        LocalDate date = LocalDate.now();
//
//        List<TimeSlot> todaySlots = trainer.getAvailableSlots().stream()
//                .filter(slot -> slot.getDay() == today)
//                .sorted(Comparator.comparing(TimeSlot::getStartTime))
//                .collect(Collectors.toList());
//
//        for (TimeSlot slot : todaySlots) {
//            Booking booking = getBookingForSlot(slot, trainer, date);
//            if (booking == null) {
//                System.out.println(slot + " - FREE");
//            } else if (booking.getFitnessClass() != null) {
//                System.out.println(slot + " - CLASS: " + booking.getFitnessClass().getName());
//            } else {
//                System.out.println(slot + " - BOOKED BY: " + booking.getMember().getName());
//            }
//        }
//    }
//    public void showScheduleForDate(Trainer trainer, LocalDate date) {
//        DayOfWeek day = date.getDayOfWeek();
//
//        List<TimeSlot> slots = trainer.getAvailableSlots().stream()
//                .filter(slot -> slot.getDay().equals(day))
//                .sorted(Comparator.comparing(TimeSlot::getStartTime))
//                .collect(Collectors.toList());
//
//        System.out.println("--- YOUR SCHEDULE FOR " + date + " (" + day + ") ---");
//
//        for (TimeSlot slot : slots) {
//            Booking booking = getBookingForSlot(slot, trainer, date);
//
//            if (booking == null) {
//                System.out.println(slot + " - FREE");
//            } else if (booking.getFitnessClass() != null) {
//                System.out.println(slot + " - CLASS: " + booking.getFitnessClass().getName());
//            } else {
//                System.out.println(slot + " - BOOKED BY: " + booking.getMember().getName());
//            }
//        }
//    }
//
//    public void listTrainedMembersFilteredByLevel(Trainer trainer, String experienceLevel) {
//        Set<Member> trainedMembers = trainer.getTrainedMembers();
//
//        if (trainedMembers == null || trainedMembers.isEmpty()) {
//            System.out.println("You are not currently training any members.");
//            return;
//        }
//
//        List<Member> filteredMembers = trainedMembers.stream()
//                .filter(m -> experienceLevel == null || m.getExperienceLevel().equalsIgnoreCase(experienceLevel))
//                .sorted(Comparator.comparing(Member::getRegistrationDate))
//                .collect(Collectors.toList());
//
//        if (filteredMembers.isEmpty()) {
//            System.out.println("No members found for that experience level.");
//        } else {
//            System.out.println("Members you train:");
//            for (Member m : filteredMembers) {
//                System.out.println("- " + m.getName() +
//                        " | Level: " + m.getExperienceLevel() +
//                        " | Registered: " + m.getRegistrationDate());
//            }
//        }
//    }
//
//
//    private boolean overlaps(LocalTime bookedStart, LocalTime newStart, int duration) {
//        LocalTime bookedEnd = bookedStart.plusHours(1);
//        LocalTime newEnd = newStart.plusMinutes(duration);
//
//        return newStart.isBefore(bookedEnd) && bookedStart.isBefore(newEnd);
//    }
//
//
//
//    public void showFitnessClasses(Trainer trainer) {
//        Map<String, FitnessClass> classes = trainer.getCoordinatedClasses();
//
//        if (classes.isEmpty()) {
//            System.out.println("You are not coordinating any fitness classes.");
//            return;
//        }
//
//        System.out.println("\n--- Your Coordinated Fitness Classes ---");
//        for (Map.Entry<String, FitnessClass> entry : classes.entrySet()) {
//            System.out.println(entry.getValue());
//            System.out.println("-------------------------------");
//        }
//    }
//    public void addFitnessClass(Scanner scanner, Trainer trainer, FitnessClassService fitnessClassService) {
//        System.out.println("Enter class name (must be unique):");
//        String name = scanner.nextLine();
//
//        if (trainer.getCoordinatedClasses().containsKey(name)) {
//            System.out.println("You already have a class with that name.");
//            return;
//        }
//
//        System.out.println("Enter duration (in minutes):");
//        int duration = scanner.nextInt();
//        scanner.nextLine();
//
//        System.out.println("Enter difficulty (beginner/intermediate/advanced):");
//        String difficulty = scanner.nextLine();
//
//        System.out.println("Enter price:");
//        double price = scanner.nextDouble();
//        scanner.nextLine();
//
//        LocalDate today = LocalDate.now();
//        LocalDate minDate = today.plusWeeks(1);
//        LocalDate maxDate = today.plusMonths(1);
//        LocalDate date;
//
//        while (true) {
//            System.out.println("Enter the class date (YYYY-MM-DD):");
//            String input = scanner.nextLine();
//
//            try {
//                date = LocalDate.parse(input);
//
//                if (date.isBefore(minDate)) {
//                    System.out.println("❌ The class must be scheduled at least one week from today (" + minDate + ").");
//                } else if (date.isAfter(maxDate)) {
//                    System.out.println("❌ You can only schedule classes up to one month in advance (" + maxDate + ").");
//                } else {
//                    break; // data validă!
//                }
//
//            } catch (DateTimeParseException e) {
//                System.out.println("❌ Invalid date format. Please use YYYY-MM-DD.");
//            }
//        }
//
//
//        LocalTime startHour;
//        while (true) {
//            System.out.println("Enter start hour (HH:mm): ");
//            String input = scanner.nextLine();
//
//            try {
//                startHour = LocalTime.parse(input);
//
//                LocalTime gymOpen = LocalTime.of(6, 0);
//                LocalTime gymClose = LocalTime.of(23, 0);
//                LocalTime latestStart = gymClose.minusMinutes(duration);
//
//                if (startHour.isBefore(gymOpen)) {
//                    System.out.println("❌ The gym opens at 06:00. Please choose a later time.");
//                } else if (startHour.isAfter(latestStart)) {
//                    System.out.println("❌ Class is too long to start at this time. Latest possible start: " + latestStart);
//                } else {
//                    break; // valid!
//                }
//
//            } catch (DateTimeParseException e) {
//                System.out.println("❌ Invalid time format. Please use HH:mm.");
//            }
//        }
//
//
//        System.out.println("Enter max participants:");
//        int maxParticipants = scanner.nextInt();
//        scanner.nextLine();
//
//
//        LocalDate finalDate = date;
//        LocalTime finalStartHour = startHour;
//        boolean hasConflict = trainer.getBookings().stream()
//                .anyMatch(b -> b.getDate().equals(finalDate) &&
//                        overlaps(b.getTimeSlot(), finalStartHour, duration));
//
//        if (hasConflict) {
//            System.out.println("❌ Conflict detected: you already have a booking during that time.");
//            return;
//        }
//
//        // Dacă nu există conflicte, creăm clasa
//        FitnessClass fitnessClass = new FitnessClass(name, duration, difficulty,price, trainer,
//                new ArrayList<>(), date, startHour, maxParticipants);
//
//        trainer.getCoordinatedClasses().put(name, fitnessClass);
//        fitnessClassService.addFitnessClass(fitnessClass);
//
//        // Adăugăm toate booking-urile pentru intervalul de timp
//        // Sloturi reale care trebuie marcate ca ocupate (pe ore fixe)
//        LocalTime classEnd = startHour.plusMinutes(duration);
//        List<TimeSlot> timeSlotsToBlock = trainer.getAvailableSlots().stream()
//                .filter(slot -> slot.getDay().equals(finalDate.getDayOfWeek()))
//                .filter(slot -> {
//                    LocalTime slotStart = slot.getStartTime();
//                    LocalTime slotEnd = slotStart.plusHours(1);
//                    return finalStartHour.isBefore(slotEnd) && classEnd.isAfter(slotStart);
//                })
//                .collect(Collectors.toList());
//
//        for (TimeSlot slot : timeSlotsToBlock) {
//            Booking booking = new Booking(fitnessClass, null, date, slot.getStartTime());
//            trainer.getBookings().add(booking);
//        }
//
//
//        System.out.println("Class added successfully and added to your schedule!");
//
//    }
//    public void deleteFitnessClass(Scanner scanner, Trainer trainer, FitnessClassService fitnessClassService) {
//        Map<String, FitnessClass> classes = trainer.getCoordinatedClasses();
//
//        if (classes.isEmpty()) {
//            System.out.println("You don't have any classes to delete.");
//            return;
//        }
//
//        System.out.println("Your current classes:");
//        List<String> classNames = new ArrayList<>(classes.keySet());
//        for (int i = 0; i < classNames.size(); i++) {
//            System.out.println((i + 1) + ". " + classNames.get(i));
//        }
//
//        System.out.println("Enter the number of the class to delete:");
//        int index = scanner.nextInt();
//        scanner.nextLine();
//
//        if (index < 1 || index > classNames.size()) {
//            System.out.println("Invalid selection.");
//            return;
//        }
//
//        String classNameToRemove = classNames.get(index - 1);
//        FitnessClass toDelete = classes.get(classNameToRemove);
//
//        List<Member> participants = toDelete.getParticipants();
//        if (!participants.isEmpty()) {
//            for (Member m : participants) {
//                Optional<Payment> originalPaymentOpt = m.getPayments().stream()
//                        .filter(p -> p.getPurpose().equals("Fitness class: " + toDelete.getName()))
//                        .findFirst();
//
//                if (originalPaymentOpt.isPresent()) {
//                    Payment original = originalPaymentOpt.get();
//                    float amount = original.getAmount();
//
//                    String purpose;
//                    if (original.getPaymentMethod() == PaymentMethod.CASH) {
//                        purpose = "Class was canceled – Manual refund required (CASH)";
//                    } else {
//                        purpose = "Refund for cancelled fitness class: " + toDelete.getName();
//                    }
//
//                    Payment refund = new Payment(
//                            -amount,
//                            LocalDate.now(),
//                            original.getPaymentMethod(),
//                            m,
//                            purpose
//                    );
//                    m.getPayments().add(refund);
//                }
//            }
//        }
//
//        trainer.getBookings().removeIf(b -> {
//            return b.getFitnessClass() != null &&
//                    b.getFitnessClass().equals(toDelete);
//        });
//
//        classes.remove(classNameToRemove);
//        fitnessClassService.removeFitnessClass(toDelete);
//
//        System.out.println("Class \"" + classNameToRemove + "\" deleted successfully and removed from your schedule.");
//    }
//
//
//    public void bookPersonalTrainer(Scanner scanner, Member member) {
//        if (trainers.isEmpty()) {
//            System.out.println("No trainers available.");
//            return;
//        }
//
//        // Pas 0 – Alegere între trainerul tău sau altul
//        System.out.println("Do you want to:");
//        System.out.println("1. Book a session with your current personal trainer");
//        System.out.println("2. Book with a different trainer");
//
//        int option = scanner.nextInt();
//        scanner.nextLine(); // flush newline
//
//        Trainer selectedTrainer;
//
//        if (option == 1) {
//            selectedTrainer = member.getTrainer();
//            if (selectedTrainer == null) {
//                System.out.println("⚠ You don't have a personal trainer assigned.");
//                return;
//            }
//            System.out.println("Booking with your personal trainer: " + selectedTrainer.getName());
//        } else if (option == 2) {
//            listAllTrainersDetailed();
//
//            System.out.print("Enter the number of the trainer you want to book: ");
//            int trainerIndex = scanner.nextInt();
//            scanner.nextLine();
//
//            if (trainerIndex < 1 || trainerIndex > trainers.size()) {
//                System.out.println("Invalid selection.");
//                return;
//            }
//
//            Trainer tempTrainer = trainers.get(trainerIndex - 1);
//            selectedTrainer = tempTrainer; // Nu modifici `selectedTrainer` din altă parte
//        } else {
//            System.out.println("Invalid option.");
//            return;
//        }
//
//        // Data
//        LocalDate date;
//        while (true) {
//            System.out.print("Enter the date you want to book (YYYY-MM-DD): ");
//            String inputDate = scanner.nextLine();
//
//            try {
//                date = LocalDate.parse(inputDate);
//                LocalDate today = LocalDate.now();
//                LocalDate maxDate = today.plusMonths(1);
//
//                if (date.isBefore(LocalDate.now())) {
//                    System.out.println("❌ You cannot book a session in the past. Please choose a future date.");
//                }else if (date.isAfter(maxDate)) {
//                    System.out.println("❌ You can only book up to 1 month in advance.");
//                } else {
//                    break; // dată validă
//                }
//            } catch (DateTimeParseException e) {
//                System.out.println("❌ Invalid date format or nonexistent date. Please use format YYYY-MM-DD.");
//            }
//        }
//
//
//        // Program
//        showScheduleForDate(selectedTrainer, date);
//
//        LocalTime hour;
//        while (true) {
//            System.out.print("Enter desired hour (HH:mm): ");
//            String inputTime = scanner.nextLine();
//
//            try {
//                hour = LocalTime.parse(inputTime);
//
//                if (date.equals(LocalDate.now()) && hour.isBefore(LocalTime.now())) {
//                    System.out.println("❌ You can't book a session earlier than the current time.");
//                } else {
//                    break; // oră validă
//                }
//
//            } catch (DateTimeParseException e) {
//                System.out.println("❌ Invalid time format. Please use format HH:mm.");
//            }
//        }
//
//
//        LocalDate finalDate = date;
//        LocalTime finalHour = hour;
//        boolean isAvailable = selectedTrainer.getAvailableSlots().stream()
//                .anyMatch(slot ->
//                        slot.getDay().equals(finalDate.getDayOfWeek()) &&
//                                slot.getStartTime().equals(finalHour) &&
//                                slot.getTrainer().equals(selectedTrainer));
//
//        boolean alreadyBooked = selectedTrainer.getBookings().stream()
//                .anyMatch(b -> b.getDate().equals(finalDate) &&
//                        b.getTimeSlot().equals(finalHour));
//
//        boolean memberAlreadyBooked = selectedTrainer.getBookings().stream()
//                .anyMatch(b -> b.getDate().equals(finalDate) &&
//                        b.getTimeSlot().equals(finalHour) &&
//                        b.getMember().equals(member));
//
//        if (!isAvailable) {
//            System.out.println("❌ This time slot is not part of the trainer's schedule.");
//            return;
//        }
//
//        if (alreadyBooked) {
//            System.out.println("❌ That time slot is already booked by someone else.");
//            return;
//        }
//
//        if (memberAlreadyBooked) {
//            System.out.println("⚠ You already have a booking with this trainer at that time.");
//            return;
//        }
//
//        Booking booking = new Booking(selectedTrainer, member, date, hour, "Personal Training");
//
//        selectedTrainer.getBookings().add(booking);
//        selectedTrainer.getTrainedMembers().add(member);
//
//        System.out.println("Select payment method (CARD, CASH, ONLINE):");
//        PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());
//
//        Payment payment = new Payment(
//                (float) selectedTrainer.getPricePerHour(),
//                LocalDate.now(),
//                method,
//                member,
//                "Personal training with " + selectedTrainer.getName()
//        );
//
//        member.getPayments().add(payment);
//
//        System.out.println("✅ Personal training session booked successfully with " + selectedTrainer.getName() +
//                " on " + date + " at " + hour);
//    }
//
//
//
//    public void addReviewForTrainer(Scanner scanner, Member member) {
//        Set<Trainer> eligibleTrainers = new HashSet<>();
//
//        if(member.getTrainer() != null) {
//            eligibleTrainers.add(member.getTrainer());
//        }
//
//        // Căutăm în toate booking-urile tuturor trainerilor
//        for (Trainer trainer : this.trainers) {
//            for (Booking booking : trainer.getBookings()) {
//                if (booking.getMember() != null && booking.getMember().equals(member)) {
//                    LocalDateTime sessionTime = LocalDateTime.of(booking.getDate(), booking.getTimeSlot());
//                    if (sessionTime.isBefore(LocalDateTime.now())) {
//                        eligibleTrainers.add(trainer);
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (eligibleTrainers.isEmpty()) {
//            System.out.println("You haven't had any completed sessions with a trainer yet.");
//            return;
//        }
//
//        System.out.println("You can leave a review for the following trainers:");
//
//        List<Trainer> trainerList = new ArrayList<>(eligibleTrainers);
//        for (int i = 0; i < trainerList.size(); i++) {
//            System.out.println((i + 1) + ". " + trainerList.get(i).getName());
//        }
//
//        System.out.print("Select the trainer number to leave a review: ");
//        int choice = scanner.nextInt();
//        scanner.nextLine();
//
//        if (choice < 1 || choice > trainerList.size()) {
//            System.out.println("Invalid selection.");
//            return;
//        }
//
//        Trainer selectedTrainer = trainerList.get(choice - 1);
//
//        System.out.print("Please enter your rating for " + selectedTrainer.getName() + " (1-5): ");
//        int rating = scanner.nextInt();
//        scanner.nextLine();
//
//        if (rating < 1 || rating > 5) {
//            System.out.println("Rating must be between 1 and 5.");
//            return;
//        }
//
//        selectedTrainer.getReviewScores().add(rating);
//        System.out.println("✅ Review submitted successfully for " + selectedTrainer.getName() + "!");
//    }
//    public void showReviewStats(Trainer trainer) {
//        List<Integer> reviews = trainer.getReviewScores();
//
//        if (reviews.isEmpty()) {
//            System.out.println("You have not received any reviews yet.");
//            return;
//        }
//
//        System.out.println("Your Reviews:");
//        for (int i = 0; i < reviews.size(); i++) {
//            System.out.println("Review #" + (i + 1) + ": " + reviews.get(i) + " stars");
//        }
//
//        double average = reviews.stream().mapToInt(Integer::intValue).average().orElse(0);
//        System.out.printf("⭐ Average Rating: %.2f out of 5\n", average);
//    }
//
//
//}


package services;

import models.*;

import java.time.DayOfWeek;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import dao.*;

public class TrainerService {

    private final TrainerDAO trainerDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final BookingDAO bookingDAO;
    private final FitnessClassDAO fitnessClassDAO;
    private final PaymentDAO paymentDAO;
    private final ReviewDAO reviewDAO;
    private final UserDAO userDAO;
    private final MemberDAO memberDAO;
    private final ClassParticipantsDAO classParticipantsDAO;

    public TrainerService(TrainerDAO trainerDAO, TimeSlotDAO timeSlotDAO, BookingDAO bookingDAO,
                          FitnessClassDAO fitnessClassDAO, PaymentDAO paymentDAO, ReviewDAO reviewDAO,
                          UserDAO userDAO, MemberDAO memberDAO, ClassParticipantsDAO classParticipantsDAO) {
        this.trainerDAO = trainerDAO;
        this.timeSlotDAO = timeSlotDAO;
        this.bookingDAO = bookingDAO;
        this.fitnessClassDAO = fitnessClassDAO;
        this.paymentDAO = paymentDAO;
        this.reviewDAO = reviewDAO;
        this.userDAO = userDAO;
        this.memberDAO = memberDAO;
        this.classParticipantsDAO = classParticipantsDAO;
    }

    public Trainer findByUsernameAndPassword(String username, String password) {
        return trainerDAO.findByUsernameAndPassword(username, password);
    }

    public boolean isUsernameTaken(String username) {
        return userDAO.isUsernameTaken(username);
    }

    public void addTrainer(Trainer newTrainer) {
        if (userDAO.isUsernameTaken(newTrainer.getUsername())) {
            throw new IllegalArgumentException("Username already taken! Please choose a different one.");
        }

        trainerDAO.addTrainer(newTrainer);

        List<TimeSlot> generatedSlots = generateWeeklyTimeSlotsForTrainer(newTrainer);
        newTrainer.setAvailableSlots(generatedSlots);

        for (TimeSlot slot : generatedSlots) {
            timeSlotDAO.addTimeSlot(slot);
        }
        AuditService.getInstance().log("Trainer added: " + newTrainer.getUsername());
    }

    public List<Trainer> getTrainers() {
        return trainerDAO.readAll();
    }

    public void listTrainers() {
        List<Trainer> trainers = getTrainers();
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

    public List<TimeSlot> generateWeeklyTimeSlotsForTrainer(Trainer trainer) {
        List<TimeSlot> slots = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            for (int hour = 6; hour < 23; hour++) {
                LocalTime start = LocalTime.of(hour, 0);
                LocalTime end = start.plusHours(1);

                TimeSlot slot = new TimeSlot(start, end, day, trainer);
                timeSlotDAO.addTimeSlot(slot); // salvăm fiecare slot în DB
                slots.add(slot);
            }
        }

        return slots;
    }

    public void showScheduleForToday(Trainer trainer) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalDate date = LocalDate.now();

        System.out.println("Schedule for Trainer: " + trainer.getName());

        // Generează sloturile 06:00 - 23:00
        List<TimeSlot> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(23, 0);

        while (start.isBefore(end)) {
            LocalTime next = start.plusHours(1);
            TimeSlot slot = new TimeSlot(start, next, today, trainer);

            // Adaugă în baza de date dacă nu există deja
            if (!timeSlotDAO.exists(trainer.getId(), start, today)) {
                timeSlotDAO.create(slot);
            }

            slots.add(slot);
            start = next;
        }

        // Obține toate clasele și rezervările pentru azi
        List<FitnessClass> classes = fitnessClassDAO.getClassesForTrainerByDate(trainer.getId(), date);
        List<Booking> bookings = bookingDAO.getBookingsForTrainerByDate(trainer.getId(), date);

        slots.sort(Comparator.comparing(TimeSlot::getStartTime));

        for (TimeSlot slot : slots) {
            LocalTime slotStart = slot.getStartTime();
            LocalTime slotEnd = slotStart.plusHours(1);

            // Verifică dacă o clasă se suprapune cu acest slot
            Optional<FitnessClass> classInSlot = classes.stream()
                    .filter(c -> {
                        LocalTime classStart = c.getHour();
                        LocalTime classEnd = classStart.plusMinutes(c.getDuration());
                        return slotStart.isBefore(classEnd) && slotEnd.isAfter(classStart);
                    })
                    .findFirst();

            if (classInSlot.isPresent()) {
                System.out.println(slot + " - CLASS: " + classInSlot.get().getName());
                continue;
            }

            Optional<Booking> bookingInSlot = bookings.stream()
                    .filter(b -> b.getTimeSlot().equals(slotStart) && b.getFitnessClass() == null)
                    .findFirst();

            if (bookingInSlot.isPresent()) {
                Booking b = bookingInSlot.get();
                if (b.getMember() != null) {
                    System.out.println(slot + " - BOOKED BY: " + b.getMember().getName());
                } else {
                    System.out.println(slot + " - BOOKED");
                }
            } else {
                System.out.println(slot + " - FREE");
            }
        }
    }

    public void showScheduleForDate(Trainer trainer, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        System.out.println("Schedule for Trainer: " + trainer.getName() + " on " + day + " (" + date + ")");

        List<TimeSlot> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(23, 0);

        // 1. Generează sloturi orare de la 6:00 la 23:00
        while (start.isBefore(end)) {
            LocalTime next = start.plusHours(1);
            TimeSlot slot = new TimeSlot(start, next, day, trainer);

            if (!timeSlotDAO.exists(trainer.getId(), start, day)) {
                timeSlotDAO.create(slot); // dacă vrei să fie salvat și în DB
            }

            slots.add(slot);
            start = next;
        }

        // 2. Obține clasele și rezervările existente pentru acea zi
        List<FitnessClass> classes = fitnessClassDAO.getClassesForTrainerByDate(trainer.getId(), date);
        List<Booking> bookings = bookingDAO.getBookingsForTrainerByDate(trainer.getId(), date);

        // 3. Sortează și afișează
        slots.sort(Comparator.comparing(TimeSlot::getStartTime));

        for (TimeSlot slot : slots) {
            LocalTime slotTime = slot.getStartTime();

            // Dacă e clasă planificată la acel moment
            Optional<FitnessClass> classAtThisSlot = classes.stream()
                    .filter(c -> {
                        LocalTime classStart = c.getHour();
                        LocalTime classEnd = classStart.plusMinutes(c.getDuration());
                        LocalTime slotEnd = slotTime.plusHours(1);

                        return slotTime.isBefore(classEnd) && slotEnd.isAfter(classStart);
                    })

                    .findFirst();


            if (classAtThisSlot.isPresent()) {
                System.out.println(slot + " - CLASS: " + classAtThisSlot.get().getName());
                continue;
            }

            // Dacă e rezervare la acel moment

            Optional<Booking> bookingAtThisSlot = bookings.stream()
                    .filter(b -> b.getTimeSlot().equals(slotTime) && b.getFitnessClass() == null)
                    .findFirst();


            if (bookingAtThisSlot.isPresent()) {
                Booking booking = bookingAtThisSlot.get();
                if (booking.getMember() != null) {
                    System.out.println(slot + " - BOOKED BY: " + booking.getMember().getName());
                } else {
                    System.out.println(slot + " - BOOKED");
                }
            } else {
                System.out.println(slot + " - FREE");
            }
        }
    }

    public void listTrainedMembersFilteredByLevel(Trainer trainer, String experienceLevel) {
        List<Member> trainedMembers = memberDAO.getMembersByTrainerId(trainer.getId());

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



    private boolean overlaps(LocalTime bookedStart, LocalTime newStart, int duration) {
        LocalTime bookedEnd = bookedStart.plusHours(1);
        LocalTime newEnd = newStart.plusMinutes(duration);

        return newStart.isBefore(bookedEnd) && bookedStart.isBefore(newEnd);
    }

    public void showFitnessClasses(Trainer trainer) {
        List<FitnessClass> classes = fitnessClassDAO.getClassesByTrainerId(trainer.getId());

        if (classes.isEmpty()) {
            System.out.println("You are not coordinating any fitness classes.");
            return;
        }

        System.out.println("\n--- Your Coordinated Fitness Classes ---");
        for (FitnessClass fc : classes) {
            System.out.println(fc);
            System.out.println("-------------------------------");
        }
    }


    public void addFitnessClass(Scanner scanner, Trainer trainer) {
        System.out.println("Enter class name (must be unique):");
        String name = scanner.nextLine();

        List<FitnessClass> existingClasses = fitnessClassDAO.getClassesByTrainerId(trainer.getId());
        boolean duplicate = existingClasses.stream().anyMatch(fc -> fc.getName().equalsIgnoreCase(name));
        if (duplicate) {
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

        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusWeeks(1);
        LocalDate maxDate = today.plusMonths(1);
        LocalDate date;

        while (true) {
            System.out.println("Enter the class date (YYYY-MM-DD):");
            String input = scanner.nextLine();

            try {
                date = LocalDate.parse(input);

                if (date.isBefore(minDate)) {
                    System.out.println("❌ The class must be scheduled at least one week from today (" + minDate + ").");
                } else if (date.isAfter(maxDate)) {
                    System.out.println("❌ You can only schedule classes up to one month in advance (" + maxDate + ").");
                } else {
                    break; // data validă!
                }

            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use YYYY-MM-DD.");
            }
        }


        LocalTime startHour;
        while (true) {
            System.out.println("Enter start hour (HH:mm): ");
            String input = scanner.nextLine();

            try {
                startHour = LocalTime.parse(input);

                LocalTime gymOpen = LocalTime.of(6, 0);
                LocalTime gymClose = LocalTime.of(23, 0);
                LocalTime latestStart = gymClose.minusMinutes(duration);

                if (startHour.isBefore(gymOpen)) {
                    System.out.println("❌ The gym opens at 06:00. Please choose a later time.");
                } else if (startHour.isAfter(latestStart)) {
                    System.out.println("❌ Class is too long to start at this time. Latest possible start: " + latestStart);
                } else {
                    break; // valid!
                }

            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid time format. Please use HH:mm.");
            }
        }


        System.out.println("Enter max participants:");
        int maxParticipants = scanner.nextInt();
        scanner.nextLine();

        final LocalTime finalStartHour = startHour;

        // Verificare conflict DB
        List<Booking> existingBookings = bookingDAO.getBookingsForTrainerByDate(trainer.getId(), date);
        boolean conflict = existingBookings.stream()
                .anyMatch(b -> overlaps(b.getTimeSlot(), finalStartHour, duration));

        if (conflict) {
            System.out.println("❌ Conflict: you already have something booked in that time.");
            return;
        }

        // Dacă nu există conflicte, creăm clasa
        FitnessClass fitnessClass = new FitnessClass(name, duration, difficulty, price, trainer,
                new ArrayList<>(), date, startHour, maxParticipants);

        fitnessClassDAO.addFitnessClass(fitnessClass);
        // Adăugăm toate sloturile ocupate pentru această clasă în bookings
        LocalTime classEnd = startHour.plusMinutes(duration);
        LocalTime slotStart = startHour;

        while (slotStart.isBefore(classEnd)) {
            Booking booking = new Booking(fitnessClass, null, date, slotStart);
            bookingDAO.addBooking(booking);

            // Treci la următorul slot (1h)
            slotStart = slotStart.plusHours(1);
        }


        System.out.println("✅ Class created and schedule updated.");
        AuditService.getInstance().log("Trainer " + trainer.getUsername() + " added class: " + name + " on " + date + " at " + startHour);
    }


    public void deleteFitnessClass(Scanner scanner, Trainer trainer) {
        List<FitnessClass> classes = fitnessClassDAO.getClassesByTrainerId(trainer.getId());

        if (classes.isEmpty()) {
            System.out.println("You don't have any classes to delete.");
            return;
        }


        System.out.println("Your current classes:");
        for (int i = 0; i < classes.size(); i++) {
            System.out.println((i + 1) + ". " + classes.get(i).getName());
        }

        System.out.println("Enter the number of the class to delete:");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > classes.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        FitnessClass toDelete = classes.get(index - 1);

        List<Member> participants = classParticipantsDAO.getParticipantsForClass(toDelete.getId());

        if (!participants.isEmpty()) {
            for (Member m : participants) {
                List<Payment> payments = paymentDAO.getPaymentsForMember(m.getId());

                Optional<Payment> originalPaymentOpt = payments.stream()
                        .filter(p -> p.getPurpose().equals("Fitness class: " + toDelete.getName()))
                        .findFirst();

                if (originalPaymentOpt.isPresent()) {
                    Payment original = originalPaymentOpt.get();
                    float amount = original.getAmount();

                    String purpose = (original.getPaymentMethod() == PaymentMethod.CASH)
                            ? "Class was canceled – Manual refund required (CASH)"
                            : "Refund for cancelled fitness class: " + toDelete.getName();

                    Payment refund = new Payment(
                            -amount,
                            LocalDate.now(),
                            original.getPaymentMethod(),
                            m,
                            purpose
                    );

                    paymentDAO.addPayment(refund);
                }
            }
        }

        //  Ștergem toate booking-urile asociate clasei
        bookingDAO.deleteBookingsForClass(toDelete);

        // Ștergem participanții din tabela de legătură
        classParticipantsDAO.removeParticipantsForClass(toDelete.getId());

        //  Ștergem clasa propriu-zisă
        boolean deleted = fitnessClassDAO.delete(toDelete.getId());
        if (!deleted) {
            System.out.println("❌ Something went wrong while deleting the fitness class.");
        }

        System.out.println("✅ Class \"" + toDelete.getName() + "\" deleted successfully.");
        AuditService.getInstance().log("Trainer " + trainer.getUsername() + " deleted class: " + toDelete.getName());
    }


    public void bookPersonalTrainer(Scanner scanner, Member member) {
        List<Trainer> allTrainers = trainerDAO.readAll();
        if (allTrainers.isEmpty()) {
            System.out.println("No trainers available.");
            return;
        }

        // Pas 0 – Alegere între trainerul tău sau altul
        System.out.println("Do you want to:");
        System.out.println("1. Book a session with your current personal trainer");
        System.out.println("2. Book with a different trainer");

        int option = scanner.nextInt();
        scanner.nextLine(); // flush newline

        Trainer selectedTrainer;

        if (option == 1) {
            selectedTrainer = member.getTrainer();
            if (selectedTrainer == null) {
                System.out.println("⚠ You don't have a personal trainer assigned.");
                return;
            }
            System.out.println("Booking with your personal trainer: " + selectedTrainer.getName());
        } else if (option == 2) {
            for (int i = 0; i < allTrainers.size(); i++) {
                Trainer t = allTrainers.get(i);
                System.out.printf("%d. %s | %s | %.2f RON/h\n", i + 1, t.getName(), t.getSpecialization(), t.getPricePerHour());
            }

            System.out.print("Choose trainer number: ");
            int trainerIndex = scanner.nextInt();
            scanner.nextLine();

            if (trainerIndex < 1 || trainerIndex > allTrainers.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            selectedTrainer = allTrainers.get(trainerIndex - 1);
        } else {
            System.out.println("Invalid option.");
            return;
        }

        // Data
        LocalDate date;
        while (true) {
            System.out.print("Enter the date you want to book (YYYY-MM-DD): ");
            String inputDate = scanner.nextLine();

            try {
                date = LocalDate.parse(inputDate);
                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusMonths(1);

                if (date.isBefore(LocalDate.now())) {
                    System.out.println("❌ You cannot book a session in the past. Please choose a future date.");
                }else if (date.isAfter(maxDate)) {
                    System.out.println("❌ You can only book up to 1 month in advance.");
                } else {
                    break; // dată validă
                }
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format or nonexistent date. Please use format YYYY-MM-DD.");
            }
        }

        // Program
        showScheduleForDate(selectedTrainer, date);

        LocalTime hour;
        while (true) {
            System.out.print("Enter desired hour (HH:mm): ");
            String inputTime = scanner.nextLine();

            try {
                hour = LocalTime.parse(inputTime);

                if (date.equals(LocalDate.now()) && hour.isBefore(LocalTime.now())) {
                    System.out.println("❌ You can't book a session earlier than the current time.");
                } else {
                    break; // oră validă
                }

            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid time format. Please use format HH:mm.");
            }
        }


        final LocalTime finalHour = hour;
        DayOfWeek day = date.getDayOfWeek();
        List<TimeSlot> availableSlots = timeSlotDAO.getTimeSlotsForTrainerByDay(selectedTrainer.getId(), day);

        boolean slotExists = availableSlots.stream()
                .anyMatch(slot -> slot.getStartTime().equals(finalHour));

        boolean slotBooked = bookingDAO.isSlotBooked(selectedTrainer.getId(), hour, date);
        boolean alreadyBookedByMember = bookingDAO.getBookingsForTrainerByDate(selectedTrainer.getId(), date).stream()
                .anyMatch(b -> b.getTimeSlot().equals(finalHour) && b.getMember() != null && b.getMember().getId() == member.getId());

        if (!slotExists) {
            System.out.println("❌ That slot is not part of the trainer's schedule.");
            return;
        }

        if (slotBooked) {
            System.out.println("❌ Slot is already booked.");
            return;
        }

        if (alreadyBookedByMember) {
            System.out.println("⚠ You already have a session at that time.");
            return;
        }

        // Save booking
        Booking booking = new Booking(selectedTrainer, member, date, hour, "Personal Training");
        bookingDAO.addBooking(booking);

        // Save payment
        System.out.print("Choose payment method (CARD, CASH, ONLINE): ");
        PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());

        Payment payment = new Payment(
                (float) selectedTrainer.getPricePerHour(),
                LocalDate.now(),
                method,
                member,
                "Personal training with " + selectedTrainer.getName()
        );

        paymentDAO.addPayment(payment);

        System.out.println("✅ Session booked with " + selectedTrainer.getName() + " on " + date + " at " + hour);
        AuditService.getInstance().log("Member " + member.getUsername() + " booked session with trainer " + selectedTrainer.getUsername() +
                " on " + date + " at " + hour);

    }


    public void addReviewForTrainer(Scanner scanner, Member member) {
        List<Trainer> allTrainers = trainerDAO.readAll();
        Set<Trainer> eligibleTrainers = new HashSet<>();

        // 1. Adăugăm antrenorul personal dacă are
        if (member.getTrainer() != null) {
            eligibleTrainers.add(member.getTrainer());
        }

        // 2. Găsim alți traineri cu care a avut sesiuni (bookings în trecut)
        for (Trainer trainer : allTrainers) {
            List<Booking> pastBookings = bookingDAO.getBookingsForTrainerByMember(trainer.getId(), member.getId());

            for (Booking booking : pastBookings) {
                if (booking.getDate().isBefore(LocalDate.now())) {
                    eligibleTrainers.add(trainer);
                    break;
                }
            }
        }

        if (eligibleTrainers.isEmpty()) {
            System.out.println("❌ You haven't had any completed sessions with a trainer yet.");
            return;
        }

        List<Trainer> trainerList = new ArrayList<>(eligibleTrainers);
        System.out.println("You can leave a review for the following trainers:");

        for (int i = 0; i < trainerList.size(); i++) {
            System.out.println((i + 1) + ". " + trainerList.get(i).getName());
        }

        System.out.print("Select the trainer number to leave a review: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > trainerList.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Trainer selectedTrainer = trainerList.get(choice - 1);

        System.out.print("Please enter your rating for " + selectedTrainer.getName() + " (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine();

        if (rating < 1 || rating > 5) {
            System.out.println("❌ Rating must be between 1 and 5.");
            return;
        }

        reviewDAO.addReview(member.getId(), selectedTrainer.getId(), rating, LocalDate.now());

        System.out.println("✅ Review submitted successfully for " + selectedTrainer.getName() + "!");
        AuditService.getInstance().log("Member " + member.getUsername() + " rated trainer " + selectedTrainer.getUsername() + " with " + rating + " stars");
    }

    public void showReviewStats(Trainer trainer) {
        List<Integer> reviews = reviewDAO.readRatingsByTrainerId(trainer.getId());

        if (reviews.isEmpty()) {
            System.out.println("You have not received any reviews yet.");
            return;
        }

        System.out.println("Your Reviews:");
        for (int i = 0; i < reviews.size(); i++) {
            System.out.println("Review #" + (i + 1) + ": " + reviews.get(i) + " stars");
        }

        double average = reviews.stream().mapToInt(Integer::intValue).average().orElse(0);
        System.out.printf("⭐ Average Rating: %.2f out of 5\n", average);
    }

    public void updateTrainerPrice(Trainer trainer) {
        trainerDAO.updatePrice(trainer.getId(), trainer.getPricePerHour());
    }

    public boolean deleteTrainerAccount(Trainer trainer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you sure you want to delete your account?");
        System.out.println("This action is permanent and cannot be undone.");
        System.out.println("Type 'yes' to confirm or 'no' to cancel.");
        String confirmation = scanner.nextLine();

        if (!confirmation.trim().equalsIgnoreCase("yes")) {
            System.out.println("Account deletion has been canceled.");
            return false;
        }

        int userId = trainer.getId();
        userDAO.deleteUserById(userId);

        System.out.println("✅ Your account and all related data have been deleted successfully.");
        AuditService.getInstance().log("Trainer deleted account: " + trainer.getUsername());
        return true;
    }

}