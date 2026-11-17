# LESSON 04: Jakarta Servlets

> **The big picture for this lesson: A BANK BRANCH**
>
> - The **bank branch building with security, a queue system, and a manager** = the **container**
> - A **teller desk** (e.g. the "Deposits" desk) = a **servlet**
> - A **customer's filled-in slip** = the **request** object
> - The **receipt** the teller gives back = the **response** object
> - The **desk sign** ("Deposits: Window 3") = the **URL mapping**
> - Opening the desk in the morning / closing it at night = **`init()` / `destroy()`**
> - **Serving each customer** = **`service()`** → `doGet()` / `doPost()`

---

## Step 1: What a servlet is

> A **servlet** is a **Java class that lives inside a container** and **answers HTTP requests**.
> The container gives it a **request** object (what the user sent) and a **response** object (where to write the answer).

```java
@WebServlet("/hello")                              // the desk sign: "I handle /hello"
public class HelloServlet extends HttpServlet {    // "I am an HTTP servlet"
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("<h1>Hello!</h1>");
    }
}
```

The name means "a small piece of a **server**", the way an *applet* was a small application.

---

## Step 2: The family tree

```
      Servlet  (interface)          the basic rules: init(), service(), destroy()
         ▲
   GenericServlet (abstract class)  works with ANY protocol; gives handy defaults
         ▲
    HttpServlet  (abstract class)   understands HTTP: its service() looks at the method
         ▲                          and calls doGet(), doPost(), doPut(), doDelete()...
   YourServlet                      you only override the doXxx() methods you need
```

When a request arrives, the container calls `service(request, response)`. `HttpServlet.service()` then checks the method:

| Request method | Calls |
|---|---|
| `GET` | `doGet(request, response)` |
| `POST` | `doPost(request, response)` |
| `PUT` / `DELETE` | `doPut` / `doDelete` |

If you **don't** override, for example, `doPost`, and someone POSTs to you, the default answers **405 Method Not Allowed**.

🏦 Every teller is trained by the same bank manual (the `Servlet` rules). An HTTP teller additionally knows to
**look at the slip type**: deposit slip goes to the deposit procedure, withdrawal slip to the withdrawal procedure.

---

## Step 3: The life cycle

The **container**, not you, controls when a servlet is born, works, and dies:

```
   ┌─────────────┐   ┌──────────────┐   ┌────────────────────────────┐   ┌──────────────┐
   │ 1. LOAD &   │──►│ 2. init()    │──►│ 3. service() → doGet/doPost│──►│ 4. destroy() │
   │ create ONE  │   │ ONCE         │   │ ONCE PER REQUEST           │   │ ONCE         │
   │ object      │   │ (setup)      │   │ (many times, in parallel)  │   │ (cleanup)    │
   └─────────────┘   └──────────────┘   └────────────────────────────┘   └──────────────┘
   on first request                                                       when app stops /
   (or at startup if                                                      is redeployed
    loadOnStartup = 1)
```

| Method | Called | Use it to | 🏦 Bank |
|---|---|---|---|
| `init()` | **Once**, before the first request | Do expensive setup: read config, open resources | Open the desk in the morning, count the cash drawer |
| `doGet`/`doPost` (via `service`) | **For every request** | Do the actual work | Serve each customer |
| `destroy()` | **Once**, at shutdown | Clean up: close resources | Close the desk at night, lock the drawer |

```java
@WebServlet(urlPatterns = "/lifecycle", loadOnStartup = 1)  // 1 = create me at app start, not on first visit
public class LifecycleServlet extends HttpServlet {
    @Override public void init()    { System.out.println("init: desk opened"); }
    @Override protected void doGet(...) { System.out.println("doGet: serving a customer"); }
    @Override public void destroy() { System.out.println("destroy: desk closed"); }
}
```

▶️ Run the example, watch the **terminal**, open `/lifecycle` several times, then press **Ctrl+C**:
you'll see `init` once, `doGet` many times, and `destroy` once.

---

## Step 4: One object, many threads (thread safety)

From Step 3: there is **ONE** servlet object, and **every** user's request runs **through that same object at the same time**
(each on its own thread).

So **fields (instance variables) are shared by ALL users.**

```java
public class BadServlet extends HttpServlet {
    private String userName;                       // ❌ ONE box shared by EVERYONE

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        userName = req.getParameter("name");       // Amaka writes "Amaka"...
        // ...at the same moment, Tolu's request writes "Tolu" into the SAME field...
        resp.getWriter().println("Hello " + userName);   // Amaka may see "Hello Tolu"! 😱
    }
}
```

```java
public class GoodServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userName = req.getParameter("name"); // ✅ LOCAL variable: each request gets its own
        resp.getWriter().println("Hello " + userName);
    }
}
```

🏦 One teller desk, but imagine **several customers writing on the same notepad on the desk at once**. Their notes get mixed up.
Each customer should write on **their own slip** (local variable).

**Rules:**

- Per-request data goes in **local variables** (or request attributes, Step 10).
- Per-user data goes in the **session** (topic 05).
- Truly shared data (like a total counter) is OK in a field or in the `ServletContext`, but use a **thread-safe** type like `AtomicInteger`.

---

## Step 5: URL mapping patterns

`@WebServlet` accepts different kinds of patterns:

| Pattern | Type | Matches | Example use |
|---|---|---|---|
| `/login` | **Exact** | only `/login` | Most servlets |
| `/products/*` | **Path prefix** | `/products`, `/products/12`, `/products/a/b` | One servlet for a whole section |
| `*.do` | **Extension** | `/save.do`, `/x/delete.do` | Old-style frameworks |
| `/` | **Default** | anything nothing else matched | Replace the default file server (rare) |
| `""` (empty) | **Context root** | exactly the app's root `/` | A home-page servlet |

You can give several: `@WebServlet({"/home", "/index"})`, or the long form:

```java
@WebServlet(name = "calc", urlPatterns = {"/calculator", "/calc"}, loadOnStartup = 1)
```

For path patterns, `request.getPathInfo()` gives the extra part. For `/products/12` it returns `"/12"`.

---

## Step 6: The request object: reading input

`HttpServletRequest` is the customer's **filled-in slip**, already decoded for you.

| Method | Returns | Example |
|---|---|---|
| `getParameter("name")` | **One** value as a `String`, or `null` if missing | text box, radio button, dropdown |
| `getParameterValues("hobby")` | **All** values as `String[]`, or `null` | **checkboxes** (several can be ticked) |
| `getParameterMap()` | Every parameter | debugging |
| `getMethod()` | `"GET"` / `"POST"` | |
| `getRequestURI()` | `/register` | |
| `getQueryString()` | `a=1&b=2` | |
| `getHeader("User-Agent")` | A header value | |
| `getRemoteAddr()` | The client's IP address | |
| `setCharacterEncoding("UTF-8")` | Call **before** reading POST params so letters like é and emoji survive | |

⚠️ **Everything arrives as `String`.** Even `age=17` is the text `"17"`. Convert it yourself (`Integer.parseInt`)
and **be ready for bad input** (`"abc"`, empty, missing → `null`).

---

## Step 7: The response object: writing output

`HttpServletResponse` is the **blank receipt** you fill in:

| Method | Does |
|---|---|
| `setContentType("text/html;charset=UTF-8")` | Sets the `Content-Type` header. **Call it before** `getWriter()` |
| `getWriter()` | A `PrintWriter` for writing text into the body |
| `setStatus(400)` | Sets the status code (default is 200) |
| `sendError(404, "No such product")` | Sends an error status and the container's error page (or yours from `web.xml`) |
| `setHeader("Cache-Control", "no-cache")` | Sets any header |
| `sendRedirect("other")` | Tells the browser to go to another URL (Step 10) |

⚠️ Headers and status must be set **before** the body is sent. Write status → headers → body, in that order, just like the HTTP message in topic 01.

---

## Step 8: Handling forms: `doGet` shows, `doPost` processes

The classic pattern is one URL with two methods:

```
GET  /register   →  doGet()  →  "Here is an empty form"
POST /register   →  doPost() →  read params → validate → if errors: show form again with messages
                                                       → if OK: do the work and show success
```

```java
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("UTF-8");
    String name = req.getParameter("name");
    String[] hobbies = req.getParameterValues("hobby");    // checkboxes: may be null!

    List<String> errors = new ArrayList<>();
    if (name == null || name.isBlank()) errors.add("Name is required");

    if (!errors.isEmpty()) {
        resp.setStatus(400);                               // the client's input was wrong
        // ...show the form again with the errors
        return;
    }
    // ...success
}
```

🏦 The teller checks the slip. If the amount is blank, they **hand it back** asking you to fix it, instead of guessing.

**Always validate on the server**, even if the HTML has `required`, because anyone can bypass the browser (for example with `curl`).

---

## Step 9: Configuration: `ServletConfig` and `ServletContext`

| | **`ServletConfig`** | **`ServletContext`** |
|---|---|---|
| Belongs to | **One** servlet | The **whole app** (all servlets) |
| Settings | `@WebInitParam` or `<init-param>` in web.xml | `<context-param>` in web.xml (topic 03) |
| Read with | `getInitParameter("x")` (inside the servlet) | `getServletContext().getInitParameter("x")` |
| Can also store objects? | No | **Yes**: `setAttribute` / `getAttribute`, shared by everyone |
| 🏦 Bank | A note taped to **one desk** ("this desk: max withdrawal ₦50,000") | The **branch notice board** everyone reads |

```java
@WebServlet(urlPatterns = "/config",
        initParams = { @WebInitParam(name = "greeting", value = "Welcome to Desk 3") })
public class ConfigServlet extends HttpServlet {
    protected void doGet(...) {
        String greeting = getInitParameter("greeting");                       // this servlet only
        String school   = getServletContext().getInitParameter("schoolName"); // whole app
    }
}
```

---

## Step 10: Working together: forward, include, redirect

Real apps have many servlets. There are three ways to hand work over:

| | **forward** | **include** | **sendRedirect** |
|---|---|---|---|
| Code | `req.getRequestDispatcher("/b").forward(req, resp)` | `req.getRequestDispatcher("/b").include(req, resp)` | `resp.sendRedirect("b")` |
| Who moves? | Happens **inside the server** | Happens **inside the server** | The **browser** is told to make a **new request** |
| Browser URL changes? | ❌ No (still shows `/a`) | ❌ No | ✅ Yes (shows `/b`) |
| Number of requests | 1 | 1 | **2** (status 302, then a GET for `/b`) |
| Request attributes survive? | ✅ Yes | ✅ Yes | ❌ No (it's a brand-new request) |
| Who writes the response? | **B** writes all of it | **A** writes, and **B's output is pasted in** | B |
| Can go to another website? | No | No | **Yes**, e.g. `sendRedirect("https://google.com")` |
| 🏦 Bank | Teller **walks your slip to the back office**. You never leave your seat | Teller **asks the back office for one line** and adds it to your receipt | Teller says **"Please go to Window 7"**. You get up and queue there yourself |

**Request attributes** are how servlets pass **Java objects** to each other during one request:

```java
req.setAttribute("message", "Sent from A");         // A puts something on the slip
req.getRequestDispatcher("/b").forward(req, resp);   // ...and passes it on
// in B:
String msg = (String) req.getAttribute("message");   // B reads it
```

(*Parameters* come **from the browser** and are always Strings. *Attributes* are put there **by your code** and can be any object.)

▶️ The example's `/dispatch` page lets you try all three and **watch the address bar**.

---

## Step 11: The Post/Redirect/Get pattern (PRG)

Here is a problem. A user submits a payment form (POST), you show "Paid!" directly from `doPost`... and then they press **F5 (refresh)**.
The browser **re-sends the POST** and they pay **twice**. 😱

**The fix: after a successful POST, redirect.**

```
POST /pay  →  doPost: save the payment  →  sendRedirect("receipt")   (302)
GET  /receipt  →  doGet: show "Paid!"
```

Now refreshing just re-loads `GET /receipt`, which is harmless. This is called **Post/Redirect/Get**, and you'll use it in every
project from topic 05 onward.

---

## Now try it

```bash
cd 04-jakarta-servlets/example
mvn package cargo:run
```

Open <http://localhost:8080/> and work through the links **while watching the terminal**.

```bash
cd 04-jakarta-servlets/project
mvn package cargo:run
```

Open <http://localhost:8080/calculator>. Try good numbers, letters instead of numbers, empty boxes, and dividing by zero.
Open **F12 → Network** to see the status codes (200 vs 400).

Then go to **[QUIZ.md](QUIZ.md)**.
