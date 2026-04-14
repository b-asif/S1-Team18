<%@ page import="java.util.List" %>
<%@ page import="com.myapp.model.Application" %>
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
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - TrackHire</title>
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
            <a href="dashboard.jsp" class="nav-item active">
                <svg viewBox="0 0 24 24">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <path d="M9 22V12h6v10"/>
                </svg>
                Dashboard
            </a>

            <a href="applications.jsp" class="nav-item">
                <svg viewBox="0 0 24 24">
                    <rect x="2" y="7" width="20" height="14" rx="2"/>
                    <path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
                </svg>
                Applications
            </a>

            <a href="interviews.jsp" class="nav-item">
                <svg viewBox="0 0 24 24">
                    <rect x="3" y="4" width="18" height="18" rx="2"/>
                    <path d="M8 2v4m8-4v4M3 10h18"/>
                </svg>
                Interviews
            </a>

            <a href="assessments.jsp" class="nav-item">
                <svg viewBox="0 0 24 24">
                    <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
                    <rect x="9" y="3" width="6" height="4" rx="1"/>
                    <path d="M9 12h6m-6 4h4"/>
                </svg>
                Assessments
            </a>

            <a href="statistics.jsp" class="nav-item">
                <svg viewBox="0 0 24 24">
                    <path d="M18 20V10M12 20V4M6 20v-4"/>
                </svg>
                Statistics
            </a>
        </nav>

        <div class="sidebar-footer">
            <a href="profile.jsp" class="nav-item">
                <svg viewBox="0 0 24 24">
                    <circle cx="12" cy="8" r="4"/>
                    <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/>
                </svg>
                Profile
            </a>

            <a href="logout" class="btn-logout" style="text-decoration:none;display:inline-flex;align-items:center;justify-content:center;">
                <svg viewBox="0 0 24 24">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                    <polyline points="16 17 21 12 16 7"/>
                    <line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                Log Out
            </a>
        </div>
    </aside>

    <main class="main-content">
        <div class="page-header">
            <div class="page-title">
                <h1 id="greetingText">Welcome, <%= firstName %></h1>
                <p>Here's what's happening with your job search.</p>
            </div>
        </div>

        <div class="page-body">

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon blue">
                        <svg viewBox="0 0 24 24">
                            <rect x="2" y="7" width="20" height="14" rx="2"/>
                            <path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
                        </svg>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value" id="statTotal">0</span>
                        <span class="stat-label">Total Applications</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon purple">
                        <svg viewBox="0 0 24 24">
                            <rect x="3" y="4" width="18" height="18" rx="2"/>
                            <path d="M8 2v4m8-4v4M3 10h18"/>
                        </svg>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value" id="statInterviews">0</span>
                        <span class="stat-label">Upcoming Interviews</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon green">
                        <svg viewBox="0 0 24 24">
                            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                            <polyline points="22 4 12 14.01 9 11.01"/>
                        </svg>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value" id="statOffers">0</span>
                        <span class="stat-label">Offers Received</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon amber">
                        <svg viewBox="0 0 24 24">
                            <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
                            <rect x="9" y="3" width="6" height="4" rx="1"/>
                        </svg>
                    </div>
                    <div class="stat-info">
                        <span class="stat-value" id="statAssessments">0</span>
                        <span class="stat-label">Assessments Due</span>
                    </div>
                </div>
            </div>

            <div class="dash-row">

                <div class="section" style="margin-bottom: 24px;">
                    <div class="section-header">
                        <h2>Add New Application</h2>
                    </div>
                    <form action="applications" method="post" style="display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
                        <input type="text" name="jobTitle" placeholder="Job Title" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px; flex: 1;">
                        <input type="text" name="companyName" placeholder="Company Name" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px; flex: 1;">
                        <select name="appStatus" style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                            <option value="Applied">Applied</option>
                            <option value="Interviewing">Interviewing</option>
                            <option value="Rejected">Rejected</option>
                            <option value="Offer">Offer</option>
                        </select>
                        <input type="date" name="dateApplied" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        <button type="submit" style="padding: 8px 16px; background-color: #2563eb; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 500;">Add</button>
                    </form>
                </div>
                
                <div class="section">
                    <div class="section-header">
                        <h2>Recent Applications</h2>
                        <a href="applications.jsp" style="font-size:13px;color:#2563eb;text-decoration:none;font-weight:500;">View all →</a>
                    </div>
                    <div class="table-wrap">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Job Title</th>
                                    <th>Company</th>
                                    <th>Status</th>
                                    <th>Date Applied</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    List<Application> apps = (List<Application>) request.getAttribute("applications");
                                    if (apps != null && !apps.isEmpty()) {
                                        for (Application app : apps) {
                                %>
                                    <tr>
                                        <td><%= app.getJobTitle() %></td>
                                        <td><%= app.getCompanyName() %></td>
                                        <td><%= app.getAppStatus() %></td>
                                        <td><%= app.getDateApplied() %></td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="4" style="text-align: center; padding: 20px; color: #6b7280;">No applications tracked yet. Add one above!</td>
                                    </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="section">
                    <div class="section-header">
                        <h2>
                            Alerts
                            <span class="badge-count" id="alertCount" style="display:none;">0</span>
                        </h2>
                        <p>Next 48 hours</p>
                    </div>
                    <div class="alerts-list" id="alertsList"></div>
                </div>

            </div>

        </div>
    </main>

</div>

<script src="js/dashboard.js"></script>
</body>
</html>