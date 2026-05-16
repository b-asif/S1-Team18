package com.myapp.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import com.myapp.dao.UserDAO;
import com.myapp.util.CsrfUtil;
import com.myapp.util.ResetTokenUtil;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ForgotPasswordServlet.class.getName());
    private static final long RESET_TOKEN_MINUTES = 15;

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!CsrfUtil.isValidToken(req)) {
            resp.sendRedirect("forgot-password?error=csrf");
            return;
        }

        String action = req.getParameter("action");

        if ("lookup".equals(action)) {
            handleLookup(req, resp);
        } else if ("reset".equals(action)) {
            handleReset(req, resp);
        } else {
            resp.sendRedirect("forgot-password");
        }
    }

    private void handleLookup(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String identifier = req.getParameter("identifier");
        if (identifier == null || identifier.trim().isEmpty()) {
            resp.sendRedirect("forgot-password?error=missing");
            return;
        }

        int userId = userDAO.getUserIdByIdentifier(identifier.trim());
        if (userId != -1) {
            String plainToken = ResetTokenUtil.generatePlainToken();
            String tokenHash = ResetTokenUtil.sha256(plainToken);
            Timestamp expiresAt = Timestamp.from(Instant.now().plus(RESET_TOKEN_MINUTES, ChronoUnit.MINUTES));

            if (userDAO.storePasswordResetToken(userId, tokenHash, expiresAt)) {
                LOGGER.log(Level.INFO,
                        "Password reset token issued for userId={0}. Token (dev only): {1}",
                        new Object[]{userId, plainToken});
            }
        }

        // Generic success response to avoid account enumeration.
        resp.sendRedirect("forgot-password?sent=1");
    }

    private void handleReset(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String token = req.getParameter("resetToken");
        if (isBlank(token)) {
            resp.sendRedirect("forgot-password?step=2&error=session");
            return;
        }

        String tokenHash = ResetTokenUtil.sha256(token.trim());
        int resetUserId = userDAO.getUserIdByValidResetTokenHash(tokenHash);
        if (resetUserId == -1) {
            resp.sendRedirect("forgot-password?step=2&error=session");
            return;
        }

        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isBlank(newPassword) || isBlank(confirmPassword)) {
            resp.sendRedirect("forgot-password?step=2&error=missing");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            resp.sendRedirect("forgot-password?step=2&error=mismatch");
            return;
        }

        if (newPassword.length() < 8) {
            resp.sendRedirect("forgot-password?step=2&error=tooshort");
            return;
        }

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean ok = userDAO.setPasswordHash(resetUserId, newHash);
        boolean consumed = userDAO.consumePasswordResetToken(tokenHash);

        if (ok && consumed) {
            resp.sendRedirect("login.jsp?reset=1");
        } else {
            resp.sendRedirect("forgot-password?error=server");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
