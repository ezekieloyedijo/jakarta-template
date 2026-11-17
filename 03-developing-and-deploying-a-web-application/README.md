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
