<%-- V = VIEW: only SHOWS what the controller prepared. No checking, no maths, no decisions about data. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.example.Temperature" %>
<%
    Temperature t = (Temperature) request.getAttribute("temperature");
    String error = (String) request.getAttribute("error");
    String input = (String) request.getAttribute("input");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>With MVC</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header><a class="brand" href="./">🧩 MVC demos</a></header>
<main>
    <h1>Converter: Model, View, Controller</h1>
    <form class="card" action="convert">
        <label>Temperature in °C <input name="celsius" value="<%= input.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;") %>"></label>
        <button class="btn">Convert</button>
    </form>
    <% if (error != null) { %><p class="msg error"><%= error %></p><% } %>
    <% if (t != null) { %>
        <p class="msg ok"><%= t.getCelsius() %> °C = <%= t.getFahrenheit() %> °F, <%= t.getFeeling() %></p>
    <% } %>
    <div class="table-wrap"><table>
        <tr><th>Model</th><td><code>Temperature.java</code>: the maths and the rules</td></tr>
        <tr><th>View</th><td><code>WEB-INF/views/convert.jsp</code>: this page, only HTML and a few getters</td></tr>
        <tr><th>Controller</th><td><code>ConvertController.java</code>: reads the form, asks the model, picks this view</td></tr>
    </table></div>
    <p>Try opening <code>/WEB-INF/views/convert.jsp</code> directly: 404. Users must go through the controller.</p>
    <p><a href="./">← back</a></p>
</main>
</body>
</html>
