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
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos of each implicit object, the scopes, and each standard action |
| [project/](project/) | **Profile Card**: a form filled into a JavaBean with `<jsp:setProperty property="*">` and shown as a card |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

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
