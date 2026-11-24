<%@ page contentType="text/html;charset=UTF-8" %>
<% String title = "Scriptlets"; %>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<%-- A scriptlet holds Java STATEMENTS (with semicolons). It prints nothing by itself. --%>
<%
    int hour = java.time.LocalTime.now().getHour();
    String greeting;
    if (hour < 12) {
        greeting = "Good morning";
    } else if (hour < 17) {
        greeting = "Good afternoon";
    } else {
        greeting = "Good evening";
    }
%>
<p class="msg ok"><%= greeting %>! (It is <%= hour %> o'clock on the server.)</p>

<h2>A loop: the 7 times table</h2>
<div class="table-wrap"><table>
    <tbody>
    <% for (int i = 1; i <= 12; i++) { %>
        <%-- the HTML between the two scriptlets is repeated 12 times --%>
        <tr><td>7 × <%= i %></td><td>= <%= 7 * i %></td></tr>
    <% } %>
    </tbody>
</table></div>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>
