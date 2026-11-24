# Topic 10: JSP Implicit Objects and Standard Actions

## The problem that existed

In topic 06 you wrote JSP pages with bits of Java. But a page often needs things it never created itself:

- the **request** (to read a form field),
- the **session** (to see who is logged in),
- the app's **shared data**,
- a way to **print** into the page.

In a servlet you get these from method parameters or by calling methods. A JSP has no method you write, so where do they come from?

Also, a lot of JSP Java code did the same few jobs again and again: "include the header", "forward to another page",
"create an object and fill it from the form". Writing those as scriptlets made pages messy.

🏨 It's like checking into a **hotel room**. You don't bring your own bed, towels, kettle, or TV.
They are **already there** when you walk in (the **implicit objects**). And instead of fixing problems
yourself, you **press a button on the phone** for common services: room service, wake-up call, cleaning
(the **standard actions**, ready-made tags for common jobs).

## The end goal of this topic

You should be able to:

1. Name the **9 implicit objects** of a JSP and say what each is for.
2. Explain the four **scopes**: page, request, session, application.
3. Use the standard actions `<jsp:include>`, `<jsp:forward>`, and `<jsp:param>`.
4. Use JavaBeans in a page with `<jsp:useBean>`, `<jsp:setProperty>`, and `<jsp:getProperty>`.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: JSP is a servlet in disguise** | Where the implicit objects come from |
| 2 | **The 9 implicit objects** | The room's furniture |
| 3 | **`request`, `response`, `out`** | The three you use every day |
| 4 | **`session` and `application`** | Data that lives longer than one request |
| 5 | **The four scopes** | How long each kind of data lives |
| 6 | **`pageContext`, `config`, `page`, `exception`** | The less common ones |
| 7 | **What standard actions are** | Tags instead of scriptlets |
| 8 | **`<jsp:include>`, `<jsp:forward>`, `<jsp:param>`** | Combining and passing pages |
| 9 | **JavaBeans and `<jsp:useBean>`, `setProperty`, `getProperty`** | Objects in a page without Java code |

## What is inside this folder

| Path | What it will show |
|---|---|
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | Small demos of each implicit object, the scopes, an error page, and each standard action |
| [project/](project/) | **Contact Book**: contact cards filled into a JavaBean with `<jsp:setProperty property="*">`; add, list, edit, and delete (CRUD) |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is a **Contact Book**: a card for each student, parent, and teacher. It is built from JSP pages that use
**standard actions** and **JavaBeans** instead of most of the Java code of topic 06.

| Feature | The user... | Request | Page |
|---|---|---|---|
| **Read** | opens the cards | `GET /` (`index.jsp`) | `index.jsp` |
| **Create** | opens the empty form | `GET /add.jsp` | `add.jsp` → forwards to `WEB-INF/parts/form.jsp` |
| **Update** | opens the filled form | `GET /edit.jsp?id=2` | `edit.jsp` → forwards to `form.jsp` |
| Save (create **or** update) | submits the form | `POST /save.jsp` | `save.jsp`: `<jsp:setProperty property="*">` |
| **Delete** | clicks Delete | `POST /delete.jsp` | `delete.jsp` |

📇 A school office with a **card box** (`ContactBook`, one for the whole school: application scope) and **blank
cards** (`Contact` beans) that the clerk fills in from your form, box by labelled box.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Builds and runs the app |
| `Contact.java` | ✅ **Yes** | **The JavaBean**: no-arg constructor, getters/setters, so `setProperty` can fill it |
| `ContactBook.java` | ✅ **Yes** | The storage, also a bean (no-arg constructor), so `<jsp:useBean scope="application">` can create it. CRUD + validation |
| `WEB-INF/parts/header.jsp`, `footer.jsp` | ✅ **Yes** | Included with `<jsp:include>`; the title arrives as a `<jsp:param>` |
| `WEB-INF/parts/form.jsp` | ✅ **Yes** | The one form for Add and Edit; it finds the `contact` bean in the request scope |
| `WEB-INF/parts/error.jsp` | ⚪ Recommended | The error page (`isErrorPage`, uses `exception`). Without it users see Tomcat's 500 page |
| `index.jsp`, `add.jsp`, `edit.jsp`, `save.jsp`, `delete.jsp` | ✅ **Yes** | One page per job |
| `WEB-INF/web.xml` | ⚪ Recommended | Welcome page `index.jsp` and UTF-8 for all JSPs |
| `css/style.css` | ⚪ Optional, but recommended | The look |

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic10-project</artifactId>`.

### Step 2: `Contact.java`, the bean

Fields `id` (int, `0` = not saved yet), `name`, `phone`, `email`, `role`, each with a getter and a setter (setters trim
and turn `null` into `""`). Plus `copy()` and `getInitials()` ("AO" for the round avatar).

### Step 3: `ContactBook.java`, the box of cards

| Method | Purpose |
|---|---|
| `ContactBook()` | The **no-arg constructor** `useBean` calls once; adds 3 sample contacts |
| `getAll()` | **Read**: copies, sorted by name (a getter, so `book.getAll()` reads naturally) |
| `find(id)` | A copy of one contact, or `null` |
| `save(contact)` | **Create** if `id == 0` (gives a new id), **update** if the id exists, `false` otherwise |
| `delete(id)` | **Delete** |
| `static validate(contact)` | Name required (max 50), phone 7–15 digits, email optional but valid, role from the list |
| `static toId`, `escape`, `ROLES` | Helpers |

It always stores and returns **copies**, so a page that changes a bean can never change the saved card by accident.

### Step 4: `web.xml`

`<welcome-file>index.jsp</welcome-file>` and a `<jsp-config>` with `<page-encoding>UTF-8</page-encoding>` for `*.jsp`.

### Step 5: The parts in `WEB-INF/parts/`

- **`header.jsp`**: `<head>`, menu, and `<h1>`, with the title from `request.getParameter("title")` (escaped).
- **`footer.jsp`**: closes the page and shows "Contacts saved: N", read from `application.getAttribute("book")`.
- **`error.jsp`**: `<%@ page isErrorPage="true" %>`, a friendly message, and `exception` printed to the terminal.

### Step 6: `index.jsp`, Read

```jsp
<jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/>
<jsp:include page="/WEB-INF/parts/header.jsp">
    <jsp:param name="title" value="All contacts"/>
</jsp:include>
```

Then loop over `book.getAll()` and print one card each: avatar, name, role badge, phone, email, **Edit** link,
and **Delete** form. Every page has `errorPage="/WEB-INF/parts/error.jsp"`.

✅ **Check:** <http://localhost:8080/> shows 3 cards. The terminal says `ContactBook created by <jsp:useBean>`.

### Step 7: `WEB-INF/parts/form.jsp`, the shared form

`<jsp:useBean id="contact" class="com.school.project.Contact" scope="request"/>` **finds** the contact that
`edit.jsp` or `save.jsp` left in the request, or **creates** an empty one (for Add). `editing` is `contact.getId() != 0`.
It prints the red error list (from the request attribute `errors`), then the form that POSTs to `save.jsp`, with a hidden `id`.

### Step 8: `add.jsp` and `edit.jsp`, opening the form

- `add.jsp`: just `<jsp:forward page="/WEB-INF/parts/form.jsp"/>`.
- `edit.jsp`: read `id` (400 / 404), `request.setAttribute("contact", book.find(id))`, then forward to the form.

### Step 9: `save.jsp`, Create and Update

1. Only POST. `request.setCharacterEncoding("UTF-8")`. If `id` is not empty and not a number → 400.
2. `<jsp:useBean id="contact" ... scope="request"/>` and **`<jsp:setProperty name="contact" property="*"/>`**. One line fills the whole bean.
3. `ContactBook.validate(contact)`. If there are errors: status 400, `request.setAttribute("errors", ...)`, and
   `<jsp:forward page="/WEB-INF/parts/form.jsp"/>`. The form finds the same bean, so typed values stay.
4. `book.save(contact)` (404 if the id doesn't exist), then redirect to `index.jsp?msg=added` or `msg=updated`.

✅ **Check:** add a contact, then edit it. Submit an empty name: the red box appears and the phone you typed is still there.

### Step 10: `delete.jsp`, Delete

POST only (405 otherwise), 400 / 404 for a bad id, `book.delete(id)`, redirect with `msg=deleted`.

✅ **Final check:** all four CRUD actions work, and `/WEB-INF/parts/form.jsp` gives a 404 (it can only be reached by a forward).

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is an **implicit object** in JSP?

<details><summary>Answer</summary>An object the container <b>creates for every JSP automatically</b>, so you can use it straight away without declaring it, for example <code>request</code> or <code>session</code>.</details>

**2.** Name all 9 implicit objects.

<details><summary>Answer</summary><code>request</code>, <code>response</code>, <code>out</code>, <code>session</code>, <code>application</code>, <code>config</code>, <code>pageContext</code>, <code>page</code>, and <code>exception</code>.</details>

**3.** What is the `out` object, and what is its type?

<details><summary>Answer</summary>The writer that prints text into the page. Its type is <code>JspWriter</code>. <code>&lt;%= x %&gt;</code> is a short way of writing <code>out.print(x)</code>.</details>

**4.** Which implicit object is the same as the servlet's `ServletContext`?

<details><summary>Answer</summary><code>application</code>. Data stored in it is shared by <b>all users</b> of the whole app.</details>

**5.** When can you use the `exception` implicit object?

<details><summary>Answer</summary>Only in an <b>error page</b>: a JSP with <code>&lt;%@ page isErrorPage="true" %&gt;</code>.</details>

**6.** Name the four scopes, from shortest-lived to longest-lived.

<details><summary>Answer</summary><b>page</b> (this page only) → <b>request</b> (this one request, including forwards) → <b>session</b> (one user, many requests) → <b>application</b> (all users, until the app stops).</details>

**7.** What is the difference between `<%@ include file="..." %>` and `<jsp:include page="..." />`?

<details><summary>Answer</summary>The <b>directive</b> copies the file in once, at <b>translation time</b> (like copy-paste). The <b>action</b> runs the other page and inserts its output on <b>every request</b> (at runtime), so its content can change.</details>

**8.** What does `<jsp:forward page="result.jsp" />` do, and how do you pass a value along?

<details><summary>Answer</summary>It hands the request over to <code>result.jsp</code> (like a servlet's <code>forward</code>; the URL does not change). Pass a value with <code>&lt;jsp:param name="color" value="blue" /&gt;</code> inside it.</details>

**9.** What rules must a class follow to be a **JavaBean**?

<details><summary>Answer</summary>A <b>public no-argument constructor</b>, <b>private fields</b>, and public <b>getters and setters</b> named after the properties (<code>getName()</code> / <code>setName()</code>).</details>

**10.** What do these three tags do together?

```jsp
<jsp:useBean id="user" class="com.school.project.User" scope="request" />
<jsp:setProperty name="user" property="*" />
<jsp:getProperty name="user" property="name" />
```

<details><summary>Answer</summary><code>useBean</code> <b>finds or creates</b> a <code>User</code> object called <code>user</code>. <code>setProperty property="*"</code> <b>fills every property</b> from the form fields with matching names. <code>getProperty</code> <b>prints</b> the user's <code>name</code> (it calls <code>getName()</code>).</details>
