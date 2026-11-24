<%-- THE OLD WAY ("Model 1"): ONE page does EVERYTHING.
     It reads the form, checks it, does the maths, decides what to show, AND builds the HTML.
     It works, but look how the jobs are tangled together. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String input = request.getParameter("celsius");
    String error = null;
    String result = null;
    if (input != null) {
        try {
            double c = Double.parseDouble(input.trim());          // checking...
            if (c < -273.15) {
                error = "Nothing is colder than -273.15 °C.";
            } else {
                double f = c * 9 / 5 + 32;                          // ...the rules (the maths)...
                String feel = c < 15 ? "cold 🥶" : c < 30 ? "warm 🙂" : "hot 🥵";
                result = c + " °C = " + Math.round(f * 10) / 10.0 + " °F, " + feel;
            }
        } catch (NumberFormatException e) {
            error = "Please type a number.";
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Without MVC</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header><a class="brand" href="./">🧩 MVC demos</a></header>
<main>
    <h1>Converter: everything in one JSP</h1>
    <form class="card" action="no-mvc.jsp">
        <label>Temperature in °C <input name="celsius" value="<%= input == null ? "" : input.replace("\"", "&quot;").replace("<", "&lt;") %>"></label>
        <button class="btn">Convert</button>
    </form>
    <% if (error != null) { %><p class="msg error"><%= error %></p><% } %>       <%-- ...and the HTML --%>
    <% if (result != null) { %><p class="msg ok"><%= result %></p><% } %>
    <p class="note">Every job is in this one file. A designer who wants to change the colours must read Java,
       and the maths can't be reused by any other page.</p>
    <p><a href="./">← back</a></p>
</main>
</body>
</html>
