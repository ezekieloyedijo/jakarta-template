<%-- DELETE: POST delete.jsp (id=2) removes the subject, then redirects to the list.
     This page has no HTML of its own: it only does the job and redirects. --%>
<%@ page import="com.school.project.GradeBook" %>
<%
    if (!"POST".equals(request.getMethod())) {
        response.sendError(405, "Delete only works with the Delete button (POST).");
        return;
    }
    Integer id = GradeBook.toId(request.getParameter("id"));
    if (id == null) {
        response.sendError(400, "The id must be a number.");
        return;
    }
    if (!GradeBook.of(application).delete(id)) {
        response.sendError(404, "There is no subject with id " + id + ".");
        return;
    }
    response.sendRedirect("index.jsp?msg=deleted");
%>
