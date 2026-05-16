package com.myapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = getConfig(
            "DB_URL",
            "jdbc:mysql://localhost:3306/trackhire?useSSL=true&serverTimezone=UTC"
    );
    private static final String USER = getConfig("DB_USER", "root");
    private static final String PASSWORD = getConfig("DB_PASSWORD", null);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }

        if (PASSWORD == null || PASSWORD.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Missing DB_PASSWORD. Set DB_PASSWORD (or -Ddb.password) before starting the app."
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getConfig(String key, String fallback) {
        String systemProperty = System.getProperty(toPropertyKey(key));
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.trim();
        }

        String envValue = System.getenv(key);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        return fallback;
    }

    private static String toPropertyKey(String envKey) {
        return envKey.toLowerCase().replace('_', '.');
    }
}