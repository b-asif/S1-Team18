package com.myapp.controller;

import com.myapp.dao.UserDAO;
import com.myapp.model.User;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String editId = request.getParameter("editId");

        // EDIT MODE
        if (editId != null) {
            try {
                int id = Integer.parseInt(editId);

                User user = userDAO.getUserById(id);

                // User not found? Safe redirect
                if (user == null) {
                    response.sendRedirect("users");
                    return;
                }

                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/editUser.jsp")
                        .forward(request, response);

            } catch (NumberFormatException e) {
                // Invalid ID? Safe fallback
                response.sendRedirect("users");
            }

            return;
        }

        // LIST MODE
        List<User> users = userDAO.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/users.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get and sanitize input
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");

        // Basic validation (prevents bad DB updates)
        if (idStr == null || name == null || email == null || username == null ||
                name.isBlank() || email.isBlank() || username.isBlank()) {

            response.sendRedirect("users");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            User user = new User();
            user.setId(id);
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setUsername(username.trim());

            userDAO.updateUser(user);

            // PRG pattern (Prevents form resubmission)
            response.sendRedirect("users?success=1");

        } catch (NumberFormatException e) {
            response.sendRedirect("users");
        }
    }
}