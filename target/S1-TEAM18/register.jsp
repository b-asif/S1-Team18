<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account - TrackHire</title>
    <link rel="stylesheet" href="css/registerPage.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>

<body>
    <%
        String regError = request.getParameter("error");
    %>

    <div class="page-wrapper">

        <div class="brand-panel">
            <div class="brand-content">
                <div class="logo">
                    <svg class="logo-icon" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <rect width="32" height="32" rx="8" fill="white" fill-opacity="0.15" />
                        <path d="M8 16l5 5 11-10" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
                    </svg>
                    <span class="logo-text">TrackHire</span>
                </div>

                <div class="brand-headline">
                    <h1>Start your job search<br>the smart way.</h1>
                    <p>Organize every application, follow up on time, and never lose track of an opportunity again.</p>
                </div>

                <ul class="feature-list">
                    <li>Track applications across every stage</li>
                    <li>Set reminders for interviews and follow-ups</li>
                    <li>Free to use - no credit card required</li>
                </ul>
            </div>
        </div>

        <div class="form-panel">
            <div class="form-container">
                <div class="form-header">
                    <h2>Create your account</h2>
                    <p>Join thousands of job seekers already using TrackHire.</p>
                </div>

                <% if ("missing".equals(regError)) { %>
                    <div class="error-banner visible">Please fill in all fields.</div>
                <% } else if ("exists".equals(regError)) { %>
                    <div class="error-banner visible">Email or username already exists.</div>
                <% } else if ("password".equals(regError)) { %>
                    <div class="error-banner visible">Password must be at least 6 characters.</div>
                <% } else if ("server".equals(regError)) { %>
                    <div class="error-banner visible">Server error. Please try again.</div>
                <% } %>

                <form class="register-form" action="register" method="post" novalidate>
                    <div class="field-row">
                        <div class="field-group">
                            <label for="firstName">First name</label>
                            <input id="firstName" name="firstName" type="text" placeholder="Jane" autocomplete="given-name">
                            <span class="field-error" id="firstNameError"></span>
                        </div>

                        <div class="field-group">
                            <label for="lastName">Last name</label>
                            <input id="lastName" name="lastName" type="text" placeholder="Smith" autocomplete="family-name">
                            <span class="field-error" id="lastNameError"></span>
                        </div>
                    </div>

                    <div class="field-group">
                        <label for="email">Email address</label>
                        <input id="email" name="email" type="email" placeholder="you@example.com" autocomplete="email">
                        <span class="field-error" id="emailError"></span>
                    </div>

                    <div class="field-group">
                        <label for="username">Username</label>
                        <input id="username" name="userName" type="text" placeholder="Choose a username" autocomplete="username">
                        <span class="field-error" id="usernameError"></span>
                    </div>

                    <div class="field-group">
                        <label for="password">Password</label>
                        <input id="password" name="password" type="password" placeholder="Create a password (min. 6 characters)" autocomplete="new-password">
                        <span class="field-error" id="passwordError"></span>
                    </div>

                    <button type="submit" class="btn-primary">Create account</button>
                </form>

                <div class="form-footer">
                    <p>Already have an account? <a href="login.jsp" class="link">Sign in</a></p>
                </div>
            </div>
        </div>

    </div>

    <script src="js/register.js"></script>
</body>
</html>