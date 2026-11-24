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
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | Small demos: a logging filter, a timing filter, a blocking filter with an init parameter, two filters in a chain |
| [project/](project/) | **Members-Only Notice Board**: a login page, a filter that guards every page under `/members/`, and CRUD for notices |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is a **Members-Only Notice Board** for teachers and prefects. Anyone can see the home page, but the
notices under `/members/` need a login. **Three filters** do the jobs every request needs, so the servlets stay simple.

| Feature | The user... | Request | Handled by |
|---|---|---|---|
| Log in | submits username and password | `GET` / `POST /login` | `LoginServlet` |
| Log out | clicks "Log out" | `POST /logout` | `LogoutServlet` |
| **Read** | opens the board | `GET /members/notices` | `NoticeListServlet` |
| **Create** | posts a new notice | `GET` / `POST /members/notices/add` | `AddNoticeServlet` |
| **Update** | edits a notice | `GET /members/notices/edit?id=2`, `POST .../edit` | `EditNoticeServlet` |
| **Delete** | clicks Delete | `POST /members/notices/delete` | `DeleteNoticeServlet` |
| *(every request)* | | `/*` | `EncodingFilter`, `AccessLogFilter` |
| *(every members' page)* | | `/members/*` | `AuthFilter`: no login → redirect to `/login` |

🏫 The school gate: `AuthFilter` is the **guard**, the session's `"user"` attribute is the **ID card**, and
`LoginServlet` is the **office** that hands out ID cards.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Builds and runs the app |
| `Accounts.java` | ✅ **Yes** | Who may log in (2 members, kept in code for now) |
| `Notice.java`, `NoticeStore.java` | ✅ **Yes** | The data, and the storage with CRUD and validation |
| `Page.java` | ✅ **Yes** | The layout (menu, the member's name, Log out button) and the notice form |
| `AuthFilter.java` | ✅ **Yes** | **The guard.** Without it, `/members/` is open to everyone |
| `EncodingFilter.java` | ✅ **Yes** | Reads forms as UTF-8, so names like "Adébáyọ̀" survive. Replaces `setCharacterEncoding` in every servlet |
| `AccessLogFilter.java` | ⚪ Optional | Logs every request in the terminal. Nice for learning and debugging |
| `LoginServlet`, `LogoutServlet` | ✅ **Yes** | Give out and take back the "ID card" |
| The 4 notice servlets | ✅ **Yes** | The CRUD. Notice that none of them checks the login |
| `WEB-INF/web.xml` | ⚪ Optional | Only the session timeout (20 minutes) and the welcome file. Every filter and servlet uses an annotation |
| `index.html`, `css/style.css` | ⚪ Optional | Public home page, and the look |

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic07-project</artifactId>`.

### Step 2: `Accounts.java`

A `Map` from username to `{password, full name}` (`teacher`/`teach123` → "Mrs. Bello", `prefect`/`prefect123` →
"Head Prefect"). One method, `check(username, password)`, returns the full name or `null`.
⚠️ Plain passwords in code are only for learning. Topic 13 shows how to store passwords safely.

### Step 3: `Notice.java` and `NoticeStore.java`

`Notice` has `final` fields `id`, `title`, `body`, `author`, and `posted` (a `LocalDate`) with getters.

| `NoticeStore` method | Purpose |
|---|---|
| `static from(app)` | The shared store in the `ServletContext`, made with 2 sample notices the first time |
| `add(title, body, author)` | **Create**, with today's date |
| `findAll()` | **Read**, newest first. `findById(id)` for one |
| `update(id, title, body)` | **Update**: keeps the original author and date |
| `delete(id)` | **Delete** |
| `static validate(title, body)` | Title required (max 80), text required (max 1000) |

### Step 4: `Page.java`

| Method | Purpose |
|---|---|
| `user(request)` | The logged-in name from the session, or `null`. Uses `getSession(false)` |
| `page(request, title, content)` | The layout. Shows "Log in", or the menu, "👤 name", and a **Log out** button (a POST form) |
| `form(request, action, id, title, body, errors, button)` | The Add/Edit form with a red error list. `id` is `null` for Add |
| `escape(text)` | Safe HTML |

### Step 5: `EncodingFilter` (`@WebFilter("/*")`)

`doFilter`: `req.setCharacterEncoding("UTF-8")`, then `chain.doFilter(req, res)`. Two lines.

### Step 6: `LoginServlet` (`/login`) and `LogoutServlet` (`/logout`)

- `LoginServlet.doGet` shows the form (plus "Please log in" or "You have logged out" for `?msg=`).
- `doPost` calls `Accounts.check`. Wrong → status **401** and the form again (keep the username, never the password).
  Right → `request.getSession()`, `request.changeSessionId()` (a fresh ID after login, explained in topic 13),
  `session.setAttribute("user", name)`, and redirect to `/members/notices`.
- `LogoutServlet.doPost`: `getSession(false)`, `invalidate()`, redirect to `/login?msg=logged-out`.

### Step 7: `AuthFilter` (`@WebFilter("/members/*")`), the guard

`if (Page.user(request) == null)` → `sendRedirect(".../login?msg=login-required")` and `return` (**no** `chain.doFilter`).
Otherwise set `Cache-Control: no-store` and call `chain.doFilter`.

✅ **Check (before any notice servlet exists):** log out and open `/members/notices` → you land on the login page.
Log in → you get a 404 (the guard let you in, but there is no servlet yet). The gate works.

### Step 8: `NoticeListServlet` (`/members/notices`), Read

Show the notices as cards (`<div class="cards">`), each with **Edit** (`edit?id=N`) and **Delete** (a POST form with a hidden id).
Show a fixed green message for `?msg=added|updated|deleted`.

✅ **Check:** after login you see "Library hours" and "Inter-house sports".

### Step 9: `AddNoticeServlet` (`/members/notices/add`), Create

`doGet`: empty form. `doPost`: trim, `validate` (400 + form if wrong), `add(title, body, Page.user(request))`,
redirect with `msg=added`. No `setCharacterEncoding`: the filter did it.

### Step 10: `EditNoticeServlet` (`/members/notices/edit`), Update

A private `find(request, response)` reads `id` and sends **400** (not a number) or **404** (no such notice).
`doGet` shows the filled form; `doPost` validates and calls `update`.

### Step 11: `DeleteNoticeServlet` (`/members/notices/delete`), Delete

`doPost` only: 400 / 404 as above, then `delete` and redirect with `msg=deleted`.

### Step 12: `AccessLogFilter`, `web.xml`, `index.html`, `style.css`

`AccessLogFilter` measures the time around `chain.doFilter` and then prints method, URI, `response.getStatus()`, and
the user. Add the 20-minute session timeout, the welcome page, and the stylesheet.

✅ **Final check:** watch the terminal while you log in, add, edit, delete, and log out. Every request has an
`[ACCESS]` line. After logging out, every `/members/` URL sends you back to the login page.

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
