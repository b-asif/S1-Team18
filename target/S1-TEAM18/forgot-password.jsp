<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Step is driven by whether the servlet placed a resetUserId in session
    boolean isStep2 = (session.getAttribute("resetUserId") != null)
                   || "2".equals(request.getParameter("step"));

    String errorParam = request.getParameter("error");
    String errorMsg   = "";

    if ("missing".equals(errorParam))   errorMsg = "Please fill in all fields.";
    else if ("notfound".equals(errorParam)) errorMsg = "No account found with that email or username.";
    else if ("mismatch".equals(errorParam)) errorMsg = "Passwords do not match.";
    else if ("tooshort".equals(errorParam)) errorMsg = "Password must be at least 6 characters.";
    else if ("session".equals(errorParam))  errorMsg = "Your session expired. Please start again.";
    else if ("server".equals(errorParam))   errorMsg = "A server error occurred. Please try again.";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - TrackHire</title>
    <link rel="stylesheet" href="css/loginPage.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>

<div class="page-wrapper">

    <!-- Left branding panel -->
    <div class="brand-panel">
        <div class="brand-content">
            <div class="logo">
                <svg class="logo-icon" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect width="32" height="32" rx="8" fill="white" fill-opacity="0.15"/>
                    <path d="M8 16l5 5 11-10" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="logo-text">TrackHire</span>
            </div>

            <div class="brand-headline">
                <% if (!isStep2) { %>
                    <h1>Forgot your<br>password?</h1>
                    <p>No worries. Enter your email or username and we'll get you back in.</p>
                <% } else { %>
                    <h1>Choose a new<br>password.</h1>
                    <p>Pick something strong — at least 6 characters.</p>
                <% } %>
            </div>

            <div class="brand-stats">
                <div class="stat">
                    <span class="stat-number">10k+</span>
                    <span class="stat-label">Jobs tracked</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat">
                    <span class="stat-number">4.8★</span>
                    <span class="stat-label">User rating</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat">
                    <span class="stat-number">2k+</span>
                    <span class="stat-label">Offers received</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Right form panel -->
    <div class="form-panel">
        <div class="form-container">

            <% if (!isStep2) { %>
            <!-- ── Step 1: look up account ── -->
            <div class="form-header">
                <h2>Reset Password</h2>
                <p>Enter the email or username associated with your account.</p>
            </div>

            <% if (!errorMsg.isEmpty()) { %>
            <div class="error-banner visible" style="margin-bottom:20px;"><%= errorMsg %></div>
            <% } %>

            <form class="login-form" action="forgot-password" method="post">
                <input type="hidden" name="action" value="lookup">

                <div class="field-group">
                    <label for="identifier">Email or Username</label>
                    <input type="text" id="identifier" name="identifier"
                           placeholder="you@example.com or your username"
                           autocomplete="username" autofocus>
                </div>

                <button type="submit" class="btn-primary">Find My Account</button>
            </form>

            <div class="form-footer">
                <p>Remembered it? <a href="login.jsp" class="link">Back to sign in</a></p>
            </div>

            <% } else { %>
            <!-- ── Step 2: set new password ── -->
            <div class="form-header">
                <h2>New Password</h2>
                <p>Choose a new password for your account.</p>
            </div>

            <% if (!errorMsg.isEmpty()) { %>
            <div class="error-banner visible" style="margin-bottom:20px;"><%= errorMsg %></div>
            <% } %>

            <form class="login-form" action="forgot-password" method="post" id="resetForm">
                <input type="hidden" name="action" value="reset">

                <div class="field-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword"
                           placeholder="At least 6 characters" autofocus>
                </div>

                <div class="field-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword"
                           placeholder="Repeat your new password">
                    <span id="matchError" style="display:none;font-size:12px;color:#dc2626;margin-top:2px;">
                        Passwords do not match.
                    </span>
                </div>

                <button type="button" class="btn-primary" onclick="submitReset()">Set New Password</button>
            </form>

            <div class="form-footer">
                <p><a href="forgot-password" class="link">← Start over</a></p>
            </div>
            <% } %>

        </div>
    </div>

</div>

<script>
function submitReset() {
    var pw  = document.getElementById('newPassword').value;
    var cpw = document.getElementById('confirmPassword').value;
    var err = document.getElementById('matchError');

    if (pw !== cpw) {
        err.style.display = 'block';
        return;
    }
    err.style.display = 'none';
    document.getElementById('resetForm').submit();
}
</script>

</body>
</html>
