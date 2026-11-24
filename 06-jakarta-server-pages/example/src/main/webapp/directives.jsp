<%-- DIRECTIVES give instructions to the JSP translator. They print nothing. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%
    String title = "Directives";
    // Thanks to the import directive we can write LocalDate instead of java.time.LocalDate
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"));
    List<String> subjects = List.of("Maths", "English", "Biology");
%>
<%-- the include DIRECTIVE pastes the file's text right here, BEFORE the page is compiled --%>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<div class="table-wrap"><table>
    <thead><tr><th>Directive</th><th>What it did on this page</th></tr></thead>
    <tbody>
    <tr><td><code>&lt;%@ page contentType="text/html;charset=UTF-8" %&gt;</code></td>
        <td>Told the browser "this is HTML in UTF-8", so ✅ and é show correctly</td></tr>
    <tr><td><code>&lt;%@ page import="java.time.LocalDate, ..." %&gt;</code></td>
        <td>Today is <b><%= today %></b>, and we have <%= subjects.size() %> subjects</td></tr>
    <tr><td><code>&lt;%@ include file="/WEB-INF/fragments/header.jspf" %&gt;</code></td>
        <td>Pasted in the header you see at the top (the same one on every demo page)</td></tr>
    </tbody>
</table></div>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
