package com.myapp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.myapp.dao.UserDAO;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    /** GET: show the page. Step is determined by whether resetUserId is in session. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("lookup".equals(action)) {
            handleLookup(req, resp);
        } else if ("reset".equals(action)) {
            handleReset(req, resp);
        } else {
            resp.sendRedirect("forgot-password");
        }
    }

    /** Step 1: find user by email or username and store their ID in session. */
    private void handleLookup(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String identifier = req.getParameter("identifier");
        if (identifier == null || identifier.trim().isEmpty()) {
            resp.sendRedirect("forgot-password?error=missing");
            return;
        }

        int userId = userDAO.getUserIdByIdentifier(identifier.trim());
        if (userId == -1) {
            resp.sendRedirect("forgot-password?error=notfound");
            return;
        }

        // Store in session so step 2 knows which account to reset
        HttpSession session = req.getSession();
        session.setAttribute("resetUserId", userId);
        resp.sendRedirect("forgot-password?step=2");
    }

    /** Step 2: update the password for the user stored in session. */
    private void handleReset(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        Integer resetUserId = (session != null) ? (Integer) session.getAttribute("resetUserId") : null;

        if (resetUserId == null) {
            // Session expired or someone navigated here directly
            resp.sendRedirect("forgot-password?error=session");
            return;
        }

        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isBlank(newPassword) || isBlank(confirmPassword)) {
            resp.sendRedirect("forgot-password?step=2&error=missing");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            resp.sendRedirect("forgot-password?step=2&error=mismatch");
            return;
        }

        if (newPassword.length() < 6) {
            resp.sendRedirect("forgot-password?step=2&error=tooshort");
            return;
        }

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean ok = userDAO.setPasswordHash(resetUserId, newHash);

        // Clear the reset session attribute regardless of outcome
        session.removeAttribute("resetUserId");

        if (ok) {
            resp.sendRedirect("login.jsp?reset=1");
        } else {
            resp.sendRedirect("forgot-password?error=server");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
