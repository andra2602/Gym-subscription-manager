package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void main(String[] args) {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "username TEXT NOT NULL UNIQUE," +
                "email TEXT NOT NULL," +
                "phone TEXT NOT NULL," +
                "password TEXT NOT NULL" +
                ");";

        String sqlTrainers = "CREATE TABLE IF NOT EXISTS trainers (" +
                "user_id INTEGER PRIMARY KEY," +
                "specialization TEXT NOT NULL," +
                "years_of_experience REAL NOT NULL," +
                "price_per_hour REAL NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ");";

        String sqlMembers = "CREATE TABLE IF NOT EXISTS members (" +
                "user_id INTEGER PRIMARY KEY," +
                "registration_date TEXT NOT NULL," +
                "weight REAL NOT NULL," +
                "height REAL NOT NULL," +
                "experience_level TEXT NOT NULL," +
                "trainer_id INTEGER, " +
                "is_student INTEGER NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY(trainer_id) REFERENCES users(id) ON DELETE SET NULL" +
                ");";

        String sqlSubscriptions = "CREATE TABLE IF NOT EXISTS subscriptions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "member_id INTEGER NOT NULL," +
                "type TEXT NOT NULL," +
                "start_date TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "is_active INTEGER NOT NULL," +
                "promotion_id INTEGER," +
                "extended_months INTEGER DEFAULT 0," +
                "FOREIGN KEY(member_id) REFERENCES members(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY(promotion_id) REFERENCES promotions(id)" +
                ");";

        String sqlPayments = "CREATE TABLE IF NOT EXISTS payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL NOT NULL," +
                "payment_date TEXT NOT NULL," +
                "payment_method TEXT NOT NULL," +
                "member_id INTEGER NOT NULL," +
                "purpose TEXT NOT NULL," +
                "FOREIGN KEY(member_id) REFERENCES members(user_id) ON DELETE CASCADE" +
                ");";

        String sqlReviews = "CREATE TABLE IF NOT EXISTS reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "member_id INTEGER NOT NULL," +
                "trainer_id INTEGER NOT NULL," +
                "rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5)," +
                "review_date TEXT NOT NULL," +
                "FOREIGN KEY(member_id) REFERENCES members(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY(trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE" +
                ");";

        String sqlFitnessClasses = "CREATE TABLE IF NOT EXISTS fitness_classes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "duration INTEGER NOT NULL," +
                "difficulty TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "trainer_id INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "hour TEXT NOT NULL," +
                "max_participants INTEGER NOT NULL," +
                "FOREIGN KEY(trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE" +
                ");";

        String sqlClassParticipants = "CREATE TABLE IF NOT EXISTS class_participants (" +
                "fitness_class_id INTEGER NOT NULL," +
                "member_id INTEGER NOT NULL," +
                "PRIMARY KEY (fitness_class_id, member_id)," +
                "FOREIGN KEY(fitness_class_id) REFERENCES fitness_classes(id) ON DELETE CASCADE," +
                "FOREIGN KEY(member_id) REFERENCES members(user_id) ON DELETE CASCADE" +
                ");";

        String sqlPromotions = "CREATE TABLE IF NOT EXISTS promotions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "discount_percent REAL NOT NULL CHECK (discount_percent >= 0 AND discount_percent <= 100)," +
                "start_date TEXT NOT NULL," +
                "end_date TEXT NOT NULL," +
                "active INTEGER NOT NULL DEFAULT 1," +
                "UNIQUE(name, start_date)"+
                ");";

        String sqlBookings = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "member_id INTEGER," +
                "trainer_id INTEGER," +
                "fitness_class_id INTEGER," +
                "date TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "purpose TEXT," +
                "FOREIGN KEY(member_id) REFERENCES members(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY(trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY(fitness_class_id) REFERENCES fitness_classes(id) ON DELETE CASCADE" +
                ");";

        String sqlTimeSlots = "CREATE TABLE IF NOT EXISTS time_slots (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_time TEXT NOT NULL," +
                "end_time TEXT NOT NULL," +
                "day TEXT NOT NULL," +
                "trainer_id INTEGER NOT NULL," +
                "FOREIGN KEY(trainer_id) REFERENCES trainers(user_id) ON DELETE CASCADE" +
                ");";




        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers);
            System.out.println("'users' tabel was created successfully!");

            stmt.execute(sqlTrainers);
            System.out.println("'trainers' tabel was created successfully!");


            stmt.execute(sqlMembers);
            System.out.println("'members' tabel was created successfully!");

            stmt.execute(sqlSubscriptions);
            System.out.println("'subscriptions' tabel was created successfully!");

            stmt.execute(sqlPayments);
            System.out.println("'payments' tabel was created successfully!");

            stmt.execute(sqlReviews);
            System.out.println("'reviews' table created successfully!");

            stmt.execute(sqlFitnessClasses);
            System.out.println("'FitnessClasses' table created successfully!");

            stmt.execute(sqlClassParticipants);
            System.out.println("'ClassParticipants' table created successfully!");

            stmt.execute(sqlPromotions);
            System.out.println("'promotions' table created successfully!");

            stmt.execute(sqlBookings);
            System.out.println("'bookings' table created successfully!");

            stmt.execute(sqlTimeSlots);
            System.out.println("'timeSlots' table created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
