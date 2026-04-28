<%@ page import="java.util.List" %>
<%@ page import="com.myapp.model.Application" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String firstName = (String) session.getAttribute("firstName");
    if (firstName == null || firstName.trim().isEmpty()) firstName = "User";

    List<Application> apps = (List<Application>) request.getAttribute("applications");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Applications - TrackHire</title>
    <link rel="stylesheet" href="css/dashboardPage.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>

<div class="app-layout">

    <aside class="sidebar">
        <div class="sidebar-header">
            <a href="dashboard.jsp" class="sidebar-logo">
                <div class="sidebar-logo-icon">
                    <svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3 8l3.5 3.5L13 5" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </div>
                <span class="sidebar-logo-text">TrackHire</span>
            </a>
        </div>

        <nav class="sidebar-nav">
            <a href="dashboard.jsp" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M9 22V12h6v10"/></svg>
                Dashboard
            </a>
            <a href="applications" class="nav-item active">
                <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                Applications
            </a>
            <a href="interviews.jsp" class="nav-item">
                <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M8 2v4m8-4v4M3 10h18"/></svg>
                Interviews
            </a>
            <a href="assessments.jsp" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 12h6m-6 4h4"/></svg>
                Assessments
            </a>
            <a href="statistics.jsp" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M18 20V10M12 20V4M6 20v-4"/></svg>
                Statistics
            </a>
        </nav>

        <div class="sidebar-footer">
            <a href="profile.jsp" class="nav-item">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
                Profile
            </a>
            <a href="logout" class="btn-logout" style="text-decoration:none;display:inline-flex;align-items:center;justify-content:center;">
                <svg viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                Log Out
            </a>
        </div>
    </aside>

    <main class="main-content">
        <div class="page-header">
            <div class="page-title">
                <h1>Applications</h1>
                <p>Track and manage all your job applications.</p>
            </div>
            <div class="header-actions">
                <button class="btn btn-primary" onclick="document.getElementById('addModal').classList.add('open')">
                    <svg viewBox="0 0 24 24" style="width:14px;height:14px;stroke:white;fill:none;stroke-width:2.5;stroke-linecap:round;">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Add Application
                </button>
            </div>
        </div>

        <div class="page-body">

            <% if ("missing".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">Please fill in all required fields.</div>
            <% } else if ("server".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">A server error occurred. Please try again.</div>
            <% } %>

            <div class="section">
                <div class="filter-row" id="filterRow">
                    <button class="filter-pill active" data-filter="all">All</button>
                    <button class="filter-pill" data-filter="Applied">Applied</button>
                    <button class="filter-pill" data-filter="Interviewing">Interviewing</button>
                    <button class="filter-pill" data-filter="Offer">Offer</button>
                    <button class="filter-pill" data-filter="Rejected">Rejected</button>
                </div>
                <div class="table-wrap">
                    <table class="data-table" id="appTable">
                        <thead>
                            <tr>
                                <th>Job Title</th>
                                <th>Company</th>
                                <th>Status</th>
                                <th>Date Applied</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (apps != null && !apps.isEmpty()) {
                                    for (Application app : apps) {
                                        String statusClass = "status-" + app.getAppStatus().toLowerCase();
                            %>
                            <tr data-status="<%= app.getAppStatus() %>">
                                <td class="td-truncate"><%= app.getJobTitle() %></td>
                                <td class="td-truncate td-muted"><%= app.getCompanyName() %></td>
                                <td>
                                    <span class="status-badge <%= statusClass %>"><%= app.getAppStatus() %></span>
                                </td>
                                <td class="td-muted"><%= app.getDateApplied() %></td>
                                <td>
                                    <div class="actions-cell">
                                        <form method="post" action="applications" style="display:inline;"
                                              onsubmit="return confirm('Delete this application?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                            <button type="submit" class="action-btn delete">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr id="emptyRow">
                                <td colspan="5">
                                    <div class="empty-state">
                                        <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                                        <p>No applications yet</p>
                                        <span>Click "Add Application" to get started.</span>
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

<!-- Add Application Modal -->
<div class="modal-overlay" id="addModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Add Application</h3>
            <button class="modal-close" onclick="document.getElementById('addModal').classList.remove('open')">
                <svg viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <form method="post" action="applications">
            <div class="modal-body">
                <div class="form-stack">
                    <div class="field-group">
                        <label>Job Title <span class="req">*</span></label>
                        <input type="text" name="jobTitle" placeholder="e.g. Software Engineer" required>
                    </div>
                    <div class="field-group">
                        <label>Company Name <span class="req">*</span></label>
                        <input type="text" name="companyName" placeholder="e.g. Acme Corp" required>
                    </div>
                    <div class="field-row">
                        <div class="field-group">
                            <label>Status</label>
                            <select name="appStatus">
                                <option value="Applied">Applied</option>
                                <option value="Interviewing">Interviewing</option>
                                <option value="Offer">Offer</option>
                                <option value="Rejected">Rejected</option>
                            </select>
                        </div>
                        <div class="field-group">
                            <label>Date Applied <span class="req">*</span></label>
                            <input type="date" name="dateApplied" required>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        onclick="document.getElementById('addModal').classList.remove('open')">Cancel</button>
                <button type="submit" class="btn btn-primary">Add Application</button>
            </div>
        </form>
    </div>
</div>

<script>
// Filter pills
document.querySelectorAll('.filter-pill').forEach(pill => {
    pill.addEventListener('click', function() {
        document.querySelectorAll('.filter-pill').forEach(p => p.classList.remove('active'));
        this.classList.add('active');
        const filter = this.dataset.filter;
        document.querySelectorAll('#appTable tbody tr[data-status]').forEach(row => {
            row.style.display = (filter === 'all' || row.dataset.status === filter) ? '' : 'none';
        });
    });
});

// Close modal on overlay click
document.getElementById('addModal').addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
});
</script>
</body>
</html>
