package com.myapp.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import com.myapp.util.CsrfUtil;
import com.myapp.util.DBConnection;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!CsrfUtil.isValidToken(req)) {
            resp.sendRedirect("register.jsp?error=csrf");
            return;
        }

        String firstName = trim(req.getParameter("firstName"));
        String lastName = trim(req.getParameter("lastName"));
        String email = trim(req.getParameter("email"));
        String userName = trim(req.getParameter("userName"));
        String password = req.getParameter("password");

        if (isBlank(firstName) || isBlank(lastName) || isBlank(email)
                || isBlank(userName) || isBlank(password)) {
            resp.sendRedirect("register.jsp?error=missing");
            return;
        }

        if (password.length() < 8) {
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

            String insertSql = "INSERT INTO Users (firstName, lastName, email, userName, password, isAdmin) VALUES (?, ?, ?, ?, ?, 0)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, firstName.trim());
                insertStmt.setString(2, lastName.trim());
                insertStmt.setString(3, email.trim());
                insertStmt.setString(4, userName.trim());
                insertStmt.setString(5, hashedPassword);
                insertStmt.executeUpdate();
            }

            resp.sendRedirect("login.jsp?registered=1");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Registration failed due to server error.", e);
            resp.sendRedirect("register.jsp?error=server");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}