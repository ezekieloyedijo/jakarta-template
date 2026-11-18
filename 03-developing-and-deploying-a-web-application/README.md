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
2. Explain what `web.xml` is for, and when to use it instead of annotations.
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
| 5 | **Annotations vs `web.xml`** | Two ways to say the same thing, and when to use each |
| 6 | **Context path and URL anatomy** | Once deployed, how does a URL find *your* app and *your* servlet? |
| 7 | **Packaging: the WAR file** | Zip the structure into one deliverable |
| 8 | **Deploying to Tomcat** (with Maven, and by hand) | Hand the WAR to a server |
| 9 | **What the container does at deploy time** | What happens inside when the app starts |
| 10 | **The development cycle and reading logs** | How you'll work every day from now on |

## What is inside this folder

| Path | What it shows | Run it |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson | Read |
| [example/](example/) | A "tour" app: a servlet registered in `web.xml`, one registered by annotation, a custom welcome file, a hidden file inside `WEB-INF`, `context-param` settings, and custom 404/500 error pages | `cd example` then `mvn package cargo:run` |
| [project/](project/) | **My Portfolio**: a small personal website with a contact form, error pages, and settings from `web.xml`, built into a WAR you can deploy anywhere | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |

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

**6.** Give one advantage of annotations (`@WebServlet`) and one advantage of `web.xml`.

<details><summary>Answer</summary><b>Annotations</b>: short, and the setting sits right next to the code. <b>web.xml</b>: everything is in one place, and you can change settings <b>without changing Java code</b>.</details>

**7.** What is a **WAR** file?

<details><summary>Answer</summary>A <b>W</b>eb application <b>AR</b>chive: the whole app (classes, pages, <code>WEB-INF</code>) zipped into <b>one file</b> that any Jakarta server can deploy. Like a shipping box.</details>

**8.** Which command builds the WAR, and which command builds it **and** runs it on Tomcat?

<details><summary>Answer</summary><code>mvn package</code> builds <code>target/&lt;name&gt;.war</code>. <code>mvn package cargo:run</code> builds it and starts it on Tomcat.</details>

**9.** What is the **context path**?

<details><summary>Answer</summary>The part of the URL that chooses <b>which app</b> on the server. In <code>http://server/shop/cart</code>, <code>/shop</code> is the context path and <code>/cart</code> is the servlet path. Our apps are deployed at <code>/</code> (the root), so there is no extra part.</details>

**10.** You changed a servlet, but the browser still shows the old page. Why, and what do you do?

<details><summary>Answer</summary>Tomcat is still running the <b>old WAR</b>. Press <b>Ctrl + C</b>, then run <code>mvn package cargo:run</code> again to rebuild and redeploy.</details>
