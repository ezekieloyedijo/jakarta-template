# LESSON 06: Jakarta Server Pages (JSP)

> **The big picture for this lesson: A FILL-IN-THE-BLANKS LETTER**
>
> - A **printed letter with blank spaces** ("Dear ______, your score is ___") = a **JSP page** (HTML with gaps)
> - **Writing a word in a blank** = an **expression** `<%= ... %>`
> - **A note in the margin: "repeat this line for every subject"** = a **scriptlet** `<% ... %>`
> - **The instructions on the cover: "print on A4, attach the school logo"** = a **directive** `<%@ ... %>`
> - **The secretary who turns your template into a finished letter** = the **JSP engine** (Jasper, inside Tomcat)

---

## Step 1: Recap, the `out.println` pain

In topic 04 every page was built inside Java strings:

```java
out.println("<tr><td>" + escape(s.getName()) + "</td><td>" + s.getScore() + "</td></tr>");
```

That is **hard to read**, **easy to break** (one missing `"` and nothing compiles), and a web designer who knows HTML but
not Java **can't help**. The page is hidden inside Java.

A JSP turns this upside down: **you write the HTML, and put small bits of Java inside it.**

| Servlet | JSP |
|---|---|
| Java with HTML inside | HTML with Java inside |
| Good at: logic, checking input, deciding | Good at: showing a page |

---

## Step 2: Your first JSP

A JSP is a file ending in **`.jsp`**, placed in `src/main/webapp/` (next to `index.html`). No `@WebServlet`, no
`web.xml` entry: Tomcat finds `.jsp` files by itself.

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<body>
    <h1>My first JSP</h1>
    <p>The time on the server is <b><%= java.time.LocalTime.now() %></b>.</p>
</body>
</html>
```

Open `http://localhost:8080/first.jsp`. Press F5: the time changes. Now **View Page Source**: the browser sees
only `<b>16:42:07</b>`, never the Java.

**Demo:** `example/first.jsp`

---

## Step 3: What happens behind the scenes

**A JSP is a servlet in disguise.** The first time someone opens it, Tomcat does three things:

```
first.jsp  ──1. TRANSLATE──▶  first_jsp.java  ──2. COMPILE──▶  first_jsp.class  ──3. RUN──▶  HTML page
           (HTML becomes out.write("..."),                       (a real servlet,
            your Java is copied in)                               loaded once)
```

From then on, every request just runs the compiled servlet (step 3). If you **edit** the `.jsp`, Tomcat notices and
repeats steps 1 and 2.

Want to see it? After opening a page, look in
`target/cargo/configurations/tomcat10x/work/Catalina/localhost/ROOT/org/apache/jsp/scriptlets_jsp.java`.
You'll find a method `_jspService(request, response)` full of `out.write("<p>...")`.

That's why a JSP can use **`request`**, **`response`**, **`out`**, **`session`**, and **`application`** without creating
them: they are the variables of that hidden method (topic 10 covers all of them).

📝 The secretary analogy: the first letter takes time (she reads the template and prepares). After that, each new letter is quick.

---

## Step 4: Expressions `<%= ... %>`

> An **expression** is **one value**. The server works it out and **prints it** into the page.

```jsp
<p>2 + 3 = <%= 2 + 3 %></p>                         → 2 + 3 = 5
<p>Hello <%= "ada".toUpperCase() %></p>             → Hello ADA
<p>Average: <%= book.average() %></p>              → Average: 65.7
```

⚠️ **No semicolon.** `<%= 2 + 3; %>` is an error, because Tomcat turns it into `out.print(2 + 3;);`.

⚠️ **Escape text that a user typed**, exactly like in topic 04:
`<%= GradeBook.escape(s.getName()) %>`, never `<%= s.getName() %>`.

**Demo:** `example/expressions.jsp`

---

## Step 5: Scriptlets `<% ... %>`

> A **scriptlet** holds normal Java **statements** (with semicolons): variables, `if`, loops. It prints nothing by itself.

```jsp
<% int hour = java.time.LocalTime.now().getHour(); %>

<% if (hour < 12) { %>
    <p>Good morning!</p>
<% } else { %>
    <p>Good afternoon!</p>
<% } %>
```

The surprising part: the **HTML between two scriptlets** belongs to the `if` or the loop. This repeats a row 12 times:

```jsp
<% for (int i = 1; i <= 12; i++) { %>
    <tr><td>7 × <%= i %></td><td><%= 7 * i %></td></tr>
<% } %>
```

⚠️ Every `{` must be closed by a `}` in a later scriptlet. A missing `<% } %>` gives a long compile error.

**Demo:** `example/scriptlets.jsp`

---

## Step 6: Declarations `<%! ... %>`

> A **declaration** creates a **field** or a **method** of the hidden servlet class.

```jsp
<%!
    private int pageHits = 0;               // a FIELD: one copy for EVERY user

    private String stars(int count) {       // a METHOD: fine
        return "⭐".repeat(count);
    }
%>
```

| Written in | Becomes | Shared by all users? |
|---|---|---|
| `<% int x = 0; %>` | a **local variable** in `_jspService` | No, new for every request |
| `<%! int x = 0; %>` | a **field** of the servlet | **Yes**: there is only one servlet object (topic 04) |

⚠️ **The danger:** a field in a declaration is shared by everyone, like a whiteboard in a corridor. If you store
"the logged-in user" there, every visitor sees the last person who logged in. Use declarations only for small helper
methods, and keep one user's data in the session.

**Demo:** `example/declarations.jsp`. Open it in two browsers and watch the counters.

---

## Step 7: Directives `<%@ ... %>`

> A **directive** is an instruction to the **translator** (step 3), not to the browser. It prints nothing.

**1. The `page` directive:** settings for this page.

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>      <%-- the type and character set --%>
<%@ page import="java.util.List, com.school.project.GradeBook" %>   <%-- like Java's import --%>
<%@ page errorPage="error.jsp" %>                      <%-- where to go if this page throws (topic 10) --%>
```

**2. The `include` directive:** pastes another file's text **here**, before translating.

```jsp
<% String title = "My subjects"; %>
<%@ include file="/WEB-INF/fragments/header.jspf" %>   <%-- the header can use "title" --%>
...
<%@ include file="/WEB-INF/fragments/footer.jspf" %>
```

The shared pieces end in **`.jspf`** ("JSP fragment") and live in **`WEB-INF`**, so nobody can open half a page.
Because the text is pasted in **before** compiling, the header can use the variable `title` that the page created.
Change the header once, and every page changes.

**3. The `taglib` directive:** adds a library of tags. It comes in topic 12 (JSTL).

To make `.jsp` and `.jspf` files read as UTF-8, both apps have this in `web.xml`:

```xml
<jsp-config>
    <jsp-property-group>
        <url-pattern>*.jsp</url-pattern>
        <url-pattern>*.jspf</url-pattern>
        <page-encoding>UTF-8</page-encoding>
    </jsp-property-group>
</jsp-config>
```

**Demo:** `example/directives.jsp`

---

## Step 8: Comments

| Comment | Example | Sent to the browser? |
|---|---|---|
| HTML | `<!-- note -->` | **Yes**, anyone can read it with View Page Source |
| JSP | `<%-- note --%>` | No, removed on the server |
| Java (in a scriptlet) | `<% // note %>` | No |

Rule: notes for programmers go in `<%-- --%>`. Never put a secret in an HTML comment.

**Demo:** `example/comments.jsp`

---

## Step 9: JSP and servlets working together

Each does the job it is good at:

```
Browser ──GET /timetable──▶ TimetableServlet                       WEB-INF/views/timetable.jsp
                            1. does the Java work (get the data)
                            2. request.setAttribute("lessons", list)
                            3. forward ──────────────────────────▶ reads request.getAttribute("lessons")
                                                                   and builds the HTML ──▶ Browser
```

```java
// in the servlet
request.setAttribute("lessons", lessons);
request.getRequestDispatcher("/WEB-INF/views/timetable.jsp").forward(request, response);
```
```jsp
<%-- in the JSP --%>
<% List<String> lessons = (List<String>) request.getAttribute("lessons"); %>
```

The JSP lives in `WEB-INF`, so the user **must** go through the servlet. This idea grows into **MVC** in topic 11,
and in topic 12 the scriptlets in the JSP will disappear.

**Demo:** `example/` → `/timetable`

---

## Now try it

The project in [project/](project/) is a **Grade Book** built only from JSP pages: list your subjects with their
grades and average (read), add a subject (create), edit one (update), and delete one. The Java rules live in a normal
class, `GradeBook`, and the pages share a header, a footer, and one form through `include` directives.
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
