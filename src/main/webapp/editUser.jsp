<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.myapp.model.User" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>

    <style>
        body {
            font-family: Arial, sans-serif;
        }

        .container {
            width: 400px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
        }

        h2 {
            text-align: center;
        }

        label {
            display: block;
            margin-top: 10px;
        }

        input {
            width: 100%;
            padding: 8px;
            margin-top: 5px;
        }

        button {
            margin-top: 15px;
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        a {
            display: block;
            text-align: center;
            margin-top: 10px;
            text-decoration: none;
            color: blue;
        }
    </style>
</head>

<body>

<%
    User user = (User) request.getAttribute("user");
%>

<div class="container">

    <h2>Edit User</h2>

    <form action="users" method="post">

        <input type="hidden" name="id" value="<%= user.getId() %>" />

        <label>Name</label>
        <input type="text" name="name" value="<%= user.getName() %>" required />

        <label>Email</label>
        <input type="email" name="email" value="<%= user.getEmail() %>" required />

        <label>Username</label>
        <input type="text" name="username" value="<%= user.getUsername() %>" required />

        <button type="submit">Update</button>

    </form>

    <a href="users">Back to Users</a>

</div>

</body>
</html>