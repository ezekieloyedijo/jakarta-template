# LESSON 10: JSP Implicit Objects and Standard Actions

> **The big picture for this lesson: A FURNISHED CLASSROOM**
>
> - **The furniture already in the room** (board, chalk, register, timetable) = the **implicit objects**: ready to use, you never bring them
> - **The board** you write on = `out`
> - **The note a pupil hands in** = `request`; **your reply** = `response`
> - **The pupil's own exercise book**, kept all term = `session`
> - **The notice board in the corridor**, for the whole school = `application`
> - **Ready-made stamps** ("Seen", "Excellent", "See me") instead of writing by hand = **standard actions** (`<jsp:...>` tags)
> - **A form with labelled boxes** that the office fills in automatically = a **JavaBean**

---

## Step 1: Recap, JSP is a servlet in disguise

Topic 06, Step 3: Tomcat turns every JSP into a servlet with a method `_jspService(request, response)`.
At the top of that method, Tomcat writes lines like these **for you**:

```java
public void _jspService(HttpServletRequest request, HttpServletResponse response) {
    PageContext pageContext = ...;
    HttpSession session = pageContext.getSession();
    ServletContext application = pageContext.getServletContext();
    ServletConfig config = pageContext.getServletConfig();
    JspWriter out = pageContext.getOut();
    Object page = this;
    // ... then YOUR page
}
```

That's why you can write `request.getParameter(...)` in a JSP without creating `request`: it's already a variable.

---

## Step 2: The 9 implicit objects

| Object | Type | What it is | Used |
|---|---|---|---|
| `request` | `HttpServletRequest` | What the browser sent | **every day** |
| `response` | `HttpServletResponse` | The answer going back | often |
| `out` | `JspWriter` | Writes into the page | often |
| `session` | `HttpSession` | This visitor's data (topic 05) | often |
| `application` | `ServletContext` | Data for the whole app | often |
| `pageContext` | `PageContext` | The toolbox that holds all the others, plus page scope | sometimes |
| `config` | `ServletConfig` | This JSP's settings | rarely |
| `page` | `Object` | The JSP's servlet object itself (`this`) | rarely |
| `exception` | `Throwable` | The error that happened (**only in error pages**) | in error pages |

🪑 The furniture: you don't carry a board into the classroom. It's already there.

---

## Step 3: `request`, `response`, `out`

```jsp
<p>You asked for <%= request.getRequestURI() %> with <%= request.getMethod() %></p>
<p>Name: <%= Person.escape(request.getParameter("name")) %></p>

<% response.setHeader("X-Demo", "implicit-objects"); %>   <%-- before any HTML is sent --%>

<%
    for (int i = 3; i >= 1; i--) {
        out.println("<span>" + i + "</span>");   // print from INSIDE a scriptlet
    }
%>
```

`<%= x %>` is just a short way of writing `<% out.print(x); %>`.

⚠️ A gotcha: inside a scriptlet, even in a Java comment, never write `%>`. It ends the scriptlet early and the page fails to compile.

**Demo:** `example/request-response-out.jsp`

---

## Step 4: `session` and `application`

```jsp
<%
    Integer mine = (Integer) session.getAttribute("myVisits");          // only YOU
    session.setAttribute("myVisits", mine == null ? 1 : mine + 1);

    synchronized (application) {                                         // EVERYBODY shares it
        Integer all = (Integer) application.getAttribute("allVisits");
        application.setAttribute("allVisits", all == null ? 1 : all + 1);
    }
%>
```

`application` is the same `ServletContext` that servlets get with `getServletContext()` (topics 04 and 08). Many
users change it at the same time, so changes need `synchronized`.

**Demo:** `example/session-application.jsp`, in a normal window and in a private window.

---

## Step 5: The four scopes

A **scope** is "how long an attribute lives, and who can see it":

| Scope | Lives for | Who sees it | Set with |
|---|---|---|---|
| **page** | this JSP, this request | only this page | `pageContext.setAttribute(name, value)` |
| **request** | one request, **including forward and include** | the pages that handle this request | `request.setAttribute(...)` |
| **session** | one visitor, until logout or timeout | only this visitor | `session.setAttribute(...)` |
| **application** | until the app stops | everybody | `application.setAttribute(...)` |

```
 page  ⊂  request  ⊂  session  ⊂  application
 (shortest)                          (longest)
```

`pageContext.findAttribute("colour")` searches **page → request → session → application** and returns the first
match. Remember this order: EL (`${colour}`) uses exactly the same search in topic 12.

🏫 page = a sticky note on your desk; request = a note passed along one row of desks; session = your exercise book;
application = the school notice board.

**Demo:** `example/scopes.jsp`

---

## Step 6: `pageContext`, `config`, `page`, `exception`

- **`pageContext`**: the toolbox. `pageContext.getRequest()` is the same object as `request`. It also gives the page scope.
- **`config`**: the JSP's `ServletConfig`, for example `config.getServletName()`.
- **`page`**: `this`, the servlet object. You almost never need it.
- **`exception`**: only in a page marked `isErrorPage="true"`:

```jsp
<%-- boom.jsp: if anything here throws, show the error page instead of Tomcat's 500 page --%>
<%@ page errorPage="/WEB-INF/parts/error.jsp" %>
<% int x = 30 / 0; %>
```
```jsp
<%-- WEB-INF/parts/error.jsp --%>
<%@ page isErrorPage="true" %>
<p>Sorry! (<%= exception.getClass().getName() %>)</p>
```

The error page lives in `WEB-INF`, so nobody can open it directly (where `exception` would be `null`).

**Demos:** `example/others.jsp` and `example/boom.jsp`

---

## Step 7: What standard actions are

Scriptlets mix Java into HTML, and pages become hard to read. **Standard actions** are **XML tags** that do common
jobs without Java:

| Action | Does |
|---|---|
| `<jsp:include page="..."/>` | Runs another page and puts its output here |
| `<jsp:forward page="..."/>` | Hands the whole request to another page |
| `<jsp:param name="..." value="..."/>` | Sends a value to an included or forwarded page |
| `<jsp:useBean id="..." class="..." scope="..."/>` | Finds or creates a JavaBean |
| `<jsp:setProperty name="..." property="..."/>` | Fills bean properties (from the form) |
| `<jsp:getProperty name="..." property="..."/>` | Prints a bean property |

They are built into every JSP: no `taglib` line, no jar. (JSTL in topic 12 adds many more tags like these.)

---

## Step 8: `<jsp:include>`, `<jsp:forward>`, `<jsp:param>`

```jsp
<jsp:include page="/WEB-INF/parts/greeting.jsp">
    <jsp:param name="who" value="SS2"/>
    <jsp:param name="subject" value="Chemistry"/>
</jsp:include>
```

The included page reads them as normal parameters: `request.getParameter("who")`.

**The two kinds of include:**

| | `<%@ include file="..." %>` (topic 06) | `<jsp:include page="..."/>` |
|---|---|---|
| When | at **translation** time: the text is pasted in | at **request** time: the page **runs** and its output is pasted in |
| Shares variables with the page? | **Yes** | **No** (use `<jsp:param>` or request attributes) |
| Can send parameters? | No | **Yes**, with `<jsp:param>` |
| Like | photocopying a page into your notes | phoning a colleague and writing down what they say |

**Forward:**

```jsp
<% if (problem) { %>
    <jsp:forward page="/WEB-INF/parts/error.jsp"/>   <%-- nothing below this line runs --%>
<% } %>
```

Like `RequestDispatcher.forward` in topic 04: it happens inside the server, the address bar doesn't change, and
request attributes travel along.

**Demo:** `example/actions.jsp` and `actions.jsp?go=yes`

---

## Step 9: JavaBeans and `<jsp:useBean>`, `setProperty`, `getProperty`

A **JavaBean** is a normal class that follows three rules:
1. a `public` constructor **with no parameters**;
2. `private` fields;
3. `public getX()` / `setX(...)` for every property (`isX()` for a boolean).

```java
public class Person {
    private String name = "";
    private int age;
    public Person() { }
    public String getName() { return name; }      public void setName(String n) { name = n; }
    public int getAge()     { return age; }       public void setAge(int a)     { age = a; }
}
```

**1. Find or create:**

```jsp
<jsp:useBean id="person" class="com.school.example.Person" scope="session"/>
```

"Look in the **session** for an attribute called `person`. If it isn't there, create `new Person()` and store it.
Either way, call it `person` on this page."

**2. Fill it from the form:**

```jsp
<jsp:setProperty name="person" property="*"/>               <%-- EVERY parameter with a matching name --%>
<jsp:setProperty name="person" property="age"/>             <%-- just the parameter "age" --%>
<jsp:setProperty name="person" property="name" value="Ada"/> <%-- a fixed value --%>
```

`property="*"` calls `setName(request.getParameter("name"))`, `setAge(...)`, and so on, and **converts** text to
`int` for you. Empty parameters are skipped. ⚠️ A value like `"abc"` for an `int` property causes an error, so check
numbers first.

**3. Print a property:**

```jsp
<jsp:getProperty name="person" property="age"/>
```

⚠️ `<jsp:getProperty>` does **not** escape HTML. Use it for numbers; for text a user typed, print it escaped.

**Demo:** `example/bean.jsp`

---

## Now try it

The project in [project/](project/) is a **Contact Book**: contact cards for students, parents, and teachers. The book
itself is a bean in the **application** scope (`<jsp:useBean scope="application">` creates it the first time). `save.jsp`
fills a `Contact` bean from the form with **one** `<jsp:setProperty property="*"/>`, and the header, footer, and form
are joined with `<jsp:include>` and `<jsp:forward>`. You can add, list, edit, and delete contacts (CRUD).
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
