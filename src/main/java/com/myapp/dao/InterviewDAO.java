package com.myapp.dao;

import com.myapp.model.Interview;
import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InterviewDAO {

    public List<Interview> getInterviewsByUser(int userId) {
        List<Interview> interviews = new ArrayList<>();

        String sql = "SELECT interviewId, roleTitle, departmentName, interviewerName, " +
                     "interviewType, interviewDate, startTime, endTime, location, notes " +
                     "FROM interviews WHERE userId = ? ORDER BY interviewDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Interview interview = new Interview();

                    interview.setInterviewId(rs.getInt("interviewId"));
                    interview.setRoleTitle(rs.getString("roleTitle"));
                    interview.setDepartmentName(rs.getString("departmentName"));
                    interview.setInterviewerName(rs.getString("interviewerName"));
                    interview.setInterviewType(rs.getString("interviewType"));
                    interview.setInterviewDate(rs.getDate("interviewDate"));
                    interview.setStartTime(rs.getTime("startTime"));
                    interview.setEndTime(rs.getTime("endTime"));
                    interview.setLocation(rs.getString("location"));
                    interview.setNotes(rs.getString("notes"));

                    interviews.add(interview);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return interviews;
    }

    public boolean addInterview(int userId,
                                String roleTitle,
                                String departmentName,
                                String interviewerName,
                                String interviewType,
                                Date interviewDate,
                                Time startTime,
                                Time endTime,
                                String location,
                                String notes) {

        String sql = "INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, " +
                     "interviewType, interviewDate, startTime, endTime, location, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, roleTitle);
            stmt.setString(3, departmentName);
            stmt.setString(4, interviewerName);
            stmt.setString(5, interviewType);
            stmt.setDate(6, interviewDate);
            stmt.setTime(7, startTime);
            stmt.setTime(8, endTime);
            stmt.setString(9, location);
            stmt.setString(10, notes);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInterview(int interviewId, int userId) {
        String sql = "DELETE FROM interviews WHERE interviewId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}