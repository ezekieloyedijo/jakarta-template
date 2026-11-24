# LESSON 05: Session Tracking

> **The big picture for this lesson: A GYM WITH A FORGETFUL RECEPTIONIST**
>
> - The **receptionist who forgets you the moment you walk away** = **HTTP** (stateless)
> - **Writing your details on every form you hand in** = **hidden form fields**
> - **Writing your details at the end of every address** = **URL rewriting**
> - **A card that you carry, with your details written on it** = a **cookie**
> - **A card with only a number, and your file kept in the office** = an **HttpSession** (the number is `JSESSIONID`)
> - **Your file is shredded if you don't come for a month** = **session timeout**
> - **Handing back your card when you leave the gym** = **`invalidate()`** (log out)

---

## Step 0: A new shortcut, `@WebServlet`

In topics 02 to 04 every servlet needed **two entries** in `web.xml`:

```xml
<servlet>
    <servlet-name>cart</servlet-name>
    <servlet-class>com.school.project.CartServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>cart</servlet-name>
    <url-pattern>/cart</url-pattern>
</servlet-mapping>
```

From now on, **one line on the class** does the same job:

```java
@WebServlet("/cart")                       // "this class answers /cart"
public class CartServlet extends HttpServlet { ... }
```

When Tomcat starts, it **scans** the classes in `WEB-INF/classes` for `@WebServlet` and registers each one for you.

| | `web.xml` | `@WebServlet` |
|---|---|---|
| Where the URL is written | In a separate file | On the class itself |
| Lines needed | About 8 | 1 |
| Change the URL without recompiling? | Yes | No |
| Who wins if both say something? | **`web.xml`** | |

🏷️ It's like the difference between a **staff list at the entrance** (`web.xml`) and a **name badge on each teller**
(`@WebServlet`). Both tell customers who does what; the badge is just quicker.

`web.xml` does not disappear: it still holds things that have no annotation, like the **session timeout** and
**welcome files**. Other forms of the annotation:

```java
@WebServlet(urlPatterns = {"/cart", "/basket"})                 // two URLs
@WebServlet(urlPatterns = "/report", loadOnStartup = 1)          // = <load-on-startup>1</load-on-startup>
```

---

## Step 1: Recap, HTTP is stateless

Every request is **a stranger** to the server. Request number 2 carries **no memory** of request number 1.

```
Request 1:  POST /cart/add  code=UNI-01      → "Added!"
Request 2:  GET  /cart                       → "Whose cart? I've never seen you."
```

That is fine for a news page, but logins, carts, and quizzes all need the server to say **"ah, it's you again"**.

---

## Step 2: What "tracking a session" means

> A **session** is **one conversation** between one browser and the server: many requests over some minutes,
> which the server treats as belonging together.

To track a session, every request must carry **something that says who it comes from**. There are four classic ways
to carry that something, and they differ in **where the data lives**:

| Technique | Where the data travels / lives | Survives closing the tab? |
|---|---|---|
| Hidden form fields | Inside each **form** | No |
| URL rewriting | At the end of each **link** | No |
| Cookie | In the **browser**, sent automatically | Yes, until it expires |
| HttpSession | On the **server**; browser keeps only an ID | Until timeout or logout |

---

## Step 3: Hidden form fields

Put the data **inside the next form** as an input the user can't see:

```html
<form method="post" action="hidden">
    <input type="hidden" name="name" value="Ada">      <!-- carried along, not shown -->
    <label>Favourite subject <input name="subject"></label>
    <button>Finish</button>
</form>
```

When the form is submitted, `request.getParameter("name")` gives `"Ada"` again.

| 👍 Good | 👎 Bad |
|---|---|
| Very simple, no cookies needed | Only works **form to form**; a plain link loses it |
| Nothing stored on the server | The user can **see and change** it (View Page Source) |

🎒 It's like a student who carries their **report card** from teacher to teacher: it works, but they could edit it on the way.

**Demo:** `example/` → `/hidden` (`HiddenFieldServlet`)

---

## Step 4: URL rewriting

Put the data **at the end of every link**:

```html
<a href="url-rewriting?lang=fr&page=2">Next page</a>
```

| 👍 Good | 👎 Bad |
|---|---|
| Works with plain links, even with cookies switched off | **Every** link must be written with the data, or it is lost |
| | The data shows in the address bar, history, and shared links. Never put a password there |

Servlets also have `response.encodeURL("cart")`. If the browser refuses cookies, it adds the session ID to the link
(`cart;jsessionid=8A3F...`), so sessions still work.

**Demo:** `/url-rewriting` (`UrlRewritingServlet`)

---

## Step 5: Cookies

> A **cookie** is a small **name=value** text that the server asks the browser to keep.
> After that, the browser **sends it back automatically** with every request to that site.

```java
// CREATE (or UPDATE: same name, new value)
Cookie cookie = new Cookie("visits", "3");
cookie.setMaxAge(60 * 60 * 24);       // keep it 1 day (seconds). Without this: deleted when the browser closes
cookie.setHttpOnly(true);             // JavaScript on the page can't read it
response.addCookie(cookie);

// READ: you get ALL cookies, and must find yours. Careful: null if there are none!
Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie c : cookies) {
        if (c.getName().equals("visits")) { ... c.getValue() ... }
    }
}

// DELETE: same name, max age 0
Cookie gone = new Cookie("visits", "");
gone.setMaxAge(0);
response.addCookie(gone);
```

| 👍 Good | 👎 Bad |
|---|---|
| The browser does the carrying | Small (about 4 KB each), and text only |
| Can last for days (e.g. "remember me") | The user can read, change, or delete them. **Never trust a cookie value** |

🎫 Cookies are like a **stamp on your hand** at a party: you carry it, and the guard checks it each time you come back in.

**Demo:** `/cookie-counter` (`CookieCounterServlet`). Press F5, then look in F12 → Application → Cookies.

---

## Step 6: HttpSession

The most used technique. The **data stays on the server**. The browser carries only a long random **ID**, in a
cookie named **`JSESSIONID`** that Tomcat creates for you.

```
Browser                                  Server (Tomcat)
  │  1st request (no ID)                   │
  │ ─────────────────────────────────────▶ │  getSession(): new session #8A3F, empty
  │ ◀───────── Set-Cookie: JSESSIONID=8A3F │
  │  2nd request, Cookie: JSESSIONID=8A3F  │
  │ ─────────────────────────────────────▶ │  getSession(): finds #8A3F with its data
```

```java
HttpSession session = request.getSession();        // find this user's session, or CREATE one
session.setAttribute("cart", cart);                 // save any Java object
Cart cart = (Cart) session.getAttribute("cart");    // read it back (null if never saved). Note the cast
session.removeAttribute("cart");                    // forget one thing

HttpSession maybe = request.getSession(false);      // find it, but DON'T create one (null if none)
```

| 👍 Good | 👎 Bad |
|---|---|
| Stores **any Java object**, any size | Uses **server memory** for every visitor |
| The user can't see or change the data | Lost when Tomcat stops, unless saved (objects should be `Serializable`) |

🗂️ The **gym membership card** from the big picture: the card has only a number, and your file is in the office.

**Demo:** `/session-counter` (`SessionCounterServlet`). Open it in a private window to see a second, separate session.

---

## Step 7: Timeout and logout

Sessions must end, or the server's memory fills up with visitors who left hours ago.

**1. Timeout:** if a session has no requests for some time, Tomcat deletes it. The default is **30 minutes**.

```xml
<!-- web.xml: for the whole app, in MINUTES -->
<session-config>
    <session-timeout>15</session-timeout>
</session-config>
```
```java
session.setMaxInactiveInterval(10 * 60);   // for this one session, in SECONDS
```

**2. Logout:** throw the session away straight away:

```java
HttpSession session = request.getSession(false);
if (session != null) {
    session.invalidate();      // every attribute in it is gone
}
```

⚠️ After `invalidate()`, calling `getAttribute` on that same session object throws `IllegalStateException`.
Call `request.getSession()` again to get a **new**, empty one.

**Demo:** `/session-end` (`SessionEndServlet`)

---

## Step 8: Choosing the right technique

| You need to... | Use |
|---|---|
| Carry a value through a 2–3 page form | **Hidden fields** |
| Make a link that can be bookmarked or shared (`?page=2`, `?lang=fr`) | **URL parameters** |
| Remember a small preference for days (theme, language, "remember me") | **Cookie** |
| Remember who is logged in, or a shopping cart | **HttpSession** |

In real apps, the **session** does most of the work, and it uses a **cookie** to carry its ID.
So the techniques are not rivals; they work together.

---

## Now try it

The project in [project/](project/) is a **School Shop**. Each customer has a **cart** kept in their session:
add products (create), see the cart (read), change a quantity (update), remove one product (delete), and empty the
whole cart with `invalidate()`. Open it in two browsers: each one has its own cart.
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
