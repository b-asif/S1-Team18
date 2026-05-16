<%@ page import="java.util.List" %>
<%@ page import="com.myapp.model.Technical" %>
<%@ page import="com.myapp.util.CsrfUtil" %>
<%@ page import="com.myapp.util.HtmlUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String firstName = (String) session.getAttribute("firstName");
    if (firstName == null || firstName.trim().isEmpty()) {
        firstName = "User";
    }

    @SuppressWarnings("unchecked")
    List<Technical> technicals = (List<Technical>) request.getAttribute("technicals");
    String error = request.getParameter("error");
    String csrfToken = CsrfUtil.getOrCreateToken(session);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Technicals - TrackHire</title>
    <link rel="stylesheet" href="css/dashboardPage.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>

<div class="app-layout">

    <aside class="sidebar">
        <div class="sidebar-header">
            <a href="dashboard" class="sidebar-logo">
                <div class="sidebar-logo-icon">
                    <svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3 8l3.5 3.5L13 5" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </div>
                <span class="sidebar-logo-text">TrackHire</span>
            </a>
        </div>

        <nav class="sidebar-nav">
            <a href="dashboard" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M9 22V12h6v10"/></svg>
                Dashboard
            </a>
            <a href="applications" class="nav-item">
                <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                Applications
            </a>
            <a href="interviews" class="nav-item">
                <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M8 2v4m8-4v4M3 10h18"/></svg>
                Interviews
            </a>
            <a href="technicals" class="nav-item active">
                <svg viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 12h6m-6 4h4"/></svg>
                Assessments
            </a>
            <a href="statistics" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M18 20V10M12 20V4M6 20v-4"/></svg>
                Statistics
            </a>
            <% if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) { %>
            <a href="users" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Users
            </a>
            <% } %>
        </nav>

        <div class="sidebar-footer">
            <a href="profile" class="nav-item">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
                Profile
            </a>
            <form action="logout" method="post" style="margin:0;">
                <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
                <button type="submit" class="btn-logout" style="display:inline-flex;align-items:center;justify-content:center;width:100%;background:none;border:none;">
                <svg viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                Log Out
                </button>
            </form>
        </div>
    </aside>

    <main class="main-content">
        <div class="page-header">
            <div class="page-title">
                <h1>Technicals</h1>
                <p>Track and manage your technical assessments.</p>
            </div>
            <div class="header-actions">
                <button class="btn btn-primary" onclick="document.getElementById('addModal').classList.add('open')">
                    Add Technical
                </button>
            </div>
        </div>

        <div class="page-body">

            <% if ("missing".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">Please fill in all required fields.</div>
            <% } else if ("csrf".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">Session validation failed. Please try again.</div>
            <% } else if ("server".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">A server error occurred.</div>
            <% } %>

            <div class="section">
                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Assessment</th>
                                <th>Assigned</th>
                                <th>Due Date</th>
                                <th>Status</th>
                                <th>Score / Result</th>
                                <th>Notes</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (technicals != null && !technicals.isEmpty()) {
                                    for (Technical t : technicals) {
                            %>
                            <tr>
                                <td class="td-truncate"><%= HtmlUtil.escape(t.getAssessmentTitle()) %></td>
                                <td class="td-muted"><%= t.getAssignedDate() != null ? t.getAssignedDate().toString() : "" %></td>
                                <td class="td-muted"><%= t.getDueDate() != null ? t.getDueDate().toString() : "—" %></td>
                                <td><%= HtmlUtil.escape(t.getCompletionStatus()) %></td>
                                <td><%= HtmlUtil.escape(t.getScoreOrPassFail()) %></td>
                                <td class="td-truncate td-muted"><%= HtmlUtil.escape(t.getAssessmentNotes()) %></td>
                                <td>
                                    <div class="actions-cell">
                                        <form method="post" action="technicals"
                                              onsubmit="return confirm('Delete this technical assessment?');">
                                            <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="assessmentId" value="<%= t.getAssessmentId() %>">
                                            <button type="submit" class="action-btn delete">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7">
                                    <div class="empty-state">
                                        <p>No technical assessments added</p>
                                        <span>Click "Add Technical" to get started.</span>
                                    </div>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<div class="modal-overlay" id="addModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Add Technical Assessment</h3>
            <button type="button" class="modal-close" onclick="document.getElementById('addModal').classList.remove('open')">X</button>
        </div>
        <form method="post" action="technicals">
            <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
            <div class="modal-body">
                <div class="form-stack">
                    <input type="text" name="assessmentTitle" placeholder="Assessment Title" required maxlength="255">
                    <input type="date" name="assignedDate" required>
                    <input type="date" name="dueDate">

                    <select name="completionStatus">
                        <option value="Pending">Pending</option>
                        <option value="In Progress">In Progress</option>
                        <option value="Completed">Completed</option>
                    </select>

                    <input type="text" name="scoreOrPassFail" placeholder="Score / Pass / Fail" maxlength="100">
                    <textarea name="assessmentNotes" placeholder="Assessment Notes" maxlength="4000"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        onclick="document.getElementById('addModal').classList.remove('open')">Cancel</button>
                <button type="submit" class="btn btn-primary">Add Technical</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
