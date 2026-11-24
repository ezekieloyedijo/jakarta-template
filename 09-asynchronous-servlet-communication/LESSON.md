# LESSON 09: Asynchronous Servlet Communication

> **The big picture for this lesson: A COFFEE SHOP**
>
> - **The cashier** = a **request thread** from Tomcat's pool (there are only a few of them)
> - **A customer's order** = a **request**
> - **A cashier who makes every coffee themselves** while the queue grows = a **blocking** servlet
> - **The cashier takes the order, gives it to a barista, and serves the next customer** = an **async** servlet
> - **The baristas in the back** = **worker threads** (an `ExecutorService`)
> - **The order ticket with your name on it** = the **`AsyncContext`** (it remembers where to send the answer)
> - **"Your coffee is ready!"** = **`complete()`**, or **`dispatch()`** to hand it to a JSP
> - **"Sorry, the machine is broken"** after waiting too long = a **timeout**

---

## Step 1: Recap, one thread per request

In topic 04 you learned that one servlet object serves many users at once, **each request on its own thread**.
Those threads come from a **pool**: Tomcat keeps a fixed number of them (200 by default) and reuses them.

```
Request 1 ─▶ thread exec-1 ─▶ doGet ─▶ response ─▶ exec-1 goes back to the pool
Request 2 ─▶ thread exec-2 ─▶ doGet ─▶ ...
```

A thread is only given back **when `doGet` returns**.

---

## Step 2: The blocking problem

What if `doGet` has to wait for something slow: a big report, another website, a payment provider?

```java
protected void doGet(...) {
    Thread.sleep(3000);         // a slow job: the thread does NOTHING for 3 seconds
    out.println("Done");
}
```

For those 3 seconds, the thread is **stuck**. If 200 users ask at once, **all 200 threads** are stuck, and user 201
waits in line even for a tiny page. The server isn't busy working; it is busy **waiting**.

**Demo:** `example/` → `/blocking`. The terminal shows `exec-3 starts the slow job and waits...`, then 3 seconds later `exec-3 is free again`.

---

## Step 3: The async idea

☕ A good coffee shop doesn't make the cashier brew each coffee. The cashier takes the order, hands the **ticket** to a
barista, and **immediately** serves the next customer. When the coffee is ready, the barista calls your name.

| | Blocking | Async |
|---|---|---|
| Who does the slow job? | Tomcat's request thread | A **worker** thread |
| Request thread is free after | the whole job (3 s) | a few milliseconds |
| What the user sees | waits 3 s | **also waits 3 s** |
| What the server gains | nothing | the request thread serves other users meanwhile |

⚠️ Async does **not** make one request faster. It lets the server handle **more** requests with the same threads.

---

## Step 4: `asyncSupported = true`

Async is **off** by default. Switch it on for the servlet:

```java
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet { ... }
```

⚠️ **Every filter** the request passes through must allow it too: `@WebFilter(urlPatterns = "/*", asyncSupported = true)`.
If one filter doesn't, `startAsync()` throws `IllegalStateException`.

---

## Step 5: `startAsync()` and `AsyncContext`

```java
AsyncContext async = request.startAsync();   // "don't send the response when doGet returns"
```

Normally, when `doGet` returns, Tomcat sends the response and closes it. After `startAsync()`, Tomcat keeps the
response **open**. The `AsyncContext` is the **order ticket**: it holds the request and response, so another thread
can finish the job later:

```java
async.getRequest()        // the original request
async.getResponse()       // where to write the answer
async.setTimeout(10_000)  // give up after 10 seconds (milliseconds)
async.complete()          // finished: send the response
async.dispatch("/x.jsp")  // finished: let a JSP build the page
```

---

## Step 6: Doing the work in the background (executor)

Who does the slow job? A small pool of **worker threads**, an `ExecutorService`. Create it **once**, when the app
starts, with a listener (topic 08), and shut it down when the app stops:

```java
@WebListener
public class WorkerPool implements ServletContextListener {
    public void contextInitialized(ServletContextEvent e) {
        e.getServletContext().setAttribute("workers", Executors.newFixedThreadPool(4));
    }
    public void contextDestroyed(ServletContextEvent e) {
        ((ExecutorService) e.getServletContext().getAttribute("workers")).shutdownNow();
    }
}
```

Then the servlet hands the job over and **returns at once**:

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    AsyncContext async = request.startAsync();

    workers.submit(() -> {                          // this lambda runs LATER, on a worker thread
        slowJob();
        async.getResponse().getWriter().println("Done!");
        async.complete();
    });
    // doGet ends here, a few ms after it started. Tomcat's thread goes back to the pool.
}
```

**Demo:** `/async`. The page shows two different thread names: `http-nio-8080-exec-4` (Tomcat's) and `pool-1-thread-1` (the worker).

---

## Step 7: `complete()` and `dispatch()`

There are two ways to finish:

| | `complete()` | `dispatch(path)` |
|---|---|---|
| Who writes the page? | The worker, before calling it | A JSP or servlet at `path` |
| Like | The barista hands you the cup | The barista puts the cup on the counter for the waiter to serve |

```java
// dispatch: put the result in the request, let a JSP show it
async.getRequest().setAttribute("result", "Sunny, 31°C");
async.dispatch("/WEB-INF/views/result.jsp");
```

You can also call `response.sendRedirect(...)` from the worker and then `complete()`. The project does that to keep Post/Redirect/Get.

⚠️ You must **always** finish, with `complete()` or `dispatch()`, or the browser waits until the timeout.

**Demo:** `/async-dispatch`

---

## Step 8: Timeouts and `AsyncListener`

What if the barista never comes back? After the timeout (default 30 s on Tomcat), the request is ended. To decide
**what the user sees**, add an `AsyncListener`:

```java
async.setTimeout(2000);
async.addListener(new AsyncListener() {
    public void onTimeout(AsyncEvent event) throws IOException {     // too slow
        HttpServletResponse res = (HttpServletResponse) event.getAsyncContext().getResponse();
        res.setStatus(503);
        res.getWriter().println("Sorry, that took too long.");
        event.getAsyncContext().complete();
    }
    public void onComplete(AsyncEvent event)   { }   // the response has been sent
    public void onError(AsyncEvent event)      { }   // something went wrong
    public void onStartAsync(AsyncEvent event) { }
});
```

There is a **race**: the worker might finish at the same moment as the timeout. Only one of them may answer.
An `AtomicBoolean` decides who is first:

```java
AtomicBoolean answered = new AtomicBoolean(false);
// in onTimeout AND in the worker:
if (answered.compareAndSet(false, true)) {   // true only for the FIRST one to get here
    ... write the answer, complete() ...
}
```

**Demo:** `/timeout`. The job takes 5 s but we only wait 2 s: you get a 503 page, and 3 s later the terminal shows
"the job finished, but the user already got the timeout page".

---

## Now try it

The project in [project/](project/) is a **Report Generator**. Create, list, edit, and delete report requests (CRUD).
Pressing **Generate** sends the 3-second job to a worker thread with `startAsync()`; when it is done, the worker
redirects you to the finished report. Try generating two reports at once: both finish after about 3 seconds, and the list
page still opens instantly while they run.
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
