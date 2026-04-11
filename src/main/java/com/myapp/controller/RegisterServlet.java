package com.myapp.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import com.myapp.util.DBConnection;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");

        if (isBlank(firstName) || isBlank(lastName) || isBlank(email)
                || isBlank(userName) || isBlank(password)) {
            resp.sendRedirect("register.jsp?error=missing");
            return;
        }

        if (password.length() < 6) {
            resp.sendRedirect("register.jsp?error=password");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String checkSql = "SELECT userId FROM Users WHERE email = ? OR userName = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, userName);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        resp.sendRedirect("register.jsp?error=exists");
                        return;
                    }
                }
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            String insertSql = "INSERT INTO Users (firstName, lastName, email, userName, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setString(4, userName);
                insertStmt.setString(5, hashedPassword);
                insertStmt.executeUpdate();
            }

            resp.sendRedirect("login.jsp?registered=1");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("register.jsp?error=server");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}