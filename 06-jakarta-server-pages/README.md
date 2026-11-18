# Topic 06: Jakarta Server Pages (JSP)

## The problem that existed

In topic 04 every page was built inside Java with lines like this:

```java
out.println("<html><body>");
out.println("<h1>Hello " + name + "</h1>");
out.println("<table><tr><td>" + price + "</td></tr></table>");
```

For a real page with hundreds of lines of HTML this becomes painful:

- It's hard to read and easy to break (one missing quote and nothing compiles).
- A web designer who knows HTML but not Java cannot touch the page.
- Every small design change means changing Java code and rebuilding.

✉️ It's like writing a **wedding invitation** by dictating every single word, comma, and space of it to a typist,
even though 95% of it is the same for every guest.

**JSP** turns this around. You write a **normal HTML page** and put **small bits of Java** only where something changes,
like a printed invitation with a blank line where the guest's name is filled in.
Behind the scenes the container **turns each JSP into a servlet** for you.

## The end goal of this topic

You should be able to:

1. Write a JSP page and run it.
2. Explain that a JSP is **translated into a servlet** and compiled the first time it is requested.
3. Use the JSP building blocks: **expressions**, **scriptlets**, **declarations**, **directives**, and **comments**.
4. Say when to use a JSP and when to use a servlet.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: the `out.println` pain** | The problem we are solving |
| 2 | **Your first JSP** | See it working before learning the parts |
| 3 | **What happens behind the scenes** (translate → compile → run) | JSP is really a servlet in disguise |
| 4 | **Expressions `<%= ... %>`** | The simplest part: print one value |
| 5 | **Scriptlets `<% ... %>`** | Run Java statements: `if`, loops |
| 6 | **Declarations `<%! ... %>`** | Fields and methods (and why they are dangerous) |
| 7 | **Directives `<%@ ... %>`** | Page settings, imports, and including other files |
| 8 | **Comments** | HTML comments vs JSP comments |
| 9 | **JSP and servlets working together** | Each does the job it is good at (leads to MVC in topic 11) |

## What is inside this folder

| Path | What it will show |
|---|---|
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos of each JSP building block |
| [project/](project/) | **Grade Calculator**: a form posted to a JSP that shows the average and grade |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is a **JSP**?

<details><summary>Answer</summary>A <b>Jakarta Server Page</b>: an HTML page (file ending <code>.jsp</code>) with small pieces of Java inside, which the server runs to build a dynamic page.</details>

**2.** What problem with servlets does JSP solve?

<details><summary>Answer</summary>Writing HTML with many <code>out.println(...)</code> lines inside Java is hard to read and change. JSP lets you write <b>normal HTML</b> and add Java only where needed.</details>

**3.** What does the container do with a JSP the first time it is requested?

<details><summary>Answer</summary>It <b>translates</b> the JSP into a <b>servlet</b> (a <code>.java</code> file), <b>compiles</b> it, and then runs it. Later requests reuse that compiled servlet.</details>

**4.** What does `<%= name %>` do?

<details><summary>Answer</summary>It is an <b>expression</b>: it <b>prints the value</b> of <code>name</code> into the page. (No semicolon inside it.)</details>

**5.** What is a **scriptlet**, and what does it look like?

<details><summary>Answer</summary>A block of normal Java statements, written <code>&lt;% ... %&gt;</code>, for example <code>&lt;% if (age &gt;= 18) { %&gt; ... &lt;% } %&gt;</code>. It runs on <b>every request</b>.</details>

**6.** What is a **declaration** (`<%! ... %>`), and why should you be careful with it?

<details><summary>Answer</summary>It declares a <b>field or method</b> of the generated servlet class. A field is <b>shared by all users</b> (one servlet object, many threads), so it causes the same thread-safety problems as servlet fields.</details>

**7.** Write the directive that imports `java.time.LocalDate` into a JSP.

<details><summary>Answer</summary><code>&lt;%@ page import="java.time.LocalDate" %&gt;</code></details>

**8.** What is the difference between `<!-- ... -->` and `<%-- ... --%>`?

<details><summary>Answer</summary><code>&lt;!-- --&gt;</code> is an <b>HTML comment</b>: it is sent to the browser (visible in "View Source"). <code>&lt;%-- --%&gt;</code> is a <b>JSP comment</b>: it is removed on the server and the browser never sees it.</details>

**9.** What does `<%@ include file="header.jsp" %>` do?

<details><summary>Answer</summary>It <b>copies the contents</b> of <code>header.jsp</code> into this page when the JSP is translated. It is useful for parts every page shares, such as a header or menu.</details>

**10.** In which folder do JSP files go in our project, and why might you put one inside `WEB-INF`?

<details><summary>Answer</summary>In <code>src/main/webapp</code>. A JSP inside <code>WEB-INF</code> is <b>private</b>: the browser cannot open it directly, and only a servlet can <b>forward</b> to it. This is how MVC apps (topic 11) hide their views.</details>
