<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.school.example.Person" %>
<%
    request.setCharacterEncoding("UTF-8");
    String age = request.getParameter("age");
    if (age != null && !age.matches("\\d{1,3}")) {          // setProperty can't turn "abc" into an int
        response.sendError(400, "The age must be a whole number.");
        return;
    }
%>
<%-- 1. FIND the bean "person" in the session, or CREATE a new Person and put it there --%>
<jsp:useBean id="person" class="com.school.example.Person" scope="session"/>

<%-- 2. COPY every request parameter into the property with the same name: name -> setName, age -> setAge --%>
<jsp:setProperty name="person" property="*"/>

<jsp:include page="/WEB-INF/parts/top.jsp">
    <jsp:param name="title" value="JavaBeans: useBean, setProperty, getProperty"/>
</jsp:include>

<form class="card" method="post" action="bean.jsp">
    <label>Name <input name="name" value="<%= Person.escape(person.getName()) %>"></label>
    <label>Age <input name="age" type="number" min="0" max="120" value="<%= person.getAge() %>"></label>
    <button class="btn">Save into the bean</button>
</form>

<%-- 3. READ a property --%>
<p class="msg ok">The bean in your session says: age <b><jsp:getProperty name="person" property="age"/></b>,
   name <b><%= Person.escape(person.getName()) %></b>.</p>
<p class="note">⚠️ <code>&lt;jsp:getProperty&gt;</code> does <b>not</b> escape HTML. It is fine for a number like the age,
   but for text that a user typed we print it with <code>Person.escape(...)</code>. (Topic 12's <code>&lt;c:out&gt;</code> fixes this.)
   The bean lives in the <b>session</b>: open another page and come back, the values are still here.</p>

<jsp:include page="/WEB-INF/parts/bottom.jsp"/>
