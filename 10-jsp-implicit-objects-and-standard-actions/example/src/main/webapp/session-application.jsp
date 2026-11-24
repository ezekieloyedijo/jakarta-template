<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // SESSION: one per visitor (topic 05)
    Integer mine = (Integer) session.getAttribute("myVisits");
    mine = (mine == null) ? 1 : mine + 1;
    session.setAttribute("myVisits", mine);

    // APPLICATION: ONE for the whole app, shared by everybody (it is the ServletContext)
    int everyone;
    synchronized (application) {
        Integer all = (Integer) application.getAttribute("allVisits");
        everyone = (all == null) ? 1 : all + 1;
        application.setAttribute("allVisits", everyone);
    }
%>
<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="session and application"/>
</jsp:include>

<div class="tiles">
    <div class="tile"><b><%= mine %></b><span><code>session</code>: your visits</span></div>
    <div class="tile"><b><%= everyone %></b><span><code>application</code>: everybody's visits</span></div>
</div>
<p>Refresh a few times, then open this page in a <b>private window</b>. The session number starts again at 1;
   the application number keeps going, because every visitor shares it.</p>
<p>Server: <%= application.getServerInfo() %></p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
