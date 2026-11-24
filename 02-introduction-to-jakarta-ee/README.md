# Topic 02: Introduction to Jakarta EE

## The problem that existed

In topic 01 you built `MiniWebApp.java` by hand. Remember all the **plumbing**: opening a port,
reading raw text, decoding `%20`, matching paths, writing headers...

Now picture the late 1990s. **Every company** building web apps in Java was writing that same plumbing,
**each in its own way**:

- Company A's code only worked on **Company A's** server product. Want to switch? **Rewrite everything.**
- A developer who moved jobs had to **learn a new home-made system** at every company.
- Every team wasted months on plumbing and bugs (security holes, crashes with many users) instead of building **features**.

The industry needed **one shared set of rules** that says: *"This is how a Java web component
receives a request and sends a response. Any server that follows these rules can run any app that
follows these rules."* It also needed ready-made servers that **do the plumbing for you**.

Those rules were first called **J2EE**, then **Java EE**. Today they are called **Jakarta EE**.

## The end goal of this topic

You should be able to explain:

1. That Jakarta EE is a set of **specifications** (rules), not one program.
2. What a **container** is and what jobs it does for you, so that you only write the "recipe".
3. Why you see `jakarta.servlet...` in new code and `javax.servlet...` in old tutorials.
4. How our tools (Maven + Tomcat) fit in, and you should be able to **run your first Jakarta app**.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: the plumbing problem** | We start from the pain you felt in topic 01 |
| 2 | **Specification vs Implementation** | The cure for the pain: agree on *rules*, then many products can follow them |
| 3 | **Jakarta EE is a family of specifications** | Now that you know what a "spec" is: Jakarta EE is a *collection* of them, and we'll meet the ones in this course |
| 4 | **The Container** | The most important idea: the program that follows the specs and does the plumbing for you |
| 5 | **"Don't call us, we'll call you"** | How your code and the container work together (who calls whom) |
| 6 | **Kinds of servers** | With "container" understood, you can tell Tomcat/Jetty apart from GlassFish/WildFly |
| 7 | **The name story: Java EE to Jakarta EE, `javax` to `jakarta`** | Explains old tutorials, version numbers, and why Tomcat 9 won't run our code |
| 8 | **API vs Implementation in your project (`provided`)** | How a project "uses the rules" without containing the server |
| 9 | **Your first Jakarta web app** | All of the above in action, and a comparison with topic 01 |

## What is inside this folder

| Path | What it shows | Run it |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson | Read |
| [example/](example/) | The smallest Jakarta app: one servlet (`/hello`) plus one page that **reports what the container does for you** (`/container-info`) | `cd example` then `mvn package cargo:run` |
| [project/](project/) | **Topic 01's MiniWebApp rebuilt with Jakarta**: same pages, a fraction of the code | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |

> ⚙️ From this topic on you need **Maven**. Read [SETUP.md](../SETUP.md) first if you haven't.

## How to build the project, step by step

The project is topic 01's Mini Web App, rebuilt with Jakarta. These are all its files and whether each is needed:

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Without it Maven can't build or run the app |
| `src/main/webapp/index.html` | ✅ **Yes** | It *is* the home page. Without it, `http://localhost:8080/` gives a 404 |
| `Page.java` | ✅ **Yes** | Both servlets call it to build their pages |
| `TimeServlet.java`, `GreetServlet.java` | ✅ **Yes** | The two dynamic pages |
| `src/main/webapp/WEB-INF/web.xml` | ✅ **Yes** | It tells Tomcat which URL goes to which servlet (`/time` → `TimeServlet`, `/greet` → `GreetServlet`). A servlet that isn't listed here can never be reached: 404 |
| `src/main/webapp/css/style.css` | ⚪ Optional | Only makes the pages look nice. Without it everything still works, just plain |

(`index.html` needs no entry in `web.xml`: Tomcat shows it for `/` automatically.)

Build it in this order. After each step, **run** it with `mvn package cargo:run`, check it in the browser,
and stop it with Ctrl + C.

### Step 1: `pom.xml`, the app's identity card

Copy it from the template and set `<artifactId>topic02-project</artifactId>`. It says
"my parent is `../../pom.xml`" (Java 17, the Jakarta libraries, Tomcat) and "package me as a WAR".

### Step 2: `index.html`, the static home page

A plain HTML file in `src/main/webapp/`. It has a link to `time` and a form with `action="greet"` and `method="get"`.
It contains **no Java**: Tomcat sends the file as it is.

✅ Check: <http://localhost:8080/> shows the page. The links don't work yet (404), which is expected.

### Step 3: `css/style.css` (optional)

Copy the shared stylesheet into `src/main/webapp/css/`. `index.html` and `Page.wrap()` link to it with
`<link rel="stylesheet" href="css/style.css">`.

### Step 4: `Page.java`, the layout helper

A plain Java class (no Jakarta in it) with two static methods, written **before** the servlets because both use it:

| Method | Purpose |
|---|---|
| `wrap(title, content)` | Puts the title and content into a full HTML page with the stylesheet link and a Home link, so every page has the same layout |
| `escape(text)` | Makes user-typed text safe inside HTML by replacing `& < > "` (topic 13) |

The `private Page() {}` constructor stops anyone from creating `new Page()`. It is only a box of helper methods.

### Step 5: `TimeServlet.java`, the first servlet

The class `extends HttpServlet`.

| Method | Purpose |
|---|---|
| `doGet(request, response)` | Tomcat calls it for every GET to `/time`. It reads the current time, sets the content type to HTML (`setContentType`), and writes `Page.wrap(...)` to `response.getWriter()` |

### Step 6: `WEB-INF/web.xml`, tell Tomcat about the servlet

Create `src/main/webapp/WEB-INF/web.xml` and register `TimeServlet` with **two** entries:

```xml
<servlet>                                   <!-- WHO: a nickname for the class -->
    <servlet-name>time</servlet-name>
    <servlet-class>com.school.project.TimeServlet</servlet-class>
</servlet>
<servlet-mapping>                           <!-- WHERE: which URL goes to that nickname -->
    <servlet-name>time</servlet-name>
    <url-pattern>/time</url-pattern>
</servlet-mapping>
```

The `<servlet-name>` must be **the same** in both entries, because that is how they are joined together.
`<servlet-class>` is the **full** class name, with the package.

✅ Check: the "What time is it?" link works. Refresh it and the time changes.
(Try removing the `<servlet-mapping>` and restarting: `/time` gives a 404. Put it back.)

### Step 7: `GreetServlet.java`, input from the user, and its web.xml entries

| Method | Purpose |
|---|---|
| `doGet(request, response)` | Reads the name with `request.getParameter("name")` (already decoded by Tomcat). If it's missing or blank, it sets status **400** with `setStatus(400)` and shows an error page. Otherwise it shows "Hello, *name*!" using `Page.escape(name)` |

Then add a second `<servlet>` + `<servlet-mapping>` pair to `web.xml`: nickname `greet`, class `com.school.project.GreetServlet`, URL `/greet`.

✅ Check: submit the form, then open <http://localhost:8080/greet> with no name and see the 400 page.

**Compare with topic 01:** `getParameter()`, `send()`, and the 404 path check are gone. Tomcat does them.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is **Jakarta EE**?

<details><summary>Answer</summary>A <b>set of specifications</b> (agreed rules) for building Java server and web applications. It is not one program you download. Servers such as Tomcat <i>follow</i> those rules.</details>

**2.** What is the difference between a **specification** and an **implementation**?

<details><summary>Answer</summary>A <b>specification</b> is the written rule ("a servlet must have a <code>doGet</code> method..."). An <b>implementation</b> is a real product that follows the rule (for example Tomcat). Like the USB-C standard (spec) and the real cables many companies make (implementations).</details>

**3.** What is a **container**?

<details><summary>Answer</summary>The server program (for us, <b>Tomcat</b>) that <b>runs your code</b> and does the boring work for you: listening on the network, reading HTTP, creating your servlet objects, managing threads, and sending the response.</details>

**4.** Name three jobs the container does so that you don't have to.

<details><summary>Answer</summary>Any three of: listens on a port, reads and parses the HTTP request, finds the right servlet for the URL, creates and destroys servlet objects, runs many requests at once with threads, builds and sends the HTTP response, manages sessions and security.</details>

**5.** What does "**Don't call us, we'll call you**" mean in Jakarta EE?

<details><summary>Answer</summary>You never call your own <code>doGet</code> method and you never write a <code>main</code> method. <b>The container calls your code</b> when a matching request arrives.</details>

**6.** Name three specifications inside Jakarta EE that this course uses.

<details><summary>Answer</summary>Any three of: <b>Servlet</b>, <b>Jakarta Server Pages (JSP)</b>, <b>Expression Language (EL)</b>, <b>JSTL</b> (Standard Tag Library), <b>Security</b>.</details>

**7.** Why do new tutorials use `jakarta.servlet` while old ones use `javax.servlet`?

<details><summary>Answer</summary>Oracle handed Java EE to the Eclipse Foundation, and it was renamed <b>Jakarta EE</b>. From Jakarta EE 9 onward every package was renamed from <code>javax.*</code> to <code>jakarta.*</code>. Old <code>javax</code> code runs on Tomcat 9; our <code>jakarta</code> code needs Tomcat 10 or newer.</details>

**8.** What is the difference between **Tomcat** and a "full" server like **GlassFish** or **WildFly**?

<details><summary>Answer</summary>Tomcat is a <b>web container</b>: it implements only the web specs (Servlet, JSP, EL...). Full servers implement <b>all</b> of Jakarta EE (also things like EJB and messaging). Tomcat is smaller and simpler, and it is all we need.</details>

**9.** In our `pom.xml`, the Jakarta libraries have `<scope>provided</scope>`. What does that mean?

<details><summary>Answer</summary>"We need these to <b>compile</b>, but don't pack them into the WAR, because the <b>server (Tomcat) already provides them</b> when the app runs."</details>

**10.** Which two entries in `web.xml` connect the URL `/hello` to the class `HelloServlet`, and what does each one say?

<details><summary>Answer</summary><code>&lt;servlet&gt;</code> says <b>WHO</b>: it gives the class <code>com.school.example.HelloServlet</code> a nickname, for example <code>hello</code>. <code>&lt;servlet-mapping&gt;</code> says <b>WHERE</b>: requests for the URL <code>/hello</code> go to the nickname <code>hello</code>. Without both, the URL gives a 404.</details>
