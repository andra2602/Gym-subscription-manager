package dao;

import database.DBConnection;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // INSERT - creare user în baza de date
    public void create(User user) {
        String sql = "INSERT INTO users (name, username, email, phone, password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getPassword());

            stmt.executeUpdate();

            // obținem ID-ul generat și îl setăm în obiectul user
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                user.setId(generatedId); // metoda setId pe care ai adăugat-o
            }

        } catch (SQLException e) {
            System.out.println("Eroare la inserarea userului: " + e.getMessage());
        }
    }

    // SELECT - citire toți userii
    public List<User> readAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                // folosim setterele pt a evita incrementarea aiurea a id-ului
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setPassword(rs.getString("password"));

                // setăm manual ID-ul (fără să incrementăm cu nextId++)
                // dacă vrei să păstrezi consistența, poți adăuga un setId() în User temporar
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Eroare la citirea userilor: " + e.getMessage());
        }

        return users;
    }

    public User readById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setPassword(rs.getString("password"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("Eroare la citirea userului: " + e.getMessage());
        }
        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0; // returnează true dacă s-a șters ceva
        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea userului: " + e.getMessage());
        }
        return false;
    }


    public boolean isEmailTaken(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Eroare la verificarea emailului: " + e.getMessage());
        }
        return false;
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteUserById(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
