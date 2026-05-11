package com.myapp.dao;

import com.myapp.model.Technical;
import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TechnicalDAO {
    private static final Logger LOGGER = Logger.getLogger(TechnicalDAO.class.getName());

    public List<Technical> getAssessmentsByUser(int userId) {
        List<Technical> assessments = new ArrayList<>();

        String sql = "SELECT assessmentId, assessmentTitle, assignedDate, dueDate, " +
                     "assessmentNotes, completionStatus, scoreOrPassFail " +
                     "FROM technicals WHERE userId = ? ORDER BY assignedDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Technical tech = new Technical();

                    tech.setAssessmentId(rs.getInt("assessmentId"));
                    tech.setAssessmentTitle(rs.getString("assessmentTitle"));
                    tech.setAssignedDate(rs.getDate("assignedDate"));
                    tech.setDueDate(rs.getDate("dueDate"));
                    tech.setAssessmentNotes(rs.getString("assessmentNotes"));
                    tech.setCompletionStatus(rs.getString("completionStatus"));
                    tech.setScoreOrPassFail(rs.getString("scoreOrPassFail"));

                    assessments.add(tech);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to query assessments for user.", e);
        }

        return assessments;
    }

    public boolean addAssessment(int userId,
                                 String assessmentTitle,
                                 Date assignedDate,
                                 Date dueDate,
                                 String assessmentNotes,
                                 String completionStatus,
                                 String scoreOrPassFail) {

        String sql = "INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, " +
                     "assessmentNotes, completionStatus, scoreOrPassFail) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, assessmentTitle);
            stmt.setDate(3, assignedDate);
            if (dueDate != null) {
                stmt.setDate(4, dueDate);
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, assessmentNotes);
            stmt.setString(6, completionStatus);
            stmt.setString(7, scoreOrPassFail);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add assessment.", e);
            return false;
        }
    }

    public boolean deleteAssessment(int assessmentId, int userId) {
        String sql = "DELETE FROM technicals WHERE assessmentId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assessmentId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete assessment.", e);
            return false;
        }
    }

    public long countIncompleteAssessments(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM technicals WHERE userId = ? "
                + "AND (completionStatus IS NULL OR UPPER(TRIM(completionStatus)) <> 'COMPLETED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("cnt");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to count incomplete assessments.", e);
        }
        return 0L;
    }

    public long countOverdueAssessments(int userId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        String sql = "SELECT COUNT(*) AS cnt FROM technicals WHERE userId = ? AND dueDate IS NOT NULL "
                + "AND dueDate < ? AND (completionStatus IS NULL OR UPPER(TRIM(completionStatus)) <> 'COMPLETED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(today));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("cnt");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to count overdue assessments.", e);
        }
        return 0L;
    }
}