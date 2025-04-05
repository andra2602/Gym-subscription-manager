package services;

import models.Manager;

import java.util.ArrayList;
import java.util.List;

public class ManagerService {

    private List<Manager> managers;

    public ManagerService() {
        this.managers = new ArrayList<>();
        // Manageri predefiniți
        Manager manager1 = new Manager("Andra Andruta", "andra_mihaela2602", "andra.andruta60@gmail.com", "0712345678", "Password123*");
        Manager manager2 = new Manager("Alexandru Firica", "alexx.firica", "alexfirica5@gmail.com", "0723456789", "Password456!");

        managers.add(manager1);
        managers.add(manager2);
    }

    public Manager findByUsernameAndPassword(String username, String password) {
        for (Manager manager : managers) {
            if (manager.getUsername().equals(username) && manager.getPassword().equals(password)) {
                return manager;
            }
        }
        return null;
    }
}