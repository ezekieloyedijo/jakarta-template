<%-- If anything on this page throws an exception, show error.jsp instead of Tomcat's ugly 500 page --%>
<%@ page contentType="text/html;charset=UTF-8" errorPage="/WEB-INF/parts/error.jsp" %>
<%
    int pupils = 0;
    int sweetsEach = 30 / pupils;          // dividing by zero: ArithmeticException!
%>
<p>You will never see this line.</p>
