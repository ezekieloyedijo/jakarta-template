<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="How EL finds a name: the scope search"/>

<%-- <c:set> saves a value in a scope, with no Java --%>
<c:set var="colour" value="yellow (application)" scope="application"/>
<c:set var="colour" value="blue (session)" scope="session"/>
<c:set var="colour" value="green (request)" scope="request"/>
<c:if test="${param.withPage != 'no'}">
    <c:set var="colour" value="red (page)" scope="page"/>
</c:if>

<%@ include file="/WEB-INF/views/top.jspf" %>

<p class="msg ok"><code>\${colour}</code> = <b>${colour}</b></p>
<p>EL looked in <b>page → request → session → application</b> and stopped at the first "colour" it found.
   <a href="scope-search.jsp?withPage=no">Remove the page one</a> and it finds the request one instead.</p>

<p>To choose a scope yourself, use the scope objects:</p>
<div class="table-wrap"><table>
    <tr><td><code>\${pageScope.colour}</code></td><td>${pageScope.colour}</td></tr>
    <tr><td><code>\${requestScope.colour}</code></td><td>${requestScope.colour}</td></tr>
    <tr><td><code>\${sessionScope.colour}</code></td><td>${sessionScope.colour}</td></tr>
    <tr><td><code>\${applicationScope.colour}</code></td><td>${applicationScope.colour}</td></tr>
</table></div>

<%@ include file="/WEB-INF/views/bottom.jspf" %>
