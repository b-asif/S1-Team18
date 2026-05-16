package com.myapp.controller;

import com.myapp.dao.ApplicationDAO;
import com.myapp.dao.InterviewDAO;
import com.myapp.dao.TechnicalDAO;
import com.myapp.model.Application;
import com.myapp.model.Interview;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
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
        List<Application> apps = applicationDAO.getApplicationsByUser(userId);
        List<Interview> alertInterviews = interviewDAO.getInterviewsInNextDays(userId, 2, 50);

        request.setAttribute("applications", apps);
        request.setAttribute("statTotal", applicationDAO.countApplicationsForUser(userId));
        request.setAttribute("statInterviews", interviewDAO.countInterviewsInNextDays(userId, 30));
        request.setAttribute("statOffers", applicationDAO.countApplicationsWithStatus(userId, "Offer"));
        request.setAttribute("statAssessments", technicalDAO.countIncompleteAssessments(userId));
        request.setAttribute("alertInterviews", alertInterviews);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
