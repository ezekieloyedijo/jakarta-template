# LESSON 03: Developing And Deploying a Web Application

> **The big picture for this lesson: MOVING A SHOP INTO A SHOPPING MALL**
>
> - **Developing** = setting up and testing your shop **in your own garage**
> - The **standard shop layout** the mall requires (front display area, locked back store-room) = the **web app structure**
> - The **instruction sheet** you give the mall manager ("opening page is the menu board; if a customer gets lost, show them this sign") = **`web.xml`**
> - The **one sealed shipping box** with everything inside = the **WAR file**
> - **The mall** = the **server/container** (Tomcat)
> - **Moving the box into the mall and opening for business** = **deploying**
> - **Your shop's unit number in the mall** = the **context path**

---

## Step 1: Developing vs Deploying

| | **Developing** | **Deploying** |
|---|---|---|
| What | Writing and testing code | Putting the finished app on a server so **users** can reach it |
| Where | Your laptop (`localhost`) | A real server (school server, cloud machine) |
| How often | All day, every few minutes | When a version is ready |
| Our tool | `mvn package cargo:run` | `mvn package` to build a WAR, then copy it to the server |

🏪 You arrange and test your shop in your **garage** first (develop). When it's ready, you **pack** it and **move into the mall** (deploy).

---

## Step 2: The standard web app structure

**Every** Jakarta container in the world expects an app to look like this:

```
my-app/                          <- the app's ROOT folder
├── index.html                   ┐
├── about.html                   │ PUBLIC AREA
├── css/style.css                │ The browser can request these directly:
├── images/logo.png              │   http://.../my-app/css/style.css
│                                ┘
└── WEB-INF/                     ┐ PRIVATE AREA ("staff only")
    ├── web.xml                  │ The deployment descriptor (settings), see Step 4
    ├── classes/                 │ Your COMPILED Java (.class files), e.g. servlets
    │   └── com/school/MyServlet.class
    ├── lib/                     │ Extra libraries (.jar files) your app needs
    │   └── some-library.jar     │
    └── secret.html              │ ANY file here is hidden from browsers
                                 ┘
```

**The golden rule: the browser can NEVER directly open anything inside `WEB-INF`.**
Ask for `/WEB-INF/web.xml` and the container answers **404**. That's what makes it safe for settings, code, and (later) JSP pages
that should only be shown through a servlet (topic 11).

🏪 The **shop floor** (public area) is where customers walk around. The **store-room** (`WEB-INF`) has a "STAFF ONLY" door.
Customers can't go in, but **staff** (your servlets) can fetch things from it and bring them out.

---

## Step 3: How our Maven project becomes that structure

Look at any `example/` or `project/` folder. It **doesn't** look exactly like Step 2. That's because we keep
**source files** (what we write) separate from the **built app** (what the server runs). Maven builds one from the other:

```
WHAT YOU WRITE (source)                       WHAT MAVEN BUILDS (target/topic03-project/)
───────────────────────                       ──────────────────────────────────────────
src/main/java/com/school/X.java   ─compile─►  WEB-INF/classes/com/school/X.class
src/main/webapp/index.html        ──copy───►  index.html
src/main/webapp/css/style.css     ──copy───►  css/style.css
src/main/webapp/WEB-INF/web.xml   ──copy───►  WEB-INF/web.xml
pom.xml <dependency> (not provided) ─copy──►  WEB-INF/lib/some-library.jar
```

So: **`src/main/webapp/` = the public root**, and **Java files go in `src/main/java/`**.
Never put `.java` files in `webapp/`.

---

## Step 4: The deployment descriptor `web.xml`

`WEB-INF/web.xml` is the app's **instruction sheet for the container**. Tomcat reads it when the app starts,
and it is the place where **every servlet is registered** and every app-wide setting is written. Its skeleton:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
    <!-- settings go here -->
</web-app>
```

The most common things you'll put in it:

**a) Welcome files: "which page to show when someone visits just the folder `/`"**

```xml
<welcome-file-list>
    <welcome-file>home.html</welcome-file>
    <welcome-file>index.html</welcome-file>   <!-- tried in order -->
</welcome-file-list>
```

**b) Registering a servlet the classic way (two parts: WHO, then WHERE)**

```xml
<servlet>                                   <!-- 1. WHO: give a class a nickname -->
    <servlet-name>classic</servlet-name>
    <servlet-class>com.school.example.ClassicServlet</servlet-class>
</servlet>
<servlet-mapping>                           <!-- 2. WHERE: which URL goes to that nickname -->
    <servlet-name>classic</servlet-name>
    <url-pattern>/classic</url-pattern>
</servlet-mapping>
```

**c) App-wide settings (context parameters): values your code can read, without hard-coding them**

```xml
<context-param>
    <param-name>schoolName</param-name>
    <param-value>Bright Future Academy</param-value>
</context-param>
```
Read it in a servlet with `getServletContext().getInitParameter("schoolName")`.

**d) Custom error pages: "if something goes wrong, show this friendly page instead of the ugly default"**

```xml
<error-page>
    <error-code>404</error-code>
    <location>/errors/404.html</location>
</error-page>
<error-page>
    <exception-type>java.lang.Exception</exception-type>   <!-- any crash in your code -->
    <location>/errors/500.html</location>
</error-page>
```

**e) Session timeout (topic 05), security rules (topic 13), filters and listeners (topics 07, 08)**: these also go here.

🏪 The mall manager reads your sheet: *"The first thing customers see is the price board (welcome file).
If a customer asks for a product we don't have, show the 'Sorry' sign (404 page). The shop's name is 'Bright Future'."*

---

## Step 5: How the container finds your servlets (the rules of registration)

A servlet is just a Java class. Tomcat does **not** go looking through your classes. It only runs a servlet
that is **registered** in `web.xml` with the two entries from Step 4 (b). The rules:

| Rule | Example | If you break it |
|---|---|---|
| `<servlet-name>` must be **identical** in `<servlet>` and `<servlet-mapping>`. It's what joins them | `classic` and `classic` | The app **fails to start** (the log names the unknown servlet) |
| `<servlet-class>` is the **full** class name: package + class | `com.school.example.ClassicServlet` | The app fails to start, or gives **500**: `ClassNotFoundException` |
| `<url-pattern>` starts with `/` (exact, like `/about`, or prefix, like `/shop/*`), or is `*.ext`, or is **empty** (the app's root) | `/classic` | The app fails to start: "Invalid url-pattern" |
| One servlet can answer at **several** URLs: add more `<url-pattern>`s | `/students` and `/pupils` | |
| A class with **no** mapping is never called | | Its URL gives **404** |

🏪 `web.xml` is the **mall directory board**. A shop that's not on the board still exists, but no customer can find it.

Why learn it this way first? Because `web.xml` shows you, in one file, **everything** the container knows about your app:
which servlets exist, at which URLs, with which settings. Topics 02 to 04 register every servlet like this.

> 💡 **Coming in topic 05: the shortcut.** Writing `@WebServlet("/classic")` on top of the class replaces
> the two entries above. It's shorter, but it hides the registration inside the Java code.
> You'll understand exactly what it does, because it does exactly what you've been writing in `web.xml`.
> (If both are used for the same servlet, `web.xml` wins.)

---

## Step 6: Context path and URL anatomy

One server can run **many apps** at the same time. The **context path** says which app a URL belongs to:

```
http://localhost:8080 /portfolio /contact
└──────┬────────────┘ └───┬────┘ └──┬───┘
   the server        context path  the path INSIDE the app
   (the mall)        (shop unit)   (the counter inside the shop)
```

- On **Tomcat**, the context path is normally the **WAR file name**: `portfolio.war` → `/portfolio`.
  A WAR named `ROOT.war` gets the empty context path `/`.
- With our **`mvn package cargo:run`**, the parent pom tells Tomcat to use the context path `/` (`<context>/</context>`),
  so URLs stay short: `http://localhost:8080/contact`.

**Tip for your code:** use **relative links** (`href="contact"`, not `href="/contact"`) so the app works under any context path.
In JSPs (later) you can also use `request.getContextPath()`.

---

## Step 7: Packaging the WAR file

A **WAR** (Web ARchive) is simply a **ZIP file** with the Step-2 structure inside and the extension `.war`.

```bash
cd 03-developing-and-deploying-a-web-application/project
mvn package
```

Maven will:

1. **compile** `src/main/java` into `.class` files
2. **copy** `src/main/webapp` and the libraries into the structure
3. **zip** it into **`target/topic03-project.war`**

**Look inside it yourself:** copy the `.war` file, rename the copy to `.zip`, and open it. You'll see `index.html`, `WEB-INF/web.xml`,
and `WEB-INF/classes/...`, exactly the structure from Step 2.

📦 The WAR is the **sealed shipping box**. One file, everything inside, in the standard arrangement that every mall accepts.

---

## Step 8: Deploying

### a) In class: Tomcat started by Maven (what we use)

```bash
mvn package cargo:run
```

This one command does the whole deployment for you, and you can watch each step in the terminal:

1. `package` builds `target/topic03-project.war` (Step 7)
2. `cargo:run` downloads Tomcat 10.1 (first time only) and creates a private Tomcat setup inside `target/cargo/`
3. It **copies the WAR into that Tomcat's `webapps` folder**, which *is* deploying
4. It starts Tomcat, and you see `Tomcat 10.1.34 started on port [8080]`

This is the "testing in the garage" mode: a real Tomcat, but only on your laptop.

### b) On a Tomcat you installed yourself (a lab machine or a real server)

This is exactly what Cargo did for you, done by hand:

1. `mvn package` to get `target/topic03-project.war`
2. Copy it into Tomcat's **`webapps/`** folder
3. Start Tomcat (`bin\startup.bat` on Windows). Tomcat **unzips and starts it automatically** (this is called *hot deployment*)
4. Visit `http://localhost:8080/topic03-project/`

- **Redeploy** a new version: copy the new WAR over the old one.
- **Undeploy**: delete the WAR from `webapps/`.

The same WAR also deploys on GlassFish, WildFly, Payara, and others, because it's **standard**.

---

## Step 9: What the container does at deploy time

When an app is deployed, the container:

1. **Unpacks** the WAR (if needed)
2. **Reads `WEB-INF/web.xml`**
3. **Scans `WEB-INF/classes` and `WEB-INF/lib`** for annotations (`@WebServlet` and friends, which you'll use from topic 05)
4. Builds its **map of URLs → servlets**
5. Makes the app **available** at its context path

If `web.xml` has a mistake (for example a misspelled class name), the app **fails to start**,
and the reason is printed in the **log** (the terminal). That brings us to Step 10.

---

## Step 10: The development cycle and reading logs

Your daily loop from now on:

```
  ┌──► 1. EDIT code ──► 2. SAVE ──► 3. Ctrl+C, then  mvn package cargo:run  ──┐
  │                                                                           │
  └──── 5. FIX ◄──── read the error in the TERMINAL (log) ◄──── 4. TEST in browser
```

- **After ANY change** (Java, JSP, HTML, CSS): stop Tomcat with `Ctrl + C` and run `mvn package cargo:run` again.
  Tomcat runs the **packaged WAR**, so it only sees your change after you re-package.
  Tip: press the **↑ arrow key** in the terminal to bring the last command back.
- **When something breaks, read the terminal from the bottom up** and look for the first line mentioning **your** class names.
  Java errors (*stack traces*) look scary, but the useful line is usually the one that contains `com.school...`.
- `System.out.println("...")` in a servlet prints to that same terminal, which is the simplest debugging tool there is.

---

## Now try it

### Example: the guided tour

```bash
cd 03-developing-and-deploying-a-web-application/example
mvn package cargo:run
```

Open <http://localhost:8080/> and follow the links. The home page **itself** explains what each link proves:
welcome file from `web.xml`, a `web.xml`-mapped servlet, an annotated servlet, the hidden `WEB-INF` file, context params,
and the custom 404 and 500 pages.

### Project: My Portfolio

```bash
cd 03-developing-and-deploying-a-web-application/project
mvn package cargo:run   # build the WAR (target/topic03-project.war) AND deploy it to Tomcat
```

Afterwards, look in `target/cargo/configurations/tomcat10x/webapps/`. That's the `webapps` folder of the Tomcat that
Maven set up. Your app is deployed there as **`ROOT.war`**, which is why it lives at `/` (remember Step 6).

Change the owner name in `src/main/webapp/WEB-INF/web.xml` (`<context-param>`) to **your** name, restart, and watch it appear on every page.

Then go to **[QUIZ.md](QUIZ.md)**.
