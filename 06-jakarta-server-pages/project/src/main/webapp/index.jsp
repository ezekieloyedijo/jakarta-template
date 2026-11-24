<%-- READ: the list of subjects, the average, and the final grade --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.project.GradeBook, com.school.project.Subject, java.util.List" %>
<%
    String title = "My subjects";
    GradeBook book = GradeBook.of(application);     // "application" = the ServletContext, ready to use in any JSP
    List<Subject> subjects = book.all();

    // a fixed message after add / edit / delete (never print the raw parameter)
    String msg = request.getParameter("msg");
    String message = null;
    if ("added".equals(msg)) message = "Subject added.";
    if ("updated".equals(msg)) message = "Subject updated.";
    if ("deleted".equals(msg)) message = "Subject deleted.";
%>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<% if (message != null) { %>
    <p class="msg ok"><%= message %></p>
<% } %>

<% if (subjects.isEmpty()) { %>
    <p class="empty">No subjects yet. <a href="add.jsp">Add your first subject</a>.</p>
<% } else { %>
    <div class="table-wrap"><table>
        <thead><tr><th>Subject</th><th class="num">Score</th><th>Grade</th><th></th></tr></thead>
        <tbody>
        <% for (Subject s : subjects) { %>
            <tr>
                <td><%= GradeBook.escape(s.getName()) %></td>
                <td class="num"><%= s.getScore() %></td>
                <td><span class="badge<%= s.getScore() < 40 ? " err" : "" %>"><%= s.getGrade() %></span></td>
                <td class="actions">
                    <a class="btn small" href="edit.jsp?id=<%= s.getId() %>">Edit</a>
                    <form action="delete.jsp" method="post" data-name="<%= GradeBook.escape(s.getName()) %>"
                          onsubmit="return confirm('Delete ' + this.dataset.name + '?')">
                        <input type="hidden" name="id" value="<%= s.getId() %>">
                        <button class="btn small danger" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        <% } %>
        </tbody>
        <tfoot>
            <tr><td>Average (<%= subjects.size() %> subjects)</td>
                <td class="num"><%= book.average() %></td>
                <td><span class="badge ok"><%= GradeBook.gradeFor(book.average()) %></span></td>
                <td></td></tr>
        </tfoot>
    </table></div>
<% } %>

<p style="margin-top:16px"><a class="btn" href="add.jsp">➕ Add subject</a></p>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
