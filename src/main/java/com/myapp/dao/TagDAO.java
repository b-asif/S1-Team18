package com.myapp.dao;

import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagDAO {
    private static final Logger LOGGER = Logger.getLogger(TagDAO.class.getName());

    public List<String> getTagNamesForApplication(int applicationId, int userId) {
        List<String> names = new ArrayList<>();
        String sql = "SELECT t.name FROM ApplicationTag at "
                + "INNER JOIN Tag t ON t.tagId = at.tagId "
                + "INNER JOIN Applications a ON a.applicationId = at.applicationId "
                + "WHERE at.applicationId = ? AND a.userId = ? AND t.userId = ? "
                + "ORDER BY t.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load tags for application.", e);
        }
        return names;
    }

    /**
     * Ensures tag exists for user (case-insensitive unique per userId in Java layer).
     */
    public int getOrCreateTagId(int userId, String rawName) {
        if (rawName == null) {
            return -1;
        }
        String name = rawName.trim();
        if (name.isEmpty() || name.length() > 100) {
            return -1;
        }

        String findSql = "SELECT tagId FROM Tag WHERE userId = ? AND LOWER(name) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(findSql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tagId");
                }
            }

            String insertSql = "INSERT INTO Tag (userId, name) VALUES (?, ?)";
            try (PreparedStatement ins = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ins.setInt(1, userId);
                ins.setString(2, name);
                ins.executeUpdate();
                try (ResultSet keys = ins.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                try (Connection conn2 = DBConnection.getConnection();
                     PreparedStatement stmt2 = conn2.prepareStatement(findSql)) {
                    stmt2.setInt(1, userId);
                    stmt2.setString(2, name);
                    try (ResultSet rs = stmt2.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("tagId");
                        }
                    }
                } catch (SQLException e2) {
                    LOGGER.log(Level.SEVERE, "Failed to resolve tag after duplicate.", e2);
                }
                return -1;
            }
            LOGGER.log(Level.SEVERE, "Failed to get or create tag.", e);
        }
        return -1;
    }

    public boolean linkTagToApplication(int applicationId, int tagId, int userId) {
        String ownApp = "SELECT 1 FROM Applications WHERE applicationId = ? AND userId = ?";
        String ownTag = "SELECT 1 FROM Tag WHERE tagId = ? AND userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement a = conn.prepareStatement(ownApp);
             PreparedStatement t = conn.prepareStatement(ownTag)) {

            a.setInt(1, applicationId);
            a.setInt(2, userId);
            try (ResultSet rs = a.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
            }
            t.setInt(1, tagId);
            t.setInt(2, userId);
            try (ResultSet rs = t.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
            }

            String sql = "INSERT INTO ApplicationTag (applicationId, tagId) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, applicationId);
                stmt.setInt(2, tagId);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                return true;
            }
            LOGGER.log(Level.SEVERE, "Failed to link tag.", e);
            return false;
        }
    }

    /**
     * Unlink by tag name for owner.
     */
    public boolean unlinkTagFromApplication(int applicationId, String tagName, int userId) {
        if (tagName == null || tagName.trim().isEmpty()) {
            return false;
        }
        String findTag = "SELECT tagId FROM Tag WHERE userId = ? AND name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement find = conn.prepareStatement(findTag)) {
            find.setInt(1, userId);
            find.setString(2, tagName.trim());
            int tagId = -1;
            try (ResultSet rs = find.executeQuery()) {
                if (rs.next()) {
                    tagId = rs.getInt("tagId");
                }
            }
            if (tagId < 0) {
                return false;
            }
            String del = "DELETE FROM ApplicationTag WHERE applicationId = ? AND tagId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(del)) {
                stmt.setInt(1, applicationId);
                stmt.setInt(2, tagId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to unlink tag.", e);
            return false;
        }
    }
}
