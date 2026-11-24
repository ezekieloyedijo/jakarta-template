<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Async + dispatch</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<header><a class="brand" href="<%= request.getContextPath() %>/">⏳ Async demos</a></header>
<main>
    <h1>Async + dispatch</h1>
    <p class="msg ok"><%= request.getAttribute("result") %></p>
    <p>A worker thread finished the job at <b><%= request.getAttribute("finishedAt") %></b> and called
       <code>dispatch("/WEB-INF/views/result.jsp")</code>. This JSP built the page.</p>
    <p><a href="<%= request.getContextPath() %>/">← back to all demos</a></p>
</main>
</body>
</html>
