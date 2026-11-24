<%@ page contentType="text/html;charset=UTF-8" %>
<%-- <jsp:forward>: if the URL has ?go=yes, hand this request to another page. Nothing below runs. --%>
<% if ("yes".equals(request.getParameter("go"))) { %>
    <jsp:forward page="/WEB-INF/parts/forwarded.jsp">
        <jsp:param name="from" value="actions.jsp"/>
    </jsp:forward>
<% } %>

<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="include, forward, param"/>
</jsp:include>

<p><code>&lt;jsp:include&gt;</code> runs another page <b>at request time</b> and puts its output here.
   The same piece, included 3 times with different <code>&lt;jsp:param&gt;</code> values:</p>

<div class="cards">
    <jsp:include page="/WEB-INF/parts/greeting.jsp">
        <jsp:param name="who" value="JSS1"/>
        <jsp:param name="subject" value="Basic Science"/>
    </jsp:include>
    <jsp:include page="/WEB-INF/parts/greeting.jsp">
        <jsp:param name="who" value="SS2"/>
        <jsp:param name="subject" value="Chemistry"/>
    </jsp:include>
    <jsp:include page="/WEB-INF/parts/greeting.jsp">
        <jsp:param name="who" value="SS3"/>
        <jsp:param name="subject" value="Further Maths"/>
    </jsp:include>
</div>

<p style="margin-top:16px"><a class="btn" href="actions.jsp?go=yes">Try &lt;jsp:forward&gt;</a></p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
