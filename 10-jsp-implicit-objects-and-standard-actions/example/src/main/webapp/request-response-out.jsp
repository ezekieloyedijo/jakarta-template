<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.example.Person" %>
<%
    // RESPONSE: change the answer before any HTML is sent
    response.setHeader("X-Demo", "implicit-objects");
%>
<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="request, response, out"/>
</jsp:include>

<h2><code>request</code>: what the browser sent</h2>
<div class="table-wrap"><table>
    <tr><th>request.getMethod()</th><td><%= request.getMethod() %></td></tr>
    <tr><th>request.getRequestURI()</th><td><%= Person.escape(request.getRequestURI()) %></td></tr>
    <tr><th>request.getParameter("name")</th><td><%= Person.escape(request.getParameter("name")) %></td></tr>
    <tr><th>request.getHeader("User-Agent")</th><td><%= Person.escape(request.getHeader("User-Agent")) %></td></tr>
</table></div>
<p><a href="?name=Ada">Try it with <code>?name=Ada</code></a></p>

<h2><code>out</code>: write into the page from Java</h2>
<%
    for (int i = 3; i >= 1; i--) {
        out.println("<span class='badge'>" + i + "</span>");     // like an expression, but from inside a scriptlet
    }
    out.println("<b>Lift-off!</b>");
%>

<h2><code>response</code>: the answer going back</h2>
<p>This page added a header <code>X-Demo: implicit-objects</code> with <code>response.setHeader</code>.
   See it in F12 → Network → this page → Headers.</p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
