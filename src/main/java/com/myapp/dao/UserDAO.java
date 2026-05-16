package com.myapp.dao;

import com.myapp.model.User;
import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT userId, firstName, userName, email, isAdmin FROM Users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("userId"));
                user.setName(rs.getString("firstName"));
                user.setUserName(rs.getString("userName"));
                user.setEmail(rs.getString("email"));
                user.setAdmin(rs.getBoolean("isAdmin"));
                users.add(user);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load users list.", e);
        }

        return users;
    }

    public User getUserById(int userId) {
        String sql = "SELECT userId, firstName, lastName, email, userName, isAdmin FROM Users WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("userId"));
                    user.setName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setEmail(rs.getString("email"));
                    user.setUserName(rs.getString("userName"));
                    user.setAdmin(rs.getBoolean("isAdmin"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load user profile by ID.", e);
        }
        return null;
    }

    /**
     * Updates account info. Returns null on success, or an error key string.
     */
    public String updateInfo(int userId, String firstName, String lastName, String email, String userName) {
        // Check uniqueness of email and userName (excluding the current user)
        String checkSql = "SELECT userId FROM Users WHERE (email = ? OR userName = ?) AND userId != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            checkStmt.setString(2, userName);
            checkStmt.setInt(3, userId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return "exists";
                }
            }

            String updateSql = "UPDATE Users SET firstName = ?, lastName = ?, email = ?, userName = ? WHERE userId = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, firstName);
                updateStmt.setString(2, lastName);
                updateStmt.setString(3, email);
                updateStmt.setString(4, userName);
                updateStmt.setInt(5, userId);
                updateStmt.executeUpdate();
            }

            return null; // success

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update user profile.", e);
            return "server";
        }
    }

    /** Returns the stored BCrypt hash for a user, or null if not found. */
    public String getPasswordHash(int userId) {
        String sql = "SELECT password FROM Users WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to read password hash.", e);
        }
        return null;
    }

    /** Stores a pre-computed BCrypt hash as the user's new password. */
    public boolean setPasswordHash(int userId, String newHash) {
        String sql = "UPDATE Users SET password = ? WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update password hash.", e);
            return false;
        }
    }

    /** Returns the userId for the given email or username, or -1 if not found. */
    public int getUserIdByIdentifier(String identifier) {
        String sql = "SELECT userId FROM Users WHERE email = ? OR userName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("userId");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to resolve user by identifier.", e);
        }
        return -1;
    }

    /** Permanently deletes the user record. Returns true on success. */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete user account.", e);
            return false;
        }
    }

    /**
     * Creates a reset token entry with expiration. Token value should be pre-hashed.
     */
    public boolean storePasswordResetToken(int userId, String tokenHash, Timestamp expiresAt) {
        String sql = "INSERT INTO PasswordResetTokens (userId, tokenHash, expiresAt) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, tokenHash);
            stmt.setTimestamp(3, expiresAt);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to store password reset token.", e);
            return false;
        }
    }

    /**
     * Resolves userId for a valid, unexpired token hash.
     */
    public int getUserIdByValidResetTokenHash(String tokenHash) {
        String sql = "SELECT userId FROM PasswordResetTokens " +
                     "WHERE tokenHash = ? AND expiresAt > NOW() ORDER BY resetId DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tokenHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("userId");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to validate password reset token.", e);
        }
        return -1;
    }

    /**
     * Removes a token so it cannot be reused.
     */
    public boolean consumePasswordResetToken(String tokenHash) {
        String sql = "DELETE FROM PasswordResetTokens WHERE tokenHash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tokenHash);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to consume password reset token.", e);
            return false;
        }
    }
}
