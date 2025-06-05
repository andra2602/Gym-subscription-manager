package dao;

import database.DBConnection;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class MemberDAO extends BaseDAO<Member, Integer>{

    private static MemberDAO instance;

    private final PromotionDAO promotionDAO = PromotionDAO.getInstance();
    private final SubscriptionDAO subscriptionDAO = SubscriptionDAO.getInstance(promotionDAO);
    private TrainerDAO trainerDAO ;

    private MemberDAO() {
        // constructor privat pt singleton
    }

    public static MemberDAO getInstance() {
        if (instance == null) {
            instance = new MemberDAO();
        }
        return instance;
    }

    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }


    @Override
    public void create(Member member) {
        // 1. Inserare în users
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            userStmt.setString(1, member.getName());
            userStmt.setString(2, member.getUsername());
            userStmt.setString(3, member.getEmail());
            userStmt.setString(4, member.getPhoneNumber());
            userStmt.setString(5, member.getPassword());

            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                member.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Nu s-a generat ID-ul pentru user.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în users: " + e.getMessage());
            return;
        }

        // 2. Inserare în members
        String memberSql = "INSERT INTO members (user_id, registration_date, weight, height, experience_level, trainer_id, is_student) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement memberStmt = conn.prepareStatement(memberSql)) {
            memberStmt.setInt(1, member.getId());
            memberStmt.setString(2, member.getRegistrationDate().toString());
            memberStmt.setFloat(3, member.getWeight());
            memberStmt.setFloat(4, member.getHeight());
            memberStmt.setString(5, member.getExperienceLevel());

            if (member.getTrainer() != null) {
                memberStmt.setInt(6, member.getTrainer().getId());
            } else {
                memberStmt.setNull(6, Types.INTEGER);
            }

            memberStmt.setInt(7, member.isStudent() ? 1 : 0);
            memberStmt.executeUpdate();

            System.out.println("Membrul a fost adăugat în baza de date.");

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea în members: " + e.getMessage());
        }
    }
    @Override
    public Optional<Member> read(Integer id) {
        Member member = readById(id);
        return Optional.ofNullable(member);
    }

    public void addMember(Member member) {
        String userSql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        String memberSql = "INSERT INTO members (user_id, registration_date, weight, height, experience_level, trainer_id, is_student) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement memberStmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, member.getName());
            userStmt.setString(2, member.getUsername());
            userStmt.setString(3, member.getEmail());
            userStmt.setString(4, member.getPhoneNumber());
            userStmt.setString(5, member.getPassword());
            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);

                memberStmt = conn.prepareStatement(memberSql);
                memberStmt.setInt(1, userId);
                memberStmt.setString(2, member.getRegistrationDate().toString());
                memberStmt.setFloat(3, member.getWeight());
                memberStmt.setFloat(4, member.getHeight());
                memberStmt.setString(5, member.getExperienceLevel());
                memberStmt.setObject(6, member.getTrainer() != null ? member.getTrainer().getId() : null); // poate fi null
                memberStmt.setBoolean(7, member.isStudent());
                memberStmt.executeUpdate();

                conn.commit();
            } else {
                throw new SQLException("User ID not generated.");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (userStmt != null) userStmt.close();
                if (memberStmt != null) memberStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Member> readAll() {
        List<Member> members = new ArrayList<>();
        Map<Member, Integer> trainerMap = new HashMap<>();

        String sql = "SELECT * FROM users u JOIN members m ON u.id = m.user_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int memberId = rs.getInt("user_id");
                int trainerId = rs.getInt("trainer_id");

                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("registration_date")),
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null, // trainer
                        null, // subscription
                        rs.getBoolean("is_student")
                );
                member.setId(memberId);

                if (!rs.wasNull()) {
                    trainerMap.put(member, trainerId);
                }

                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Member member : members) {
            try {
                // setăm trainerul dacă există
                Integer trainerId = trainerMap.get(member);
                if (trainerId != null) {
                    Trainer trainer = trainerDAO.findById(trainerId);
                    member.setTrainer(trainer);
                }

                // setăm abonamentul activ, dacă există
                Subscription subscription = subscriptionDAO.findActiveByMemberId(member.getId());
                member.setSubscription(subscription);

            } catch (Exception e) {
                System.out.println("Eroare la completarea trainerului/subscriptiei pentru membrul: " + member.getUsername());
            }
        }

        return members;
    }

    public Member readById(int id) {
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "m.registration_date, m.weight, m.height, m.experience_level, m.trainer_id, m.is_student " +
                "FROM users u " +
                "JOIN members m ON u.id = m.user_id " +
                "WHERE u.id = ?";

        Member member = null;
        Integer trainerId = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    member = new Member(
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("password"),
                            LocalDate.parse(rs.getString("registration_date")),
                            rs.getFloat("weight"),
                            rs.getFloat("height"),
                            rs.getString("experience_level"),
                            null,
                            null,
                            rs.getInt("is_student") == 1
                    );
                    member.setId(rs.getInt("id"));
                    member.setPayments(new ArrayList<>());
                    trainerId = rs.getInt("trainer_id");
                    if (rs.wasNull()) trainerId = null;
                }
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea membrului: " + e.getMessage());
            return null;
        }

        if (member != null) {
            if (trainerId != null && trainerDAO != null) {
                Trainer trainer = trainerDAO.findById(trainerId);
                member.setTrainer(trainer);
            }

            Subscription subscription = subscriptionDAO.readByMemberId(member.getId());
            member.setSubscription(subscription);
        }

        return member;
    }


    public boolean updateTrainer(int memberId, Integer trainerId) {
        String sql = "UPDATE members SET trainer_id = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (trainerId != null) {
                stmt.setInt(1, trainerId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, memberId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Eroare la actualizarea trainerului membrului: " + e.getMessage());
            return false;
        }
    }

    public boolean updateWeight(int memberId, float newWeight) {
        String sql = "UPDATE members SET weight = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, newWeight);
            stmt.setInt(2, memberId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating member weight: " + e.getMessage());
            return false;
        }
    }


    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea membrului: " + e.getMessage());
        }

        return false;
    }


    public List<Member> getMembersByTrainerId(int trainerId) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.username, u.email, u.phone, u.password, " +
                "m.registration_date, m.weight, m.height, m.experience_level, m.is_student " +
                "FROM users u JOIN members m ON u.id = m.user_id " +
                "WHERE m.trainer_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("registration_date")),
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null,
                        null,
                        rs.getInt("is_student") == 1
                );
                member.setId(rs.getInt("id"));
                member.setPayments(new ArrayList<>());
                members.add(member);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea membrilor antrenați: " + e.getMessage());
        }

        return members;
    }

    public Member findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users u JOIN members m ON u.id = m.user_id WHERE u.username = ? AND u.password = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate registrationDate = LocalDate.parse(rs.getString("registration_date"));

                Member member = new Member(
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        registrationDate,
                        rs.getFloat("weight"),
                        rs.getFloat("height"),
                        rs.getString("experience_level"),
                        null,
                        null,
                        rs.getBoolean("is_student")
                );

                member.setId(rs.getInt("id"));

                // Trainer
                int trainerId = rs.getInt("trainer_id");
                if (!rs.wasNull() && trainerDAO != null) {
                    Trainer trainer = trainerDAO.findById(trainerId);
                    member.setTrainer(trainer);
                }

                // Subscription
                Subscription subscription = subscriptionDAO.findActiveByMemberId(member.getId());
                member.setSubscription(subscription);

                return member;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void assignTrainer(int memberId, int trainerId) {
        String sql = "UPDATE members SET trainer_id = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, memberId);
            int updated = stmt.executeUpdate();

            if (updated == 0) {
                System.out.println("No rows updated. Member ID might not exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getTrainerIdForMember(int memberId) {
        String sql = "SELECT trainer_id FROM members WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("trainer_id");
                return rs.wasNull() ? null : id;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeTrainerFromMember(int memberId) {
        String sql = "UPDATE members SET trainer_id = NULL WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}

