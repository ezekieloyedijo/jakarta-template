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
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: a slow blocking servlet vs an async one, a timeout, an AsyncListener |
| [project/](project/) | **Report Generator**: requests a slow "report" that is built in the background |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

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
