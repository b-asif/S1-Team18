package com.myapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/trackhire?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "CS157A"; // change this

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to DB: " + URL);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}