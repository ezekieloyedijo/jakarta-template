# Topic 03: Developing And Deploying a Web Application

## The problem that existed

You can now write servlets. But two practical questions appear right away:

1. **"Where do I put my files?"** Java classes, HTML pages, images, settings, and libraries.
   If every developer arranged them differently, a server would never know where to look.
   (Early on, each server product *did* want its own layout, so moving an app from one server to another
   meant re-arranging everything.)
2. **"How do I move my app from my laptop to the real server?"** Copying 200 loose files over the network
   and hoping none goes missing is slow and dangerous.

The Jakarta specification fixed both:

- **A standard folder layout** that every container understands (with a private `WEB-INF` folder).
- **A standard settings file** (`web.xml`, the *deployment descriptor*) that tells the container how to run the app.
- **A standard package**, the **WAR file** (*Web ARchive*): the whole app zipped into **one file**
  that you hand to any server. This is called **deploying**.

## The end goal of this topic

You should be able to:

1. Draw the standard structure of a Java web app and say which parts are **public** and which are **private**.
2. Explain what `web.xml` is for, and register servlets in it with `<servlet>` and `<servlet-mapping>`.
3. Explain **context path**, and read any URL of a deployed app.
4. **Build** a WAR with `mvn package`, look inside it, and know how to **deploy** it to Tomcat (automatically with Maven, or by hand).
5. Follow the everyday **develop → run → test → fix** cycle.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Developing vs Deploying** | First separate the two activities this topic is about |
| 2 | **The standard web app structure** (public area, `WEB-INF`) | Before you can package anything, you must know where files belong |
| 3 | **How our Maven project becomes that structure** | Our source folders are *not* the final structure; Maven builds it |
| 4 | **The deployment descriptor: `web.xml`** | The structure has one special settings file. What can it say? |
| 5 | **How the container finds your servlets** | The rules for registering servlets in `web.xml`, and what breaks if you get them wrong |
| 6 | **Context path and URL anatomy** | Once deployed, how does a URL find *your* app and *your* servlet? |
| 7 | **Packaging: the WAR file** | Zip the structure into one deliverable |
| 8 | **Deploying to Tomcat** (with Maven, and by hand) | Hand the WAR to a server |
| 9 | **What the container does at deploy time** | What happens inside when the app starts |
| 10 | **The development cycle and reading logs** | How you'll work every day from now on |

## What is inside this folder

| Path | What it shows | Run it |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson | Read |
| [example/](example/) | A "tour" app: servlets registered in `web.xml`, a custom welcome file, a hidden file inside `WEB-INF`, `context-param` settings, and custom 404/500 error pages | `cd example` then `mvn package cargo:run` |
| [project/](project/) | **My Portfolio**: a small personal website with a contact form, error pages, and settings from `web.xml`, built into a WAR you can deploy anywhere | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |

## How to build the project, step by step

The project is **My Portfolio**, a small personal website. These are all its files and whether each is needed:

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Without it Maven can't build or run the app |
| `WEB-INF/web.xml` | ✅ **Yes** | It **registers all four servlets** (without it every page is a 404), holds the owner's **name, title, and email** that every page shows, and sets the custom error pages |
| `Layout.java` | ✅ **Yes** | Every servlet calls it to build the page (header, menu, footer) |
| `HomeServlet`, `AboutServlet`, `ProjectsServlet`, `ContactServlet` | ✅ **Yes** | The four pages of the site |
| `errors/404.html`, `errors/500.html` | ✅ Yes, because `web.xml` points to them | Friendly pages for "not found" and "crashed". If you remove the `<error-page>` entries from `web.xml`, you can delete these too |
| `css/style.css` | ⚪ Optional | Makes the header, menu, and cards look nice. Without it everything still works |
| `index.html` | ❌ **Not used** | `HomeServlet` answers the home page `/` instead |

Build it in this order. After each step, **run** it with `mvn package cargo:run`, check it in the browser,
and stop it with Ctrl + C.

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic03-project</artifactId>`.

### Step 2: `WEB-INF/web.xml`, the settings

Create `src/main/webapp/WEB-INF/web.xml` with three `<context-param>` entries: `ownerName`, `ownerTitle`, `ownerEmail`.
These are **app-wide settings**. Put your own details here once, and every page reads them.
You never type your name inside Java code.
(The servlet entries are added one by one in Steps 4 to 7, and the `<error-page>` entries in Step 8.)

**The rule for every servlet in this project:** after writing the class, add its two entries to `web.xml`, then restart.

```xml
<servlet>                       <!-- WHO: nickname + full class name -->
    <servlet-name>about</servlet-name>
    <servlet-class>com.school.project.AboutServlet</servlet-class>
</servlet>
<servlet-mapping>               <!-- WHERE: the URL for that nickname -->
    <servlet-name>about</servlet-name>
    <url-pattern>/about</url-pattern>
</servlet-mapping>
```

### Step 3: `Layout.java`, the shared page design

A plain helper class, written before the servlets because all of them use it.

| Method | Purpose |
|---|---|
| `page(app, title, content)` | Reads `ownerName` and `ownerTitle` from the `ServletContext` (`app.getInitParameter(...)`) and builds the full page: the stylesheet link, a header with the owner's name, the **menu** (Home, About, Projects, Contact), the title, your content, and a footer |
| `escape(text)` | Makes user-typed text safe inside HTML. Returns `""` for `null`, so it never crashes |

### Step 4: `HomeServlet.java`, the home page

Register it in `web.xml` as `home`, with an **empty** pattern: `<url-pattern></url-pattern>`.
The empty pattern means **exactly the root of the app**, `http://localhost:8080/`.

| Method | Purpose |
|---|---|
| `doGet(...)` | Reads `ownerName` from `web.xml` and shows a welcome message with links to the projects and contact pages |

✅ Check: <http://localhost:8080/> shows your name from `web.xml`. Change the name in `web.xml`, restart, and see it change.

### Step 5: `AboutServlet.java`

Register it in `web.xml`: nickname `about`, URL `/about`.

| Method | Purpose |
|---|---|
| `doGet(...)` | Shows all three settings from `web.xml` (name, title, and email as a `mailto:` link) |

### Step 6: `ProjectsServlet.java`, a list of items

Register it in `web.xml`: nickname `projects`, URL `/projects`.

| Part | Purpose |
|---|---|
| `record Project(name, description)` | A tiny data class holding one project |
| `projects` field | The fixed list of projects. It is safe as a servlet field because it is **only read**, never changed (`List.of` can't be changed) |
| `doGet(...)` | Loops over the list, builds one `<div class='card'>` per project with a `StringBuilder`, and passes it all to `Layout.page` |

### Step 7: `ContactServlet.java`, one URL with two jobs

Register it in `web.xml`: nickname `contact`, URL `/contact`. Write `doGet` first and test it, then add `doPost`.
(One mapping is enough for both: the same URL handles GET and POST.)

| Method | Purpose |
|---|---|
| `doGet(...)` | Shows the **empty form** (`method="post"`, fields `name` and `message`) |
| `doPost(...)` | Receives the **filled form**. `setCharacterEncoding("UTF-8")` comes first so accents and emoji survive. It reads both fields, prints the message to the terminal (a real site would save or email it), and shows a thank-you page with the text passed through `Layout.escape` |

✅ Check: send a message. It appears on the thank-you page **and** in the terminal.

### Step 8: Error pages

Create `src/main/webapp/errors/404.html` and `500.html` (plain HTML), then add the two `<error-page>` entries to `web.xml`:

- `<error-code>404</error-code>` → `/errors/404.html`: shown for any address that doesn't exist.
- `<exception-type>java.lang.Exception</exception-type>` → `/errors/500.html`: shown if any servlet crashes.

✅ Check: open <http://localhost:8080/nothing> and see your own 404 page instead of Tomcat's.

### Step 9: `css/style.css` (optional)

Copy the shared stylesheet into `src/main/webapp/css/`. The header, menu, and cards now look styled.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is the difference between **developing** and **deploying** an app?

<details><summary>Answer</summary><b>Developing</b> = writing and testing the code on your own computer. <b>Deploying</b> = putting the finished, packaged app onto a server so people can use it.</details>

**2.** Which folder in a Java web app is **private**, and what does private mean?

<details><summary>Answer</summary><code>WEB-INF</code>. The browser can <b>never</b> open a file inside it directly (it gets a 404). Only the server and your code can use those files.</details>

**3.** In our Maven project, where do Java classes go, and where do HTML/CSS files go?

<details><summary>Answer</summary>Java: <code>src/main/java</code>. HTML, CSS and JSP: <code>src/main/webapp</code>.</details>

**4.** After building, where do the compiled `.class` files end up inside the app?

<details><summary>Answer</summary>In <code>WEB-INF/classes</code>. Extra libraries (JAR files) go in <code>WEB-INF/lib</code>.</details>

**5.** What is `web.xml`, and where does it live?

<details><summary>Answer</summary>The <b>deployment descriptor</b>: the app's settings file (servlet mappings, welcome pages, error pages, context parameters, security). It lives at <code>WEB-INF/web.xml</code>.</details>

**6.** In `web.xml`, what does `<servlet>` say, what does `<servlet-mapping>` say, and what joins the two?

<details><summary>Answer</summary><code>&lt;servlet&gt;</code> says <b>WHO</b>: a nickname and the full class name. <code>&lt;servlet-mapping&gt;</code> says <b>WHERE</b>: which URL goes to that nickname. The <b><code>&lt;servlet-name&gt;</code></b>, which must be spelled the same in both, joins them.</details>

**7.** What is a **WAR** file?

<details><summary>Answer</summary>A <b>W</b>eb application <b>AR</b>chive: the whole app (classes, pages, <code>WEB-INF</code>) zipped into <b>one file</b> that any Jakarta server can deploy. Like a shipping box.</details>

**8.** Which command builds the WAR, and which command builds it **and** runs it on Tomcat?

<details><summary>Answer</summary><code>mvn package</code> builds <code>target/&lt;name&gt;.war</code>. <code>mvn package cargo:run</code> builds it and starts it on Tomcat.</details>

**9.** What is the **context path**?

<details><summary>Answer</summary>The part of the URL that chooses <b>which app</b> on the server. In <code>http://server/shop/cart</code>, <code>/shop</code> is the context path and <code>/cart</code> is the servlet path. Our apps are deployed at <code>/</code> (the root), so there is no extra part.</details>

**10.** You changed a servlet, but the browser still shows the old page. Why, and what do you do?

<details><summary>Answer</summary>Tomcat is still running the <b>old WAR</b>. Press <b>Ctrl + C</b>, then run <code>mvn package cargo:run</code> again to rebuild and redeploy.</details>
