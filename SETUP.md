# SETUP: Your Tools, Explained Simply

## Part 1: What we use, and why

This course uses **Apache Tomcat 10.1**, the same server as the reference material, but **without Eclipse**
and **without installing Tomcat by hand**. Maven downloads Tomcat and runs your app inside it with one command.

### The real-life picture

Think of running a web app like **holding a class in a hall**.

- **The old way (Eclipse + manually installed Tomcat):** You must first **build the hall yourself** (download Tomcat, unzip it),
  **register the hall with the school office** (add the server inside Eclipse, set paths, pick versions),
  and then **book it every time** ("Run on Server", restart, re-publish). When something breaks,
  you are not sure whether the problem is your lesson, the hall, or the booking office.

- **Our way (Maven + Tomcat via the Cargo plugin):** You have **an assistant with the keys to a ready-made hall**.
  You say one sentence, `mvn package cargo:run`. The assistant (Maven) packs your lesson (builds the WAR),
  fetches the hall (downloads Tomcat the first time), puts your app inside, and opens the door at `http://localhost:8080`.
  When you're done, press `Ctrl + C` and the hall is locked again.

| Word | What it really is | In the analogy |
|---|---|---|
| **JDK** | The Java toolkit (compiler + runtime) | The electricity everything runs on |
| **Maven** | A build tool: downloads libraries, compiles code, packages the app | The assistant who follows the instruction sheet |
| **pom.xml** | Maven's instruction sheet for a project | The instruction sheet |
| **Tomcat** | The Jakarta web server (servlet container) that runs your app | The hall |
| **Cargo** | A Maven plugin that downloads, starts, and stops servers like Tomcat | The assistant's set of keys |
| **Your editor** (VS Code or IntelliJ) | Where you write code | Your notebook and pen |

### Why this is good for a classroom

1. **Real Tomcat**, exactly what the reference material and many companies use.
2. **Nothing to install or configure** except Java and Maven. Tomcat is downloaded automatically.
3. **Same on every computer:** the `pom.xml` fixes the exact Tomcat version (10.1.34), so "it works on my machine" problems mostly disappear.
4. **Any editor works**, because the server is started from the terminal, not from the editor.

---

## Part 2: Install (one time only)

### Step 1: Java (JDK 17 or newer)

You already have **JDK 23**, which is fine. Check in a terminal:

```bash
java -version
```

For students who don't have it: download "JDK 21 LTS" from <https://adoptium.net> and install it with the default options.

### Step 2: Maven

1. Download the **Binary zip archive** from <https://maven.apache.org/download.cgi>
   (file name looks like `apache-maven-3.9.x-bin.zip`).
2. Unzip it to a simple place, for example `C:\tools\apache-maven-3.9.9`.
3. Add Maven's `bin` folder to your **PATH**:
   - Press the Windows key and type **"environment variables"**, then open **"Edit the system environment variables"**.
   - Click **Environment Variables...**
   - Under *User variables*, select **Path**, click **Edit**, click **New**, and paste `C:\tools\apache-maven-3.9.9\bin`
   - Click OK on every window.
4. **Close and reopen** your editor/terminal, then check:

```bash
mvn -v
```

You should see `Apache Maven 3.9.x` and your Java version.

> **The first run downloads things** (Tomcat, about 13 MB, plus the Jakarta libraries) into a folder called `.m2`
> in your user folder. It takes a few minutes **once**, and needs internet. After that, starting an app takes a few seconds.
> 💡 **Classroom tip:** run any app once on each lab computer *before* the lesson, so the downloads are already done.

### Step 3: Your editor

Use **VS Code** or **IntelliJ IDEA**. The course works the same in both.

- **VS Code:** install the **Extension Pack for Java** (by Microsoft) from the Extensions panel (`Ctrl+Shift+X`).
- **IntelliJ IDEA:** `File → Open...` and pick the course folder's **`pom.xml`**, then choose **Open as Project**.
  (JSP editing support needs the *Ultimate* edition, which is free for teachers and students via the JetBrains education programme.)

### Step 4: Open the course folder

Open the `jakarta-template` folder in your editor, then open the built-in terminal
(VS Code: `` Ctrl+` ``, IntelliJ: `Alt+F12`).

---

## Part 3: Running an app

```bash
cd 04-jakarta-servlets/example
mvn package cargo:run
```

`package` builds the WAR, and `cargo:run` starts Tomcat with it. Wait until you see:

```
[INFO] Tomcat 10.1.34 started on port [8080]
```

Then open **<http://localhost:8080/>** in your browser.
To stop the server, click in the terminal and press **Ctrl + C**.

### Changed some code? Restart.

Tomcat runs the **packaged** WAR, so after you change **any** file (Java, JSP, HTML, CSS):

1. Press **Ctrl + C** to stop Tomcat
2. Run **`mvn package cargo:run`** again (press the ↑ arrow key to bring the command back)
3. Refresh the browser

### Useful commands

| Command (run inside an example/ or project/ folder) | What it does |
|---|---|
| `mvn package cargo:run` | Build the app and start it on Tomcat at <http://localhost:8080/> |
| `mvn package cargo:run -Dtomcat.port=9090` | Same, but on port 9090 (use this if 8080 is busy) |
| `mvn package` | Only build the **WAR file** in `target/` (see topic 03) |
| `mvn clean` | Delete the `target/` folder (all compiled output, including the copy of Tomcat's settings) |

Run from the **course root folder**, `mvn package` builds **every** app in the course at once.
That is a good way to check that everything compiles.

---

## Part 4: Common problems

| Problem | Cause | Fix |
|---|---|---|
| `mvn is not recognized` | Maven's `bin` is not on PATH, or the terminal was opened before you changed PATH | Redo Step 2, then **restart your editor** |
| `Address already in use` / `Port 8080 is in use` | Another server (or a forgotten old run) is using port 8080 | Stop the other one (Ctrl+C in its terminal) or run with `-Dtomcat.port=9090` |
| Download errors like `Connection reset` / `Could not transfer artifact` | The internet dropped during the first download | Just run the command again. Maven continues where it stopped |
| Browser shows **404** | Wrong URL | Check the URL in the topic's README. All apps run at the root: `http://localhost:8080/...` |
| My change doesn't show | Tomcat is still running the old WAR | Ctrl+C, then `mvn package cargo:run` again |
| Red errors on `jakarta.servlet` imports in the editor | The editor hasn't read the `pom.xml` yet | Run `mvn package` once from the root. VS Code: command palette → *"Java: Clean Java Language Server Workspace"*. IntelliJ: Maven panel → *Reload All Maven Projects* |

---

## Part 5: Using your own installed Tomcat (optional)

Every app is a standard WAR, so you can also deploy to a Tomcat you installed yourself (for example in a computer lab):

1. Download **Tomcat 10.1 or newer** from <https://tomcat.apache.org> (Tomcat 9 and older use the old `javax` names and **will not** work).
2. In any example/project folder run `mvn package`. This creates `target/<name>.war`.
3. Copy that `.war` into Tomcat's `webapps/` folder and run `bin\startup.bat`.
4. Open `http://localhost:8080/<name>/`. A manually installed Tomcat uses the WAR file name as the context path (topic 03 explains this).
