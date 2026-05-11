<%@ page import="java.util.List" %>
<%@ page import="com.myapp.model.Application" %>
<%@ page import="com.myapp.util.CsrfUtil" %>
<%@ page import="com.myapp.util.HtmlUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String firstName = (String) session.getAttribute("firstName");
    if (firstName == null || firstName.trim().isEmpty()) firstName = "User";

    @SuppressWarnings("unchecked")
    List<Application> apps = (List<Application>) request.getAttribute("applications");
    String searchQ = (String) request.getAttribute("searchQuery");
    String filterStatus = (String) request.getAttribute("statusFilter");
    if (searchQ == null) searchQ = "";
    String error = request.getParameter("error");
    String csrfToken = CsrfUtil.getOrCreateToken(session);
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
            <a href="applications" class="nav-item active">
                <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                Applications
            </a>
            <a href="interviews" class="nav-item">
                <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M8 2v4m8-4v4M3 10h18"/></svg>
                Interviews
            </a>
            <a href="technicals" class="nav-item">
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
                <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
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
            <% } else if ("csrf".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">Session validation failed. Please try again.</div>
            <% } else if ("server".equals(error)) { %>
            <div class="error-banner visible" style="margin-bottom:16px;">A server error occurred. Please try again.</div>
            <% } %>

            <div class="section">
                <form method="get" action="applications" class="filter-row" style="flex-wrap:wrap;align-items:center;gap:10px;margin-bottom:12px;">
                    <input type="search" name="q" value="<%= HtmlUtil.escape(searchQ) %>" placeholder="Search title, company, location, notes…" style="min-width:220px;padding:8px;border-radius:6px;border:1px solid #d1d5db;">
                    <select name="status" style="padding:8px;border-radius:6px;">
                        <option value="all" <%= (filterStatus == null || filterStatus.isEmpty() || "all".equalsIgnoreCase(filterStatus)) ? "selected" : "" %>>All statuses</option>
                        <option value="Applied" <%= "Applied".equals(filterStatus) ? "selected" : "" %>>Applied</option>
                        <option value="Interviewing" <%= "Interviewing".equals(filterStatus) ? "selected" : "" %>>Interviewing</option>
                        <option value="Offer" <%= "Offer".equals(filterStatus) ? "selected" : "" %>>Offer</option>
                        <option value="Rejected" <%= "Rejected".equals(filterStatus) ? "selected" : "" %>>Rejected</option>
                        <option value="Withdrawn" <%= "Withdrawn".equals(filterStatus) ? "selected" : "" %>>Withdrawn</option>
                    </select>
                    <button type="submit" class="btn btn-primary" style="padding:8px 14px;">Search</button>
                    <a href="applications" class="btn btn-secondary" style="padding:8px 14px;text-decoration:none;display:inline-flex;align-items:center;">Clear</a>
                </form>

                <div class="filter-row" id="filterRow" style="margin-bottom:8px;">
                    <span class="td-muted" style="margin-right:8px;">Quick filter:</span>
                    <button type="button" class="filter-pill active" data-filter="all">All</button>
                    <button type="button" class="filter-pill" data-filter="Applied">Applied</button>
                    <button type="button" class="filter-pill" data-filter="Interviewing">Interviewing</button>
                    <button type="button" class="filter-pill" data-filter="Offer">Offer</button>
                    <button type="button" class="filter-pill" data-filter="Rejected">Rejected</button>
                    <button type="button" class="filter-pill" data-filter="Withdrawn">Withdrawn</button>
                </div>

                <div class="table-wrap">
                    <table class="data-table" id="appTable">
                        <thead>
                            <tr>
                                <th>Job Title</th>
                                <th>Company</th>
                                <th>Location</th>
                                <th>Status</th>
                                <th>Date Applied</th>
                                <th>Tags</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (apps != null && !apps.isEmpty()) {
                                    for (Application app : apps) {
                                        String rawStatus = app.getAppStatus() == null ? "" : app.getAppStatus();
                                        String statusClass = "status-" + rawStatus.toLowerCase().replaceAll("[^a-z0-9\\-]", "");
                            %>
                            <tr data-status="<%= HtmlUtil.escape(rawStatus) %>">
                                <td class="td-truncate">
                                    <%= HtmlUtil.escape(app.getJobTitle()) %>
                                    <% if (app.getJobUrl() != null && !app.getJobUrl().isEmpty()) { %>
                                    <br><a href="<%= HtmlUtil.escape(app.getJobUrl()) %>" target="_blank" rel="noopener noreferrer" style="font-size:12px;">Posting link</a>
                                    <% } %>
                                </td>
                                <td class="td-truncate td-muted"><%= HtmlUtil.escape(app.getCompanyName()) %></td>
                                <td class="td-muted"><%= HtmlUtil.escape(app.getJobLocation()) %></td>
                                <td>
                                    <span class="status-badge <%= statusClass %>"><%= HtmlUtil.escape(rawStatus) %></span>
                                </td>
                                <td class="td-muted"><%= app.getDateApplied() %></td>
                                <td style="max-width:180px;">
                                    <% for (String tag : app.getTags()) { %>
                                    <span class="status-badge" style="margin:2px;display:inline-block;"><%= HtmlUtil.escape(tag) %></span>
                                    <% } %>
                                    <form method="post" action="applications" style="margin-top:6px;display:flex;gap:4px;flex-wrap:wrap;">
                                        <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                                        <input type="hidden" name="action" value="addTag">
                                        <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                        <input type="hidden" name="q" value="<%= HtmlUtil.escape(searchQ) %>">
                                        <input type="hidden" name="status" value="<%= HtmlUtil.escape(filterStatus != null ? filterStatus : "") %>">
                                        <input type="text" name="tagName" placeholder="Add tag" maxlength="100" style="max-width:100px;padding:4px;font-size:12px;">
                                        <button type="submit" class="action-btn" style="font-size:12px;padding:4px 8px;">+</button>
                                    </form>
                                    <% for (String tag : app.getTags()) { %>
                                    <form method="post" action="applications" style="display:inline;">
                                        <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                                        <input type="hidden" name="action" value="removeTag">
                                        <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                        <input type="hidden" name="tagName" value="<%= HtmlUtil.escape(tag) %>">
                                        <input type="hidden" name="q" value="<%= HtmlUtil.escape(searchQ) %>">
                                        <input type="hidden" name="status" value="<%= HtmlUtil.escape(filterStatus != null ? filterStatus : "") %>">
                                        <button type="submit" class="action-btn delete" style="font-size:11px;padding:2px 6px;margin:1px;" title="Remove tag">× <%= HtmlUtil.escape(tag) %></button>
                                    </form>
                                    <% } %>
                                </td>
                                <td>
                                    <div class="actions-cell" style="display:flex;flex-direction:column;gap:6px;">
                                        <button type="button" class="action-btn"
                                                onclick="openEditApplication(<%= app.getApplicationId() %>, '<%= HtmlUtil.escapeJsString(app.getJobTitle()) %>', '<%= HtmlUtil.escapeJsString(app.getCompanyName()) %>', '<%= HtmlUtil.escapeJsString(rawStatus) %>', '<%= app.getDateApplied() %>', '<%= HtmlUtil.escapeJsString(app.getJobUrl() != null ? app.getJobUrl() : "") %>', '<%= HtmlUtil.escapeJsString(app.getJobLocation() != null ? app.getJobLocation() : "") %>', '<%= HtmlUtil.escapeJsString(app.getNotes() != null ? app.getNotes() : "") %>')">Edit</button>
                                        <form method="post" action="applications" style="display:inline;"
                                              onsubmit="return confirm('Delete this application?');">
                                            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                                            <input type="hidden" name="q" value="<%= HtmlUtil.escape(searchQ) %>">
                                            <input type="hidden" name="status" value="<%= HtmlUtil.escape(filterStatus != null ? filterStatus : "") %>">
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
                                <td colspan="7">
                                    <div class="empty-state">
                                        <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                                        <p>No applications match</p>
                                        <span>Try clearing search or add a new application.</span>
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
            <button type="button" class="modal-close" onclick="document.getElementById('addModal').classList.remove('open')">
                <svg viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <form method="post" action="applications">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
            <input type="hidden" name="q" value="<%= HtmlUtil.escape(searchQ) %>">
            <input type="hidden" name="status" value="<%= HtmlUtil.escape(filterStatus != null ? filterStatus : "") %>">
            <div class="modal-body">
                <div class="form-stack">
                    <div class="field-group">
                        <label>Job Title <span class="req">*</span></label>
                        <input type="text" name="jobTitle" placeholder="e.g. Software Engineer" required maxlength="255">
                    </div>
                    <div class="field-group">
                        <label>Company Name <span class="req">*</span></label>
                        <input type="text" name="companyName" placeholder="e.g. Acme Corp" required maxlength="255">
                    </div>
                    <div class="field-group">
                        <label>Job URL</label>
                        <input type="url" name="jobUrl" placeholder="https://…" maxlength="512">
                    </div>
                    <div class="field-group">
                        <label>Location</label>
                        <input type="text" name="jobLocation" placeholder="City or remote" maxlength="255">
                    </div>
                    <div class="field-group">
                        <label>Notes</label>
                        <textarea name="notes" placeholder="Notes" maxlength="4000" rows="3"></textarea>
                    </div>
                    <div class="field-row">
                        <div class="field-group">
                            <label>Status</label>
                            <select name="appStatus">
                                <option value="Applied">Applied</option>
                                <option value="Interviewing">Interviewing</option>
                                <option value="Offer">Offer</option>
                                <option value="Rejected">Rejected</option>
                                <option value="Withdrawn">Withdrawn</option>
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

<!-- Edit Application Modal -->
<div class="modal-overlay" id="editModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Edit Application</h3>
            <button type="button" class="modal-close" onclick="document.getElementById('editModal').classList.remove('open')">×</button>
        </div>
        <form method="post" action="applications">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="applicationId" id="editApplicationId" value="">
            <input type="hidden" name="q" value="<%= HtmlUtil.escape(searchQ) %>">
            <input type="hidden" name="status" value="<%= HtmlUtil.escape(filterStatus != null ? filterStatus : "") %>">
            <div class="modal-body">
                <div class="form-stack">
                    <div class="field-group">
                        <label>Job Title <span class="req">*</span></label>
                        <input type="text" name="jobTitle" id="editJobTitle" required maxlength="255">
                    </div>
                    <div class="field-group">
                        <label>Company <span class="req">*</span></label>
                        <input type="text" name="companyName" id="editCompanyName" required maxlength="255">
                    </div>
                    <div class="field-group">
                        <label>Job URL</label>
                        <input type="url" name="jobUrl" id="editJobUrl" maxlength="512">
                    </div>
                    <div class="field-group">
                        <label>Location</label>
                        <input type="text" name="jobLocation" id="editJobLocation" maxlength="255">
                    </div>
                    <div class="field-group">
                        <label>Notes</label>
                        <textarea name="notes" id="editNotes" maxlength="4000" rows="3"></textarea>
                    </div>
                    <div class="field-row">
                        <div class="field-group">
                            <label>Status</label>
                            <select name="appStatus" id="editAppStatus">
                                <option value="Applied">Applied</option>
                                <option value="Interviewing">Interviewing</option>
                                <option value="Offer">Offer</option>
                                <option value="Rejected">Rejected</option>
                                <option value="Withdrawn">Withdrawn</option>
                            </select>
                        </div>
                        <div class="field-group">
                            <label>Date Applied <span class="req">*</span></label>
                            <input type="date" name="dateApplied" id="editDateApplied" required>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="document.getElementById('editModal').classList.remove('open')">Cancel</button>
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </form>
    </div>
</div>

<script>
function openEditApplication(id, title, company, status, dateApplied, url, loc, notes) {
    document.getElementById('editApplicationId').value = id;
    document.getElementById('editJobTitle').value = title;
    document.getElementById('editCompanyName').value = company;
    document.getElementById('editAppStatus').value = status;
    document.getElementById('editDateApplied').value = dateApplied;
    document.getElementById('editJobUrl').value = url || '';
    document.getElementById('editJobLocation').value = loc || '';
    document.getElementById('editNotes').value = notes || '';
    document.getElementById('editModal').classList.add('open');
}
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
document.getElementById('addModal').addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
});
document.getElementById('editModal').addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
});
</script>
</body>
</html>
