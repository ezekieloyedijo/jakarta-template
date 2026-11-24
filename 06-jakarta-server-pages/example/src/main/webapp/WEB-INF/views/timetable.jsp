<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%
    String title = "Servlet + JSP together";
    // read what TimetableServlet put in the request
    String day = (String) request.getAttribute("day");
    @SuppressWarnings("unchecked")
    List<String> lessons = (List<String>) request.getAttribute("lessons");
%>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<p>Today is <b><%= day %></b>. The data came from <code>TimetableServlet</code>; this JSP only shows it.</p>
<ul class="demos">
    <% for (String lesson : lessons) { %>
        <li><%= lesson %></li>
    <% } %>
</ul>
<p>The URL is <code>/timetable</code> (the servlet). Try opening <code>/WEB-INF/views/timetable.jsp</code>
   directly: you get a 404, because files in <code>WEB-INF</code> are private.</p>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
