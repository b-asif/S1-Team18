package com.myapp.controller;

import com.myapp.dao.UserDAO;
import com.myapp.model.User;
import com.myapp.util.CsrfUtil;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            response.sendRedirect("dashboard");
            return;
        }

        String editId = request.getParameter("editId");
        if (editId != null && !editId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(editId.trim());
                User editUser = userDAO.getUserById(id);
                if (editUser != null) {
                    request.setAttribute("editUser", editUser);
                }
            } catch (NumberFormatException ignored) {
                // ignore invalid editId
            }
        }

        request.setAttribute("users", userDAO.getAllUsers());
        request.getRequestDispatcher("/WEB-INF/view/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!CsrfUtil.isValidToken(request)) {
            response.sendRedirect("users?error=csrf");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            response.sendRedirect("dashboard");
            return;
        }

        String action = request.getParameter("action");
        if (!"update".equals(action)) {
            response.sendRedirect("users");
            return;
        }

        String idParam = request.getParameter("userId");
        String firstName = trim(request.getParameter("firstName"));
        String lastName = trim(request.getParameter("lastName"));
        String email = trim(request.getParameter("email"));
        String userName = trim(request.getParameter("userName"));

        if (idParam == null || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || userName.isEmpty()) {
            response.sendRedirect("users?error=missing");
            return;
        }

        try {
            int targetUserId = Integer.parseInt(idParam);
            String result = userDAO.updateInfo(targetUserId, firstName, lastName, email, userName);
            if (result == null) {
                int selfId = (int) session.getAttribute("userId");
                if (targetUserId == selfId) {
                    session.setAttribute("firstName", firstName);
                    session.setAttribute("email", email);
                    session.setAttribute("username", userName);
                }
                response.sendRedirect("users?success=1");
            } else if ("exists".equals(result)) {
                response.sendRedirect("users?error=exists&editId=" + targetUserId);
            } else {
                response.sendRedirect("users?error=server");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("users");
        }
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
