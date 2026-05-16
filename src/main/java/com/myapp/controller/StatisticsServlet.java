package com.myapp.controller;

import com.myapp.dao.ApplicationDAO;
import com.myapp.dao.InterviewDAO;
import com.myapp.dao.TechnicalDAO;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * JDBC-backed summary counts for the statistics page (FR 13).
 * Detailed breakdown is populated here; {@link DashboardServlet} uses similar DAOs for the home page.
 */
@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final InterviewDAO interviewDAO = new InterviewDAO();
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

        Map<String, Long> statusCounts = applicationDAO.countApplicationsByStatusForUser(userId);
        long upcomingInterviews = interviewDAO.countInterviewsInNextDays(userId, 30);
        long interviewsNext48h = interviewDAO.countInterviewsInNextDays(userId, 2);
        long pendingTechnicals = technicalDAO.countIncompleteAssessments(userId);
        long overdueTechnicals = technicalDAO.countOverdueAssessments(userId);

        request.setAttribute("statusCounts", statusCounts);
        request.setAttribute("totalApplications", applicationDAO.countApplicationsForUser(userId));
        request.setAttribute("upcomingInterviews", upcomingInterviews);
        request.setAttribute("interviewsNext48h", interviewsNext48h);
        request.setAttribute("pendingTechnicals", pendingTechnicals);
        request.setAttribute("overdueTechnicals", overdueTechnicals);

        request.getRequestDispatcher("/statistics.jsp").forward(request, response);
    }
}
