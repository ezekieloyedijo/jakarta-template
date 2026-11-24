# Topic 09: Asynchronous Servlet Communication

## The problem that existed

In topic 04 you learned that the container handles each request on a **thread**, and it only has a
limited number of them (Tomcat's default is 200).

Now imagine a servlet that does something **slow**: calls another company's website, builds a big report,
or waits for a payment to be confirmed. That takes 10 seconds. During those 10 seconds the thread just
**sits and waits**, doing nothing. If 200 users do this at once, **all threads are stuck** and the 201st visitor,
who only wanted the home page, gets no answer at all.

☕ It's like a coffee shop where the **cashier** takes your order and then **stands at the coffee machine
waiting** for your drink. The queue behind you doesn't move.

A smarter coffee shop: the cashier takes your order, gives it to the **barista**, and **immediately serves the next
customer**. When the drink is ready, someone calls your name.

**Asynchronous servlets** work the same way. The request thread hands the slow job to a **background worker** and goes
back to serve other requests. When the job is done, the worker finishes the response.

## The end goal of this topic

You should be able to:

1. Explain why a slow job **blocks** a request thread, and why that hurts the whole server.
2. Turn on async support with `asyncSupported = true`.
3. Start async processing with `request.startAsync()` and finish it with `complete()`.
4. Run the slow work on a **background thread** (an executor).
5. Handle **timeouts** and events with an `AsyncListener`.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: one thread per request** | Where the problem comes from |
| 2 | **The blocking problem** | See a slow servlet hold a thread hostage |
| 3 | **The async idea** | The cashier and the barista |
| 4 | **`asyncSupported = true`** | Switching the feature on |
| 5 | **`startAsync()` and `AsyncContext`** | Keeping the response open after the thread leaves |
| 6 | **Doing the work in the background (executor)** | Who does the slow job |
| 7 | **`complete()` and `dispatch()`** | Finishing the response |
| 8 | **Timeouts and `AsyncListener`** | What if the job takes too long or fails? |

## What is inside this folder

| Path | What it will show |
|---|---|
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | Small demos: a slow blocking servlet vs an async one, a timeout with an AsyncListener, async + dispatch to a JSP |
| [project/](project/) | **Report Generator**: create, list, edit, and delete report requests (CRUD); each report is built in the background by an async servlet |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is a **Report Generator**: a teacher asks for a class report ("First term results, SS1, 25 students"),
then clicks **Generate**. Building a report is slow (about 3 seconds), so it runs on a **worker thread**.

| Feature | The user... | Request | Servlet |
|---|---|---|---|
| **Read** (list) | opens the list | `GET /reports` | `ReportListServlet` |
| **Create** | submits a new request | `GET` / `POST /reports/add` | `AddReportServlet` |
| **Update** | edits a request | `GET /reports/edit?id=1`, `POST .../edit` | `EditReportServlet` |
| **Delete** | clicks Delete | `POST /reports/delete` | `DeleteReportServlet` |
| Generate | clicks Generate | `POST /reports/generate` | `GenerateReportServlet` (**async**) |
| **Read** (one) | opens a finished report | `GET /reports/view?id=1` | `ViewReportServlet` |

☕ The coffee shop: `GenerateReportServlet` is the **cashier**, the worker pool made by `AppListener` holds the
**baristas**, and the `AsyncContext` is the **order ticket**.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Builds and runs the app |
| `Report.java` | ✅ **Yes** | The data: title, class, number of students, status (`PENDING`/`READY`), scores, and average/highest/lowest |
| `ReportStore.java` | ✅ **Yes** | CRUD, plus `markReady` for the worker, and validation |
| `ReportBuilder.java` | ✅ **Yes** | **The slow job** (3 s, then random scores) |
| `AppListener.java` | ✅ **Yes** | Creates the store and the **worker pool** at startup, stops the workers at shutdown |
| `GenerateReportServlet.java` | ✅ **Yes** | **The async servlet**: `startAsync`, worker, timeout, redirect, `complete` |
| The other 5 servlets, `Page.java` | ✅ **Yes** | Normal (not async) CRUD pages and the layout |
| `WEB-INF/web.xml` | ⚪ Optional | Only makes `/` open `/reports` |
| `css/style.css` | ⚪ Optional, but recommended | The look (status badges, result tiles) |

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic09-project</artifactId>`.

### Step 2: `Report.java`

`final` fields: `id`, `title`, `className`, `students`, `status` (an `enum Status { PENDING, READY }`), `scores`
(a `List<Integer>`), `generatedAt`, `tookMillis`. A factory `pending(id, title, className, students)` makes a new,
empty request. `average()`, `highest()`, and `lowest()` work on the scores.

### Step 3: `ReportStore.java`

A `ConcurrentHashMap` (request threads **and** worker threads use it at the same time) and an `AtomicInteger` for ids.

| Method | Purpose |
|---|---|
| `add(title, className, students)` | **Create** a `PENDING` request |
| `findAll()`, `findById(id)` | **Read** |
| `update(id, ...)` | **Update**. The old results no longer match, so it goes back to `PENDING` |
| `markReady(id, scores, took)` | Called **by the worker** when the report is built. `false` if it was deleted meanwhile |
| `delete(id)` | **Delete** |
| `static validate(...)`, `toNumber(...)` | Title required (max 60), class from the list, 5–60 students |

### Step 4: `ReportBuilder.java`, the slow job

`build(report)`: `Thread.sleep(3000)`, then one random score (30–100) per student.

### Step 5: `AppListener.java` (`@WebListener`)

`contextInitialized`: create the store with 2 sample requests, and `Executors.newFixedThreadPool(3)`. Save both in the
`ServletContext`. `contextDestroyed`: `shutdownNow()`.

### Step 6: `Page.java`

The layout, a fixed `message(code)` for `?msg=added|updated|deleted|generated`, and the Add/Edit `form(...)` with a
class dropdown.

### Step 7: `ReportListServlet` (`/reports`), Read

A table. Pending rows get a **Generate** button (a POST form whose text changes to "Generating…" on click), and ready
rows get a **View** link. Every row has **Edit** and **Delete**.

✅ **Check:** <http://localhost:8080/> shows 2 pending requests.

### Step 8: `AddReportServlet`, `EditReportServlet`, `DeleteReportServlet`

The usual CRUD pattern: read, validate (400), save, redirect with `msg=...`. `EditReportServlet` has a `static find(...)`
(400 / 404) that View, Delete, and Generate reuse.

### Step 9: `GenerateReportServlet`, the async servlet

`@WebServlet(urlPatterns = "/reports/generate", asyncSupported = true)`, `doPost`:
1. `find(...)` the report (400 / 404 answered normally, before going async).
2. `AsyncContext async = request.startAsync();` and `async.setTimeout(10_000);`
3. `addListener(...)`: `onTimeout` answers **503** "took too long".
4. `workers.submit(() -> { ... })`: `ReportBuilder.build`, `store.markReady`, then
   `sendRedirect(".../view?id=N&msg=generated")` and `async.complete()`.
5. An `AtomicBoolean answered` makes sure only the worker **or** the timeout answers.
6. `doPost` returns at once. The terminal says "exec-7 handed report #1 to a worker and is free again".

✅ **Check:** click Generate. After about 3 seconds you land on the report with its average, highest, and lowest scores.

### Step 10: `ViewReportServlet` (`/reports/view`), Read one

If the report is still pending, say so. Otherwise show three tiles (average, highest, lowest), when and how fast it
was built, and a table of every student's score.

✅ **Final check:** click Generate on two reports in two tabs. Both finish after about 3 seconds (not 6), and `/reports` opens
instantly while they run. Edit a ready report: it goes back to pending.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What problem do asynchronous servlets solve?

<details><summary>Answer</summary>Slow jobs keep a <b>request thread waiting</b> and doing nothing. The server has a limited number of threads, so when many slow requests arrive, other users get no answer. Async frees the thread while the slow job runs.</details>

**2.** What does "asynchronous" mean in plain words?

<details><summary>Answer</summary>"Not at the same time": you <b>start</b> a job and <b>don't wait</b> for it. You do other work and come back when it is finished.</details>

**3.** How do you allow a servlet to work asynchronously?

<details><summary>Answer</summary>Add <code>asyncSupported = true</code> to the annotation: <code>@WebServlet(urlPatterns = "/report", asyncSupported = true)</code>.</details>

**4.** Which method starts async processing, and what does it return?

<details><summary>Answer</summary><code>request.startAsync()</code>, which returns an <b><code>AsyncContext</code></b>. It keeps the request and response "open" after <code>doGet</code> returns.</details>

**5.** Which method must you call when the background job has finished writing the response?

<details><summary>Answer</summary><code>asyncContext.complete()</code>. It tells the container the response is finished and can be sent to the browser.</details>

**6.** What happens to the original request thread after `startAsync()` and `doGet` returns?

<details><summary>Answer</summary>It goes <b>back to the thread pool</b> and can serve other requests. The response is not sent yet; it waits until <code>complete()</code> is called.</details>

**7.** Where should the slow work run?

<details><summary>Answer</summary>On a <b>background thread</b>, usually from an <b>executor</b> (a pool of worker threads), for example <code>asyncContext.start(...)</code> or your own <code>ExecutorService</code>.</details>

**8.** How do you stop an async request from waiting forever?

<details><summary>Answer</summary>Set a timeout: <code>asyncContext.setTimeout(5000);</code> (milliseconds). If it runs out, the container calls the listener's <code>onTimeout</code>.</details>

**9.** Name the four methods of an `AsyncListener`.

<details><summary>Answer</summary><code>onComplete</code>, <code>onTimeout</code>, <code>onError</code>, and <code>onStartAsync</code>.</details>

**10.** Does async make the page appear **faster** for the user who asked for the slow job?

<details><summary>Answer</summary><b>No.</b> That user still waits for the job to finish. The benefit is for the <b>server</b>: its threads are free, so <b>other users</b> are not stuck waiting.</details>
