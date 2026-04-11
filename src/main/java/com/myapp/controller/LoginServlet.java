package com.myapp.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.myapp.util.DBConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String identifier = req.getParameter("identifier");
        String password = req.getParameter("password");

        if (identifier == null || password == null ||
            identifier.trim().isEmpty() || password.trim().isEmpty()) {
            resp.sendRedirect("login.jsp?error=missing");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT userId, firstName, email, userName, password FROM Users WHERE email = ? OR userName = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                resp.sendRedirect("login.jsp?error=invalid");
                return;
            }

            String storedHash = rs.getString("password");

            if (!BCrypt.checkpw(password, storedHash)) {
                resp.sendRedirect("login.jsp?error=invalid");
                return;
            }

            HttpSession session = req.getSession();
            session.setAttribute("userId", rs.getInt("userId"));
            session.setAttribute("firstName", rs.getString("firstName"));
            session.setAttribute("email", rs.getString("email"));
            session.setAttribute("username", rs.getString("userName"));

            resp.sendRedirect("dashboard.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("login.jsp?error=server");
        }
    }
}