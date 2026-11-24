# Topic 05: Session Tracking

## The problem that existed

In topic 01 you learned that **HTTP is stateless**: the server forgets you after every request.
That is fine for reading a news page, but it breaks almost every real app:

- You log in, click "My Account"... and the server asks "who are you?" again.
- You add shoes to a shopping cart, open the next page... and the cart is empty.

🏋️ It's like a gym receptionist with **no memory at all**. Every time you walk through the door, even
five minutes after your last visit, they ask for your name, your membership, and your payment again.

The fix is a **membership card**. The first time you arrive, the gym gives you a card with a number.
After that you just **show your card**, and the receptionist looks up your file.
On the web, that card is usually a **cookie** holding a **session ID**, and the file kept at the desk is the **session**.

## The end goal of this topic

You should be able to:

1. Explain **why** a server needs extra help to remember users.
2. Name the four classic techniques: **hidden form fields**, **URL rewriting**, **cookies**, and **HttpSession**.
3. Create, read, and delete **cookies**.
4. Store and read data in an **HttpSession**, and end a session (log out).

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 0 | **A new shortcut: `@WebServlet`** | Until now every servlet needed a `<servlet>` and a `<servlet-mapping>` in `web.xml`. From this topic on, one line on the class, `@WebServlet("/cart")`, does the same job |
| 1 | **Recap: HTTP is stateless** | The problem we are solving |
| 2 | **What "tracking a session" means** | Name the goal before the tools |
| 3 | **Hidden form fields** | The simplest trick: carry the data inside each form |
| 4 | **URL rewriting** | The same idea for links: carry the data in the URL |
| 5 | **Cookies** | Let the **browser** carry small data for you automatically |
| 6 | **HttpSession** | Keep the data on the **server**; the browser carries only an ID (a cookie called `JSESSIONID`) |
| 7 | **Session timeout and logout (`invalidate`)** | Sessions must end: when the user is idle or clicks log out |
| 8 | **Choosing the right technique** | Comparing all four |

## What is inside this folder

| Path | What it will show |
|---|---|
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | Small demos: hidden fields, URL rewriting, a visit counter with a cookie, a visit counter with a session, ending a session |
| [project/](project/) | **School Shop**: a shopping cart kept in the session. Add, view, change quantity, remove, and empty (CRUD) |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is a **School Shop**. Every customer gets their own **cart**, stored in their **HttpSession**:

| Feature | The user... | Request | Servlet |
|---|---|---|---|
| Browse | sees the products | `GET /shop` | `ShopServlet` |
| **Create** | clicks "Add to cart" | `POST /cart/add` | `AddToCartServlet` |
| **Read** | opens the cart | `GET /cart` | `CartServlet` |
| **Update** | changes a quantity | `POST /cart/update` | `UpdateCartServlet` |
| **Delete** | clicks "Remove" | `POST /cart/remove` | `RemoveFromCartServlet` |
| Delete all | clicks "Empty cart" | `POST /cart/clear` | `ClearCartServlet` (uses `invalidate()`) |

🛒 Think of a real shop: the **shelf** (`Catalog`) is the same for everyone, but each customer pushes **their own
trolley** (`Cart`). The session is the trolley bay that keeps each customer's trolley under their number.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Builds and runs the app |
| `Product.java` | ✅ **Yes** | The data: one product (code, name, price) |
| `Catalog.java` | ✅ **Yes** | The fixed list of products, the same for every customer |
| `Cart.java` | ✅ **Yes** | One customer's cart: the CRUD methods, stored in the session |
| `Page.java` | ✅ **Yes** | The shared HTML layout, so servlets stay short |
| The 6 servlets | ✅ **Yes** | One per job (see the table above). Each has `@WebServlet`, so **no `<servlet>` entries are needed** |
| `WEB-INF/web.xml` | ⚪ Optional | Only holds the session timeout (30 minutes, which is also Tomcat's default) and the welcome file |
| `index.html` | ⚪ Optional | The welcome page for `/`. Without it, start at `/shop` |
| `css/style.css` | ⚪ Optional, but recommended | The look: header, tables, buttons, message boxes |

**The order:** data first, then the cart, then one servlet at a time. Run with `mvn package cargo:run` after each servlet.

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic05-project</artifactId>`.

### Step 2: `Product.java`, the data

Three `final` fields with getters: `code` (like `"UNI-01"`), `name`, and `price` (whole naira).
It `implements Serializable`, because it ends up inside the session.

### Step 3: `Catalog.java`, the shelf

| Method | Purpose |
|---|---|
| `all()` | The fixed `List<Product>` (uniform, bag, textbooks...) |
| `find(code)` | The product with that code, or `null`. Used to check that a posted code is real |
| `money(naira)` | Formats `8500` as `₦8,500` |

### Step 4: `Cart.java`, one customer's trolley

It holds a `LinkedHashMap<String, Integer>` (product code → quantity), which keeps the order items were added.

| Method | Purpose |
|---|---|
| `static of(session)` | **The key method.** Gets the cart from the session, or makes an empty one and saves it with `setAttribute("cart", ...)` |
| `static countFor(request)` | The item count for the header. Uses `getSession(false)`, so just looking at a page doesn't create a session |
| `add(code, qty)` | **Create**: adds to the quantity. Returns `false` above 20 of one product |
| `lines()` | **Read**: one `Line(product, quantity)` per row, each with `subtotal()` |
| `update(code, qty)` | **Update**: sets a new quantity. `false` if the product isn't in the cart |
| `remove(code)` | **Delete**: takes the product out. `false` if it wasn't there |
| `count()`, `total()` | For the "Total (5 items) ₦20,600" row |
| `static parseQuantity(text)` | Turns the typed quantity into a number from 1 to 20, or `null` if it's invalid |

The methods are `synchronized`, because one customer can send two requests at the same moment (two tabs).

### Step 5: `Page.java`, the layout

| Method | Purpose |
|---|---|
| `page(request, title, content)` | Full page: header with "My cart (n)", title, content. Links start with `request.getContextPath()` so they work from any URL, even `/cart/add` |
| `message(code)` | Turns `?msg=added` into a fixed green sentence. Unknown codes show nothing |
| `error(text)`, `escape(text)` | A red box, and safe HTML |

### Step 6: `ShopServlet` (`@WebServlet("/shop")`)

`doGet` loops over `Catalog.all()` and prints one row per product, with a small form:
hidden `code`, a number input `quantity`, and an **Add to cart** button that POSTs to `/cart/add`.

✅ **Check:** <http://localhost:8080/shop> lists 6 products. "Add to cart" gives a 404 for now; that's next.

### Step 7: `AddToCartServlet` (`@WebServlet("/cart/add")`), Create

`doPost` only:
1. `Catalog.find(code)` and `Cart.parseQuantity(quantity)`. If either is `null`, send **400** with a message.
2. `Cart.of(request.getSession())` gets this customer's cart (the session is created here).
3. `cart.add(...)`. If it returns `false`, send 400 ("at most 20").
4. `sendRedirect(".../cart?msg=added")` (Post/Redirect/Get).

It also has a small `sendError(...)` helper that Update and Remove reuse.

### Step 8: `CartServlet` (`@WebServlet("/cart")`), Read

`doGet` gets the cart and shows "Your cart is empty" or a table: each row has an **Update** form
(POST `/cart/update`) and a **Remove** button (POST `/cart/remove`). The footer shows the total, and an
**Empty cart** button POSTs to `/cart/clear`.

✅ **Check:** add 2 uniforms → the cart shows ₦17,000 and the header says "My cart (2)".
Open a **private window**: its cart is empty. That's the session at work.

### Step 9: `UpdateCartServlet` (`/cart/update`), Update

`doPost`: parse the quantity (400 if invalid), call `cart.update(code, quantity)` (400 if the product isn't in the cart),
then redirect with `msg=updated`.

### Step 10: `RemoveFromCartServlet` (`/cart/remove`), Delete

`doPost`: `cart.remove(code)` (400 if it wasn't there), then redirect with `msg=removed`.

### Step 11: `ClearCartServlet` (`/cart/clear`), delete everything

`doPost`: `request.getSession(false)`; if it isn't `null`, call `session.invalidate()`. Then redirect with `msg=cleared`.
The cart and the session are gone; the next page gets a new, empty one.

✅ **Check:** change a quantity, remove a line, then empty the cart. Typing `/cart/add` in the address bar (a GET) gives **405**, because only `doPost` exists.

### Step 12: `web.xml`, `index.html`, and `style.css`

`web.xml` has only `<session-config><session-timeout>30</session-timeout></session-config>` and the welcome file:
the servlets don't need it any more. Add the welcome page with two tiles (Products, My cart) and the stylesheet.

✅ **Final check:** everything above works, and after 30 idle minutes the cart is empty again.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** Why does a web app need session tracking at all?

<details><summary>Answer</summary>Because <b>HTTP is stateless</b>: the server forgets the user after each request. Without session tracking, logins and shopping carts could not work.</details>

**2.** What is a **session**?

<details><summary>Answer</summary>A series of requests from the <b>same user</b> (same browser) over a period of time, which the server treats as <b>one conversation</b> and can store data for.</details>

**3.** Name the four session-tracking techniques.

<details><summary>Answer</summary><b>Hidden form fields</b>, <b>URL rewriting</b>, <b>cookies</b>, and <b>HttpSession</b>.</details>

**4.** What is a **cookie**, and where is it stored?

<details><summary>Answer</summary>A small piece of text (a name and a value) that the server asks the browser to keep. It is stored <b>in the browser</b>, and the browser sends it back with every request to that server.</details>

**5.** Write the code to create a cookie `theme=dark` that lasts one hour, and send it to the browser.

<details><summary>Answer</summary><pre>Cookie c = new Cookie("theme", "dark");
c.setMaxAge(60 * 60);      // in seconds
response.addCookie(c);</pre></details>

**6.** How do you **delete** a cookie?

<details><summary>Answer</summary>Send a cookie with the <b>same name</b> and <code>setMaxAge(0)</code>. The browser then throws it away.</details>

**7.** Where is the data of an `HttpSession` stored: in the browser or on the server?

<details><summary>Answer</summary><b>On the server.</b> The browser only holds the session's <b>ID</b>, in a cookie called <code>JSESSIONID</code>.</details>

**8.** Write the code to save a username in the session and read it back later.

<details><summary>Answer</summary><pre>HttpSession session = request.getSession();
session.setAttribute("username", "ada");
// later, in another request:
String name = (String) session.getAttribute("username");</pre></details>

**9.** Which method ends a session, for example when the user logs out?

<details><summary>Answer</summary><code>session.invalidate()</code>. All data in that session is thrown away.</details>

**10.** What happens to a session when the user does nothing for a long time?

<details><summary>Answer</summary>It <b>times out</b> and the server deletes it. On Tomcat the default is <b>30 minutes</b>. You can change it with <code>session.setMaxInactiveInterval(seconds)</code> or in <code>web.xml</code>.</details>
