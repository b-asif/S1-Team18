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
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.myapp.util.CsrfUtil;
import com.myapp.util.DBConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!CsrfUtil.isValidToken(req)) {
            resp.sendRedirect("login.jsp?error=csrf");
            return;
        }

        String identifier = req.getParameter("identifier");
        String password = req.getParameter("password");
        if (identifier != null) {
            identifier = identifier.trim();
        }

        if (identifier == null || password == null ||
            identifier.isEmpty() || password.trim().isEmpty()) {
            resp.sendRedirect("login.jsp?error=missing");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT userId, firstName, email, userName, password, isAdmin FROM Users WHERE email = ? OR userName = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, identifier);
                stmt.setString(2, identifier);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        resp.sendRedirect("login.jsp?error=invalid");
                        return;
                    }

                    String storedHash = rs.getString("password");
                    if (!BCrypt.checkpw(password, storedHash)) {
                        resp.sendRedirect("login.jsp?error=invalid");
                        return;
                    }

                    // Prevent session fixation by rotating session ID on login.
                    HttpSession existingSession = req.getSession(false);
                    if (existingSession != null) {
                        existingSession.invalidate();
                    }

                    HttpSession session = req.getSession(true);
                    CsrfUtil.getOrCreateToken(session);
                    session.setAttribute("userId", rs.getInt("userId"));
                    session.setAttribute("firstName", rs.getString("firstName"));
                    session.setAttribute("email", rs.getString("email"));
                    session.setAttribute("username", rs.getString("userName"));
                    session.setAttribute("isAdmin", rs.getBoolean("isAdmin"));
                }
            }

            resp.sendRedirect("dashboard");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Login failed due to server error.", e);
            resp.sendRedirect("login.jsp?error=server");
        }
    }
}