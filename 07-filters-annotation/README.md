# Topic 07: Filters (and Annotations)

## The problem that existed

Imagine an app with 20 servlets, and each one needs the **same checks**:

- "Is this user logged in? If not, send them to the login page."
- "Write a line in the log: who asked for what, and how long it took."
- "Make sure the text is read as UTF-8."

Without filters you copy the same lines into **all 20 servlets**. Forget one, and that page is unprotected.
Change the rule, and you must edit 20 files.

🛂 It's like an office building where **every single office** has its own security guard checking ID cards.
That's expensive, and if one guard falls asleep, anyone can walk into that office.

**Filters** move the check to the **front door**. One guard at the entrance checks everyone **before**
they reach any office, and can even note when they leave. You write the check **once**, and it covers many pages.

## The end goal of this topic

You should be able to:

1. Explain what a **filter** is and where it sits between the browser and the servlet.
2. Write a filter with `@WebFilter` that runs code **before** and **after** a servlet.
3. Explain the **filter chain** and `chain.doFilter(...)`.
4. **Block** a request (for example a login check) and control the order of several filters.
5. Use the main web annotations: `@WebServlet`, `@WebFilter`, `@WebListener`, `@WebInitParam`.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: repeated code in many servlets** | The problem we are solving |
| 2 | **What a filter is** | The guard at the front door |
| 3 | **Your first filter: `@WebFilter` and `doFilter`** | See it working |
| 4 | **The filter chain: `chain.doFilter(...)`** | How the request moves on (or doesn't) |
| 5 | **Before and after** | Code before `chain.doFilter` runs on the way in; code after it runs on the way out |
| 6 | **Blocking a request** | The login check: redirect instead of passing on |
| 7 | **URL patterns and filter order** | Which requests a filter sees, and who goes first |
| 8 | **Filter life cycle and init parameters** | `init`, `destroy`, `@WebInitParam` |
| 9 | **The annotations family** | All the `@Web...` annotations side by side |

## What is inside this folder

| Path | What it will show |
|---|---|
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: a logging filter, a timing filter, two filters in a chain |
| [project/](project/) | **Members-Only Area**: a login page plus a filter that guards every page under `/members/` |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is a **filter**?

<details><summary>Answer</summary>A Java class that runs <b>before</b> (and optionally <b>after</b>) a servlet or page for every request that matches its URL pattern. It can inspect, change, or <b>block</b> the request.</details>

**2.** Give three common uses for filters.

<details><summary>Answer</summary>Any three of: checking <b>login</b>, <b>logging</b> requests, <b>timing</b> how long requests take, setting the <b>character encoding</b> (UTF-8), adding security headers, compressing responses.</details>

**3.** Which interface does a filter implement, and which method does the work?

<details><summary>Answer</summary>The interface <code>jakarta.servlet.Filter</code>. The work happens in <code>doFilter(ServletRequest request, ServletResponse response, FilterChain chain)</code>.</details>

**4.** What does `chain.doFilter(request, response)` do?

<details><summary>Answer</summary>It <b>passes the request on</b> to the next filter in the chain, or to the servlet if this is the last filter.</details>

**5.** What happens if a filter does **not** call `chain.doFilter(...)`?

<details><summary>Answer</summary>The request <b>stops there</b>: the servlet never runs. This is how a login filter blocks a user (it usually redirects them to the login page instead).</details>

**6.** Where do you put code that should run **after** the servlet has finished?

<details><summary>Answer</summary>In <code>doFilter</code>, on the lines <b>after</b> <code>chain.doFilter(...)</code>. For example, to measure the time a request took.</details>

**7.** What does `@WebFilter("/*")` mean?

<details><summary>Answer</summary>"Run this filter for <b>every</b> request in the app." <code>/*</code> matches all URLs.</details>

**8.** You have two filters. How can you be **sure** which one runs first?

<details><summary>Answer</summary>List them in <code>web.xml</code> with <code>&lt;filter-mapping&gt;</code>: they run <b>in the order the mappings appear</b>. With annotations alone, the order is <b>not guaranteed</b>.</details>

**9.** Name the life-cycle methods of a filter.

<details><summary>Answer</summary><code>init()</code> (once, when the app starts), <code>doFilter()</code> (once per matching request), and <code>destroy()</code> (once, when the app stops). Just like a servlet.</details>

**10.** What do `@WebServlet`, `@WebFilter`, `@WebListener`, and `@WebInitParam` each declare?

<details><summary>Answer</summary><code>@WebServlet</code> = this class is a <b>servlet</b>. <code>@WebFilter</code> = this class is a <b>filter</b>. <code>@WebListener</code> = this class is a <b>listener</b> (reacts to events, topic 08). <code>@WebInitParam</code> = a <b>name/value setting</b> given to a servlet or filter.</details>
