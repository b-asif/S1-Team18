package com.myapp.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.myapp.dao.ApplicationDAO;
import com.myapp.model.Application;

class StartupReadinessIT {

    @BeforeEach
    void resetDatabase() throws Exception {
        H2TestSupport.resetDatabase();
    }

    @Test
    void startupConfig_systemPropertiesProvidedForTests() {
        assertNotNull(System.getProperty("db.url"));
        assertNotNull(System.getProperty("db.user"));
        assertNotNull(System.getProperty("db.password"));
        assertFalse(System.getProperty("db.password").isBlank());
    }

    @Test
    void startupConfig_dbConnectionCanExecuteSimpleQuery() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) == 1);
        }
    }

    @Test
    void startupConfig_firstDaoCriticalPathExecutesSuccessfully() {
        ApplicationDAO dao = new ApplicationDAO();
        List<Application> apps = dao.getApplicationsByUser(1);

        assertNotNull(apps);
        assertFalse(apps.isEmpty());
    }
}
