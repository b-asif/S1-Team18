<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.myapp.model.User" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Users List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
        }

        h2 {
            text-align: center;
        }

        table {
            border-collapse: collapse;
            width: 70%;
            margin: 20px auto;
        }

        th, td {
            border: 1px solid #333;
            padding: 10px;
            text-align: center;
        }

        th {
            background-color: #f2f2f2;
        }

        a {
            text-decoration: none;
            color: blue;
        }
    </style>
</head>

<body>

<h2>Users</h2>

<table>
    <tr>
        <th>Username</th>
        <th>Name</th>
        <th>ID</th>
        <th>Email</th>
        <th>Action</th>
    </tr>

<%
    List<User> users = (List<User>) request.getAttribute("users");

    if (users != null) {
        for (User user : users) {
%>

    <tr>
        <td><%= user.getuserName() %></td>
        <td><%= user.getName() %></td>
        <td><%= user.getId() %></td>
        <td><%= user.getEmail() %></td>
        <td>
            <a href="users?editId=<%= user.getId() %>">Edit</a>
        </td>
    </tr>

<%
        }
    }
%>

</table>

</body>
</html>