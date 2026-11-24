<%-- V = VIEW for Add and Edit. The controller gives it: form, id (null when adding), errors, classes. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.project.model.StudentForm, com.school.project.view.Html, java.util.List" %>
<%
    StudentForm form = (StudentForm) request.getAttribute("form");
    Integer id = (Integer) request.getAttribute("id");
    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) request.getAttribute("errors");
    @SuppressWarnings("unchecked")
    List<String> classes = (List<String>) request.getAttribute("classes");
    boolean editing = id != null;
    String title = editing ? "Edit student" : "Add a student";
%>
<%@ include file="header.jspf" %>

<% if (!errors.isEmpty()) { %>
    <div class="msg error"><b>Please fix the following:</b>
        <ul><% for (String e : errors) { %><li><%= Html.escape(e) %></li><% } %></ul>
    </div>
<% } %>

<form class="card" action="students" method="post">
    <input type="hidden" name="action" value="<%= editing ? "update" : "insert" %>">
    <% if (editing) { %><input type="hidden" name="id" value="<%= id %>"><% } %>

    <label>Full name <input name="name" value="<%= Html.escape(form.getName()) %>" maxlength="60" required></label>
    <fieldset>
        <legend>Gender</legend>
        <label class="inline"><input type="radio" name="gender" value="Male"<%= "Male".equals(form.getGender()) ? " checked" : "" %>> Male</label>
        <label class="inline"><input type="radio" name="gender" value="Female"<%= "Female".equals(form.getGender()) ? " checked" : "" %>> Female</label>
    </fieldset>
    <label>Age <input name="age" type="number" min="<%= StudentForm.MIN_AGE %>" max="<%= StudentForm.MAX_AGE %>"
                      value="<%= Html.escape(form.getAge()) %>" required></label>
    <label>Class
        <select name="className" required>
            <option value="">-- choose --</option>
            <% for (String c : classes) { %>
                <option<%= c.equals(form.getClassName()) ? " selected" : "" %>><%= c %></option>
            <% } %>
        </select>
    </label>
    <div class="buttons">
        <button class="btn" type="submit"><%= editing ? "Save changes" : "Save student" %></button>
        <a class="btn secondary" href="students">Cancel</a>
    </div>
</form>

<%@ include file="footer.jspf" %>
