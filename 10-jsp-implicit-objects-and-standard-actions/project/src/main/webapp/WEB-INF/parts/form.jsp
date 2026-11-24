<%-- The Add / Edit form. add.jsp, edit.jsp and save.jsp FORWARD here.
     It finds the bean "contact" that they left in the REQUEST scope, and an optional list "errors". --%>
<%@ page import="com.school.project.ContactBook, java.util.List" %>
<jsp:useBean id="contact" class="com.school.project.Contact" scope="request"/>
<%
    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) request.getAttribute("errors");
    boolean editing = contact.getId() != 0;
    String pageTitle = editing ? "Edit contact" : "Add a contact";
%>
<jsp:include page="/WEB-INF/parts/header.jsp">
    <jsp:param name="title" value="<%= pageTitle %>"/>
</jsp:include>

<% if (errors != null && !errors.isEmpty()) { %>
    <div class="msg error"><b>Please fix the following:</b>
        <ul><% for (String e : errors) { %><li><%= ContactBook.escape(e) %></li><% } %></ul>
    </div>
<% } %>

<form class="card" action="<%= request.getContextPath() %>/save.jsp" method="post">
    <input type="hidden" name="id" value="<%= editing ? String.valueOf(contact.getId()) : "" %>">
    <label>Full name <input name="name" value="<%= ContactBook.escape(contact.getName()) %>" maxlength="50" required></label>
    <label>Phone <input name="phone" value="<%= ContactBook.escape(contact.getPhone()) %>" maxlength="15" required></label>
    <label>Email (optional) <input name="email" type="email" value="<%= ContactBook.escape(contact.getEmail()) %>"></label>
    <label>Role
        <select name="role" required>
            <option value="">-- choose --</option>
            <% for (String r : ContactBook.ROLES) { %>
                <option<%= r.equals(contact.getRole()) ? " selected" : "" %>><%= r %></option>
            <% } %>
        </select>
    </label>
    <div class="buttons">
        <button class="btn" type="submit"><%= editing ? "Save changes" : "Save contact" %></button>
        <a class="btn secondary" href="<%= request.getContextPath() %>/">Cancel</a>
    </div>
</form>

<jsp:include page="/WEB-INF/parts/footer.jsp"/>
