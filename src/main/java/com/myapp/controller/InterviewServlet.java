package com.myapp.controller;

import com.myapp.dao.InterviewDAO;
import com.myapp.model.Interview;
import com.myapp.util.CsrfUtil;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/interviews")
public class InterviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(InterviewServlet.class.getName());

    private final InterviewDAO interviewDAO = new InterviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String q = request.getParameter("q");

        List<Interview> interviews = interviewDAO.getInterviewsForUser(userId, q);
        request.setAttribute("interviews", interviews);
        request.setAttribute("searchQuery", q);

        request.getRequestDispatcher("/interviews.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(request)) {
            response.sendRedirect(appendInterviewQuery(request, "interviews?error=csrf"));
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
            String idParam = request.getParameter("interviewId");

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int interviewId = Integer.parseInt(idParam);
                    int userId = (int) session.getAttribute("userId");
                    interviewDAO.deleteInterview(interviewId, userId);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Rejected invalid interviewId during delete: {0}", idParam);
                }
            }

            response.sendRedirect(appendInterviewQuery(request, "interviews"));
            return;
        }

        // ADD (default)
        int userId = (int) session.getAttribute("userId");

        String roleTitle = request.getParameter("roleTitle");
        String departmentName = request.getParameter("departmentName");
        String interviewerName = request.getParameter("interviewerName");
        String interviewType = request.getParameter("interviewType");
        String dateStr = request.getParameter("interviewDate");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String location = request.getParameter("location");
        String notes = request.getParameter("notes");

        // Validation
        if (isBlank(roleTitle) || isBlank(dateStr) || isBlank(startTimeStr)) {
            response.sendRedirect(appendInterviewQuery(request, "interviews?error=missing"));
            return;
        }

        try {
            Date interviewDate = Date.valueOf(dateStr);
            Time startTime = Time.valueOf(startTimeStr + ":00"); // expects HH:MM
            Time endTime = null;

            if (!isBlank(endTimeStr)) {
                endTime = Time.valueOf(endTimeStr + ":00");
            }

            interviewDAO.addInterview(
                    userId,
                    roleTitle.trim(),
                    departmentName,
                    interviewerName,
                    interviewType,
                    interviewDate,
                    startTime,
                    endTime,
                    location,
                    notes
            );

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add interview.", e);
            response.sendRedirect(appendInterviewQuery(request, "interviews?error=server"));
            return;
        }

        response.sendRedirect(appendInterviewQuery(request, "interviews"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String appendInterviewQuery(HttpServletRequest request, String base) {
        String q = request.getParameter("q");
        if (q == null || q.trim().isEmpty()) {
            return base;
        }
        try {
            String enc = java.net.URLEncoder.encode(q.trim(), java.nio.charset.StandardCharsets.UTF_8.name());
            return base + (base.contains("?") ? '&' : '?') + "q=" + enc;
        } catch (java.io.UnsupportedEncodingException e) {
            return base;
        }
    }
}