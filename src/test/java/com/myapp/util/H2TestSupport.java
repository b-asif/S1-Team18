package com.myapp.util;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;

import org.h2.tools.RunScript;

public final class H2TestSupport {
    private static final String DB_URL = System.getProperty("db.url");
    private static final String DB_USER = System.getProperty("db.user");
    private static final String DB_PASSWORD = System.getProperty("db.password");

    private H2TestSupport() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void resetDatabase() throws Exception {
        runScript("schema-h2.sql");
        runScript("data-h2.sql");
    }

    private static void runScript(String classpathResource) throws Exception {
        InputStream in = H2TestSupport.class.getClassLoader().getResourceAsStream(classpathResource);
        if (in == null) {
            throw new IllegalStateException("Missing test resource: " + classpathResource);
        }

        try (Connection conn = getConnection();
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            RunScript.execute(conn, reader);
        }
    }
}
