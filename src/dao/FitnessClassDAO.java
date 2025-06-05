package dao;

import database.DBConnection;
import models.FitnessClass;
import models.Trainer;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FitnessClassDAO extends BaseDAO<FitnessClass, Integer> {

    private static FitnessClassDAO instance;

    private final ClassParticipantsDAO classParticipantsDAO = ClassParticipantsDAO.getInstance();
    private TrainerDAO trainerDAO;
    private FitnessClassDAO() {
    }

    public static FitnessClassDAO getInstance() {
        if (instance == null) {
            instance = new FitnessClassDAO();
        }
        return instance;
    }
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Override
    public void create(FitnessClass fitnessClass) {
        String sql = "INSERT INTO fitness_classes (name, duration, difficulty, price, trainer_id, date, hour, max_participants) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fitnessClass.getName());
            stmt.setInt(2, fitnessClass.getDuration());
            stmt.setString(3, fitnessClass.getDifficulty());
            stmt.setDouble(4, fitnessClass.getPrice());
            stmt.setInt(5, fitnessClass.getTrainer().getId());
            stmt.setString(6, fitnessClass.getDate().toString());
            stmt.setString(7, fitnessClass.getHour().toString());
            stmt.setInt(8, fitnessClass.getMaxParticipants());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                fitnessClass.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Eroare la crearea clasei fitness: " + e.getMessage());
        }
    }

    @Override
    public Optional<FitnessClass> read(Integer id) {
        String sql = "SELECT * FROM fitness_classes WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Ia trainerul asociat
                int trainerId = rs.getInt("trainer_id");
                Trainer trainer = trainerDAO.findById(trainerId);

                FitnessClass fc = new FitnessClass(
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("difficulty"),
                        rs.getDouble("price"),
                        trainer,
                        new ArrayList<>(),  // participanții pot fi încărcați separat
                        LocalDate.parse(rs.getString("date")),
                        LocalTime.parse(rs.getString("hour")),
                        rs.getInt("max_participants")
                );

                fc.setId(rs.getInt("id"));
                return Optional.of(fc);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea clasei fitness cu ID " + id + ": " + e.getMessage());
        }

        return Optional.empty();
    }



    public void addFitnessClass(FitnessClass fitnessClass) {
        String sql = "INSERT INTO fitness_classes (name, duration, difficulty, price, trainer_id, date, hour, max_participants) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fitnessClass.getName());
            stmt.setInt(2, fitnessClass.getDuration());
            stmt.setString(3, fitnessClass.getDifficulty());
            stmt.setDouble(4, fitnessClass.getPrice());
            stmt.setInt(5, fitnessClass.getTrainer().getId());
            stmt.setString(6, fitnessClass.getDate().toString());
            stmt.setString(7, fitnessClass.getHour().toString());
            stmt.setInt(8, fitnessClass.getMaxParticipants());

            stmt.executeUpdate();

            // Obține ID-ul generat și setează-l în obiectul Java
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                fitnessClass.setId(id); // <- ESSENTIAL!
            } else {
                System.out.println("❌ Could not retrieve generated ID for fitness class.");
            }

        } catch (SQLException e) {
            System.out.println("Error inserting fitness class: " + e.getMessage());
        }
    }


    public List<FitnessClass> readAllAvailable() {
        List<FitnessClass> classes = new ArrayList<>();

        class ClassTempData {
            int id;
            int trainerId;
            String name;
            int duration;
            String difficulty;
            double price;
            String date;
            String hour;
            int maxParticipants;

            ClassTempData(ResultSet rs) throws SQLException {
                id = rs.getInt("id");
                trainerId = rs.getInt("trainer_id");
                name = rs.getString("name");
                duration = rs.getInt("duration");
                difficulty = rs.getString("difficulty");
                price = rs.getDouble("price");
                date = rs.getString("date");
                hour = rs.getString("hour");
                maxParticipants = rs.getInt("max_participants");
            }
        }

        List<ClassTempData> tempList = new ArrayList<>();

        String sql = "SELECT * FROM fitness_classes WHERE date >= DATE('now') ORDER BY date, hour";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tempList.add(new ClassTempData(rs));
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea claselor disponibile: " + e.getMessage());
            return classes;
        }

        for (ClassTempData temp : tempList) {
            Trainer trainer = trainerDAO.findById(temp.trainerId);

            FitnessClass fc = new FitnessClass(
                    temp.name,
                    temp.duration,
                    temp.difficulty,
                    temp.price,
                    trainer,
                    new ArrayList<>(),
                    LocalDate.parse(temp.date),
                    LocalTime.parse(temp.hour),
                    temp.maxParticipants
            );
            fc.setId(temp.id);
            classes.add(fc);
        }

        return classes;
    }

    public List<FitnessClass> readAll() {
        List<FitnessClass> classes = new ArrayList<>();
        Map<FitnessClass, Integer> trainerIdMap = new HashMap<>();

        String sql = "SELECT * FROM fitness_classes ORDER BY date, hour";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int classId = rs.getInt("id");
                int trainerId = rs.getInt("trainer_id");

                Trainer trainerPlaceholder = new Trainer(); // temporar, doar pt constructor
                trainerPlaceholder.setId(trainerId);

                FitnessClass fc = new FitnessClass(
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("difficulty"),
                        rs.getDouble("price"),
                        trainerPlaceholder,
                        new ArrayList<>(),
                        LocalDate.parse(rs.getString("date")),
                        LocalTime.parse(rs.getString("hour")),
                        rs.getInt("max_participants")
                );
                fc.setId(classId);

                classes.add(fc);
                trainerIdMap.put(fc, trainerId);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea tuturor claselor: " + e.getMessage());
        }

        for (FitnessClass fc : classes) {
            int trainerId = trainerIdMap.get(fc);
            Trainer trainer = trainerDAO.findById(trainerId);
            fc.setTrainer(trainer);
        }

        return classes;
    }


    public boolean delete(int id) {
        String sql = "DELETE FROM fitness_classes WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea clasei fitness: " + e.getMessage());
            return false;
        }
    }

    public List<FitnessClass> getClassesByTrainerId(int trainerId) {
        List<FitnessClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM fitness_classes WHERE trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            Trainer trainer = trainerDAO.getTrainerById(trainerId, conn);
            while (rs.next()) {
                FitnessClass fc = new FitnessClass(
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("difficulty"),
                        rs.getDouble("price"),
                        trainer,
                        new ArrayList<>(),
                        LocalDate.parse(rs.getString("date")),
                        LocalTime.parse(rs.getString("hour")),
                        rs.getInt("max_participants")
                );
                fc.setId(rs.getInt("id"));
                classes.add(fc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public List<FitnessClass> getClassesForTrainerByDate(int trainerId, LocalDate date) {
        List<FitnessClass> classes = new ArrayList<>();

        String sql = "SELECT * FROM fitness_classes WHERE trainer_id = ? AND date = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setString(2, date.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FitnessClass fc = new FitnessClass(
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("difficulty"),
                        rs.getDouble("price"),
                        new Trainer(rs.getInt("trainer_id")),
                        new ArrayList<>(),
                        date,
                        LocalTime.parse(rs.getString("hour")),
                        rs.getInt("max_participants")
                );
                fc.setId(rs.getInt("id"));
                classes.add(fc);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea claselor pentru trainer: " + e.getMessage());
        }

        return classes;
    }

}