<%-- CREATE:
       GET  add.jsp -> show the empty form
       POST add.jsp -> check it; save; redirect to the list (or show the form again with errors) --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.project.GradeBook, java.util.List" %>
<%
    request.setCharacterEncoding("UTF-8");
    String title = "Add a subject";
    String action = "add.jsp";
    String button = "Save subject";
    Integer id = null;                                   // a new subject has no id yet
    String name = "";
    String score = "";
    List<String> errors = List.of();

    if ("POST".equals(request.getMethod())) {
        name = request.getParameter("name") == null ? "" : request.getParameter("name").trim();
        score = request.getParameter("score") == null ? "" : request.getParameter("score").trim();
        errors = GradeBook.validate(name, score);

        if (errors.isEmpty()) {
            GradeBook.of(application).add(name, GradeBook.toScore(score));
            response.sendRedirect("index.jsp?msg=added");   // Post/Redirect/Get
            return;                                       // stop: don't build the page below
        }
        response.setStatus(400);                          // something was wrong: show the form again
    }
%>
<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/subject-form.jspf" %>
<%@ include file="/WEB-INF/fragments/footer.jspf" %>
