<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My first JSP</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<main>
    <h1>My first JSP</h1>
    <%-- This is plain HTML... --%>
    <p>This page is HTML with a little Java inside.</p>

    <%-- ...and this is Java. The server runs it and puts the RESULT in the page. --%>
    <p class="msg ok">The time on the server is <b><%= java.time.LocalTime.now().withNano(0) %></b>.</p>

    <p>Press F5: the time changes, because the page is built again for every request.
       Right-click → View Page Source: the browser only sees the result, never the Java.</p>
    <p><a href="./">← back to all demos</a></p>
</main>
</body>
</html>
