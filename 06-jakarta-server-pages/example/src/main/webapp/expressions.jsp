<%@ page contentType="text/html;charset=UTF-8" %>
<% String title = "Expressions"; %>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<p>An expression is <b>one value</b>. The server works it out and prints it. No semicolon!</p>

<div class="table-wrap"><table>
    <thead><tr><th>You write</th><th>The page shows</th></tr></thead>
    <tbody>
    <tr><td><code>&lt;%= 2 + 3 %&gt;</code></td><td><%= 2 + 3 %></td></tr>
    <tr><td><code>&lt;%= "ada".toUpperCase() %&gt;</code></td><td><%= "ada".toUpperCase() %></td></tr>
    <tr><td><code>&lt;%= 10 &gt; 3 %&gt;</code></td><td><%= 10 > 3 %></td></tr>
    <tr><td><code>&lt;%= Math.round(7.6) %&gt;</code></td><td><%= Math.round(7.6) %></td></tr>
    <tr><td><code>&lt;%= java.time.LocalDate.now() %&gt;</code></td><td><%= java.time.LocalDate.now() %></td></tr>
    </tbody>
</table></div>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
