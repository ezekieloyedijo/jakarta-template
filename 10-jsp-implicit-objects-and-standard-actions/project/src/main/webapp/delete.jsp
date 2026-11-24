<%-- DELETE: POST delete.jsp (id=2) --%>
<%@ page errorPage="/WEB-INF/parts/error.jsp" %>
<%@ page import="com.school.project.ContactBook" %>
<jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>
<%
    if (!"POST".equals(request.getMethod())) {
        response.sendError(405, "Delete only works with the Delete button (POST).");
        return;
    }
    Integer id = ContactBook.toId(request.getParameter("id"));
    if (id == null) {
        response.sendError(400, "The id must be a number.");
        return;
    }
    if (!book.delete(id)) {
        response.sendError(404, "There is no contact with id " + id + ".");
        return;
    }
    response.sendRedirect("index.jsp?msg=deleted");
%>
