# QUIZ 03: Developing And Deploying a Web Application

## Part A: Quick check

**1.** A user types `http://localhost:8080/WEB-INF/web.xml`. What happens?

<details><summary>Answer</summary>They get a <b>404</b>. Nothing inside <code>WEB-INF</code> can be requested directly by a browser.</details>

**2.** Where do compiled servlet classes end up inside a WAR?
a) the root  b) `WEB-INF/lib`  c) `WEB-INF/classes`  d) `META-INF`

<details><summary>Answer</summary>c) <code>WEB-INF/classes</code>. Library <code>.jar</code> files go in <code>WEB-INF/lib</code>.</details>

**3.** In our Maven projects, which folder becomes the public root of the app?

<details><summary>Answer</summary><code>src/main/webapp/</code></details>

**4.** What is a WAR file, really?

<details><summary>Answer</summary>A <b>ZIP file</b> containing the standard web-app structure, with the extension <code>.war</code> (Web ARchive).</details>

**5.** On Tomcat, you deploy `shop.war`. What is the app's context path?

<details><summary>Answer</summary><code>/shop</code>, so the app lives at <code>http://localhost:8080/shop/</code>. (<code>ROOT.war</code> would be <code>/</code>.)</details>

**6.** A servlet has `@WebServlet("/a")`, and `web.xml` maps the same servlet to `/b`. Which wins?

<details><summary>Answer</summary><code>web.xml</code> wins when it configures the same servlet.</details>

**7.** Which command builds the WAR? Which runs the app for development?

<details><summary>Answer</summary><code>mvn package</code> builds <code>target/&lt;name&gt;.war</code>. <code>mvn package cargo:run</code> runs for development.</details>

## Part B: Explain in your own words

**8.** Using the shopping-mall picture, explain: WAR, deploying, context path, `WEB-INF`.

<details><summary>Model answer</summary>The WAR is the sealed box with the whole shop inside. Deploying is moving the box into the mall and opening for business. The context path is the shop's unit number in the mall. WEB-INF is the "staff only" store-room: customers can't go in, but staff (servlets) can bring things out.</details>

**9.** Give two reasons to put a value in a `<context-param>` instead of writing it in Java code.

<details><summary>Model answer</summary>(1) You can change it without recompiling: edit web.xml and restart. (2) It's in one central place that every servlet can read, so there's no copy-pasting.</details>

**10.** Your app won't start, and the terminal shows a long error. What's your strategy?

<details><summary>Model answer</summary>Read it calmly and look for the first line that mentions <i>my own</i> classes (<code>com.school...</code>) or a clear message like <code>ClassNotFoundException: com.school.example.ClasicServlet</code>, which here is a typo in web.xml. Fix that, save, and run again.</details>

## Part C: Hands-on

**11.** In `example/`, change the welcome file in `web.xml` from `home.html` to `index.html` and create a simple `index.html`. Restart and confirm `/` shows the new page.

**12.** In `project/`, change `ownerName` to your own name in `web.xml`. Restart and check every page.

**13.** Run `mvn package` in `project/`. Copy `target/topic03-project.war`, rename the copy to `.zip`, and open it. List three files you find and where they came from.

**14. Challenge:** add a servlet `/skills` that reads a context-param `skills` with the value `Java,HTML,CSS,Servlets` and shows it as a bulleted list.
*Hint:* `getInitParameter("skills").split(",")`
