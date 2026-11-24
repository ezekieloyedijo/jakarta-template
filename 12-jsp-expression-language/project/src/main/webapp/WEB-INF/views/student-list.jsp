<%-- VIEW for the list, version 2: EL and JSTL only. Compare it with topic 11's student-list.jsp:
     no imports, no casts, no request.getAttribute, no <% %>. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="All students"/>
<%@ include file="header.jspf" %>

<c:if test="${not empty message}">
    <p class="msg ok">${message}</p>
</c:if>

<form class="search" action="students">
    <input name="q" value="<c:out value='${q}'/>" placeholder="Search by name or class" aria-label="Search">
    <button class="btn" type="submit">Search</button>
    <c:if test="${not empty q}"><a class="btn secondary" href="students">Clear</a></c:if>
</form>

<p class="toolbar">
    <span>${students.size()} student(s)<c:if test="${not empty q}"> for "<c:out value='${q}'/>"</c:if></span>
    <a class="btn" href="students?action=new">➕ Add student</a>
</p>

<c:choose>
    <c:when test="${empty students}">
        <p class="empty">No students found.</p>
    </c:when>
    <c:otherwise>
        <div class="table-wrap"><table>
            <thead><tr><th>ID</th><th>Name</th><th>Gender</th><th>Age</th><th>Class</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="s" items="${students}">
                <tr>
                    <td>${s.id}</td>
                    <td><c:out value="${s.name}"/></td>
                    <td>${s.gender}</td>
                    <td>${s.age}</td>
                    <td><c:out value="${s.className}"/></td>
                    <td class="actions">
                        <a class="btn small" href="students?action=edit&id=${s.id}">Edit</a>
                        <form action="students" method="post" data-name="<c:out value='${s.name}'/>"
                              onsubmit="return confirm('Delete ' + this.dataset.name + '?')">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${s.id}">
                            <button class="btn small danger" type="submit">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table></div>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>
