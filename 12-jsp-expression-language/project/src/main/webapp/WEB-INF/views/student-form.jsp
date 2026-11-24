<%-- VIEW for Add and Edit, version 2. The controller gives it: form, id (empty when adding), errors, classes. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="editing" value="${not empty id}"/>
<c:set var="title" value="${editing ? 'Edit student' : 'Add a student'}"/>
<%@ include file="header.jspf" %>

<c:if test="${not empty errors}">
    <div class="msg error"><b>Please fix the following:</b>
        <ul>
            <c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach>
        </ul>
    </div>
</c:if>

<form class="card" action="students" method="post">
    <input type="hidden" name="action" value="${editing ? 'update' : 'insert'}">
    <c:if test="${editing}"><input type="hidden" name="id" value="${id}"></c:if>

    <label>Full name <input name="name" value="<c:out value='${form.name}'/>" maxlength="60" required></label>
    <fieldset>
        <legend>Gender</legend>
        <label class="inline"><input type="radio" name="gender" value="Male" ${form.gender == 'Male' ? 'checked' : ''}> Male</label>
        <label class="inline"><input type="radio" name="gender" value="Female" ${form.gender == 'Female' ? 'checked' : ''}> Female</label>
    </fieldset>
    <label>Age <input name="age" type="number" min="5" max="30" value="<c:out value='${form.age}'/>" required></label>
    <label>Class
        <select name="className" required>
            <option value="">-- choose --</option>
            <c:forEach var="c" items="${classes}">
                <option ${c == form.className ? 'selected' : ''}>${c}</option>
            </c:forEach>
        </select>
    </label>
    <div class="buttons">
        <button class="btn" type="submit">${editing ? 'Save changes' : 'Save student'}</button>
        <a class="btn secondary" href="students">Cancel</a>
    </div>
</form>

<%@ include file="footer.jspf" %>
