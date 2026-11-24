<%-- UPDATE:
       GET  edit.jsp?id=2 -> show the form filled with subject 2
       POST edit.jsp      -> check it; save the changes; redirect to the list --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.project.GradeBook, com.school.project.Subject, java.util.List" %>
<%
    request.setCharacterEncoding("UTF-8");
    GradeBook book = GradeBook.of(application);
    String title = "Edit subject";
    String action = "edit.jsp";
    String button = "Save changes";
    List<String> errors = List.of();

    Integer id = GradeBook.toId(request.getParameter("id"));
    if (id == null) {
        response.sendError(400, "The id must be a number.");
        return;
    }
    Subject subject = book.find(id);
    if (subject == null) {
        response.sendError(404, "There is no subject with id " + id + ".");
        return;
    }

    // GET: start with the saved values
    String name = subject.getName();
    String score = String.valueOf(subject.getScore());

    if ("POST".equals(request.getMethod())) {
        name = request.getParameter("name") == null ? "" : request.getParameter("name").trim();
        score = request.getParameter("score") == null ? "" : request.getParameter("score").trim();
        errors = GradeBook.validate(name, score);

        if (errors.isEmpty()) {
            book.update(id, name, GradeBook.toScore(score));
            response.sendRedirect("index.jsp?msg=updated");
            return;
        }
        response.setStatus(400);
    }
%>
<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/subject-form.jspf" %>
<%@ include file="/WEB-INF/fragments/footer.jspf" %>
