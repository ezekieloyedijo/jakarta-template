<%-- CREATE, step 1: show an empty form. The form page creates an empty Contact bean by itself. --%>
<%@ page contentType="text/html;charset=UTF-8" errorPage="/WEB-INF/parts/error.jsp" %>
<jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>
<jsp:forward page="/WEB-INF/parts/form.jsp"/>
