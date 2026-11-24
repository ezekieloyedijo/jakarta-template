<%-- A small piece of page, included by actions.jsp. It reads the <jsp:param> values like normal parameters. --%>
<%@ page import="com.school.example.Person" %>
<div class="item">
    <h3>👋 Hello, <%= Person.escape(request.getParameter("who")) %>!</h3>
    <p>Your lesson today: <b><%= Person.escape(request.getParameter("subject")) %></b></p>
</div>
