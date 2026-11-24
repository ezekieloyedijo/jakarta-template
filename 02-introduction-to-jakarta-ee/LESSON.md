# LESSON 02: Introduction to Jakarta EE

> **The big picture for this lesson: A FRANCHISE RESTAURANT (like a fast-food chain)**
>
> - The **franchise rule book** (how orders are taken, how food is plated) = the **Jakarta EE specifications**
> - A **restaurant building with all its staff** (doorman, waiters, cleaners, cashier) = the **container** (Tomcat)
> - **You, the chef** = **your code** (servlets)
> - The **recipe** you write = the code inside your servlet
>
> Any chef trained on the rule book can cook in **any** branch of the franchise.
> Any Jakarta app can run in **any** Jakarta container.

---

## Step 1: Recap of the plumbing problem

In `01.../project/MiniWebApp.java`, maybe **70%** of the code was plumbing: decoding the query string,
writing headers, matching paths, handling 404. Only a few lines were the **real job** ("say hello to the user").

🍽️ It's like a chef who must also build the restaurant, seat guests, and wash the plates.
We want the chef to **only cook**.

---

## Step 2: Specification vs Implementation

This is the key idea behind all of Jakarta EE.

| | **Specification (spec)** | **Implementation** |
|---|---|---|
| What it is | A **document of rules**: "a component must have a method called `doGet`; the server must give it a request object with a method `getParameter`..." | A **real program** that follows those rules |
| Can you run it? | No, it is just an agreement | Yes |
| Real-life | The **USB-C standard**: shape, voltage, pins | The actual chargers made by Samsung, Anker, Apple... |

Because every USB-C charger follows the same standard, **any charger works with any USB-C phone**.
Likewise, because Jetty, Tomcat, GlassFish, and others follow the same Jakarta spec,
**your app works on any of them**. You are **not locked in** to one company.

---

## Step 3: Jakarta EE is a family of specifications

Jakarta EE is not one rule book but a **shelf of rule books**, each one for a different job.
The ones in this course:

| Specification | What it's for | Topic |
|---|---|---|
| **Jakarta Servlet** | Java classes that receive requests and send responses. **The foundation** | 04, 05, 07, 09 |
| **Jakarta Server Pages (JSP)** | HTML pages with Java mixed in | 06, 10 |
| **Jakarta Expression Language (EL)** | `${...}` short expressions inside pages | 12 |
| **Jakarta Standard Tag Library (JSTL)** | Ready-made tags like `<c:forEach>`, `<c:if>` | 12 |
| **Jakarta Security / Servlet security** | Login, roles, and protection | 13 |

Others you'll meet later in your career: **Jakarta Persistence (JPA)** for databases,
**Jakarta REST** for APIs, **CDI** for wiring objects, and more.

The specs are grouped into **profiles** (bundles), a bit like phone plans:

- **Core Profile**: the smallest, for tiny cloud services
- **Web Profile**: what most web apps need (Servlet, JSP, EL, JSTL, Persistence, ...)
- **Platform**: everything

(Database access with **JDBC** in topic 08 comes from standard Java itself, not from Jakarta EE.)

---

## Step 4: The Container (the most important idea)

A **container** is the program that **follows the specs and does the plumbing**.
Your code lives **inside** it, like a chef inside a staffed restaurant.

What the container does **for you**, compared with topic 01:

| Job | Topic 01 (by hand) | With a container |
|---|---|---|
| Open the port, accept connections | You wrote it | ✅ Done for you |
| Read the raw request text and split it into parts | You wrote it | ✅ Done. You get a ready **`HttpServletRequest`** object |
| Decode `name=Amaka%20Obi` | You wrote `getParameter` yourself | ✅ `request.getParameter("name")` gives `"Amaka Obi"` |
| Decide which code handles `/greet` | `createContext` and manual `if`s | ✅ Write it **once** in `WEB-INF/web.xml`, and the container sends every `/greet` request there |
| Write status line and headers in the exact format | You did it | ✅ You get an **`HttpServletResponse`** object with methods like `setContentType` |
| 404 for unknown paths | You did it | ✅ Automatic |
| Serve **many users at once** | Not handled | ✅ The container uses a **thread** per request |
| Create and destroy your objects at the right time | — | ✅ **Lifecycle management** (topic 04) |
| Remember users between requests | — | ✅ **Sessions** (topic 05) |
| Login and access control | — | ✅ **Security** (topic 13) |

🍽️ The container is the **restaurant staff**. The doorman accepts customers (connections), the waiter
**translates** the customer's order into a neat order card (the request object), hands it to the right chef,
gives the chef an **empty plate** (the response object), and delivers the plate when the chef is done.
Many waiters work in parallel (threads), so many customers are served at once.

---

## Step 5: "Don't call us, we'll call you"

In normal Java programs, **you** write `main()` and **you** call everything.
In a Jakarta web app there is **no `main()` method**. Instead:

1. **You** write a class that follows the rules (for example `extends HttpServlet` and has `doGet`).
2. **You** write its address in the app's instruction sheet, `WEB-INF/web.xml`: "the class `HelloServlet` answers `/hello`".
3. The **container** reads `web.xml` at startup, creates the object, and **calls your `doGet`
   whenever** a request for `/hello` arrives.

This is called **Inversion of Control** (control is "inverted": the framework calls you, not the other way round).

🎭 **Real life:** an actor at an audition. The actor doesn't phone the director every minute asking
"is it my turn?" The director says *"Don't call us, we'll call you."* The actor just needs to be **ready
with their lines** (your `doGet` method). The director (container) decides **when**.

---

## Step 6: Kinds of servers

Now that you know what a container is, the server names make sense:

| Kind | What it contains | Examples |
|---|---|---|
| **Web server** | Only serves files and passes requests along. No Java container | Nginx, Apache HTTP Server |
| **Servlet container** (web container) | Runs **Servlets, JSP, EL, JSTL**. Exactly what this course needs | **Tomcat** (we use it), Jetty |
| **Full application server** | Implements the **whole Jakarta EE platform** | GlassFish, WildFly, Payara, Open Liberty |

🍽️ A food stand (web server) only hands out ready-packed items. A restaurant with a kitchen (servlet container)
can cook. A **hotel** (application server) has restaurants plus rooms, a gym, a laundry...

**We use Apache Tomcat 10.1**, the most widely used servlet container. Maven downloads it and starts it for us
(`mvn package cargo:run`), so there is nothing to install. See [SETUP.md](../SETUP.md).
Because Tomcat and Jetty follow the **same spec**, the same code would run on Jetty too.

---

## Step 7: The name story (Java EE to Jakarta EE, `javax` to `jakarta`)

| Year | Name | Package names in code |
|---|---|---|
| 1999 | **J2EE** (by Sun Microsystems) | `javax.servlet...` |
| 2006 | **Java EE** (Oracle bought Sun in 2010) | `javax.servlet...` |
| 2017 | Oracle gives it to the **Eclipse Foundation** (an open, non-profit group) | |
| 2019 | Renamed **Jakarta EE 8** (Oracle kept the "Java" trademark) | still `javax...` |
| 2020 | **Jakarta EE 9**: the big rename | **`jakarta.servlet...`** |
| 2022 | **Jakarta EE 10**: what this course uses (Servlet 6.0) | `jakarta...` |

**Why it matters to you:**

- Old tutorials and old Stack Overflow answers say `import javax.servlet.http.HttpServlet;`.
  In **our** code it must be `import jakarta.servlet.http.HttpServlet;`. Same class, new name.
- **Tomcat 9 and older only understand `javax`.** You need **Tomcat 10.1+** (we use 10.1) for `jakarta` code.

🏷️ Like a company that changes its name: the people and the work stay the same, but every sign and letterhead gets
the new name, and old letters with the old name are no longer accepted at the new reception.

---

## Step 8: API vs Implementation in your project (`provided`)

To **compile** a servlet, Java needs to know what `HttpServlet` and `HttpServletRequest` look like.
Those are in the **Servlet API**: a small library holding **the rule book written as Java interfaces and classes**.

But the **real working code** (the plumbing) is inside the **container** (Tomcat).
So in the `pom.xml` we say:

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>    <!-- "the server will PROVIDE this at runtime, don't pack it in my app" -->
</dependency>
```

🍽️ A chef needs to **know** the restaurant has an oven to write the recipe. The chef doesn't **bring an oven** to work.
The building provides it. `provided` means: "I need to know about it, but the server brings it."

> In this course, that dependency is already written **once** in the parent [pom.xml](../pom.xml),
> and every app inherits it. Each app's own `pom.xml` is only a few lines.

---

## Step 9: Your first Jakarta web app

Here is topic 01's greeting, Jakarta-style. It takes **two small files**.

**File 1: the Java class** (`src/main/java/com/school/project/GreetServlet.java`): the chef's recipe.

```java
package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GreetServlet extends HttpServlet {          // 1. "I follow the Servlet rules"

    @Override
    protected void doGet(HttpServletRequest request,     // 2. container hands me the order card...
                         HttpServletResponse response)   //    ...and an empty plate
            throws IOException {
        String name = request.getParameter("name");      // 3. already decoded for me!
        response.setContentType("text/html");            // 4. the header, in one line
        response.getWriter().println("<h1>Hello, " + name + "!</h1>");   // 5. the body
    }
}
```

**File 2: the instruction sheet** (`src/main/webapp/WEB-INF/web.xml`): tells the container *which URL goes to which class*.

```xml
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0">
    <servlet>                                       <!-- WHO: give the class a nickname -->
        <servlet-name>greet</servlet-name>
        <servlet-class>com.school.project.GreetServlet</servlet-class>
    </servlet>
    <servlet-mapping>                               <!-- WHERE: which URL goes to that nickname -->
        <servlet-name>greet</servlet-name>
        <url-pattern>/greet</url-pattern>
    </servlet-mapping>
</web-app>
```

(The real file in `project/` has a longer first line with the full "schema" address. Copy that one.)

🍽️ The Java class is the **chef**. `web.xml` is the **staff list on the kitchen wall**: "orders for *greet* go to chef Greet".
A chef who isn't on the list never gets any orders, so **a servlet that isn't in `web.xml` gives a 404.**

Compare with topic 01: no port, no parsing, no decoding, no path matching, no status line. **The chef only cooks.**
(Topic 03 explains `web.xml` fully, and topic 04 explains every line of a servlet. For now, enjoy how short it is.)

> 💡 From topic 05 onward you'll learn a shortcut: one line, `@WebServlet("/greet")`, written on the class,
> can replace the two `web.xml` entries. We use `web.xml` first so you see clearly what the container needs to know.

### Run the example

```bash
cd 02-introduction-to-jakarta-ee/example
mvn package cargo:run
```

- <http://localhost:8080/>: a static welcome page
- <http://localhost:8080/hello>: your first servlet
- <http://localhost:8080/container-info>: the container tells you **about itself** and the work it did for this request.
  **Refresh several times** and watch the *thread name* change. That's the container serving requests with different "waiters".

### Run the project

```bash
cd 02-introduction-to-jakarta-ee/project
mvn package cargo:run
```

It's topic 01's MiniWebApp rebuilt: <http://localhost:8080/>, `/time`, `/greet?name=Tolu`, and any wrong URL gives a 404.
Open `01.../project/MiniWebApp.java` and `02.../project/src/main/java/...` side by side and compare.

Then go to **[QUIZ.md](QUIZ.md)**.
