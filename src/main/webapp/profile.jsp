<%@ page import="com.myapp.model.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User profileUser = (User) request.getAttribute("profileUser");
    if (profileUser == null) {
        response.sendRedirect("profile");
        return;
    }

    String firstName = profileUser.getName()     != null ? profileUser.getName()     : "";
    String lastName  = profileUser.getLastName() != null ? profileUser.getLastName() : "";
    String email     = profileUser.getEmail()    != null ? profileUser.getEmail()    : "";
    String userName  = profileUser.getUserName() != null ? profileUser.getUserName() : "";

    // Initials for avatar
    String initials = "";
    if (!firstName.isEmpty()) initials += firstName.charAt(0);
    if (!lastName.isEmpty())  initials += lastName.charAt(0);
    if (initials.isEmpty())   initials = "U";
    initials = initials.toUpperCase();

    String successMsg = "";
    String errorMsg   = "";
    String successParam = request.getParameter("success");
    String errorParam   = request.getParameter("error");

    if ("info".equals(successParam))         successMsg = "Account information updated successfully.";
    else if ("password".equals(successParam)) successMsg = "Password changed successfully.";

    if ("missing".equals(errorParam))     errorMsg = "All fields are required.";
    else if ("exists".equals(errorParam)) errorMsg = "That email or username is already taken.";
    else if ("pwmissing".equals(errorParam)) errorMsg = "Please fill in all password fields.";
    else if ("pwmatch".equals(errorParam))   errorMsg = "New passwords do not match.";
    else if ("pwshort".equals(errorParam))   errorMsg = "New password must be at least 6 characters.";
    else if ("pwwrong".equals(errorParam))   errorMsg = "Current password is incorrect.";
    else if ("delmissing".equals(errorParam)) errorMsg = "Please enter your password to confirm deletion.";
    else if ("delwrong".equals(errorParam))   errorMsg = "Incorrect password. Account not deleted.";
    else if ("server".equals(errorParam))    errorMsg = "A server error occurred. Please try again.";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile - TrackHire</title>
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
            <a href="profile" class="nav-item active">
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
                <h1>Profile Settings</h1>
                <p>Manage your account information and security.</p>
            </div>
        </div>

        <div class="page-body">
            <div class="profile-grid">

                <!-- Left: avatar card -->
                <div class="profile-card">
                    <div class="avatar"><%= initials %></div>
                    <h3><%= firstName %> <%= lastName %></h3>
                    <p><%= email %></p>
                    <p style="margin-top:4px;font-size:12px;color:#94a3b8;">@<%= userName %></p>
                </div>

                <!-- Right: settings sections -->
                <div class="profile-main">

                    <!-- Account Information -->
                    <div class="settings-section">
                        <div class="settings-section-header">
                            <h3>Account Information</h3>
                            <p>Update your name, email address, and username.</p>
                        </div>
                        <div class="settings-section-body">
                            <form action="profile" method="post" class="form-stack" id="infoForm">
                                <input type="hidden" name="action" value="updateInfo">
                                <div class="field-row">
                                    <div class="field-group">
                                        <label for="firstName">First Name <span class="req">*</span></label>
                                        <input type="text" id="firstName" name="firstName"
                                               value="<%= firstName %>" required>
                                    </div>
                                    <div class="field-group">
                                        <label for="lastName">Last Name <span class="req">*</span></label>
                                        <input type="text" id="lastName" name="lastName"
                                               value="<%= lastName %>" required>
                                    </div>
                                </div>
                                <div class="field-group">
                                    <label for="email">Email Address <span class="req">*</span></label>
                                    <input type="email" id="email" name="email"
                                           value="<%= email %>" required>
                                </div>
                                <div class="field-group">
                                    <label for="userName">Username <span class="req">*</span></label>
                                    <input type="text" id="userName" name="userName"
                                           value="<%= userName %>" required>
                                </div>
                                <div style="display:flex;justify-content:flex-end;margin-top:4px;">
                                    <button type="submit" class="btn btn-primary">Save Changes</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Change Password -->
                    <div class="settings-section">
                        <div class="settings-section-header">
                            <h3>Change Password</h3>
                            <p>Choose a strong password of at least 6 characters.</p>
                        </div>
                        <div class="settings-section-body">
                            <form action="profile" method="post" class="form-stack" id="pwForm">
                                <input type="hidden" name="action" value="updatePassword">
                                <div class="field-group">
                                    <label for="currentPassword">Current Password <span class="req">*</span></label>
                                    <input type="password" id="currentPassword" name="currentPassword"
                                           placeholder="Enter your current password" required>
                                </div>
                                <div class="field-row">
                                    <div class="field-group">
                                        <label for="newPassword">New Password <span class="req">*</span></label>
                                        <input type="password" id="newPassword" name="newPassword"
                                               placeholder="At least 6 characters" required>
                                    </div>
                                    <div class="field-group">
                                        <label for="confirmPassword">Confirm Password <span class="req">*</span></label>
                                        <input type="password" id="confirmPassword" name="confirmPassword"
                                               placeholder="Repeat new password" required>
                                    </div>
                                </div>
                                <div style="display:flex;justify-content:flex-end;margin-top:4px;">
                                    <button type="submit" class="btn btn-primary">Update Password</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Danger Zone -->
                    <div class="settings-section danger-zone">
                        <div class="settings-section-header">
                            <h3>Danger Zone</h3>
                            <p>Permanent actions that cannot be undone.</p>
                        </div>
                        <div class="settings-section-body">
                            <div class="danger-item">
                                <div class="danger-item-info">
                                    <strong>Delete Account</strong>
                                    <span>Permanently remove your account and all associated data.</span>
                                </div>
                                <button type="button" class="btn btn-danger btn-sm"
                                        onclick="document.getElementById('deleteModal').classList.add('open')">
                                    Delete Account
                                </button>
                            </div>
                        </div>
                    </div>

                </div><!-- /profile-main -->
            </div><!-- /profile-grid -->
        </div><!-- /page-body -->
    </main>

</div><!-- /app-layout -->

<!-- Delete account confirmation modal -->
<div class="modal-overlay" id="deleteModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Delete Account</h3>
            <button class="modal-close" onclick="document.getElementById('deleteModal').classList.remove('open')">
                <svg viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <div class="modal-body">
            <p class="confirm-text">
                This will <strong>permanently delete</strong> your account and all your data.
                This action <strong>cannot be undone</strong>.
            </p>
            <p class="confirm-text" style="margin-bottom:16px;">
                Enter your password to confirm:
            </p>
            <form action="profile" method="post" id="deleteForm" class="form-stack">
                <input type="hidden" name="action" value="deleteAccount">
                <div class="field-group">
                    <label for="delPassword">Password <span class="req">*</span></label>
                    <input type="password" id="delPassword" name="confirmPassword"
                           placeholder="Enter your current password" required>
                    <span class="field-error" id="delError"></span>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary"
                    onclick="document.getElementById('deleteModal').classList.remove('open')">
                Cancel
            </button>
            <button type="button" class="btn btn-danger" onclick="submitDelete()">
                Delete My Account
            </button>
        </div>
    </div>
</div>

<!-- Toast notification -->
<div class="toast" id="toast"></div>

<script>
function submitDelete() {
    var pw = document.getElementById('delPassword').value.trim();
    var err = document.getElementById('delError');
    if (!pw) {
        err.textContent = 'Password is required.';
        err.classList.add('visible');
        return;
    }
    err.classList.remove('visible');
    document.getElementById('deleteForm').submit();
}

// Close modal on backdrop click
document.getElementById('deleteModal').addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
});

(function () {
    var success = "<%= successMsg %>";
    var error   = "<%= errorMsg %>";
    var toast   = document.getElementById("toast");

    function showToast(msg, isError) {
        toast.textContent = msg;
        toast.className = "toast" + (isError ? " toast-error" : "");
        toast.offsetHeight;
        toast.classList.add("show");
        setTimeout(function () { toast.classList.remove("show"); }, 3500);
    }

    if (success) {
        showToast(success, false);
    } else if (error) {
        showToast(error, true);
    }
})();
</script>

</body>
</html>
