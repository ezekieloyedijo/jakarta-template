# Topic 12: JSP Expression Language (EL) and JSTL

## The problem that existed

After topic 11 our views are cleaner, but they still contain Java like this:

```jsp
<% Student s = (Student) request.getAttribute("student"); %>
<p>Name: <%= s.getName() %></p>
<% if (s == null) { %> ... <% } %>
```

- A designer has to understand casts, `null` checks, and curly braces.
- Forget one `}` and the page doesn't compile.
- If `s` is `null`, the page crashes with a `NullPointerException`.

📺 It's like using a TV by **opening the back and connecting wires** every time you want to change the channel.
It works, but only an electrician can do it.

**Expression Language (EL)** is the **remote control**. You simply write `${student.name}` and EL finds the object,
calls `getName()`, and shows nothing (instead of crashing) if it's missing.
**JSTL** (the JSP Standard Tag Library) adds the remaining buttons, such as **loops** and **if/else**, as clean tags, so the page needs **no Java at all**.

## The end goal of this topic

You should be able to:

1. Show data with `${...}`, including bean properties (`${student.name}`).
2. Explain how EL **searches the scopes** for a name.
3. Use EL's implicit objects (`param`, `sessionScope`...) and operators (`empty`, `==`, `and`, `?:`).
4. Use the JSTL core tags `<c:forEach>`, `<c:if>`, `<c:choose>`, and `<c:out>`.
5. Rewrite a scriptlet page from topic 11 with **no Java code** in it.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: scriptlets in the view** | The problem we are solving |
| 2 | **Your first `${...}`** | See it working |
| 3 | **Reading bean properties: `${student.name}`** | EL calls getters for you |
| 4 | **How EL finds names: the scope search** | page → request → session → application |
| 5 | **EL implicit objects** | `param`, `sessionScope`, `requestScope`, `header`, `cookie`... |
| 6 | **EL operators** | Maths, comparisons, `empty`, and the `? :` choice |
| 7 | **JSTL: adding the tag library** | The `taglib` line |
| 8 | **`<c:if>`, `<c:choose>`, `<c:forEach>`** | Decisions and loops without Java |
| 9 | **`<c:out>` and escaping** | Showing user text safely (leads to topic 13) |
| 10 | **Rewriting the topic 11 view** | All of it together |

## What is inside this folder

| Path | What it will show |
|---|---|
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: EL basics, scope search, operators, each JSTL core tag |
| [project/](project/) | **Student List, version 2**: topic 11's app with every scriptlet replaced by EL and JSTL |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is **Expression Language (EL)**, and what does it look like?

<details><summary>Answer</summary>A short way to <b>show data in a JSP without Java code</b>. It is written <code>${...}</code>, for example <code>${message}</code>.</details>

**2.** What does `${student.name}` do?

<details><summary>Answer</summary>It finds an attribute called <code>student</code> and calls its <b><code>getName()</code></b> method, then prints the result.</details>

**3.** If you write `${user}`, in which order does EL look through the scopes?

<details><summary>Answer</summary><b>page</b> → <b>request</b> → <b>session</b> → <b>application</b>. It uses the first one it finds.</details>

**4.** What does EL show if the value is `null` or the attribute doesn't exist?

<details><summary>Answer</summary><b>Nothing</b> (an empty string). It does <b>not</b> crash with a <code>NullPointerException</code>.</details>

**5.** How do you show the value of the form field `city` with EL?

<details><summary>Answer</summary><code>${param.city}</code></details>

**6.** How do you read `cart` from the **session** only, even if a request attribute has the same name?

<details><summary>Answer</summary><code>${sessionScope.cart}</code></details>

**7.** What does `${empty list}` return?

<details><summary>Answer</summary><code>true</code> if <code>list</code> is <code>null</code> <b>or</b> has no items (an empty list or empty string); otherwise <code>false</code>.</details>

**8.** What is **JSTL**, and which line adds its core tags to a page?

<details><summary>Answer</summary>The <b>JSP Standard Tag Library</b>: ready-made tags for loops, decisions, and more. Add it with:<br><code>&lt;%@ taglib prefix="c" uri="jakarta.tags.core" %&gt;</code></details>

**9.** Write a JSTL loop that prints the name of each student in the attribute `students`.

<details><summary>Answer</summary><pre>&lt;c:forEach var="s" items="${students}"&gt;
    &lt;p&gt;${s.name}&lt;/p&gt;
&lt;/c:forEach&gt;</pre></details>

**10.** What is the difference between `${comment}` and `<c:out value="${comment}" />`?

<details><summary>Answer</summary><code>&lt;c:out&gt;</code> <b>escapes</b> HTML characters (<code>&lt;</code> becomes <code>&amp;lt;</code>), so text a user typed cannot run as code in the page. Plain <code>${comment}</code> does <b>not</b> escape. Use <code>&lt;c:out&gt;</code> for anything a user typed (this protects against XSS, topic 13).</details>
