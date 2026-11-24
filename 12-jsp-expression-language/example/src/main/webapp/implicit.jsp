<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="EL implicit objects"/>
<% session.setAttribute("seen", true); /* so a JSESSIONID cookie exists for the demo below */ %>
<%@ include file="/WEB-INF/views/top.jspf" %>

<p><a href="implicit.jsp?name=Ada&subject=Maths&subject=Physics">Open with parameters</a>, then look at the table.</p>
<div class="table-wrap"><table>
    <thead><tr><th>You write</th><th>Means</th><th>Value</th></tr></thead>
    <tbody>
    <tr><td><code>\${param.name}</code></td><td><code>request.getParameter("name")</code></td><td><c:out value="${param.name}"/></td></tr>
    <tr><td><code>\${paramValues.subject[1]}</code></td><td>the 2nd value of "subject"</td><td><c:out value="${paramValues.subject[1]}"/></td></tr>
    <tr><td><code>\${header['User-Agent']}</code></td><td><code>request.getHeader("User-Agent")</code></td><td><c:out value="${header['User-Agent']}"/></td></tr>
    <tr><td><code>\${cookie.JSESSIONID.value}</code></td><td>the value of a cookie</td><td><c:out value="${cookie.JSESSIONID.value}"/></td></tr>
    <tr><td><code>\${initParam.school}</code></td><td>a <code>&lt;context-param&gt;</code> from web.xml</td><td><c:out value="${initParam.school}"/></td></tr>
    <tr><td><code>\${pageContext.request.contextPath}</code></td><td>the app's path (empty here, because it runs at /)</td><td>"${pageContext.request.contextPath}"</td></tr>
    <tr><td><code>\${pageContext.request.method}</code></td><td><code>request.getMethod()</code></td><td>${pageContext.request.method}</td></tr>
    </tbody>
</table></div>
<p class="note">Values a user can type (parameters, headers, cookies) are printed with <code>&lt;c:out&gt;</code>, which escapes HTML. See demo 6.</p>

<%@ include file="/WEB-INF/views/bottom.jspf" %>
