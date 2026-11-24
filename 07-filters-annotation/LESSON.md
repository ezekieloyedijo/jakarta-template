# LESSON 07: Filters and Annotations

> **The big picture for this lesson: THE SECURITY GATE OF A SCHOOL**
>
> - **The classrooms** = your **servlets** (they do the real work)
> - **The security guard at the gate** = a **filter** (checks everyone before they reach a classroom)
> - **"Show your ID card or go to the office"** = **blocking** a request (redirect to the login page)
> - **"Pass through"** = **`chain.doFilter(...)`**
> - **The gate, then the reception desk, then the classroom** = the **filter chain**
> - **The visitors' book signed going in and going out** = code **before** and **after** `chain.doFilter`
> - **The guard's instructions sheet** = **init parameters**

---

## Step 1: Recap, repeated code in many servlets

Imagine the notice board app from the project **without** filters. Every servlet would begin like this:

```java
request.setCharacterEncoding("UTF-8");                       // copy 1 of 4...
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("user") == null) {
    response.sendRedirect("login");                          // copy 1 of 4...
    return;
}
System.out.println("[LOG] " + request.getRequestURI());     // copy 1 of 4...
```

Four servlets means four copies. Add a fifth page and **forget** the check, and anyone can open it. That is a real,
common security hole. These jobs aren't about one page; they're about **every** request. They belong at the **gate**.

---

## Step 2: What a filter is

> A **filter** is a class that runs **before** (and after) the servlet for every request that matches its URL pattern.
> It can **look at** the request, **change** it, **block** it, or **let it pass**.

```
                      ┌─────────── FILTER ───────────┐
Browser ── request ──▶│ check / log / set encoding   │──▶ Servlet ──┐
        ◀─ response ──│ (can also act on the way back)│◀─────────────┘
                      └──────────────────────────────┘
```

Typical jobs for filters: **login checks**, **logging**, **character encoding**, **timing**, **compressing**, and adding
**security headers**.

---

## Step 3: Your first filter, `@WebFilter` and `doFilter`

```java
@WebFilter("/*")                                      // which requests: "/*" means ALL of them
public class LogFilter implements Filter {            // implement the Filter interface

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;    // cast to use HTTP methods
        System.out.println("[LOG] " + request.getMethod() + " " + request.getRequestURI());

        chain.doFilter(req, res);                     // pass it on!
    }
}
```

Three things to notice:
1. A filter **implements `Filter`**; it does not extend `HttpServlet`.
2. `doFilter` receives `ServletRequest` and `ServletResponse` (not the HTTP versions), so you **cast** them.
3. `init` and `destroy` have default versions, so you only write `doFilter`.

**Demo:** `example/` → `/hello`, then look at the terminal.

---

## Step 4: The filter chain, `chain.doFilter(...)`

`chain.doFilter(req, res)` means **"pass the request to the next one in line"**: the next filter or, at the end, the servlet.

| In `doFilter` you... | Result |
|---|---|
| call `chain.doFilter(req, res)` | The request **continues** |
| don't call it | The request **stops here**. The servlet never runs. Whatever you wrote to `res` is the answer |

⚠️ The most common bug: **forgetting** `chain.doFilter`. The result is a **blank page**, because nobody wrote anything.

---

## Step 5: Before and after

`chain.doFilter` **waits** until the servlet (and every later filter) has finished. So a filter wraps the servlet like a sandwich:

```java
long start = System.currentTimeMillis();     // BEFORE: on the way in
chain.doFilter(req, res);                     // ... the servlet runs here ...
long took = System.currentTimeMillis() - start;   // AFTER: on the way out
System.out.println(uri + " took " + took + " ms");
```

The project's `AccessLogFilter` uses the "after" part to log the **status code** (`response.getStatus()`), which is
only known after the servlet has run.

**Demo:** `/slow` → the terminal shows `[TIME] /slow took 506 ms`.

---

## Step 6: Blocking a request

This is the filter's most important job: the **login check**.

```java
@WebFilter("/members/*")                               // guards everything under /members/
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");   // go to the office!
            return;                                                         // and STOP
        }
        chain.doFilter(req, res);                                           // ID card OK: pass
    }
}
```

Now **no** servlet under `/members/` needs a login check, including ones you add next year.

Other ways to block: `response.sendError(403)` (Forbidden), or write your own page, like `SecretKeyFilter` does.

**Demo:** `/secret/vault` (blocked, 403) and `/secret/vault?key=open-sesame` (let through).

---

## Step 7: URL patterns and filter order

Filters use the same patterns as servlets (topic 04):

| Pattern | The filter sees... |
|---|---|
| `/*` | **every** request, including `.html`, `.css`, and images |
| `/members/*` | everything under `/members/` |
| `*.jsp` | every JSP |
| `/login` | exactly `/login` |

A filter can also be attached to a servlet by name: `@WebFilter(servletNames = "reportServlet")`.

**Order.** When several filters match the same request, they form a chain. **Who goes first?**

| Registered with | Order |
|---|---|
| `web.xml` `<filter-mapping>` | **The order they are written in** the file. They also run **before** annotated filters |
| `@WebFilter` | **Not defined**. Don't write code that depends on it |

So when order matters, use `web.xml`:

```xml
<filter>
    <filter-name>first</filter-name>
    <filter-class>com.school.example.TrailFilter</filter-class>
</filter>
<filter-mapping>                 <!-- written first = runs first -->
    <filter-name>first</filter-name>
    <url-pattern>/chain</url-pattern>
</filter-mapping>
```

On the way **out**, the order is reversed: `first in → second in → servlet → second out → first out`.
Like Russian nesting dolls, the outer doll opens first and closes last.

**Demo:** `/chain`, then the terminal.

---

## Step 8: Filter life cycle and init parameters

A filter lives exactly like a servlet (topic 04): **one object**, created when the app starts.

| Method | When | Typical use |
|---|---|---|
| `init(FilterConfig config)` | **Once**, at app start | Read settings |
| `doFilter(...)` | **Every** matching request | The work |
| `destroy()` | **Once**, at app stop | Clean up |

Settings come from **init parameters**:

```java
@WebFilter(urlPatterns = "/secret/*",
           initParams = @WebInitParam(name = "key", value = "open-sesame"))
public class SecretKeyFilter implements Filter {
    private String key;

    public void init(FilterConfig config) {
        key = config.getInitParameter("key");        // "open-sesame"
    }
    ...
}
```

`@WebInitParam` works on `@WebServlet` too, and is the annotation version of `<init-param>` (topic 04).

---

## Step 9: The annotations family

Topics 02 to 04 wrote everything in `web.xml`. This is the full list of annotations that replace it:

| Annotation | Put it on | Replaces in `web.xml` | Topic |
|---|---|---|---|
| `@WebServlet("/url")` | a servlet | `<servlet>` + `<servlet-mapping>` | 05 |
| `@WebFilter("/url/*")` | a filter | `<filter>` + `<filter-mapping>` | 07 |
| `@WebInitParam(name, value)` | inside the two above | `<init-param>` | 07 |
| `@WebListener` | a listener | `<listener>` | 08 |
| `@MultipartConfig` | a servlet that receives file uploads | `<multipart-config>` | |
| `@ServletSecurity` | a servlet | `<security-constraint>` | 13 |

Things that are still **only** in `web.xml`: session timeout, welcome files, error pages, filter **order**, and
`<context-param>`. If `web.xml` and an annotation disagree, **`web.xml` wins**.

---

## Now try it

The project in [project/](project/) is a **Members-Only Notice Board**. Three filters do the work that every page needs:
`EncodingFilter` (UTF-8 forms), `AccessLogFilter` (one line per request), and `AuthFilter` (no login, no entry to
`/members/`). Behind the gate, members can post, read, edit, and delete notices (CRUD).
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
