<%-- CREATE and UPDATE, step 2: POST save.jsp. Fill a bean from the form, check it, save it.
     id empty -> a new contact.   id = 2 -> change contact 2. --%>
<%@ page contentType="text/html;charset=UTF-8" errorPage="/WEB-INF/parts/error.jsp" %>
<%@ page import="com.school.project.ContactBook, java.util.List" %>
<%
    if (!"POST".equals(request.getMethod())) {
        response.sendRedirect("add.jsp");
        return;
    }
    request.setCharacterEncoding("UTF-8");               // before ANY parameter is read
    String idText = request.getParameter("id");
    if (idText != null && !idText.isEmpty() && ContactBook.toId(idText) == null) {
        response.sendError(400, "The id must be a number.");   // setProperty can't turn "abc" into an int
        return;
    }
%>
<jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>

<%-- 1. a new, empty Contact bean in the REQUEST scope --%>
<jsp:useBean id="contact" class="com.school.project.Contact" scope="request"/>

<%-- 2. ONE line copies every form field into the bean: name -> setName(), phone -> setPhone(), ...
        (an empty "id" is skipped, so a new contact keeps id 0) --%>
<jsp:setProperty name="contact" property="*"/>

<%
    // 3. check it
    List<String> errors = ContactBook.validate(contact);
    if (!errors.isEmpty()) {
        response.setStatus(400);
        request.setAttribute("errors", errors);
%>
        <%-- show the form again: it finds the same "contact" bean, so the typed values stay --%>
        <jsp:forward page="/WEB-INF/parts/form.jsp"/>
<%
    }

    // 4. save it, then redirect (Post/Redirect/Get)
    boolean isNew = contact.getId() == 0;
    if (!book.save(contact)) {
        response.sendError(404, "There is no contact with id " + contact.getId() + ".");
        return;
    }
    response.sendRedirect("index.jsp?msg=" + (isNew ? "added" : "updated"));
%>
