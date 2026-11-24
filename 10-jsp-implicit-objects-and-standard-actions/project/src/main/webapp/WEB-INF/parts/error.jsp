<%-- The error page for every page (errorPage="/WEB-INF/parts/error.jsp"). It gets the "exception" object. --%>
<%@ page isErrorPage="true" %>
<jsp:include page="/WEB-INF/parts/header.jsp">
    <jsp:param name="title" value="Something went wrong"/>
</jsp:include>
<p class="msg error">Sorry, the page could not be shown. Please go back and try again.</p>
<% System.out.println("[ERROR] " + exception); %>
<p><a class="btn secondary" href="<%= request.getContextPath() %>/">← All contacts</a></p>
<jsp:include page="/WEB-INF/parts/footer.jsp"/>
