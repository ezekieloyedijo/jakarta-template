# Topic 04: Jakarta Servlets

## The problem that existed

In the early web (mid-1990s), dynamic pages were made with **CGI** (Common Gateway Interface).
The idea was simple: **for every single request, the server started a brand-new program**, let it print the HTML,
and then **killed it**.

- 1,000 visitors meant **1,000 programs started and stopped**. That's slow and eats memory.
- Nothing stayed in memory between requests, so every request had to redo all the setup (open files, connect to the database...).
- Every CGI program parsed the raw request in its own way.

🍳 It's like a restaurant that **hires a new chef for every customer**, waits while the chef puts on the uniform
and learns the kitchen, has them cook **one** meal, and then **fires** them. Ten customers means ten hires and ten firings.

**Servlets** (1997) fixed this:

- The container creates **ONE servlet object** and **keeps it alive** in memory.
- Each request is handled by a lightweight **thread** that runs that same object's method, which is fast.
- Setup happens **once** (`init`), and cleanup happens **once** (`destroy`).
- The container gives you the request already **parsed** into a neat Java object.

🍳 Now it's **one experienced chef who stays all day**, and many waiters (threads) bring orders to them at the same time.

## The end goal of this topic

You should be able to write servlets that:

- **receive** any input from a browser (links, forms, checkboxes, dropdowns),
- **validate** it and **respond** correctly (HTML, status codes),
- **pass work to each other** (forward, include, redirect),
- use **configuration** values,

and you should understand the servlet **life cycle**, which explains *why* you must never store one user's data
in a servlet's fields.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **What a servlet is** | The definition, and the CGI story above |
| 2 | **The family tree: `Servlet` → `GenericServlet` → `HttpServlet`** | Why we always write `extends HttpServlet`, and where `doGet`/`doPost` come from |
| 3 | **The life cycle: `init` → `service` → `destroy`** | Once you know what the class is, learn *when* the container calls it |
| 4 | **One object, many threads (thread safety)** | A direct consequence of the life cycle, and the #1 beginner bug |
| 5 | **URL mapping patterns** | How a request finds *your* servlet |
| 6 | **The request object: reading input** | The servlet has been found. Now read what the user sent |
| 7 | **The response object: writing output** | Then answer them |
| 8 | **Handling forms: `doGet` shows, `doPost` processes** | Steps 6 and 7 together, the most common job of a servlet |
| 9 | **Configuration: `ServletConfig` and `ServletContext`** | Avoid hard-coding values, and share data across the app |
| 10 | **Working together: forward, include, redirect** | Real apps have many servlets that hand work to each other |
| 11 | **The Post/Redirect/Get pattern** | A practical rule that follows from Step 10 |

## What is inside this folder

| Path | What it shows | Run it |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson | Read |
| [example/](example/) | `/lifecycle` (watch init/service/destroy), `/register` (every kind of form input), `/config` (init params), and `/dispatch` (forward vs include vs redirect, side by side) | `cd example` then `mvn package cargo:run` |
| [project/](project/) | **Simple Calculator**: form, validation, errors with proper status codes, configuration, and an app-wide counter shared by all users | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is a **servlet**?

<details><summary>Answer</summary>A Java class (usually <code>extends HttpServlet</code>) that runs inside the container, <b>receives an HTTP request</b>, and <b>writes the HTTP response</b>.</details>

**2.** Which class do we extend to write a web servlet, and which methods do we usually override?

<details><summary>Answer</summary><code>HttpServlet</code>. We override <code>doGet</code> (for GET requests) and/or <code>doPost</code> (for POST requests).</details>

**3.** Name the three life-cycle methods in order, and say how often each is called.

<details><summary>Answer</summary><code>init()</code> once, when the servlet is created. <code>service()</code> (which calls <code>doGet</code>/<code>doPost</code>) <b>once per request</b>. <code>destroy()</code> once, when the app stops.</details>

**4.** How many objects of one servlet class does the container usually create?

<details><summary>Answer</summary><b>One.</b> All requests share it, each running on its own thread.</details>

**5.** Why must you **not** store one user's data in a servlet's field (instance variable)?

<details><summary>Answer</summary>Because the one servlet object is shared by <b>all users at the same time</b>. User B could see or overwrite user A's data. Use local variables inside <code>doGet</code>/<code>doPost</code> instead.</details>

**6.** Which method reads the value of a form field called `email`?

<details><summary>Answer</summary><code>request.getParameter("email")</code>. It returns a <code>String</code>, or <code>null</code> if the field was not sent.</details>

**7.** Which two lines do you write before sending HTML from a servlet?

<details><summary>Answer</summary><code>response.setContentType("text/html;charset=UTF-8");</code> and then <code>PrintWriter out = response.getWriter();</code>. Then you write the HTML with <code>out.println(...)</code>.</details>

**8.** What is the difference between `forward` and `sendRedirect`?

<details><summary>Answer</summary><b>forward</b> passes the <b>same</b> request to another resource on the server, so the address bar does not change. <b>sendRedirect</b> tells the browser to make a <b>new</b> request to a different URL, so the address bar changes.</details>

**9.** What is the difference between `ServletConfig` and `ServletContext`?

<details><summary>Answer</summary><code>ServletConfig</code> holds settings for <b>one servlet</b> (its init parameters). <code>ServletContext</code> is shared by the <b>whole app</b> (context parameters and app-wide attributes).</details>

**10.** What is the **Post/Redirect/Get** pattern, and what problem does it solve?

<details><summary>Answer</summary>After handling a POST, the servlet <b>redirects</b> to a GET page instead of showing the result directly. This stops the form from being <b>submitted twice</b> when the user presses refresh.</details>
