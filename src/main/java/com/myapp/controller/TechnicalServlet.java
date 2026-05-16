package com.myapp.controller;

import com.myapp.dao.TechnicalDAO;
import com.myapp.model.Technical;
import com.myapp.util.CsrfUtil;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/technicals")
public class TechnicalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(TechnicalServlet.class.getName());

    private final TechnicalDAO technicalDAO = new TechnicalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        List<Technical> technicals = technicalDAO.getAssessmentsByUser(userId);
        request.setAttribute("technicals", technicals);

        request.getRequestDispatcher("/technicals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(request)) {
            response.sendRedirect("technicals?error=csrf");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        // DELETE
        if ("delete".equals(action)) {
            String idParam = request.getParameter("assessmentId");

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int assessmentId = Integer.parseInt(idParam);
                    int userId = (int) session.getAttribute("userId");
                    technicalDAO.deleteAssessment(assessmentId, userId);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Rejected invalid assessmentId during delete: {0}", idParam);
                }
            }

            response.sendRedirect("technicals");
            return;
        }

        // ADD (default)
        int userId = (int) session.getAttribute("userId");

        String title = request.getParameter("assessmentTitle");
        String assignedDateStr = request.getParameter("assignedDate");
        String dueDateStr = request.getParameter("dueDate");
        String notes = request.getParameter("assessmentNotes");
        String status = request.getParameter("completionStatus");
        String result = request.getParameter("scoreOrPassFail");

        // Validation
        if (isBlank(title) || isBlank(assignedDateStr)) {
            response.sendRedirect("technicals?error=missing");
            return;
        }

        try {
            Date assignedDate = Date.valueOf(assignedDateStr);
            Date dueDate = null;

            if (!isBlank(dueDateStr)) {
                dueDate = Date.valueOf(dueDateStr);
            }

            technicalDAO.addAssessment(
                    userId,
                    title.trim(),
                    assignedDate,
                    dueDate,
                    notes,
                    status,
                    result
            );

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add assessment.", e);
            response.sendRedirect("technicals?error=server");
            return;
        }

        response.sendRedirect("technicals");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}