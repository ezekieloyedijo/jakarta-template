<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="c:out and escaping"/>
<%-- Imagine a user typed this as their name: --%>
<c:set var="typed" value="<b style='color:red'>BIG RED NAME</b>"/>
<%@ include file="/WEB-INF/views/top.jspf" %>

<div class="table-wrap"><table>
    <thead><tr><th>You write</th><th>The browser shows</th></tr></thead>
    <tbody>
    <tr><td><code>\${typed}</code></td><td>${typed}</td></tr>
    <tr><td><code>&lt;c:out value="\${typed}"/&gt;</code></td><td><c:out value="${typed}"/></td></tr>
    </tbody>
</table></div>

<p>Plain <code>\${...}</code> prints the text <b>as HTML</b>, so the browser obeyed the tags.
   <code>&lt;c:out&gt;</code> turned <code>&lt;</code> into <code>&amp;lt;</code>, so the browser shows the tags as text.</p>
<p class="msg error">Here it's only bold red text. With <code>&lt;script&gt;</code> instead of <code>&lt;b&gt;</code>, it would
   run the attacker's code in every visitor's browser. That attack, <b>XSS</b>, is topic 13.</p>
<p class="note"><b>Rule:</b> anything a user typed goes through <code>&lt;c:out&gt;</code>. Numbers and your own fixed text can use <code>\${...}</code>.</p>

<%@ include file="/WEB-INF/views/bottom.jspf" %>
