# LESSON 12: JSP Expression Language (EL) and JSTL

> **The big picture for this lesson: A NOTICE BOARD WITH NAME SLOTS**
>
> - In topic 11 the view was a clerk who had to **open the drawer, find the folder, check its label, and read it out**
>   (`(List<Student>) request.getAttribute("students")`, then a loop in Java)
> - With EL, the board has **slots**: you write `${student.name}` and the right value **appears**
> - **The clerk who checks every drawer in order** (desk → class → staffroom → office) = the **scope search**
> - **Ready-made stickers**: "only if...", "for each...", "one of these..." = **JSTL tags** (`<c:if>`, `<c:forEach>`, `<c:choose>`)
> - **A glass cover over the slot**, so nobody can scribble on the board = **`<c:out>`** (escaping)

---

## Step 1: Recap, scriptlets in the view

Topic 11's list view still had Java in it:

```jsp
<%@ page import="com.school.project.model.Student, com.school.project.view.Html, java.util.List" %>
<% List<Student> students = (List<Student>) request.getAttribute("students"); %>
<% for (Student s : students) { %>
    <tr><td><%= Html.escape(s.getName()) %></td></tr>
<% } %>
```

Imports, a cast, a Java loop, an escaping helper. A web designer can't safely touch this. Version 2 of the same view:

```jsp
<c:forEach var="s" items="${students}">
    <tr><td><c:out value="${s.name}"/></td></tr>
</c:forEach>
```

No Java at all. That's this whole topic.

---

## Step 2: Your first `${...}`

**EL** (Expression Language) is a small language **for reading values**. You write `${ ... }` anywhere in a JSP:
in text, or inside an attribute.

```jsp
<p>Hello ${pupil.name}, you are ${pupil.age} years old.</p>
<p>Next year: ${pupil.age + 1}</p>
<a href="students?action=edit&id=${s.id}">Edit</a>
```

Two big differences from `<%= %>`:
- **Missing values print nothing.** `${nothingHere}` gives an empty string, not `null` and not a crash.
- **No casts, no imports.** EL works out the types for you.

To show the characters `${` themselves, write `\${`.

**Demo:** `example/` → `/el-basics`

---

## Step 3: Reading bean properties: `${student.name}`

`${pupil.name}` means "the attribute `pupil`, then its **property** `name`". EL calls the **getter** (the JavaBean rule
from topic 10):

| You write | EL calls |
|---|---|
| `${pupil.name}` | `pupil.getName()` |
| `${pupil.prefect}` | `pupil.isPrefect()` (a boolean) |
| `${subjects[0]}` | `subjects.get(0)` (a list or an array) |
| `${scores['English']}` or `${scores.English}` | `scores.get("English")` (a map) |
| `${classmates[1].name}` | `classmates.get(1).getName()` |
| `${students.size()}` | a method call (allowed since EL 2.2) |

⚠️ EL needs a **public getter**. A field without a getter can't be read.

---

## Step 4: How EL finds names: the scope search

`${colour}` looks for an attribute called `colour` in the four scopes from topic 10, **in this order**, and stops at
the first one it finds:

```
page  →  request  →  session  →  application
```

That's exactly `pageContext.findAttribute(...)`. To pick one scope yourself, use the scope objects:

```jsp
${requestScope.colour}   ${sessionScope.user}   ${applicationScope.schoolName}
```

In MVC, the controller puts data in the **request**, so `${students}` simply finds it there.

**Demo:** `example/scope-search.jsp`

---

## Step 5: EL implicit objects

EL has its own ready-made objects (not the same list as the 9 JSP ones):

| EL object | Means |
|---|---|
| `param.name` | `request.getParameter("name")` |
| `paramValues.subject` | `request.getParameterValues("subject")` (an array) |
| `header['User-Agent']` | `request.getHeader("User-Agent")` |
| `cookie.JSESSIONID.value` | the value of the cookie `JSESSIONID` |
| `initParam.school` | a `<context-param>` from `web.xml` |
| `pageScope`, `requestScope`, `sessionScope`, `applicationScope` | one scope (Step 4) |
| `pageContext` | the JSP's `pageContext`, for example `${pageContext.request.contextPath}` |

`${pageContext.request.contextPath}` is the EL version of `request.getContextPath()`. Use it in links to CSS and pages.

**Demo:** `example/implicit.jsp`

---

## Step 6: EL operators

| Kind | Symbols | Word forms |
|---|---|---|
| Maths | `+ - * / %` | `div`, `mod` |
| Compare | `== != < > <= >=` | `eq ne lt gt le ge` |
| Logic | `&& \|\| !` | `and or not` |
| Empty? | | `empty x`: true if `x` is null, `""`, or an empty list |
| Choice | `a ? b : c` | |
| Join text | `+=` | `${'Grade: ' += grade}` |

```jsp
${score >= 50 ? 'Pass' : 'Fail'}
${not empty errors}
${s.age gt 12 and s.age lt 20}
```

Text in EL uses single quotes `'...'` when it's inside a double-quoted attribute.

**Demo:** `example/operators.jsp`

---

## Step 7: JSTL, adding the tag library

**JSTL** (Jakarta Standard Tag Library) is a set of ready-made tags for **decisions and loops**. EL reads values;
JSTL decides what to do with them.

It isn't built into JSPs, so two things are needed:

1. **The jar.** Our parent `pom.xml` already adds it to every app (`jakarta.servlet.jsp.jstl`). Tomcat doesn't include it.
2. **The `taglib` line** at the top of each JSP that uses it:

```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
```

`prefix="c"` is a nickname you choose; by tradition it's `c` for "core". Every tag then starts with `c:`.

---

## Step 8: `<c:if>`, `<c:choose>`, `<c:forEach>`

**One decision:**

```jsp
<c:if test="${not empty message}">
    <p class="msg ok">${message}</p>
</c:if>
```

(There is no `<c:else>`. Use `<c:choose>` for that.)

**if / else if / else:**

```jsp
<c:choose>
    <c:when test="${score >= 70}">A</c:when>
    <c:when test="${score >= 60}">B</c:when>
    <c:otherwise>F</c:otherwise>
</c:choose>
```

**Loops:**

```jsp
<c:forEach var="s" items="${students}">           <%-- for (Student s : students) --%>
    <tr><td>${s.id}</td><td><c:out value="${s.name}"/></td></tr>
</c:forEach>

<c:forEach var="i" begin="1" end="5">${i} </c:forEach>     <%-- 1 2 3 4 5 --%>

<c:forEach var="day" items="${days}" varStatus="status">
    ${status.count}. ${day} ${status.last ? '(last)' : ''}
</c:forEach>
```

**Two more useful tags:**

```jsp
<c:set var="title" value="All students"/>                  <%-- create a variable (page scope) --%>
<c:url var="link" value="/students">                        <%-- build a URL, encoding the values --%>
    <c:param name="q" value="${q}"/>
</c:url>
```

**Demo:** `example/jstl-core.jsp`

---

## Step 9: `<c:out>` and escaping

`${...}` prints text **as HTML**. If a user typed `<b>BIG</b>` as their name, `${s.name}` makes the browser show
**BIG** in bold. If they typed `<script>...</script>`, the browser **runs** it. That attack is **XSS** (topic 13).

`<c:out>` **escapes** the text first (`<` becomes `&lt;`), so it is shown, not obeyed:

```jsp
${typed}                        →  BIG RED NAME (the browser obeyed the tags)
<c:out value="${typed}"/>       →  <b style='color:red'>BIG RED NAME</b> (shown as text)
```

Inside an attribute, use single quotes for the inner one:

```jsp
<input name="name" value="<c:out value='${form.name}'/>">
```

**The rule:**
- Text a **user** typed (names, search words, parameters) → **`<c:out>`**.
- Numbers, ids, and your **own fixed** text → `${...}` is fine.

**Demo:** `example/escaping.jsp`

---

## Step 10: Rewriting the topic 11 view

| Topic 11 (scriptlets) | Topic 12 (EL + JSTL) |
|---|---|
| `<%@ page import="...Student, ...Html, java.util.List" %>` | `<%@ taglib prefix="c" uri="jakarta.tags.core" %>` |
| `<% List<Student> students = (List<Student>) request.getAttribute("students"); %>` | *(nothing: `${students}` finds it)* |
| `<% if (message != null) { %> ... <% } %>` | `<c:if test="${not empty message}"> ... </c:if>` |
| `<% for (Student s : students) { %> ... <% } %>` | `<c:forEach var="s" items="${students}"> ... </c:forEach>` |
| `<%= Html.escape(s.getName()) %>` | `<c:out value="${s.name}"/>` |
| `<%= s.getAge() %>` | `${s.age}` |
| `<%= "Male".equals(form.getGender()) ? " checked" : "" %>` | `${form.gender == 'Male' ? 'checked' : ''}` |
| `String title = editing ? "Edit student" : "Add a student";` | `<c:set var="title" value="${editing ? 'Edit student' : 'Add a student'}"/>` |

The model and the controller **did not change at all**. That's MVC paying off: the view was replaced without
touching the rest.

---

## Now try it

The project in [project/](project/) is the **Student List, version 2**. It is topic 11's app with the same model and
controller, but every view is rewritten with EL and JSTL: **zero** `<% %>` in any JSP. CRUD and search work exactly
as before.
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
