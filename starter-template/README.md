# Starting a Fresh Course Folder

There are two ways to set up the folder structure for your class:

| | **Way A: Build it by hand** | **Way B: Copy the template** |
|---|---|---|
| When | **The first class** (topic 02) | **Every topic after that** |
| Time | About 20–30 minutes | About 2 minutes |
| Why | Students learn what every folder and file is *for* | Saves class time once they understand it |

> 💡 **Recommended:** do **Way A once together** in class, so every student builds one app with their own hands.
> From then on, use **Way B** for each new topic.

The finished shape you're building (the same shape as the reference course):

```
my-jakarta-course/                  ← the COURSE folder
├── pom.xml                         ← the PARENT pom: shared settings (made once)
└── 02-introduction-to-jakarta-ee/  ← one folder per TOPIC
    ├── README.md  LESSON.md  QUIZ.md
    ├── example/                    ← a small Maven web app
    │   ├── pom.xml                 ← tiny: "my parent is ../../pom.xml"
    │   └── src/main/
    │       ├── java/com/school/example/HelloServlet.java
    │       └── webapp/
    │           ├── index.html
    │           ├── css/style.css
    │           └── WEB-INF/web.xml
    └── project/                    ← another small Maven web app (same shape)
```

---

## Way A: Build it by hand (the first class)

In each step, create a folder or file **and say what it is for**. That understanding is the point of this exercise.

You can create folders and files in **VS Code's Explorer** (right-click → *New Folder...* / *New File...*)
or type the commands shown into the VS Code terminal (`` Ctrl+` ``, which uses PowerShell on Windows).

### Step 1: Create the course folder

```powershell
cd $HOME\Desktop
mkdir my-jakarta-course
code my-jakarta-course
```
(The last line opens the folder in VS Code. Or use *File → Open Folder...*)

🏫 **Say:** "This is our school building. Everything for the whole course lives inside it."

### Step 2: Create the parent `pom.xml` in the course folder

Create a file named **`pom.xml`** directly inside `my-jakarta-course/` and paste in the full contents of
[starter-template/pom.xml](pom.xml).

🏫 **Say:** "This is the school rule book. It says once, for every app we'll ever make: compile for Java 17,
give every app the Jakarta libraries, and run apps on Tomcat. Each app will just say *'follow the rule book'*."

The three most important parts to point at:

| Part of the file | What it means |
|---|---|
| `<packaging>pom</packaging>` | "I am not an app myself. I'm only shared settings for other apps." |
| `<dependencies>` | The Jakarta libraries (Servlet, JSP, EL, JSTL) every app gets |
| the `cargo-maven3-plugin` | The part that downloads Tomcat and runs our app on it |

### Step 3: Create the topic folder

```powershell
mkdir 02-introduction-to-jakarta-ee
```

Optionally create empty `README.md`, `LESSON.md`, and `QUIZ.md` files inside it for notes.

🏫 **Say:** "One floor of the building per topic."

### Step 4: Create the app folder and its `pom.xml`

```powershell
mkdir 02-introduction-to-jakarta-ee\example
```

Inside `example/`, create **`pom.xml`** with exactly this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.school.jakarta</groupId>
        <artifactId>course-parent</artifactId>
        <version>1.0</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>

    <artifactId>topic02-example</artifactId>
    <packaging>war</packaging>
</project>
```

🏫 **Say:** "This is one classroom. It says three things:"

| Line | Meaning |
|---|---|
| `<parent>` ... `<relativePath>../../pom.xml</relativePath>` | "My rule book is two folders up." (`..` means *one folder up*: `example` → `02-...` → course folder) |
| `<artifactId>topic02-example</artifactId>` | "My name is topic02-example." Must be **unique** for each app |
| `<packaging>war</packaging>` | "Package me as a WAR, a web app" |

### Step 5: Create the Java folder and your first servlet

Java code goes in `src/main/java`, and then one folder per part of the **package name**.
The package `com.school.example` becomes the folders `com\school\example`:

```powershell
mkdir 02-introduction-to-jakarta-ee\example\src\main\java\com\school\example
```

Inside that last folder create **`HelloServlet.java`**. Copy it from
[topic-template/example/src/main/java/com/school/example/HelloServlet.java](topic-template/example/src/main/java/com/school/example/HelloServlet.java).

⚠️ **Two rules students often break:**
1. The **file name must equal the class name**: `HelloServlet.java` ↔ `public class HelloServlet`
2. The **`package` line must match the folders**: `package com.school.example;` ↔ `java\com\school\example\`

### Step 6: Create the web folder (public pages and the private `WEB-INF`)

```powershell
mkdir 02-introduction-to-jakarta-ee\example\src\main\webapp\WEB-INF
mkdir 02-introduction-to-jakarta-ee\example\src\main\webapp\css
```

Then create these three files (copy them from the matching places in [topic-template/example/](topic-template/example/)):

| File | What it is |
|---|---|
| `src/main/webapp/index.html` | The home page. Everything in `webapp/` is **public**: browsers can request it |
| `src/main/webapp/css/style.css` | The stylesheet (optional, but it makes pages readable) |
| `src/main/webapp/WEB-INF/web.xml` | The app's settings, including **which URL goes to which servlet**. `WEB-INF` is **private**: browsers can never open it |

🏫 **Say:** "`webapp` is the shop floor customers can walk around. `WEB-INF` is the locked store-room, staff only."

Open `web.xml` and point at the two entries that connect `/hello` to `HelloServlet`:

```xml
<servlet>                                    <!-- WHO: a nickname for the class -->
    <servlet-name>hello</servlet-name>
    <servlet-class>com.school.example.HelloServlet</servlet-class>
</servlet>
<servlet-mapping>                            <!-- WHERE: which URL goes to that nickname -->
    <servlet-name>hello</servlet-name>
    <url-pattern>/hello</url-pattern>
</servlet-mapping>
```

🏫 **Say:** "Tomcat doesn't search for our classes. It reads this list. A servlet that isn't on the list can't be reached."

> In **project/** the class is `com.school.project.HelloServlet`. Every new servlet in topics 02–04 needs its own
> pair of entries here. From topic 05 we'll learn the `@WebServlet("/hello")` shortcut that replaces them.

### Step 7: Run it

```powershell
cd 02-introduction-to-jakarta-ee\example
mvn package cargo:run
```

Wait for **`Tomcat 10.1.34 started on port [8080]`**, then open:

- <http://localhost:8080/> shows the `index.html` page
- <http://localhost:8080/hello> shows the servlet's page

Stop with **Ctrl + C**.

### Step 8: Look at what Maven made

Open the new **`target/`** folder inside `example/`:

- `target/topic02-example.war` is the packaged app (the delivery box)
- `target/topic02-example/` is the same thing unzipped. Notice `WEB-INF/classes/com/school/example/HelloServlet.class`, your compiled servlet.

🏫 **Say:** "We never edit `target/`. Maven rebuilds it every time."

✅ **Done!** Every student now has a working Jakarta app they built from nothing.

---

## Way B: Copy the template (every topic after that)

### Once per course: create the course folder

1. Copy the whole **`starter-template`** folder to where you want your course (for example the Desktop).
2. Rename the copy to **`my-jakarta-course`** (or any name).
3. Open it in VS Code.

You now have the parent `pom.xml` and the `topic-template` folder.

### For each new topic: 4 small steps

1. **Copy** the `topic-template` folder (inside your course folder) and **rename** the copy, for example `05-session-tracking`.
2. Open `05-session-tracking/example/pom.xml` and change `topicXX-example` to **`topic05-example`**.
3. Open `05-session-tracking/project/pom.xml` and change `topicXX-project` to **`topic05-project`**.
4. Run it to check:
   ```powershell
   cd 05-session-tracking\example
   mvn package cargo:run
   ```
   Open <http://localhost:8080/hello> and you should see **"It works! 🎉"**.

Now add the lesson's servlets and pages next to `HelloServlet.java` and `index.html`.

> 💡 **Using code from the reference course:** your course folder uses the **same parent pom** as the reference course
> (`jakarta-template`), so you can copy any `src/` code from it into your own apps and it will run unchanged.

---

## Common mistakes

| Symptom | Likely cause | Fix |
|---|---|---|
| `Non-resolvable parent POM` | The app isn't exactly two folders below the parent `pom.xml`, or the parent `pom.xml` is missing | Keep the shape `course/topic/app/pom.xml`, or fix `<relativePath>` |
| `class HelloServlet is public, should be declared in a file named...` | File name and class name differ | Rename the file or the class so they match |
| Servlet gives **404** | The servlet is missing from `web.xml`, or its `<url-pattern>` is different from the URL you typed | Add both `<servlet>` and `<servlet-mapping>`, check the URL, and restart |
| App **fails to start**, or **500** with `ClassNotFoundException` | `<servlet-class>` doesn't match the real class: a typo, or the `package` line doesn't match the folders | Use the full name, e.g. `com.school.example.HelloServlet`, and check the `package` line |
| App fails to start: "unknown servlet name" | The `<servlet-name>` in `<servlet-mapping>` is spelled differently from the one in `<servlet>` | Make the two names identical |
| `Port 8080 is in use` | Another app is still running | Ctrl+C in the other terminal, or `mvn package cargo:run -Dtomcat.port=9090` |
| Change doesn't appear | Tomcat is still running the old WAR | Ctrl+C, then `mvn package cargo:run` again |
