<%@ page contentType="text/html;charset=UTF-8" %>
<%-- A DECLARATION creates a FIELD or a METHOD of the servlet that this JSP becomes. --%>
<%!
    // A field: ONE copy, shared by EVERY user (there is only one servlet object). Usually a bad idea!
    private int pageHits = 0;

    // A method: fine, as long as it doesn't change shared fields
    private String stars(int count) {
        return "⭐".repeat(count);
    }
%>
<%
    String title = "Declarations";
    int visitsToThisPage;
    synchronized (this) {           // two users at the same moment could break pageHits++
        pageHits++;
        visitsToThisPage = pageHits;
    }
    int localCounter = 0;           // a LOCAL variable: new for every request
    localCounter++;
%>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<div class="table-wrap"><table>
    <tr><th>Field <code>pageHits</code> (declared with &lt;%! %&gt;)</th><td><%= visitsToThisPage %></td></tr>
    <tr><th>Local <code>localCounter</code> (declared in &lt;% %&gt;)</th><td><%= localCounter %></td></tr>
    <tr><th>Method <code>stars(3)</code></th><td><%= stars(3) %></td></tr>
</table></div>

<p>Press F5 a few times, then open this page in another browser. <code>pageHits</code> keeps counting for
   <b>everyone</b>, while <code>localCounter</code> is always 1.</p>
<p class="note">⚠️ That is why fields in declarations are dangerous: every user shares them. Keep data for one
   user in the <b>session</b> (topic 05) and use declarations only for small helper methods.</p>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
