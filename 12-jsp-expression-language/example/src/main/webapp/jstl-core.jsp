<%@ page contentType="text/html;charset=UTF-8" %>
<%-- STEP 7: the taglib line. "c" is the prefix we chose; every tag of this library starts with c: --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="JSTL core tags"/>
<c:set var="score" value="${empty param.score ? 67 : param.score}"/>
<%@ include file="/WEB-INF/views/top.jspf" %>

<form class="search" action="jstl-core.jsp">
    <input name="score" type="number" min="0" max="100" value="<c:out value='${score}'/>" aria-label="Score">
    <button class="btn">Try another score</button>
</form>

<h2><code>&lt;c:if&gt;</code>: one decision</h2>
<c:if test="${score >= 50}"><p class="msg ok">You passed!</p></c:if>
<c:if test="${score < 50}"><p class="msg error">Not this time.</p></c:if>

<h2><code>&lt;c:choose&gt;</code>: if / else if / else</h2>
<p>Grade:
<c:choose>
    <c:when test="${score >= 70}"><span class="badge ok">A</span></c:when>
    <c:when test="${score >= 60}"><span class="badge">B</span></c:when>
    <c:when test="${score >= 50}"><span class="badge">C</span></c:when>
    <c:otherwise><span class="badge err">F</span></c:otherwise>
</c:choose>
</p>

<h2><code>&lt;c:forEach&gt;</code>: loops</h2>
<p>Counting:
    <c:forEach var="i" begin="1" end="5">${i} </c:forEach>
</p>
<c:set var="days" value="${['Mon', 'Tue', 'Wed', 'Thu', 'Fri']}"/>
<div class="table-wrap"><table>
    <thead><tr><th>status.count</th><th>day</th><th>status.first / status.last</th></tr></thead>
    <tbody>
    <c:forEach var="day" items="${days}" varStatus="status">
        <tr><td>${status.count}</td><td>${day}</td><td>${status.first ? 'first' : ''}${status.last ? 'last' : ''}</td></tr>
    </c:forEach>
    </tbody>
</table></div>

<h2><code>&lt;c:url&gt;</code>: build a link safely</h2>
<c:url var="link" value="/jstl-core.jsp"><c:param name="score" value="45"/></c:url>
<p><a href="${link}">${link}</a></p>

<%@ include file="/WEB-INF/views/bottom.jspf" %>
