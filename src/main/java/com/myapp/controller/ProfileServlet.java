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
import com.myapp.model.User;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        User user = userDAO.getUserById(userId);

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        req.setAttribute("profileUser", user);
        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = req.getParameter("action");

        if ("updateInfo".equals(action)) {
            handleUpdateInfo(req, resp, session, userId);
        } else if ("updatePassword".equals(action)) {
            handleUpdatePassword(req, resp, userId);
        } else if ("deleteAccount".equals(action)) {
            handleDeleteAccount(req, resp, session, userId);
        } else {
            resp.sendRedirect("profile?error=invalid");
        }
    }

    private void handleUpdateInfo(HttpServletRequest req, HttpServletResponse resp,
                                  HttpSession session, int userId) throws IOException {

        String firstName = trim(req.getParameter("firstName"));
        String lastName  = trim(req.getParameter("lastName"));
        String email     = trim(req.getParameter("email"));
        String userName  = trim(req.getParameter("userName"));

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || userName.isEmpty()) {
            resp.sendRedirect("profile?error=missing");
            return;
        }

        String result = userDAO.updateInfo(userId, firstName, lastName, email, userName);

        if (result == null) {
            // Update session attributes so the greeting reflects the new name immediately
            session.setAttribute("firstName", firstName);
            session.setAttribute("email", email);
            session.setAttribute("username", userName);
            resp.sendRedirect("profile?success=info");
        } else if ("exists".equals(result)) {
            resp.sendRedirect("profile?error=exists");
        } else {
            resp.sendRedirect("profile?error=server");
        }
    }

    private void handleUpdatePassword(HttpServletRequest req, HttpServletResponse resp,
                                      int userId) throws IOException {

        String currentPassword = req.getParameter("currentPassword");
        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isBlank(currentPassword) || isBlank(newPassword) || isBlank(confirmPassword)) {
            resp.sendRedirect("profile?error=pwmissing");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            resp.sendRedirect("profile?error=pwmatch");
            return;
        }

        if (newPassword.length() < 6) {
            resp.sendRedirect("profile?error=pwshort");
            return;
        }

        String storedHash = userDAO.getPasswordHash(userId);
        if (storedHash == null) {
            resp.sendRedirect("profile?error=server");
            return;
        }

        if (!BCrypt.checkpw(currentPassword, storedHash)) {
            resp.sendRedirect("profile?error=pwwrong");
            return;
        }

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean ok = userDAO.setPasswordHash(userId, newHash);

        if (ok) {
            resp.sendRedirect("profile?success=password");
        } else {
            resp.sendRedirect("profile?error=server");
        }
    }

    private void handleDeleteAccount(HttpServletRequest req, HttpServletResponse resp,
                                     HttpSession session, int userId) throws IOException {

        String confirmPassword = req.getParameter("confirmPassword");

        if (isBlank(confirmPassword)) {
            resp.sendRedirect("profile?error=delmissing");
            return;
        }

        String storedHash = userDAO.getPasswordHash(userId);
        if (storedHash == null || !BCrypt.checkpw(confirmPassword, storedHash)) {
            resp.sendRedirect("profile?error=delwrong");
            return;
        }

        boolean ok = userDAO.deleteUser(userId);
        if (ok) {
            session.invalidate();
            resp.sendRedirect("login.jsp?deleted=1");
        } else {
            resp.sendRedirect("profile?error=server");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
