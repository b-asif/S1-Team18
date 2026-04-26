package com.myapp.controller;

import com.myapp.dao.ApplicationDAO;
import com.myapp.model.Application;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/applications")
public class ApplicationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        List<Application> apps = applicationDAO.getApplicationsByUser(userId);
        request.setAttribute("applications", apps);
        request.getRequestDispatcher("/applications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String idParam = request.getParameter("applicationId");
            if (idParam != null && !idParam.isEmpty()) {
                int appId = Integer.parseInt(idParam);
                int userId = (int) session.getAttribute("userId");
                applicationDAO.deleteApplication(appId, userId);
            }
            response.sendRedirect("applications");
            return;
        }

        // Default: add new application
        int userId = (int) session.getAttribute("userId");
        String jobTitle = request.getParameter("jobTitle");
        String companyName = request.getParameter("companyName");
        String appStatus = request.getParameter("appStatus");
        String dateStr = request.getParameter("dateApplied");

        if (isBlank(jobTitle) || isBlank(companyName) || isBlank(dateStr)) {
            response.sendRedirect("applications?error=missing");
            return;
        }

        try {
            Date dateApplied = Date.valueOf(dateStr);
            applicationDAO.addApplication(userId, jobTitle.trim(), companyName.trim(),
                                          appStatus, dateApplied);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("applications?error=server");
            return;
        }

        response.sendRedirect("applications");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
