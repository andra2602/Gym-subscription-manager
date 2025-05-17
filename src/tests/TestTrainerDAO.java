package tests;

import dao.TrainerDAO;
import models.Trainer;

import java.util.List;

public class TestTrainerDAO {
    public static void main(String[] args) {
        TrainerDAO trainerDAO = new TrainerDAO();

        // 1. Test creare trainer
        Trainer newTrainer = new Trainer(
                "Ion Popescu",
                "ionpopescu",
                "ion.popescu@example.com",
                "0722333444",
                "Parola123!",
                "Fitness",
                5,
                100,
                null, null, null, null
        );

        trainerDAO.create(newTrainer);
        System.out.println("Trainer creat cu ID-ul: " + newTrainer.getId());

        // 2. Test citire toti trainerii
        List<Trainer> trainers = trainerDAO.readAll();
        System.out.println("Lista antrenori:");
        for (Trainer t : trainers) {
            System.out.printf("ID: %d, Nume: %s, Specializare: %s\n", t.getId(), t.getName(), t.getSpecialization());
        }

        // 3. Test citire trainer dupa ID
        Trainer trainerById = trainerDAO.readById(newTrainer.getId());
        if (trainerById != null) {
            System.out.println("Trainer gasit dupa ID: " + trainerById.getName());
        } else {
            System.out.println("Trainerul nu a fost gasit.");
        }
    }
}
