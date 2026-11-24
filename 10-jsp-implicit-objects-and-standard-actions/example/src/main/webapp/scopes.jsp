<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // The same name, "colour", saved in all FOUR scopes with pageContext.setAttribute(name, value, scope)
    pageContext.setAttribute("colour", "red (page)", PageContext.PAGE_SCOPE);
    pageContext.setAttribute("colour", "green (request)", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("colour", "blue (session)", PageContext.SESSION_SCOPE);
    pageContext.setAttribute("colour", "yellow (application)", PageContext.APPLICATION_SCOPE);
%>
<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="The four scopes"/>
</jsp:include>

<div class="table-wrap"><table>
    <thead><tr><th>Scope</th><th>Lives for</th><th>Value of "colour"</th></tr></thead>
    <tbody>
    <tr><td>page</td><td>this one JSP, this one request</td><td><%= pageContext.getAttribute("colour") %></td></tr>
    <tr><td>request</td><td>this request, even across forward and include</td><td><%= request.getAttribute("colour") %></td></tr>
    <tr><td>session</td><td>this visitor, until logout or timeout</td><td><%= session.getAttribute("colour") %></td></tr>
    <tr><td>application</td><td>everyone, until the app stops</td><td><%= application.getAttribute("colour") %></td></tr>
    </tbody>
</table></div>

<p><code>pageContext.findAttribute("colour")</code> searches page → request → session → application and stops at the
   first one it finds: <b><%= pageContext.findAttribute("colour") %></b></p>
<p class="note">Remember this search order: EL in topic 12 (<code>${colour}</code>) uses exactly the same one.</p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
