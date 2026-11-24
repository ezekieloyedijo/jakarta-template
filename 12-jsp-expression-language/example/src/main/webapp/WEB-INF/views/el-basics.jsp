<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>EL basics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header><a class="brand" href="${pageContext.request.contextPath}/">💲 EL &amp; JSTL demos</a></header>
<main>
    <h1>EL basics: beans, lists, maps</h1>
    <p>No scriptlets, no casts, no <code>request.getAttribute</code>. Compare with topic 11's views.</p>

    <div class="table-wrap"><table>
        <thead><tr><th>You write</th><th>The page shows</th></tr></thead>
        <tbody>
        <tr><td><code>\${pupil.name}</code> (calls <code>getName()</code>)</td><td>${pupil.name}</td></tr>
        <tr><td><code>\${pupil.age + 1}</code></td><td>${pupil.age + 1}</td></tr>
        <tr><td><code>\${pupil.prefect}</code> (calls <code>isPrefect()</code>)</td><td>${pupil.prefect}</td></tr>
        <tr><td><code>\${subjects[0]}</code> (a list)</td><td>${subjects[0]}</td></tr>
        <tr><td><code>\${scores['English']}</code> or <code>\${scores.English}</code> (a map)</td><td>${scores['English']}</td></tr>
        <tr><td><code>\${classmates[1].name}</code></td><td>${classmates[1].name}</td></tr>
        <tr><td><code>\${nothingHere}</code> (doesn't exist)</td><td>"${nothingHere}" ← empty, no error</td></tr>
        </tbody>
    </table></div>

    <h2>Every classmate, with <code>&lt;c:forEach&gt;</code></h2>
    <ul class="demos">
        <c:forEach var="p" items="${classmates}">
            <li><c:out value="${p.name}"/>, ${p.age}, ${p.className}<c:if test="${p.prefect}"> ⭐ prefect</c:if></li>
        </c:forEach>
    </ul>
    <p><a href="${pageContext.request.contextPath}/">← back to all demos</a></p>
</main>
</body>
</html>
