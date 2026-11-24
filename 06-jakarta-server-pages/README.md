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
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | Small demos of each JSP building block, plus a servlet that forwards to a JSP |
| [project/](project/) | **Grade Book**: add, list, edit, and delete subjects with their scores; shows each grade and the average (CRUD in JSP pages) |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is a **Grade Book**: a student's subjects and scores, with each subject's grade and the overall average.
It is built **only from JSP pages** (no servlets), so you practise every JSP building block.

| Feature | The user... | Request | Page |
|---|---|---|---|
| **Read** | opens the list | `GET /index.jsp` (or just `/`) | `index.jsp` |
| **Create** | opens the empty form, then submits it | `GET` then `POST /add.jsp` | `add.jsp` |
| **Update** | opens the filled form, then submits it | `GET /edit.jsp?id=2` then `POST /edit.jsp` | `edit.jsp` |
| **Delete** | clicks Delete | `POST /delete.jsp` | `delete.jsp` |

📝 Think of a **report-card template** from the school office: the boxes and lines are printed (the HTML), and the
teacher only fills in the blanks (the Java). The office's rule book (`GradeBook.java`) says what each score is worth.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Builds and runs the app |
| `Subject.java` | ✅ **Yes** | The data: one subject (id, name, score) and its grade |
| `GradeBook.java` | ✅ **Yes** | The storage (in `application`), the CRUD methods, the grade scale, and the form checks |
| `WEB-INF/fragments/header.jspf`, `footer.jspf` | ✅ **Yes** | The top and bottom of every page, pasted in with `<%@ include %>` |
| `WEB-INF/fragments/subject-form.jspf` | ✅ **Yes** | The one form shared by Add and Edit |
| `index.jsp`, `add.jsp`, `edit.jsp`, `delete.jsp` | ✅ **Yes** | One page per job |
| `WEB-INF/web.xml` | ⚪ Recommended | Makes `index.jsp` the welcome page and reads JSP files as UTF-8. Without it `/` gives a 404 (open `/index.jsp` instead) |
| `css/style.css` | ⚪ Optional, but recommended | The look |
| `index.html` | ❌ **Not used** | `index.jsp` is the home page |

**The order:** the Java classes first (the pages use them), then the fragments, then one page at a time.

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic06-project</artifactId>`.

### Step 2: `Subject.java`, the data

`final` fields `id`, `name`, `score` with getters, plus `getGrade()`, which calls `GradeBook.gradeFor(score)`.

### Step 3: `GradeBook.java`, the rules and the storage

| Method | Purpose |
|---|---|
| `static of(application)` | The one shared grade book, stored as an attribute of the `ServletContext`. Created with 3 sample subjects the first time |
| `add(name, score)` | **Create**: gives the subject the next id |
| `all()`, `find(id)` | **Read**: every subject sorted by id, or one (or `null`) |
| `update(id, name, score)` | **Update**: `false` if the id doesn't exist |
| `delete(id)` | **Delete**: `false` if the id doesn't exist |
| `average()` | The average score to 1 decimal place |
| `static gradeFor(score)` | 70+ A, 60+ B, 50+ C, 45+ D, 40+ E, below 40 F |
| `static validate(name, scoreText)` | The list of problems: name required (max 40), score a whole number 0–100 |
| `static toScore(text)`, `toId(text)` | Turn typed text into a number, or `null` |
| `static escape(text)` | Safe HTML (pages call it for every name they print) |

### Step 4: `web.xml`

Add `<welcome-file>index.jsp</welcome-file>` and the `<jsp-config>` block with `<page-encoding>UTF-8</page-encoding>`
for `*.jsp` and `*.jspf`. There are no servlets to register.

### Step 5: The fragments in `WEB-INF/fragments/`

- **`header.jspf`**: `<!DOCTYPE html>`, the `<head>` with the stylesheet, the header bar with the menu, and `<h1><%= GradeBook.escape(title) %></h1>`.
  It uses a variable `title` that each page creates first.
- **`footer.jspf`**: closes `</main></body></html>`.
- **`subject-form.jspf`**: the red error list, then the form. It uses the variables `action`, `id`, `name`, `score`, `errors`, and `button`.
  The hidden `id` input is only printed when `id != null` (editing).

### Step 6: `index.jsp`, Read

1. `<%@ page import="com.school.project.GradeBook, ..." %>`
2. A scriptlet: `title`, `GradeBook book = GradeBook.of(application)`, `book.all()`, and a fixed message for `?msg=added|updated|deleted`.
3. Include the header. If the list is empty, say so; otherwise loop with `<% for (Subject s : subjects) { %>` and print one row
   with an **Edit** link (`edit.jsp?id=...`) and a **Delete** form (POST to `delete.jsp` with a hidden id).
4. A `<tfoot>` row with `book.average()` and its grade. Include the footer.

✅ **Check:** <http://localhost:8080/> shows Mathematics, English Language, and Biology with an average of 65.7 (B).

### Step 7: `add.jsp`, Create

The scriptlet sets `action = "add.jsp"`, `id = null`, empty `name` and `score`, and no errors. Then:
`if ("POST".equals(request.getMethod()))` → read and trim the two parameters → `GradeBook.validate(...)` →
if there are no errors: `add(...)`, `response.sendRedirect("index.jsp?msg=added")`, and **`return;`**.
Otherwise `response.setStatus(400)` and fall through to show the form with the errors.
The rest is just three includes: header, form, footer.

✅ **Check:** adding "Chemistry, 38" shows an F. Submitting an empty name shows the red box and keeps what you typed.

### Step 8: `edit.jsp`, Update

Like `add.jsp`, but first read `id` (**400** if it isn't a number, **404** if there is no such subject; use
`response.sendError(...)` then `return;`). On GET, fill `name` and `score` from the saved subject. On POST, validate
and call `book.update(...)`.

### Step 9: `delete.jsp`, Delete

No HTML at all. If the method isn't POST → **405**. Read `id` (400 / 404 as above), call `delete`, redirect with
`msg=deleted`.

✅ **Final check:** edit and delete work, and `/edit.jsp?id=99` gives a 404.
Open `/WEB-INF/fragments/header.jspf`: 404, because fragments are private.

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
