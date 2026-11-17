# Jakarta EE Web Development: The Course

A step-by-step course on building Java web applications with **Jakarta EE**
(Servlets, JSP, EL, Filters, Sessions, Databases, MVC, and Security).

It is written for a **teacher who is learning the material for the first time**
and who will then teach **students aged 17 to 23**. Everything is explained in
plain language with real-life comparisons first, and the technical words come after.

---

## 1. How to use this course

Do the topics **in order**. Each one is built on top of the one before it.

Inside every topic folder you will find the same five things:

| File / Folder | What it is for | When to use it |
|---|---|---|
| `README.md` | **The story.** What problem existed, why this topic was invented, the end goal, and the **learning path** (step 1, step 2, step 3...) | Read FIRST |
| `LESSON.md` | **The teaching.** Each step from the learning path explained with real-life illustrations and small code snippets | Read SECOND, slowly |
| `example/` | **Small demos.** Tiny, focused programs that each show ONE idea. Run them and play with them | While reading the lesson |
| `project/` | **The mini project.** A small but complete app that uses all the ideas of the topic together | After the lesson |
| `QUIZ.md` | **Test yourself.** Questions, small tasks, and answers hidden in drop-downs | Last |

> **Tip for the teacher:** Read `README.md` and you know *why*. Read `LESSON.md` and you know *what* and *how*.
> Run `example/` and you have *seen it*. Build `project/` and you have *done it*. Pass `QUIZ.md` and you *own it*.

---

## 2. The topics (the full journey)

| # | Topic | The one-sentence idea |
|---|---|---|
| 01 | [Introduction To Web Applications](01-introduction-to-web-applications/README.md) | A browser asks, a server answers, and HTTP is the language they speak |
| 02 | [Introduction to Jakarta EE](02-introduction-to-jakarta-ee/README.md) | A set of agreed rules plus a "container" that does the boring server work for you |
| 03 | [Developing And Deploying a Web Application](03-developing-and-deploying-a-web-application/README.md) | The standard folder layout of a Java web app, how to package it (WAR), and how to put it on a server |
| 04 | [Jakarta Servlets](04-jakarta-servlets/README.md) | A Java class that receives a request and writes a response |
| 05 | [Session Tracking](05-session-tracking/README.md) | How a server remembers *you* between clicks |
| 06 | [Jakarta Server Pages](06-jakarta-server-pages/README.md) | HTML pages with little bits of Java inside |
| 07 | [Filters Annotation](07-filters-annotation/README.md) | Security guards that check every request before it reaches your servlet |
| 08 | [Database Access and Event Handling](08-database-access-and-event-handling/README.md) | Saving data permanently (JDBC) and reacting when the app starts or a user arrives (Listeners) |
| 09 | [Asynchronous Servlet Communication](09-asynchronous-servlet-communication/README.md) | Handling slow jobs without blocking the server |
| 10 | [JSP Implicit Objects and Standard Actions](10-jsp-implicit-objects-and-standard-actions/README.md) | Ready-made objects and tags every JSP gets for free |
| 11 | [Model-View-Controller Architecture](11-model-view-controller-architecture/README.md) | Splitting an app into Data, Screens, and Traffic control |
| 12 | [JSP Expression Language](12-jsp-expression-language/README.md) | `${...}`: a short, clean way to show data in a page (plus JSTL tags) |
| 13 | [Securing Java Web Applications](13-securing-java-web-applications/README.md) | Login, roles, and protecting against common attacks |
| 14 | [Building Application Using JSP and Servlet](14-building-application-using-jsp-and-servlet/README.md) | One complete app that uses everything |

---

## 3. Tools: no Eclipse, no separate Tomcat install

The reference material uses **Eclipse EE + Tomcat**. We keep **Tomcat** (version 10.1) but drop Eclipse
and the manual server setup:

| Old way (reference material) | Our way |
|---|---|
| Eclipse EE (heavy IDE) | **VS Code** or **IntelliJ IDEA**, whichever you prefer |
| Download Tomcat, add it inside Eclipse, configure, "Run on Server" | **Maven + Tomcat**: type `mvn package cargo:run`. Maven downloads Tomcat and starts your app in it |

Full setup instructions are in **[SETUP.md](SETUP.md)**.

### Running any app (the only command you need)

```bash
cd 04-jakarta-servlets/example      # go into any example/ or project/ folder
mvn package cargo:run               # build the app and start Tomcat
# open http://localhost:8080/ in your browser
# press Ctrl + C in the terminal to stop (and run the command again after changing code)
```

(Topic 01 is different: it uses plain Java with no server, so you run `java FileName.java`. Its README explains this.)

---

## 4. Folder map

```
jakarta-template/
├── README.md        <- you are here
├── SETUP.md         <- install & run instructions (read before topic 02)
├── pom.xml          <- shared settings for every app (the "parent")
├── starter-template/ <- a blank course + topic template for starting YOUR OWN folder in class
├── 01-introduction-to-web-applications/
│   ├── README.md  LESSON.md  QUIZ.md
│   ├── example/
│   └── project/
├── 02-introduction-to-jakarta-ee/
│   └── ... same shape ...
└── 14-building-application-using-jsp-and-servlet/
```

Every `example/` and `project/` (from topic 02 onward) is a normal Maven web app:

```
project/
├── pom.xml                         <- tiny: "my parent is ../../pom.xml"
└── src/main/
    ├── java/...                    <- your Java classes (servlets, filters, models)
    └── webapp/                     <- what the browser can reach (HTML, CSS, JSP)
        └── WEB-INF/                <- PRIVATE: the browser can never open files in here
```
