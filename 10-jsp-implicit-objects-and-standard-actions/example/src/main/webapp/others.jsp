<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="pageContext, config, page"/>
</jsp:include>

<div class="table-wrap"><table>
    <tr><th><code>config.getServletName()</code></th><td><%= config.getServletName() %></td></tr>
    <tr><th><code>page.getClass().getName()</code></th><td><%= page.getClass().getName() %></td></tr>
    <tr><th><code>pageContext.getRequest() == request</code></th><td><%= pageContext.getRequest() == request %></td></tr>
    <tr><th><code>pageContext.getSession() == session</code></th><td><%= pageContext.getSession() == session %></td></tr>
</table></div>

<p><code>page</code> is the JSP's own servlet object (<code>this</code>): its class name shows the servlet Tomcat made
   from this file. <code>pageContext</code> is the "toolbox" that holds all the other objects.
   The ninth object, <code>exception</code>, only exists in an error page: <a href="boom.jsp">see it here</a>.</p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
