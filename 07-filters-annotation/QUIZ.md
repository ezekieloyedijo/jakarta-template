# QUIZ 07: Filters and Annotations

## Part A: Quick check

**1.** Which interface does a filter implement, and which method does the work?

<details><summary>Answer</summary><code>jakarta.servlet.Filter</code>, and the method <code>doFilter(ServletRequest, ServletResponse, FilterChain)</code>.</details>

**2.** A filter forgets to call `chain.doFilter(req, res)` and writes nothing. What does the user see?

<details><summary>Answer</summary>A <b>blank page</b> (status 200 with no content). The servlet never ran.</details>

**3.** Which pattern makes a filter see **every** request?

<details><summary>Answer</summary><code>/*</code></details>

**4.** Two filters are both registered with `@WebFilter("/*")`. Which runs first?

<details><summary>Answer</summary><b>It is not defined.</b> If the order matters, register them in <code>web.xml</code> with <code>&lt;filter-mapping&gt;</code>; the first one written runs first.</details>

**5.** How many times is a filter's `init` called? And `doFilter`?

<details><summary>Answer</summary><code>init</code> once, when the app starts. <code>doFilter</code> once for every matching request.</details>

**6.** Filters A then B are in the chain. Write the order of these lines: A-after, servlet, B-before, A-before, B-after.

<details><summary>Answer</summary>A-before, B-before, servlet, B-after, A-after.</details>

**7.** Why must a filter cast `ServletRequest` to `HttpServletRequest`?

<details><summary>Answer</summary>Filters can work with any protocol, so <code>doFilter</code> receives the general types. Methods like <code>getSession()</code>, <code>getMethod()</code>, and <code>sendRedirect()</code> only exist on the HTTP versions.</details>

## Part B: Explain in your own words

**8.** Why is a login filter **safer** than a login check at the top of each servlet?

<details><summary>Model answer</summary>The check is written once and runs for every URL under the pattern. A new page added under <code>/members/</code> is protected automatically. With checks in each servlet, forgetting one line in one servlet leaves that page open to everyone.</details>

**9.** Use the school-gate analogy to explain "code before and after `chain.doFilter`".

<details><summary>Model answer</summary>The guard writes the time in the visitors' book when you go <b>in</b> (before), lets you go to your classroom (<code>chain.doFilter</code>), and writes the time again when you come back <b>out</b> (after). From the two times the guard knows how long you stayed.</details>

**10.** In the project, `AuthFilter` sets `Cache-Control: no-store`. Why?

<details><summary>Model answer</summary>So the browser doesn't keep copies of members' pages. Otherwise, after logging out, someone could press the Back button on a shared computer and see the private notices from the browser's memory.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Add a page `/members/about` (a new servlet that just says "Members' area"). Without writing any login code, check that it is protected.

<details><summary>Answer</summary>Log out and open <code>/members/about</code>: you are redirected to the login page. <code>AuthFilter</code> guards <code>/members/*</code>, so new pages are protected automatically.</details>

**12.** Write a `MaintenanceFilter` with an init parameter `closed`. When it is `"true"`, every page except `index.html` and the CSS answers **503** with "Back soon".

<details><summary>Hint</summary><code>@WebFilter(urlPatterns = "/*", initParams = @WebInitParam(name = "closed", value = "true"))</code>. Read it in <code>init</code>. In <code>doFilter</code>, let the request pass if the URI ends with <code>.css</code> or is <code>/</code> or <code>/index.html</code>; otherwise <code>response.sendError(503, "Back soon")</code>.</details>

**13.** Make `AccessLogFilter` also count the requests and print the total, like `[ACCESS #42] ...`.

<details><summary>Hint</summary>Use a field <code>private final AtomicInteger count = new AtomicInteger();</code> and <code>count.incrementAndGet()</code>. Many requests run at the same time (topic 04), so a plain <code>int</code> with <code>count++</code> could lose counts.</details>

**14. Challenge:** after logging in, send the user to the page they **wanted**, not always to `/members/notices`.

<details><summary>Hint</summary>In <code>AuthFilter</code>, before redirecting, save <code>request.getRequestURI()</code> in the session as <code>"wanted"</code>. In <code>LoginServlet.doPost</code>, read it, remove it, and redirect there. Check it starts with <code>request.getContextPath() + "/members/"</code>, so nobody can send users to another website.</details>
