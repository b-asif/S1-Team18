package com.myapp.controller;

import com.myapp.dao.ApplicationDAO;
import com.myapp.dao.TagDAO;
import com.myapp.model.Application;
import com.myapp.util.CsrfUtil;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/applications")
public class ApplicationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ApplicationServlet.class.getName());

    private static final Set<String> ALLOWED_STATUS = new HashSet<>(Arrays.asList(
            "Applied", "Interviewing", "Offer", "Rejected", "Withdrawn"));

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final TagDAO tagDAO = new TagDAO();

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
        String status = request.getParameter("status");
        List<Application> apps = applicationDAO.getApplicationsForUser(userId, q, status);
        for (Application app : apps) {
            app.getTags().clear();
            for (String tag : tagDAO.getTagNamesForApplication(app.getApplicationId(), userId)) {
                app.addTag(tag);
            }
        }
        request.setAttribute("applications", apps);
        request.setAttribute("searchQuery", q);
        request.setAttribute("statusFilter", status);
        request.getRequestDispatcher("/applications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValidToken(request)) {
            response.sendRedirect("applications?error=csrf");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("addTag".equals(action)) {
            handleAddTag(request, response, userId);
            return;
        }
        if ("removeTag".equals(action)) {
            handleRemoveTag(request, response, userId);
            return;
        }

        if ("delete".equals(action)) {
            String idParam = request.getParameter("applicationId");
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int appId = Integer.parseInt(idParam);
                    applicationDAO.deleteApplication(appId, userId);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Rejected invalid applicationId during delete: {0}", idParam);
                }
            }
            response.sendRedirect(buildApplicationsRedirect(request, null));
            return;
        }

        if ("update".equals(action)) {
            handleUpdate(request, response, userId);
            return;
        }

        // add new application
        String jobTitle = request.getParameter("jobTitle");
        String companyName = request.getParameter("companyName");
        String appStatus = normalizeStatus(request.getParameter("appStatus"));
        String dateStr = request.getParameter("dateApplied");
        String jobUrl = clamp(request.getParameter("jobUrl"), 512);
        String jobLocation = clamp(request.getParameter("jobLocation"), 255);
        String notes = clamp(request.getParameter("notes"), 4000);

        if (isBlank(jobTitle) || isBlank(companyName) || isBlank(dateStr)) {
            response.sendRedirect(buildApplicationsRedirect(request, "missing"));
            return;
        }

        try {
            Date dateApplied = Date.valueOf(dateStr);
            boolean ok = applicationDAO.addApplication(
                    userId,
                    clamp(jobTitle, 255),
                    clamp(companyName, 255),
                    appStatus,
                    dateApplied,
                    emptyToNull(jobUrl),
                    emptyToNull(jobLocation),
                    emptyToNull(notes));
            if (!ok) {
                response.sendRedirect(buildApplicationsRedirect(request, "server"));
                return;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add application.", e);
            response.sendRedirect(buildApplicationsRedirect(request, "server"));
            return;
        }

        response.sendRedirect(buildApplicationsRedirect(request, null));
    }

    private void handleAddTag(HttpServletRequest request, HttpServletResponse response, int userId)
            throws IOException {
        String idParam = request.getParameter("applicationId");
        String tagName = request.getParameter("tagName");
        if (idParam == null || isBlank(tagName)) {
            response.sendRedirect(buildApplicationsRedirect(request, null));
            return;
        }
        try {
            int appId = Integer.parseInt(idParam);
            int tagId = tagDAO.getOrCreateTagId(userId, tagName);
            if (tagId > 0) {
                tagDAO.linkTagToApplication(appId, tagId, userId);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid applicationId for tag.");
        }
        response.sendRedirect(buildApplicationsRedirect(request, null));
    }

    private void handleRemoveTag(HttpServletRequest request, HttpServletResponse response, int userId)
            throws IOException {
        String idParam = request.getParameter("applicationId");
        String tagName = request.getParameter("tagName");
        if (idParam == null || isBlank(tagName)) {
            response.sendRedirect(buildApplicationsRedirect(request, null));
            return;
        }
        try {
            int appId = Integer.parseInt(idParam);
            tagDAO.unlinkTagFromApplication(appId, tagName.trim(), userId);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid applicationId for tag removal.");
        }
        response.sendRedirect(buildApplicationsRedirect(request, null));
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, int userId)
            throws IOException {
        String idParam = request.getParameter("applicationId");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(buildApplicationsRedirect(request, "missing"));
            return;
        }
        String jobTitle = request.getParameter("jobTitle");
        String companyName = request.getParameter("companyName");
        String appStatus = normalizeStatus(request.getParameter("appStatus"));
        String dateStr = request.getParameter("dateApplied");
        String jobUrl = clamp(request.getParameter("jobUrl"), 512);
        String jobLocation = clamp(request.getParameter("jobLocation"), 255);
        String notes = clamp(request.getParameter("notes"), 4000);

        if (isBlank(jobTitle) || isBlank(companyName) || isBlank(dateStr)) {
            response.sendRedirect(buildApplicationsRedirect(request, "missing"));
            return;
        }

        try {
            int appId = Integer.parseInt(idParam);
            Date dateApplied = Date.valueOf(dateStr);
            boolean ok = applicationDAO.updateApplication(
                    appId,
                    userId,
                    clamp(jobTitle, 255),
                    clamp(companyName, 255),
                    appStatus,
                    dateApplied,
                    emptyToNull(jobUrl),
                    emptyToNull(jobLocation),
                    emptyToNull(notes));
            if (!ok) {
                response.sendRedirect(buildApplicationsRedirect(request, "server"));
                return;
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid applicationId for update.");
            response.sendRedirect(buildApplicationsRedirect(request, null));
            return;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update application.", e);
            response.sendRedirect(buildApplicationsRedirect(request, "server"));
            return;
        }

        response.sendRedirect(buildApplicationsRedirect(request, null));
    }

    private static String buildApplicationsRedirect(HttpServletRequest request, String errorKey) {
        String q = request.getParameter("q");
        String status = request.getParameter("status");
        StringBuilder b = new StringBuilder("applications");
        boolean hasQ = false;
        if (errorKey != null && !errorKey.isEmpty()) {
            b.append("?error=").append(urlEncode(errorKey));
            hasQ = true;
        }
        if (q != null && !q.trim().isEmpty()) {
            b.append(hasQ ? '&' : '?').append("q=").append(urlEncode(q.trim()));
            hasQ = true;
        }
        if (status != null && !status.trim().isEmpty()) {
            b.append(hasQ ? '&' : '?').append("status=").append(urlEncode(status.trim()));
        }
        return b.toString();
    }

    private static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException e) {
            return s;
        }
    }

    private static String normalizeStatus(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "Applied";
        }
        String s = raw.trim();
        return ALLOWED_STATUS.contains(s) ? s : "Applied";
    }

    private static String clamp(String value, int maxLen) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        if (t.length() > maxLen) {
            return t.substring(0, maxLen);
        }
        return t;
    }

    private static String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
