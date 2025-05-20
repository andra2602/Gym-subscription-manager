package database;

import dao.*;
import models.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class DatabaseSeeder {

    public static void seed() {
        TrainerDAO trainerDAO = new TrainerDAO();
        MemberDAO memberDAO = new MemberDAO();
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
        PromotionDAO promotionDAO = new PromotionDAO();

        // === Traineri ===
        Trainer t1 = new Trainer("Maria Fit Trainer", "mariaFit", "maria@fit.com", "0711111111", "Parola123!",
                "zumba", 4, 100, new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Trainer t2 = new Trainer("Andrei Strong", "astrong", "andrei@power.com", "0722222222", "Parola123!",
                "cardio", 6, 120, new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        Trainer t3 = new Trainer("Test Trainer", "testtrainer", "ttest@example.com", "0721345678", "Parola123!",
                "fitness", 3, 75, new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        trainerDAO.create(t1);
        trainerDAO.create(t2);
        trainerDAO.create(t3);

        // === Membri ===
        Member m1 = new Member("Ana Avansata", "Ana123456", "ana@mail.com", "0722000001", "Parola123!",
                LocalDate.of(2023, 2, 15), 60f, 1.68f, "advanced", t1, null, false);

        Member m2 = new Member("Paul Incepator", "Paul123B", "paul@mail.com", "0722000002", "Parola123!",
                LocalDate.of(2024, 1, 10), 75f, 1.80f, "beginner", t2, null, true);

        Member m3 = new Member("Test Member", "testmember", "test@example.com", "0723123456", "Parola123!",
                LocalDate.now(), 70.5f, 175f, "beginner", null, null, true);

        // Abonament activ pt Ana
        LocalDate startDateAna = LocalDate.now().minusDays(27);
        Subscription anaSub = new Subscription("monthly", startDateAna, 100f, true, null);
        m1.setSubscription(anaSub);

        memberDAO.create(m1);
        memberDAO.create(m2);
        memberDAO.create(m3);

        subscriptionDAO.create(anaSub, m1.getId());

        // === Promoții ===
        LocalDate today = LocalDate.now();

        Promotion activeNow = new Promotion(
                "Spring Fit Blast",
                "Get 20% off on all fitness classes this week!",
                20,
                today.minusDays(2),
                today.plusDays(5),
                true
        );

        Promotion comingSoon = new Promotion(
                "Summer Starter Pack",
                "Prepare for summer with 15% off subscriptions!",
                15,
                today.plusDays(3),
                today.plusDays(10)
        );

        Promotion expired = new Promotion(
                "New Year Resolution",
                "Start strong: 25% off if you joined in January!",
                25,
                today.minusMonths(2),
                today.minusMonths(1),
                true
        );

        promotionDAO.create(activeNow);
        promotionDAO.create(comingSoon);
        promotionDAO.create(expired);

        System.out.println("Baza de date a fost populată cu date de test.");
    }
}
