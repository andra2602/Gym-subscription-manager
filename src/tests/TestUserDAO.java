package tests;

import dao.UserDAO;
import models.User;

import java.util.List;

public class TestUserDAO {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        // 1. Creăm un user
        User u = new User("Andra Popescu", "andra123", "andra@email.com", "0722123456", "Parola@123");

        // 2. Îl inserăm în DB
        userDAO.create(u);
        System.out.println("✅ User creat cu ID: " + u.getId());

        // 3. Citim userul creat prin ID
        User readById = userDAO.readById(u.getId());
        System.out.println("📖 Citit prin ID: " + readById);

        // 4. Ștergem userul
        boolean deleted = userDAO.delete(u.getId());
        System.out.println("🗑️ User șters? " + deleted);

        // 5. Încercăm să-l citim din nou
        User readAfterDelete = userDAO.readById(u.getId());
        System.out.println("🔍 Citit după ștergere: " + readAfterDelete);

        // 6. Afișăm toți userii existenți (ar trebui să nu-l mai includă)
        List<User> users = userDAO.readAll();
        System.out.println("📋 Toți userii din DB:");
        for (User user : users) {
            System.out.println(user);
        }

        // 7. Adăugăm un alt user după ștergere
        User newUser = new User("Ion Popescu", "ionpop", "ion@email.com", "0733111222", "IonParola@123");
        userDAO.create(newUser);
        System.out.println("✅ User NOU creat cu ID: " + newUser.getId());

        // 8. Afișăm iar toți userii (acum trebuie să-l includă pe Ion Pop)
        List<User> finalUsers = userDAO.readAll();
        System.out.println("📋 Userii finali din DB:");
        for (User user : finalUsers) {
            System.out.println(user);
        }
    }
}
