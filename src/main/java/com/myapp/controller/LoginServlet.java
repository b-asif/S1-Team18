package com.myapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myapp.util.DBConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonBody = sb.toString();

            String identifier = extractJsonValue(jsonBody, "identifier");
            String password = extractJsonValue(jsonBody, "password");

            if (identifier == null || password == null ||
                identifier.isEmpty() || password.isEmpty()) {

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"message\":\"Missing credentials.\"}");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                String sql = "SELECT user_id, first_name, email, username, password FROM Users WHERE email = ? OR username = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, identifier);
                stmt.setString(2, identifier);

                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"message\":\"Invalid credentials.\"}");
                    return;
                }

                String storedPassword = rs.getString("password");

                // Simple comparison for now
                // Later replace with BCrypt password hash check
                if (!password.equals(storedPassword)) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"message\":\"Invalid credentials.\"}");
                    return;
                }

                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String email = rs.getString("email");
                String username = rs.getString("username");

                HttpSession session = req.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("firstName", firstName);
                session.setAttribute("email", email);
                session.setAttribute("username", username);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(
                    "{"
                    + "\"message\":\"Login successful.\","
                    + "\"userId\":" + userId + ","
                    + "\"firstName\":\"" + escapeJson(firstName) + "\","
                    + "\"email\":\"" + escapeJson(email) + "\","
                    + "\"username\":\"" + escapeJson(username) + "\""
                    + "}"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\":\"Server error.\"}");
        }
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;

        int startQuote = json.indexOf("\"", colonIndex + 1);
        if (startQuote == -1) return null;

        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        return json.substring(startQuote + 1, endQuote);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}