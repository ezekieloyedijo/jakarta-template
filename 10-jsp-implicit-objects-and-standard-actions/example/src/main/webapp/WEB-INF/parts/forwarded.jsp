<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.example.Person" %>
<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="You were forwarded"/>
</jsp:include>

<p class="msg ok">This page was sent by <code><%= Person.escape(request.getParameter("from")) %></code>
   with <code>&lt;jsp:forward&gt;</code>.</p>
<p>Look at the address bar: it still says <code>actions.jsp?go=yes</code>. A forward happens inside the server,
   so the browser never knows (topic 04).</p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
