import java.time.LocalDate;
import java.util.*;
import dao.*;
import database.DatabaseSeeder;
import models.*;
import services.*;
import validation.Validator;


public class StartUp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        // Start prompt
        showWelcomePrompt(scanner);
/**
        // Initialize lists
        List<Member> membri = new ArrayList<>();
        List<Trainer> trainers = new ArrayList<>();
        List<FitnessClass> fitnessClasses = new ArrayList<>();
        List<Promotion> promotions = new ArrayList<>();

        // Create services
        MemberService memberService = new MemberService(membri,trainers);
        TrainerService trainerService = new TrainerService(trainers);
        ManagerService managerService = new ManagerService();
        FitnessClassService fitnessClassService = new FitnessClassService(fitnessClasses);
        PromotionService promotionService = new PromotionService(promotions);
**/
        // DAO-uri
        MemberDAO memberDAO = new MemberDAO();
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        TrainerDAO trainerDAO = new TrainerDAO();
        FitnessClassDAO fitnessClassDAO = new FitnessClassDAO();
        PromotionDAO promotionDAO = new PromotionDAO();
        ReviewDAO reviewDAO = new ReviewDAO(); // dacă folosești și review-uri

// Listă inițială încărcată din DB (pentru afișare locală etc.)
        List<Trainer> trainers = trainerDAO.readAll();
        List<Promotion> promotions = promotionDAO.readAll();
        List<FitnessClass> fitnessClasses = fitnessClassDAO.readAllAvailable(); // dacă ai nevoie

// Service-uri conectate la DAO-uri
        MemberService memberService = new MemberService(memberDAO, subscriptionDAO, paymentDAO, trainers);
        TrainerService trainerService = new TrainerService(trainers, trainerDAO, reviewDAO);
        ManagerService managerService = new ManagerService();
        FitnessClassService fitnessClassService = new FitnessClassService(fitnessClasses, fitnessClassDAO);
        PromotionService promotionService = new PromotionService(promotions, promotionDAO);



        DatabaseSeeder.seed();
/*** asa aveam inainte
        // Load test users (members + trainers)
        loadTestUsers(memberService, trainerService);
        // Load test promotions
        loadTestPromotions(promotionService);
 ***/
        // Check promotion expiration
        promotionService.checkPromotionValidityOnStartup();


        // Main menu loop
        while (!exit) {
            System.out.println("\n Menu: ");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();

            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number (1–3).");
                continue;
            }

            switch (option) {
                case 1:
                    login(scanner, memberService, trainerService, managerService, fitnessClassService, promotionService);
                    break;
                case 2:
                    register(scanner, memberService, trainerService, managerService, fitnessClassService, promotionService);
                    break;
                case 3:
                    exit = true;
                    System.out.print("\nExiting program");
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(500);
                            System.out.print(".");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showWelcomePrompt(Scanner scanner) {
        System.out.println("====================================================");
        System.out.println("          Welcome to the Fitness System!           ");
        System.out.println("  _______________________________________________  ");
        System.out.println(" |                                               | ");
        System.out.println(" |    Manage memberships, bookings, payments,    | ");
        System.out.println(" |              trainers, and more!              | ");
        System.out.println(" |                                               | ");
        System.out.println(" |               Let's get started!              | ");
        System.out.println(" |_______________________________________________| ");
        System.out.println("====================================================");

        // Add a "loading" effect
        System.out.print("\nLoading application");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n\nPress Enter to continue...");
        scanner.nextLine();

        System.out.println("\nStarting the system...");
    }

    private static void loadTestUsers(MemberService memberService, TrainerService trainerService) {
        // === Traineri de test ===
        Trainer t1 = new Trainer("Maria Fit Trainer", "mariaFit", "maria@fit.com", "0711111111", "Parola123!",
                "zumba", 4, 100, new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Trainer t2 = new Trainer("Andrei Strong", "astrong", "andrei@power.com", "0722222222", "Parola123!",
                "cardio", 6, 120, new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Trainer t3 = new Trainer("Test Trainer", "testtrainer", "ttest@example.com", "0721345678", "Parola123!",
                "fitness", 3, 75, new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        trainerService.addTrainer(t1);
        trainerService.addTrainer(t2);
        trainerService.addTrainer(t3);

        // === Membri de test ===
        Member m1 = new Member("Ana Avansata", "Ana123456", "ana@mail.com", "0722000001", "Parola123!",
                LocalDate.of(2023, 2, 15), 60f, 1.68f, "advanced", t1, null, false);

        Member m2 = new Member("Paul Incepator", "Paul123B", "paul@mail.com", "0722000002", "Parola123!",
                LocalDate.of(2024, 1, 10), 75f, 1.80f, "beginner", t2, null, true);

        Member m3 = new Member("Test Member", "testmember", "test@example.com", "0723123456", "Parola123!",
                LocalDate.now(), 70.5f, 175f, "beginner", null, null, true);


        // Subscription care expiră în 3 zile pentru Ana
        LocalDate startDateAna = LocalDate.now().minusDays(27); // începe acum 27 de zile
        Subscription anaSubscription = new Subscription(
                "monthly",
                startDateAna,
                100f,
                true,
                null // fără promoție
        );
        m1.setSubscription(anaSubscription);


        memberService.addMember(m1);
        memberService.addMember(m2);
        memberService.addMember(m3);

        // ii atribuim trainerilor
        t1.getTrainedMembers().add(m1);
        t2.getTrainedMembers().add(m2);
    }

    private static void loadTestPromotions(PromotionService promotionService) {
        LocalDate today = LocalDate.now();

        // 1. Active now
        Promotion activeNow = new Promotion(
                "Spring Fit Blast",
                "Get 20% off on all fitness classes this week!",
                20,
                today.minusDays(2),
                today.plusDays(5),
                true
        );

        // 2. Coming soon
        Promotion comingSoon = new Promotion(
                "Summer Starter Pack",
                "Prepare for summer with 15% off subscriptions!",
                15,
                today.plusDays(3),
                today.plusDays(10)
        );

        // 3. Expired
        Promotion expired = new Promotion(
                "New Year Resolution",
                "Start strong: 25% off if you joined in January!",
                25,
                today.minusMonths(2),
                today.minusMonths(1),
                true
        );

        promotionService.getPromotions().add(activeNow);
        promotionService.getPromotions().add(comingSoon);
        promotionService.getPromotions().add(expired);
    }



    private static void login(Scanner scanner,MemberService memberService , TrainerService trainerService, ManagerService managerService, FitnessClassService fitnessClassService, PromotionService promotionService) {
        System.out.println("\n LOGIN: ");

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();


        Member member = memberService.findByUsernameAndPassword(username, password);
        if (member != null) {
            System.out.println("Welcome back member: " + member.getName());
            memberMenu(scanner, memberService, member, fitnessClassService, trainerService, promotionService);
            return;
        }

        Trainer trainer = trainerService.findByUsernameAndPassword(username, password);
        if (trainer != null) {
            System.out.println("Welcome back trainer: " + trainer.getName());
            trainerMenu(scanner, trainer, trainerService, fitnessClassService);
            return;
        }

        Manager manager = managerService.findByUsernameAndPassword(username, password);
        if (manager != null) {
            System.out.println("Welcome back manager: " + manager.getName());
            managerMenu(scanner, memberService, trainerService, promotionService);
            return;
        }

        System.out.println("Login failed! Please try again.");
    }

    // Meniu pentru Membru
    private static void memberMenu(Scanner scanner, MemberService memberService, Member member, FitnessClassService fitnessClassService,
                                   TrainerService trainerService, PromotionService promotionService) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n MENU");
            System.out.println("1. See subscription details");
            System.out.println("2. Creating a new subscription");
            System.out.println("3. Editing a subscription");
            System.out.println("4. Deleting a subscription");
            System.out.println("5. List of fitness classes");
            System.out.println("6. Schedule a fitness class");
            System.out.println("7. List of trainers");
            System.out.println("8. Personal trainer menu");
            System.out.println("9. Add review for trainer");
            System.out.println("10. View and manage payments");
            System.out.println("11. View active promotions");
            System.out.println("12. Delete my account");
            System.out.println("13. LOG OUT");

            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    memberService.viewSubscriptionDetails(member);
                    break;
                case 2:
                    memberService.addNewSubscription(member,promotionService);
                    break;
                case 3:
                    memberService.editSubscription(member);
                    break;
                case 4:
                    memberService.deleteSubscription(member);
                    break;
                case 5:
                    fitnessClassService.listFitnessClasses();
                    break;
                case 6:
                    fitnessClassService.scheduleFitnessClass(scanner, member, promotionService);
                    break;
                case 7:
                    trainerService.listTrainers();
                    break;
                case 8:
                    personalTrainerMenu(scanner, member, trainerService, memberService);
                    break;
                case 9:
                    trainerService.addReviewForTrainer(scanner, member);
                    break;
                case 10:
                    boolean back = false;
                    while (!back) {
                        System.out.println("\n--- Payment History ---");
                        System.out.println("1. View subscription payments");
                        System.out.println("2. View class/trainer payments");
                        System.out.println("3. View total spent");
                        System.out.println("4. Back");

                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                            case 1 -> memberService.viewSubscriptionPayments(member);
                            case 2 -> memberService.viewClassAndTrainerPayments(member);
                            case 3 -> memberService.viewTotalPayments(member);
                            case 4 -> back = true;
                            default -> System.out.println("Invalid option.");
                        }
                    }
                    break;
                case 11:
                    promotionService.listActivePromotions();
                    break;
                case 12:
                    memberService.deleteMemberAccount(member);
                    if (!memberService.isMemberExists(member)) {
                        exit = true;
                    }
                    break;
                case 13:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

    }


    private static void personalTrainerMenu(Scanner scanner, Member member, TrainerService trainerService, MemberService memberService) {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Personal Trainer Options ---");
            System.out.println("1. Assign personal trainer");
            System.out.println("2. View current personal trainer");
            System.out.println("3. Remove personal trainer");
            System.out.println("4. Book a personal trainer");
            System.out.println("5. Back to main menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    memberService.assignPersonalTrainer(scanner, member, trainerService.getTrainers());
                    break;
                case 2:
                    memberService.viewCurrentPersonalTrainer(member);
                    break;
                case 3:
                    memberService.removePersonalTrainer(scanner, member);
                    break;
                case 4:
                    trainerService.bookPersonalTrainer(scanner, member);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }


    // Meniu pentru Antrenor
    private static void trainerMenu(Scanner scanner,Trainer trainer, TrainerService trainerService, FitnessClassService fitnessClassService) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n MENU:");
            System.out.println("1. Members you train");
            System.out.println("2. Fitness classes you coordinate");
            System.out.println("3. Your schedule");
            System.out.println("4. Your rating");
            System.out.println("5. LOG OUT");

            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    boolean back = false;
                    while (!back) {
                        System.out.println("\nChoose what members to show:");
                        System.out.println("1. Beginner");
                        System.out.println("2. Intermediate");
                        System.out.println("3. Advanced");
                        System.out.println("4. All");
                        System.out.println("5. Back to main menu");
                        System.out.println();
                        System.out.print("Enter your choice: ");
                        int filterChoice = scanner.nextInt();
                        scanner.nextLine();

                        String filterLevel = switch (filterChoice) {
                            case 1 -> "beginner";
                            case 2 -> "intermediate";
                            case 3 -> "advanced";
                            case 4 -> null;
                            case 5 -> {
                                back = true;
                                yield null;
                            }
                            default -> {
                                System.out.println("Invalid option.");
                                yield null;
                            }
                        };

                        if (!back) {
                            trainerService.listTrainedMembersFilteredByLevel(trainer, filterLevel);
                        }
                    }
                    break;
                case 2:
                    boolean backToMain = false;
                    while (!backToMain) {
                        System.out.println("1. Show all my classes");
                        System.out.println("2. Add a new class");
                        System.out.println("3. Delete a class");
                        System.out.println("4. Back to main menu");

                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                            case 1:
                                trainerService.showFitnessClasses(trainer);
                                break;
                            case 2:
                                trainerService.addFitnessClass(scanner, trainer, fitnessClassService);
                                break;
                            case 3:
                                trainerService.deleteFitnessClass(scanner, trainer, fitnessClassService);
                                break;
                            case 4:
                                backToMain = true;
                                break;
                            default:
                                System.out.println("Invalid choice.");
                        }
                    }
                    break;
                case 3:
                    System.out.println("Select the date you want to see your schedule:");
                    System.out.println("1. Today");
                    System.out.println("2. Another date");

                    int optiune = scanner.nextInt();
                    scanner.nextLine(); // flush newline

                    if (optiune == 1) {
                        trainerService.showScheduleForToday(trainer);
                    } else if (optiune == 2) {
                        System.out.println("Please enter the date (YYYY-MM-DD): ");
                        String dataInput = scanner.nextLine();
                        LocalDate dataAleasa = LocalDate.parse(dataInput);

                        trainerService.showScheduleForDate(trainer, dataAleasa);
                    }
                    break;
                case 4:
                    trainerService.showReviewStats(trainer);
                    break;
                case 5:
                    // Exit the trainer menu
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Meniu pentru Manager
    private static void managerMenu(Scanner scanner, MemberService memberService, TrainerService trainerService, PromotionService promotionService) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n MENU");
            System.out.println("1. List all members");
            System.out.println("2. List all trainers");
            System.out.println("3. Manage promotions");
            System.out.println("4. Calculate revenue");
            System.out.println("5. Audit important actions in a CSV file");
            System.out.println("6. LOG OUT");

            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    memberService.listMembers();
                    break;
                case 2:
                    trainerService.listTrainers();
                    break;
                case 3:
                    boolean back = false;
                    while (!back) {
                        System.out.println("\n--- Manage Promotions ---");
                        System.out.println("1. View all promotions");
                        System.out.println("2. Add promotion");
                        System.out.println("3. Edit promotion");
                        System.out.println("4. Remove promotion");
                        System.out.println("5. Deactivate promotion");
                        System.out.println("6. Reactivate promotion");
                        System.out.println("7. Back");

                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                            case 1 -> promotionService.listAllPromotions();
                            case 2 -> promotionService.addPromotion(scanner);
                            case 3 -> promotionService.editPromotion(scanner);
                            case 4 -> promotionService.removePromotion(scanner);
                            case 5 -> promotionService.deactivatePromotion(scanner);
                            case 6 -> promotionService.reactivatePromotion(scanner);
                            case 7 -> back = true;
                            default -> System.out.println("Invalid option.");
                        }
                    }
                    break;
                case 4:
                    // Calculate revenue generated in a specific period

                    break;
                case 5:
                    // Audit important actions (e.g., additions, payments, deletions) in a CSV file

                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void register(Scanner scanner, MemberService memberService, TrainerService trainerService, ManagerService managerService, FitnessClassService fitnessClassService, PromotionService promotionService) {
        System.out.println("\nREGISTER: ");

        // Step 1 - Role selection
        System.out.println("Please select your role:");
        System.out.println("1. Member");
        System.out.println("2. Trainer");
        //System.out.print("Enter your choice: ");
        int roleChoice;
        while (true) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            try {
                roleChoice = Integer.parseInt(input);
                if (roleChoice == 1 || roleChoice == 2) {
                    break;
                } else {
                    System.out.println("❌ Please enter 1 for Member or 2 for Trainer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number (1 or 2).");
            }
        }

        String name = "", username = "", email = "", phoneNumber = "", password = "";

        // Step 2 - User input and validation for duplicate usernames
        boolean validUsername = false;
        while (!validUsername) {
            System.out.println("Enter your username: ");
            username = scanner.nextLine();
            try {
                Validator.validateUsername(username);
                if (memberService.isUsernameTaken(username) || trainerService.isUsernameTaken(username)) {
                    System.out.println("Username is already taken. Please try again with another one.");
                } else {
                    validUsername = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        boolean validName = false;
        while (!validName) {
            System.out.println("Enter your name: ");
            name = scanner.nextLine();
            try {
                Validator.validateName(name);
                validName = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        boolean validEmail = false;
        while (!validEmail) {
            System.out.println("Enter your email: ");
            email = scanner.nextLine();
            try {
                Validator.validateEmail(email);
                validEmail = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        boolean validPhone = false;
        while (!validPhone) {
            System.out.println("Enter your phone number: ");
            phoneNumber = scanner.nextLine();
            try {
                Validator.validatePhone(phoneNumber);
                validPhone = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        boolean validPassword = false;
        while (!validPassword) {
            System.out.println("Enter your password: ");
            password = scanner.nextLine();
            try {
                Validator.validatePassword(password);
                validPassword = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        if (roleChoice == 1) { /// Member
            float weight = 0, height = 0;
            String experienceLevel = "beginner";
            boolean isStudent = false;
            Trainer trainer = null;
            Subscription subscription = null;

            boolean validWeight = false;
            while (!validWeight) {
                System.out.println("Enter your weight (kg): ");
                weight = scanner.nextFloat();
                try {
                    Validator.validateWeight(weight);
                    validWeight = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            boolean validHeight = false;
            while (!validHeight) {
                System.out.println("Enter your height (cm): ");
                height = scanner.nextFloat();
                try {
                    Validator.validateHeight(height);
                    validHeight = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            scanner.nextLine(); // consume the newline character

            boolean validExperienceLevel = false;
            while (!validExperienceLevel) {
                System.out.println("Enter your experience level (beginner, intermediate, advanced): ");
                experienceLevel = scanner.nextLine();
                try {
                    Validator.validateExperienceLevel(experienceLevel);
                    validExperienceLevel = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            boolean validIsStudent = false;
            while (!validIsStudent) {
                System.out.println("Are you a student? (true/false): ");
                isStudent = scanner.nextBoolean();
                if (isStudent != true && isStudent != false) {
                    System.out.println("Please enter 'true' or 'false' for student status.");
                } else {
                    validIsStudent = true;
                }
            }

            // Create a new member and add it to members list
            Member newMember = new Member(name, username, email, phoneNumber, password,
                    LocalDate.now(), weight, height, experienceLevel,
                    trainer, subscription, isStudent);
            memberService.addMember(newMember); // Adding the member
            System.out.println("You have been successfully registered as a Member!");

        } else if (roleChoice == 2) { /// Trainer
            System.out.println("Enter your specialization: ");
            String specialization = scanner.nextLine();

            boolean validYearsOfExperience = false;
            double yearsOfExperience = 0;
            while (!validYearsOfExperience) {
                System.out.println("Enter your years of experience: ");
                yearsOfExperience = scanner.nextDouble();
                try {
                    Validator.validateYearsOfExperience(yearsOfExperience);
                    validYearsOfExperience = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            boolean validPricePerHour = false;
            double pricePerHour = 0;
            while (!validPricePerHour) {
                System.out.println("Enter your price per hour: ");
                pricePerHour = scanner.nextDouble();
                try {
                    Validator.validatePricePerHour(pricePerHour);
                    validPricePerHour = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            // Create a new trainer and add it to trainers list
            Trainer newTrainer = new Trainer(name, username, email, phoneNumber, password,
                    specialization, yearsOfExperience, pricePerHour,
                    new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            trainerService.addTrainer(newTrainer); // Adding the trainer
            System.out.println("You have been successfully registered as a Trainer!");

        } else {
            System.out.println("Invalid selection. Please try again.");
            return;
        }

        // Redirect to login
        System.out.println("\nRedirecting to login...");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        scanner.nextLine();
        login(scanner, memberService, trainerService, managerService, fitnessClassService, promotionService);
    }

}
