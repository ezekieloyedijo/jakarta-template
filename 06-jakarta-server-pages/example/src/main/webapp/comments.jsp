<%@ page contentType="text/html;charset=UTF-8" %>
<% String title = "Comments"; %>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<!-- HTML COMMENT: sent to the browser. Anyone can read it with View Page Source. -->
<%-- JSP COMMENT: removed on the server. The browser never sees it. --%>
<%
    // JAVA COMMENT inside a scriptlet: also removed on the server.
%>

<p>This page has three comments in its source file.</p>
<p>Now right-click → <b>View Page Source</b>. Only the <b>HTML</b> comment is there.</p>
<p class="note">Rule: never write a password or a secret in an HTML comment. Use <code>&lt;%-- --%&gt;</code> for notes to other programmers.</p>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
