# QUIZ 09: Asynchronous Servlet Communication

## Part A: Quick check

**1.** What must you add to `@WebServlet` before you can call `request.startAsync()`?

<details><summary>Answer</summary><code>asyncSupported = true</code>, for example <code>@WebServlet(urlPatterns = "/async", asyncSupported = true)</code>.</details>

**2.** A filter in front of your async servlet does **not** have `asyncSupported = true`. What happens?

<details><summary>Answer</summary><code>startAsync()</code> throws an <code>IllegalStateException</code>. Every filter and servlet on the way must support async.</details>

**3.** Does an async servlet make the user's page load faster?

<details><summary>Answer</summary><b>No.</b> The user still waits for the slow job. The server gains: its request thread is free to serve other users during the wait.</details>

**4.** What are the two ways to finish an async request?

<details><summary>Answer</summary><code>async.complete()</code> (the response is already written) or <code>async.dispatch("/path")</code> (a JSP or servlet writes it).</details>

**5.** In which unit is `async.setTimeout(...)`?

<details><summary>Answer</summary>Milliseconds. <code>setTimeout(10_000)</code> = 10 seconds.</details>

**6.** Which method of `AsyncListener` runs when the job is too slow?

<details><summary>Answer</summary><code>onTimeout(AsyncEvent event)</code></details>

**7.** Why is the `ExecutorService` created in a `ServletContextListener`?

<details><summary>Answer</summary>So there is <b>one</b> pool, created once when the app starts and shut down when it stops. Creating a pool per request would waste threads and never clean them up.</details>

## Part B: Explain in your own words

**8.** Explain blocking vs async with the coffee-shop analogy.

<details><summary>Model answer</summary>Blocking: the cashier makes every coffee personally, so the queue stops moving while each coffee brews. Async: the cashier takes the order, gives the ticket to a barista, and serves the next customer straight away. The barista calls the customer when the coffee is ready. Each customer waits the same time for their coffee, but the queue keeps moving.</details>

**9.** What is the `AsyncContext`, and why does the worker thread need it?

<details><summary>Model answer</summary>It is the "order ticket": it holds the request and response that are still open. The worker thread didn't receive <code>doGet</code>'s parameters, so it uses <code>async.getResponse()</code> to know where to write, and <code>async.complete()</code> to say it is finished.</details>

**10.** Why do the timeout and the worker both check an `AtomicBoolean` before answering?

<details><summary>Model answer</summary>They might both try to answer at about the same time. Writing to a response that has already been completed causes errors. <code>compareAndSet(false, true)</code> is true for only the first thread, so exactly one of them answers.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Change `ReportBuilder.WORK_MILLIS` to `12000` (12 s), restart, and click Generate. What do you see after 10 seconds? Change it back.

<details><summary>Answer</summary>A <b>503</b> page "The report took more than 10 seconds", from <code>onTimeout</code>. Two seconds later the worker finishes and saves the report (it becomes "ready" in the list), but it doesn't answer, because the timeout already did.</details>

**12.** Prove the request thread is free: open two tabs. In tab 1 click Generate; in tab 2 refresh `/reports` during the 3 seconds. Does tab 2 wait?

<details><summary>Answer</summary>No. The list opens instantly. The terminal also shows "handed report #N to a worker and is free again" a few milliseconds after the click.</details>

**13.** Show the worker thread's name on the report page ("Built by pool-1-thread-2").

<details><summary>Hint</summary>Add a <code>builtBy</code> field to <code>Report</code> and a parameter to <code>markReady</code>. In <code>GenerateReportServlet</code> you already have <code>workerThread</code>; pass it in. Show it in <code>ViewReportServlet</code>.</details>

**14. Challenge:** change the pool to `Executors.newFixedThreadPool(1)`. Click Generate on two reports quickly. How long does the second one take? Why?

<details><summary>Answer</summary>About 6 seconds. There is only one worker (one barista), so the second job waits until the first is finished. The pool size limits how many slow jobs run at the same time.</details>
