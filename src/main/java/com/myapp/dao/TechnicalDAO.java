package com.myapp.dao;

import com.myapp.model.Technical;
import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TechnicalDAO {

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
            e.printStackTrace();
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
            stmt.setDate(4, dueDate);
            stmt.setString(5, assessmentNotes);
            stmt.setString(6, completionStatus);
            stmt.setString(7, scoreOrPassFail);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}