package com.myapp.dao;

import com.myapp.model.Application;
import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public List<Application> getApplicationsByUser(int userId) {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT applicationId, jobTitle, companyName, appStatus, dateApplied " +
                     "FROM Applications WHERE userId = ? ORDER BY dateApplied DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = new Application();
                    app.setApplicationId(rs.getInt("applicationId"));
                    app.setJobTitle(rs.getString("jobTitle"));
                    app.setCompanyName(rs.getString("companyName"));
                    app.setAppStatus(rs.getString("appStatus"));
                    app.setDateApplied(rs.getDate("dateApplied"));
                    apps.add(app);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return apps;
    }

    public boolean addApplication(int userId, String jobTitle, String companyName,
                                  String appStatus, Date dateApplied) {
        String sql = "INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, jobTitle);
            stmt.setString(3, companyName);
            stmt.setString(4, appStatus);
            stmt.setDate(5, dateApplied);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteApplication(int applicationId, int userId) {
        String sql = "DELETE FROM Applications WHERE applicationId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
