# QUIZ 02: Introduction to Jakarta EE

## Part A: Quick check

**1.** Jakarta EE is best described as...
a) one server program  b) a set of specifications (rules)  c) a code editor  d) a database

<details><summary>Answer</summary>b) A set of <b>specifications</b>. Jetty, Tomcat, GlassFish, and others are <i>implementations</i> of them.</details>

**2.** Which of these is a **servlet container** (not a full application server)?
a) GlassFish  b) WildFly  c) Tomcat  d) Nginx

<details><summary>Answer</summary>c) <b>Tomcat</b> (Jetty is another). GlassFish and WildFly are full application servers. Nginx is a web server with no Java container.</details>

**3.** An old tutorial says `import javax.servlet.http.HttpServlet;`. In our project you should write...

<details><summary>Answer</summary><code>import jakarta.servlet.http.HttpServlet;</code>. The package was renamed in Jakarta EE 9.</details>

**4.** Will our `jakarta` code run on **Tomcat 9**? Why?

<details><summary>Answer</summary><b>No.</b> Tomcat 9 only understands the old <code>javax</code> names. You need Tomcat 10.1+.</details>

**5.** What does `<scope>provided</scope>` mean in the `pom.xml`?

<details><summary>Answer</summary>"I need this library to <b>compile</b>, but the <b>server provides</b> it at runtime, so don't pack it inside my app." Like a chef who doesn't bring an oven to the restaurant.</details>

**6.** Where is the `main()` method in a Jakarta web app?

<details><summary>Answer</summary>There isn't one in <i>your</i> code. The <b>container</b> starts up, finds your servlets, and calls their methods when requests arrive (Inversion of Control).</details>

## Part B: Explain in your own words

**7.** Explain **specification vs implementation** with an example that isn't from this lesson.

<details><summary>Model answer</summary>Example: the rules of football (the spec) vs an actual match played by two teams (an implementation). Or: the SIM card size standard vs the SIM cards from MTN, Airtel, and others. Any phone that follows the standard accepts any of those SIMs.</details>

**8.** List **five jobs** the container does so that you don't have to.

<details><summary>Answer</summary>Any five of these: listening on a port and accepting connections; parsing the raw HTTP request into a request object; decoding parameters; mapping URLs to servlets; writing status line and headers; generating 404s; using threads for many users; managing servlet lifecycle; sessions; security.</details>

**9.** What does "Don't call us, we'll call you" mean for a servlet?

<details><summary>Model answer</summary>We never call <code>doGet</code> ourselves. We only write it and register its address in <code>web.xml</code>. The container decides <b>when</b> to call it: every time a matching request arrives.</details>

## Part C: Hands-on

**10.** Run `example/` and open `/container-info`. Write down the **server product** and **Servlet version** it reports.

**11.** In `project/`, create a new servlet `AboutServlet` that shows your name and school. Register it in `WEB-INF/web.xml` at `/about` (a `<servlet>` and a `<servlet-mapping>`). Restart the app, then visit `/about`.

<details><summary>Hint: what goes in web.xml</summary><pre>&lt;servlet&gt;
    &lt;servlet-name&gt;about&lt;/servlet-name&gt;
    &lt;servlet-class&gt;com.school.project.AboutServlet&lt;/servlet-class&gt;
&lt;/servlet&gt;
&lt;servlet-mapping&gt;
    &lt;servlet-name&gt;about&lt;/servlet-name&gt;
    &lt;url-pattern&gt;/about&lt;/url-pattern&gt;
&lt;/servlet-mapping&gt;</pre>If you get a 404, check that the class name in <code>&lt;servlet-class&gt;</code> is spelled exactly right, including the package.</details>

**12.** In `project/`, visit `/greet` **without** `?name=`. What status code do you get? Find the line in `GreetServlet` that sets it.
*Hint:* in Chrome press **F12**, open the **Network** tab, and refresh to see status codes.

<details><summary>Answer</summary><b>400</b>, set by <code>response.setStatus(400);</code></details>

**13. Challenge:** add `/square?n=7` (like topic 01's challenge) as a servlet. Compare how many lines it took this time.
