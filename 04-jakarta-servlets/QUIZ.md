# QUIZ 04: Jakarta Servlets

## Part A: Quick check

**1.** How many times is `init()` called for one servlet? And `doGet()`?

<details><summary>Answer</summary><code>init()</code> is called <b>once</b>. <code>doGet()</code> is called <b>once per GET request</b>, which could be millions of times.</details>

**2.** How many objects of a servlet class does the container normally create?

<details><summary>Answer</summary><b>One.</b> All requests share it, each on its own thread.</details>

**3.** A form has three checkboxes named `color`. Which method gets all ticked values?

<details><summary>Answer</summary><code>request.getParameterValues("color")</code> returns a <code>String[]</code>, or <code>null</code> if nothing was ticked.</details>

**4.** Your servlet only overrides `doGet`. Someone sends a POST. What do they get?

<details><summary>Answer</summary><b>405 Method Not Allowed</b></details>

**5.** Which one changes the URL in the browser's address bar: `forward` or `sendRedirect`?

<details><summary>Answer</summary><code>sendRedirect</code>. The browser is told to make a <i>new</i> request to the new URL.</details>

**6.** You set a request attribute and then call `sendRedirect`. Can the next page read the attribute?

<details><summary>Answer</summary><b>No.</b> A redirect makes a brand-new request, and attributes belong to the old one.</details>

**7.** Which URL pattern would catch `/shop/shoes/12`?
a) `/shop`  b) `/shop/*`  c) `*.shop`

<details><summary>Answer</summary>b) <code>/shop/*</code>. <code>getPathInfo()</code> would return <code>/shoes/12</code>.</details>

## Part B: Explain in your own words

**8.** What was the problem with CGI, and how do servlets solve it? (Use an analogy.)

<details><summary>Model answer</summary>CGI started a new program for every request and killed it afterwards. That's like hiring and firing a chef for every customer: slow and wasteful. A servlet is created once, stays in memory, and handles every request on a lightweight thread, like one chef who stays all day while many waiters bring orders.</details>

**9.** Why is this code dangerous?

```java
public class CartServlet extends HttpServlet {
    private List<String> items = new ArrayList<>();
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        items.add(req.getParameter("item"));
    }
}
```

<details><summary>Answer</summary>There's only ONE servlet object, so <code>items</code> is shared by <b>every user</b>. Everyone puts things into the same cart. Also, <code>ArrayList</code> isn't thread-safe, so simultaneous requests can corrupt it. Per-user data belongs in the <b>session</b> (topic 05).</details>

**10.** Explain the difference between a **parameter** and an **attribute**.

<details><summary>Model answer</summary>A parameter comes <b>from the browser</b> (form, query string) and is always a String, which you can read but not change. An attribute is put on the request (or session or context) <b>by your code</b>, can be any Java object, and is used to pass data between servlets and pages.</details>

**11.** What is Post/Redirect/Get and what problem does it prevent?

<details><summary>Model answer</summary>After a successful POST, the servlet redirects to a GET page instead of showing the result directly. Refreshing then repeats the harmless GET instead of re-submitting the POST, which prevents double orders or double payments.</details>

## Part C: Hands-on

**12.** In `example/`, open `/lifecycle` five times, then stop the server. Copy the lines from the terminal that prove the life cycle.

**13.** In `project/`, open `WEB-INF/web.xml`. Change `loadSampleData` to `false`, and add `JSS4` to the end of `classNames`. Restart. What two things changed, and why didn't you need to touch any Java code?

<details><summary>Answer</summary>The list starts <b>empty</b>, and <b>JSS4</b> appears in the Class dropdown. Both values are <b>read from web.xml</b> by the code (<code>getInitParameter</code> and <code>getServletContext().getInitParameter</code>), so changing the settings file is enough.</details>

**14.** In `project/`, remove the `<load-on-startup>1</load-on-startup>` line from `web.xml`, restart, and open <http://localhost:8080/add-student> **first** (before the list page). Submit the form. What happens, and why?

<details><summary>Answer</summary>A <b>500 error</b> (a <code>NullPointerException</code>). Without <code>load-on-startup</code>, <code>StudentListServlet.init()</code> only runs on the first visit to <code>/students</code>, so the <code>StudentStore</code> was never put in the <code>ServletContext</code>, and <code>StudentStore.from(...)</code> returned <code>null</code>. Put the line back.</details>

**15. Challenge:** add a servlet `/stats` (register it in `web.xml`!) that shows how many students there are in each class.
Why can it see the same students as the other servlets?
*Hint:* get the store with `StudentStore.from(getServletContext())`, then count with a loop over `findAll()`.

<details><summary>Answer</summary>Because the <code>ServletContext</code> is <b>one object shared by the whole application</b>. <code>StudentListServlet</code> put the <code>StudentStore</code> there, so every servlet that reads it gets the very same store.</details>
