<%-- READ: every contact as a card --%>
<%@ page contentType="text/html;charset=UTF-8" errorPage="/WEB-INF/parts/error.jsp" %>
<%@ page import="com.school.project.Contact, com.school.project.ContactBook" %>

<%-- Find the shared ContactBook in the APPLICATION scope, or create it (only the very first time) --%>
<jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>

<jsp:include page="/WEB-INF/parts/header.jsp">
    <jsp:param name="title" value="All contacts"/>
</jsp:include>

<%
    String msg = request.getParameter("msg");
    String text = "added".equals(msg) ? "Contact added." : "updated".equals(msg) ? "Contact updated."
            : "deleted".equals(msg) ? "Contact deleted." : null;
    if (text != null) {
        out.println("<p class='msg ok'>" + text + "</p>");
    }
%>

<% if (book.getAll().isEmpty()) { %>
    <p class="empty">No contacts yet.</p>
<% } else { %>
    <div class="cards">
        <% for (Contact c : book.getAll()) { %>
            <div class="item">
                <span class="avatar"><%= ContactBook.escape(c.getInitials()) %></span>
                <h3><%= ContactBook.escape(c.getName()) %></h3>
                <p><span class="badge"><%= ContactBook.escape(c.getRole()) %></span></p>
                <p>📞 <%= ContactBook.escape(c.getPhone()) %></p>
                <% if (!c.getEmail().isEmpty()) { %><p>✉️ <%= ContactBook.escape(c.getEmail()) %></p><% } %>
                <div class="buttons">
                    <a class="btn small" href="edit.jsp?id=<%= c.getId() %>">Edit</a>
                    <form action="delete.jsp" method="post" data-name="<%= ContactBook.escape(c.getName()) %>"
                          onsubmit="return confirm('Delete ' + this.dataset.name + '?')">
                        <input type="hidden" name="id" value="<%= c.getId() %>">
                        <button class="btn small danger" type="submit">Delete</button>
                    </form>
                </div>
            </div>
        <% } %>
    </div>
<% } %>

<p style="margin-top:16px"><a class="btn" href="add.jsp">➕ Add contact</a></p>

<jsp:include page="/WEB-INF/parts/footer.jsp"/>
