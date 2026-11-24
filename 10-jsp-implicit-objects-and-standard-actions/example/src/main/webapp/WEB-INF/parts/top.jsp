<%-- Included with <jsp:include>. It receives the page title as a <jsp:param> named "title". --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.example.Person" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= Person.escape(request.getParameter("title")) %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<header><a class="brand" href="<%= request.getContextPath() %>/">🧰 Implicit objects &amp; actions</a></header>
<main>
    <h1><%= Person.escape(request.getParameter("title")) %></h1>
