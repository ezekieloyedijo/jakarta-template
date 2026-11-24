<%-- isErrorPage="true" gives this page the ninth implicit object: "exception" --%>
<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ page import="com.school.example.Person" %>
<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="Oops! (the exception object)"/>
</jsp:include>

<p class="msg error">Something went wrong on the page you opened.</p>
<div class="table-wrap"><table>
    <tr><th><code>exception.getClass().getName()</code></th><td><%= exception.getClass().getName() %></td></tr>
    <tr><th><code>exception.getMessage()</code></th><td><%= Person.escape(exception.getMessage()) %></td></tr>
</table></div>
<p class="note">A real app would show only a friendly message, and write the details to a log, not to the page.</p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
