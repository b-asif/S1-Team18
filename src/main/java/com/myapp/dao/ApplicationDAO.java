package com.myapp.dao;

import com.myapp.model.Application;
import com.myapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationDAO {
    private static final Logger LOGGER = Logger.getLogger(ApplicationDAO.class.getName());

    public List<Application> getApplicationsByUser(int userId) {
        return getApplicationsForUser(userId, null, null);
    }

    /**
     * @param searchQuery optional substring match on title, company, location, notes
     * @param statusFilter optional exact status, or null / blank / "all" for any
     */
    public List<Application> getApplicationsForUser(int userId, String searchQuery, String statusFilter) {
        List<Application> apps = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT applicationId, jobTitle, companyName, appStatus, dateApplied, "
                        + "jobUrl, jobLocation, notes FROM Applications WHERE userId = ?");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (statusFilter != null && !statusFilter.trim().isEmpty()
                && !"all".equalsIgnoreCase(statusFilter.trim())) {
            sql.append(" AND appStatus = ?");
            params.add(statusFilter.trim());
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = "%" + searchQuery.trim().replace("%", "").replace("_", "") + "%";
            sql.append(" AND (jobTitle LIKE ? OR companyName LIKE ? OR COALESCE(jobLocation,'') LIKE ? "
                    + "OR COALESCE(notes,'') LIKE ? OR COALESCE(jobUrl,'') LIKE ?)");
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        sql.append(" ORDER BY dateApplied DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apps.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to query applications for user.", e);
        }

        return apps;
    }

    private static Application mapRow(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationId(rs.getInt("applicationId"));
        app.setJobTitle(rs.getString("jobTitle"));
        app.setCompanyName(rs.getString("companyName"));
        app.setAppStatus(rs.getString("appStatus"));
        app.setDateApplied(rs.getDate("dateApplied"));
        app.setJobUrl(rs.getString("jobUrl"));
        app.setJobLocation(rs.getString("jobLocation"));
        app.setNotes(rs.getString("notes"));
        return app;
    }

    public boolean addApplication(int userId, String jobTitle, String companyName,
                                  String appStatus, Date dateApplied) {
        return addApplication(userId, jobTitle, companyName, appStatus, dateApplied,
                null, null, null);
    }

    public boolean addApplication(int userId, String jobTitle, String companyName,
                                  String appStatus, Date dateApplied,
                                  String jobUrl, String jobLocation, String notes) {
        String sql = "INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, "
                + "jobUrl, jobLocation, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, jobTitle);
            stmt.setString(3, companyName);
            stmt.setString(4, appStatus);
            stmt.setDate(5, dateApplied);
            stmt.setString(6, jobUrl);
            stmt.setString(7, jobLocation);
            stmt.setString(8, notes);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add application.", e);
            return false;
        }
    }

    public boolean updateApplication(int applicationId, int userId, String jobTitle, String companyName,
                                     String appStatus, Date dateApplied,
                                     String jobUrl, String jobLocation, String notes) {
        String sql = "UPDATE Applications SET jobTitle = ?, companyName = ?, appStatus = ?, dateApplied = ?, "
                + "jobUrl = ?, jobLocation = ?, notes = ? WHERE applicationId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jobTitle);
            stmt.setString(2, companyName);
            stmt.setString(3, appStatus);
            stmt.setDate(4, dateApplied);
            stmt.setString(5, jobUrl);
            stmt.setString(6, jobLocation);
            stmt.setString(7, notes);
            stmt.setInt(8, applicationId);
            stmt.setInt(9, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update application.", e);
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
            LOGGER.log(Level.SEVERE, "Failed to delete application.", e);
            return false;
        }
    }

    public long countApplicationsForUser(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM Applications WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("cnt");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to count applications.", e);
        }
        return 0L;
    }

    public Map<String, Long> countApplicationsByStatusForUser(int userId) {
        Map<String, Long> map = new LinkedHashMap<>();
        String sql = "SELECT appStatus, COUNT(*) AS cnt FROM Applications WHERE userId = ? "
                + "GROUP BY appStatus ORDER BY appStatus";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("appStatus"), rs.getLong("cnt"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to count applications by status.", e);
        }
        return map;
    }

    public long countApplicationsWithStatus(int userId, String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM Applications WHERE userId = ? AND appStatus = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("cnt");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to count applications by status value.", e);
        }
        return 0L;
    }
}
