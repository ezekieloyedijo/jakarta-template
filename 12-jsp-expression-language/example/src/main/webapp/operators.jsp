<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="EL operators"/>
<c:set var="score" value="67"/>
<c:set var="emptyText" value=""/>
<%@ include file="/WEB-INF/views/top.jspf" %>

<p>Here <code>score</code> is ${score}.</p>
<div class="table-wrap"><table>
    <thead><tr><th>Kind</th><th>You write</th><th>Result</th></tr></thead>
    <tbody>
    <tr><td>Maths</td><td><code>\${score + 10}</code></td><td>${score + 10}</td></tr>
    <tr><td>Maths</td><td><code>\${score / 2}</code> or <code>\${score div 2}</code></td><td>${score / 2}</td></tr>
    <tr><td>Maths</td><td><code>\${score % 10}</code> or <code>\${score mod 10}</code></td><td>${score % 10}</td></tr>
    <tr><td>Compare</td><td><code>\${score &gt;= 50}</code> or <code>\${score ge 50}</code></td><td>${score >= 50}</td></tr>
    <tr><td>Compare</td><td><code>\${score == 67}</code> or <code>\${score eq 67}</code></td><td>${score == 67}</td></tr>
    <tr><td>Logic</td><td><code>\${score &gt; 50 and score &lt; 70}</code></td><td>${score > 50 and score < 70}</td></tr>
    <tr><td>Logic</td><td><code>\${not (score &gt; 90)}</code></td><td>${not (score > 90)}</td></tr>
    <tr><td>empty</td><td><code>\${empty emptyText}</code></td><td>${empty emptyText}</td></tr>
    <tr><td>empty</td><td><code>\${empty nothingAtAll}</code></td><td>${empty nothingAtAll}</td></tr>
    <tr><td>Choice</td><td><code>\${score &gt;= 50 ? 'Pass' : 'Fail'}</code></td><td>${score >= 50 ? 'Pass' : 'Fail'}</td></tr>
    <tr><td>Join text</td><td><code>\${'Grade: ' += 'B'}</code></td><td>${'Grade: ' += 'B'}</td></tr>
    </tbody>
</table></div>
<p class="note">The word forms (<code>gt lt ge le eq ne and or not div mod</code>) exist because <code>&lt;</code> and
   <code>&gt;</code> can be confusing inside HTML.</p>

<%@ include file="/WEB-INF/views/bottom.jspf" %>
