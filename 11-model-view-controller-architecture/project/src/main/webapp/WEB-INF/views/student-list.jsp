<%-- V = VIEW for the list. It only READS what the controller put in the request, and shows it. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.project.model.Student, com.school.project.view.Html, java.util.List" %>
<%
    String title = "All students";
    @SuppressWarnings("unchecked")
    List<Student> students = (List<Student>) request.getAttribute("students");
    String q = (String) request.getAttribute("q");
    String message = (String) request.getAttribute("message");
%>
<%@ include file="header.jspf" %>

<% if (message != null) { %><p class="msg ok"><%= message %></p><% } %>

<form class="search" action="students">
    <input name="q" value="<%= Html.escape(q) %>" placeholder="Search by name or class" aria-label="Search">
    <button class="btn" type="submit">Search</button>
    <% if (!q.isEmpty()) { %><a class="btn secondary" href="students">Clear</a><% } %>
</form>

<p class="toolbar">
    <span><%= students.size() %> student(s)<%= q.isEmpty() ? "" : " for \"" + Html.escape(q) + "\"" %></span>
    <a class="btn" href="students?action=new">➕ Add student</a>
</p>

<% if (students.isEmpty()) { %>
    <p class="empty">No students found.</p>
<% } else { %>
    <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Name</th><th>Gender</th><th>Age</th><th>Class</th><th></th></tr></thead>
        <tbody>
        <% for (Student s : students) { %>
            <tr>
                <td><%= s.getId() %></td>
                <td><%= Html.escape(s.getName()) %></td>
                <td><%= s.getGender() %></td>
                <td><%= s.getAge() %></td>
                <td><%= Html.escape(s.getClassName()) %></td>
                <td class="actions">
                    <a class="btn small" href="students?action=edit&id=<%= s.getId() %>">Edit</a>
                    <form action="students" method="post" data-name="<%= Html.escape(s.getName()) %>"
                          onsubmit="return confirm('Delete ' + this.dataset.name + '?')">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%= s.getId() %>">
                        <button class="btn small danger" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table></div>
<% } %>

<%@ include file="footer.jspf" %>
