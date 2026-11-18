# Topic 14: Building an Application Using JSP and Servlet

## The problem that existed

Over 13 topics you learned many separate skills: servlets, sessions, JSP, filters, databases, listeners, MVC,
EL/JSTL, and security. Each one was practised **on its own**, in a small example.

But real apps are never one skill at a time. Knowing the parts is not the same as being able to **build the whole thing**.

🏠 It's like learning to build a house: one week you learned bricklaying, then plumbing, then wiring, then roofing.
Now you have to build **one real house**, where the plumbing has to fit behind the walls and the wiring has to reach every room.

This topic builds **one complete application**, **TaskBoard**, a small to-do app where each user can register, log in,
and add, edit, complete, and delete their own tasks. Every earlier topic has a job in it.

## The end goal of this topic

You should be able to:

1. **Plan** an app before coding: pages, URLs, data, and which layer does what.
2. Build it step by step using **MVC**, a **database**, **sessions**, a **filter**, a **listener**, and **EL/JSTL**.
3. Make it **secure**: hashed passwords, PreparedStatements, escaped output, protected pages.
4. Explain where **each topic from 01 to 13** appears in the finished app.

## The learning path (read the steps in this order)

| Step | What we build | Topic it uses |
|---|---|---|
| 1 | **Plan**: features, pages, URL map, database table | 01, 03 |
| 2 | **Project setup**: pom, folders, `web.xml` | 02, 03 |
| 3 | **Model**: `User` and `Task` JavaBeans | 10, 11 |
| 4 | **Database**: H2, DAOs, a listener that creates the tables at startup | 08 |
| 5 | **Register and log in**: forms, hashed passwords, user in the session | 04, 05, 13 |
| 6 | **Protection**: a login filter for every task page | 07 |
| 7 | **Controllers**: servlets for list, add, edit, done, delete (CRUD) | 04, 11 |
| 8 | **Views**: JSPs in `WEB-INF` using EL and JSTL only | 06, 12 |
| 9 | **Polish**: Post/Redirect/Get, validation messages, error pages | 03, 04 |
| 10 | **Review**: walk through one request end to end | All |

## What is inside this folder

| Path | What it will show |
|---|---|
| `LESSON.md` | The step-by-step build guide (being written) |
| [project/](project/) | **TaskBoard**: the finished app |
| `QUIZ.md` | More questions and tasks (being written) |

This topic has no `example/` folder: the whole topic **is** one project.
Run it with `cd project` then `mvn package cargo:run`.

## Check your understanding: 10 basic questions

These questions check that you can connect the topics together. Try to answer each one **before** you open the answer.

**1.** In TaskBoard, which kind of class plays each MVC role?

<details><summary>Answer</summary><b>Controller</b> = servlets (for example <code>TaskListServlet</code>). <b>Model</b> = JavaBeans (<code>User</code>, <code>Task</code>) and DAOs (<code>TaskDao</code>). <b>View</b> = JSPs inside <code>WEB-INF</code>.</details>

**2.** What does **CRUD** stand for? Match each letter to an SQL command.

<details><summary>Answer</summary><b>C</b>reate = <code>INSERT</code>, <b>R</b>ead = <code>SELECT</code>, <b>U</b>pdate = <code>UPDATE</code>, <b>D</b>elete = <code>DELETE</code>.</details>

**3.** After a user logs in, where does the app remember who they are?

<details><summary>Answer</summary>In the <b>HttpSession</b>, for example <code>session.setAttribute("user", user)</code>. The browser keeps only the <code>JSESSIONID</code> cookie.</details>

**4.** Why do we use **one filter** to check login instead of checking inside every servlet?

<details><summary>Answer</summary>So the check is written <b>once</b> and covers every protected URL. No servlet can be forgotten, and changing the rule means changing one file.</details>

**5.** Which component creates the database tables, and when does it run?

<details><summary>Answer</summary>A <b><code>ServletContextListener</code></b> (marked <code>@WebListener</code>). Its <code>contextInitialized()</code> method runs <b>once, when the app starts</b>, before any request arrives.</details>

**6.** Why are the JSP views placed inside `WEB-INF`?

<details><summary>Answer</summary>So users <b>cannot open them directly</b>. Every request must go through a controller servlet, which checks the input and prepares the data first.</details>

**7.** After the user adds a task (a POST), why does the servlet redirect instead of showing the list directly?

<details><summary>Answer</summary>That is the <b>Post/Redirect/Get</b> pattern. If the user presses refresh, the browser repeats only the harmless GET, so the task is <b>not added twice</b>.</details>

**8.** Name three things that keep TaskBoard secure, and which attack each one stops.

<details><summary>Answer</summary><b>PreparedStatement</b> stops <b>SQL injection</b>. <b><code>&lt;c:out&gt;</code></b> escaping stops <b>XSS</b>. <b>Hashed, salted passwords</b> protect users if the database is stolen. Also: the <b>login filter</b> blocks pages for users who aren't logged in, and each user only sees <b>their own</b> tasks.</details>

**9.** Why do the views use EL and JSTL instead of scriptlets?

<details><summary>Answer</summary>The pages stay <b>clean HTML with no Java code</b>: easier to read, they don't crash on <code>null</code>, and a designer can work on them. <code>&lt;c:out&gt;</code> also escapes user text.</details>

**10.** Describe what happens, step by step, when a logged-in user opens `/tasks`.

<details><summary>Answer</summary>1. The browser sends <code>GET /tasks</code> with the <code>JSESSIONID</code> cookie. 2. Tomcat finds the session. 3. The <b>login filter</b> sees a user in the session and calls <code>chain.doFilter</code>. 4. The <b>controller servlet</b> asks the <b>TaskDao</b> for this user's tasks (a <code>SELECT</code> with a PreparedStatement). 5. The servlet puts the list in a <b>request attribute</b> and <b>forwards</b> to <code>/WEB-INF/views/tasks.jsp</code>. 6. The JSP loops over the list with <code>&lt;c:forEach&gt;</code> and builds the HTML. 7. Tomcat sends the response (200 OK) to the browser.</details>
