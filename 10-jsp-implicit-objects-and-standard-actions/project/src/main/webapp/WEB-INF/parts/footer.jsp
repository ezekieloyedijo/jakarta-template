<%@ page import="com.school.project.ContactBook" %>
<% ContactBook shared = (ContactBook) application.getAttribute("book"); %>
</main>
<footer>Contacts saved: <%= shared == null ? 0 : shared.getAll().size() %> · kept in the <b>application</b> scope</footer>
</body>
</html>
