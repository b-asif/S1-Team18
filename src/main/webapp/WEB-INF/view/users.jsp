<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.myapp.model.User" %>
<%@ page import="com.myapp.util.HtmlUtil" %>
<%@ page import="com.myapp.util.CsrfUtil" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
        response.sendRedirect("dashboard");
        return;
    }

    String csrfToken = CsrfUtil.getOrCreateToken(session);
    User editUser = (User) request.getAttribute("editUser");
    String err = request.getParameter("error");
    String ok = request.getParameter("success");

    @SuppressWarnings("unchecked")
    List<User> usersList = (List<User>) request.getAttribute("users");
    int userCount = usersList == null ? 0 : usersList.size();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users (admin) - TrackHire</title>
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
            <a href="technicals" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 12h6m-6 4h4"/></svg>
                Assessments
            </a>
            <a href="statistics" class="nav-item">
                <svg viewBox="0 0 24 24"><path d="M18 20V10M12 20V4M6 20v-4"/></svg>
                Statistics
            </a>
            <a href="users" class="nav-item active">
                <svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Users
            </a>
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
                <h1>User directory</h1>
                <p>Admin-only maintenance. Update member profiles; regular users cannot access this page.</p>
            </div>
        </div>

        <div class="page-body">

            <% if ("1".equals(ok)) { %>
            <div class="error-banner visible" style="background:#ecfdf5;border-color:#10b981;color:#065f46;margin-bottom:20px;">User updated successfully.</div>
            <% } %>
            <% if ("csrf".equals(err)) { %>
            <div class="error-banner visible" style="margin-bottom:20px;">Session validation failed.</div>
            <% } else if ("missing".equals(err)) { %>
            <div class="error-banner visible" style="margin-bottom:20px;">All fields are required.</div>
            <% } else if ("exists".equals(err)) { %>
            <div class="error-banner visible" style="margin-bottom:20px;">Email or username already in use.</div>
            <% } else if ("server".equals(err)) { %>
            <div class="error-banner visible" style="margin-bottom:20px;">Server error.</div>
            <% } %>

            <% if (editUser != null) { %>
            <div class="section">
                <div class="section-header">
                    <h2>Edit user #<%= editUser.getId() %></h2>
                    <p>Save changes to name, email, and username.</p>
                </div>
                <div style="padding:18px;">
                    <form method="post" action="users" class="form-stack" style="max-width:420px;">
                        <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="userId" value="<%= editUser.getId() %>">
                        <label>First name
                            <input type="text" name="firstName" required maxlength="100" value="<%= HtmlUtil.escape(editUser.getName()) %>">
                        </label>
                        <label>Last name
                            <input type="text" name="lastName" required maxlength="100" value="<%= HtmlUtil.escape(editUser.getLastName()) %>">
                        </label>
                        <label>Email
                            <input type="email" name="email" required maxlength="255" value="<%= HtmlUtil.escape(editUser.getEmail()) %>">
                        </label>
                        <label>Username
                            <input type="text" name="userName" required maxlength="100" value="<%= HtmlUtil.escape(editUser.getUserName()) %>">
                        </label>
                        <div style="display:flex;align-items:center;gap:10px;margin-top:8px;flex-wrap:wrap;">
                            <button type="submit" class="btn btn-primary">Save changes</button>
                            <a href="users" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
            <% } %>

            <div class="section">
                <div class="section-header">
                    <h2>All users</h2>
                    <p><%= userCount %> registered <%= userCount == 1 ? "account" : "accounts" %></p>
                </div>
                <div class="table-wrap" style="padding:0 18px 18px;">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Admin</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            if (usersList != null) {
                                for (User user : usersList) {
                        %>
                        <tr>
                            <td><%= user.getId() %></td>
                            <td><%= HtmlUtil.escape(user.getUserName()) %></td>
                            <td><%= HtmlUtil.escape(user.getName()) %></td>
                            <td><%= HtmlUtil.escape(user.getEmail()) %></td>
                            <td><%= user.isAdmin() ? "Yes" : "—" %></td>
                            <td><a href="users?editId=<%= user.getId() %>">Edit</a></td>
                        </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

</body>
</html>
