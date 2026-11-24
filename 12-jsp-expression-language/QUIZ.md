# QUIZ 12: JSP Expression Language and JSTL

## Part A: Quick check

**1.** Which getter does `${student.className}` call? And `${student.prefect}` (a boolean)?

<details><summary>Answer</summary><code>getClassName()</code>, and <code>isPrefect()</code>.</details>

**2.** The attribute `colour` exists in the session and in the request. What does `${colour}` show?

<details><summary>Answer</summary>The <b>request</b> one. EL searches page → request → session → application and stops at the first match.</details>

**3.** What does `${nobodyAddedThis}` print?

<details><summary>Answer</summary>Nothing (an empty string). No error, no "null".</details>

**4.** Write the `taglib` line for the JSTL core tags.

<details><summary>Answer</summary><code>&lt;%@ taglib prefix="c" uri="jakarta.tags.core" %&gt;</code></details>

**5.** Write the EL for "the request parameter `q`".

<details><summary>Answer</summary><code>${param.q}</code>. Print it with <code>&lt;c:out value="${param.q}"/&gt;</code>, because a user typed it.</details>

**6.** Which is true when `errors` is an empty list: `${errors == null}` or `${empty errors}`?

<details><summary>Answer</summary><code>${empty errors}</code>. <code>empty</code> is true for null, an empty string, and an empty collection.</details>

**7.** JSTL has `<c:if>`. Where is `<c:else>`?

<details><summary>Answer</summary>There isn't one. Use <code>&lt;c:choose&gt;</code> with <code>&lt;c:when&gt;</code> and <code>&lt;c:otherwise&gt;</code>.</details>

## Part B: Explain in your own words

**8.** What is the difference between `${s.name}` and `<c:out value="${s.name}"/>`, and when must you use the second?

<details><summary>Model answer</summary><code>${s.name}</code> prints the text as HTML, so tags in it are obeyed. <code>&lt;c:out&gt;</code> escapes it first, so tags are shown as text. Use <code>&lt;c:out&gt;</code> for anything a user typed, otherwise an attacker can put a script on the page (XSS).</details>

**9.** Topic 12's project changed only the JSP files. Why didn't the controller or the model have to change?

<details><summary>Model answer</summary>Because of MVC. The controller still puts the same attributes (<code>students</code>, <code>q</code>, <code>form</code>, <code>errors</code>...) in the request, and the model still provides the same data. Only the way the view reads and shows them changed.</details>

**10.** Why is EL "safer" for beginners than `<%= request.getAttribute("x").toString() %>`?

<details><summary>Model answer</summary>EL handles missing values quietly (empty instead of a <code>NullPointerException</code>), needs no casts or imports, and is shorter. There is less Java to get wrong in the view.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Show "⭐ Teen" next to the age of every student aged 13 to 19, using only EL/JSTL.

<details><summary>Answer</summary><code>&lt;td&gt;${s.age}&lt;c:if test="${s.age ge 13 and s.age le 19}"&gt; ⭐ Teen&lt;/c:if&gt;&lt;/td&gt;</code></details>

**12.** Number the rows 1, 2, 3... (not the id) in the list.

<details><summary>Hint</summary>Add <code>varStatus="status"</code> to <code>&lt;c:forEach&gt;</code> and print <code>${status.count}</code> in a new first column.</details>

**13.** Colour the class badge: SS classes in green, JSS classes in blue, using `<c:choose>`.

<details><summary>Hint</summary><code>&lt;c:choose&gt;&lt;c:when test="${s.className.startsWith('SS')}"&gt;&lt;span class="badge ok"&gt;...&lt;/span&gt;&lt;/c:when&gt;&lt;c:otherwise&gt;&lt;span class="badge"&gt;...&lt;/span&gt;&lt;/c:otherwise&gt;&lt;/c:choose&gt;</code></details>

**14.** Break it on purpose: in `student-list.jsp`, change `<c:out value="${s.name}"/>` to `${s.name}`, restart, and add a student named `<i>Ada</i>`. What do you see? Put `<c:out>` back.

<details><summary>Answer</summary>The name appears in <i>italics</i>, because the browser obeyed the tag. With a <code>&lt;script&gt;</code> it would run code. With <code>&lt;c:out&gt;</code> back, you see <code>&lt;i&gt;Ada&lt;/i&gt;</code> as plain text.</details>
