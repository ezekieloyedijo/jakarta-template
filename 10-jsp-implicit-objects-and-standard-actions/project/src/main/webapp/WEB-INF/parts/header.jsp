<%-- Included at request time with <jsp:include>. The title arrives as <jsp:param name="title" .../> --%>
<%@ page import="com.school.project.ContactBook" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= ContactBook.escape(request.getParameter("title")) %> | Contact Book</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .avatar { display: inline-grid; place-items: center; width: 42px; height: 42px; border-radius: 50%;
                  background: var(--brand); color: #fff; font-weight: 700; margin-bottom: 8px; }
    </style>
</head>
<body>
<header>
    <a class="brand" href="<%= request.getContextPath() %>/">📇 Contact Book</a>
    <nav>
        <a href="<%= request.getContextPath() %>/">All contacts</a>
        <a href="<%= request.getContextPath() %>/add.jsp">Add contact</a>
    </nav>
</header>
<main>
    <h1><%= ContactBook.escape(request.getParameter("title")) %></h1>
