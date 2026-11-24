<%-- UPDATE, step 1: GET edit.jsp?id=2 puts a copy of contact 2 in the request, then forwards to the form --%>
<%@ page contentType="text/html;charset=UTF-8" errorPage="/WEB-INF/parts/error.jsp" %>
<%@ page import="com.school.project.Contact, com.school.project.ContactBook" %>
<jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>
<%
    Integer id = ContactBook.toId(request.getParameter("id"));
    if (id == null) {
        response.sendError(400, "The id must be a number.");
        return;
    }
    Contact contact = book.find(id);
    if (contact == null) {
        response.sendError(404, "There is no contact with id " + id + ".");
        return;
    }
    request.setAttribute("contact", contact);          // form.jsp's <jsp:useBean scope="request"> will FIND it
%>
<jsp:forward page="/WEB-INF/parts/form.jsp"/>
