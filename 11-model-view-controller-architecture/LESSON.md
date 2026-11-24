# LESSON 11: Model-View-Controller Architecture

> **The big picture for this lesson: A RESTAURANT**
>
> - **The waiter** takes your order, tells the kitchen, and brings the food = the **Controller** (a servlet)
> - **The kitchen**, with the recipes and the store-room = the **Model** (Java classes: data, rules, DAO)
> - **The plate as it is served**, nicely arranged = the **View** (a JSP)
> - **The order slip** passed from waiter to kitchen and back = **request attributes**
> - **Customers never walk into the kitchen** = views are hidden in **`WEB-INF`**

---

## Step 1: Recap, everything in one JSP

In topics 06 and 10, a JSP like `add.jsp` did **every** job: read the form, check it, save it, decide where to go,
and build the HTML. Look at [example/no-mvc.jsp](example/src/main/webapp/no-mvc.jsp): reading, checking, the maths,
and the HTML are tangled in one file.

Problems:
- A designer who wants to change the colours must read Java.
- The rules (the maths, the checks) can't be reused by another page or tested on their own.
- One change can break three unrelated things.

👨‍🍳 Imagine one person who takes your order, cooks, washes up, and serves, all at the same time. It works for one
customer; it fails on a busy night.

---

## Step 2: Separation of concerns

> **Separation of concerns** = give every part of the program **one job**, and keep the jobs apart.

**MVC** splits a web app into three parts:

| Part | Its one job | In our apps | Knows about HTTP? | Knows about HTML? |
|---|---|---|---|---|
| **Model** | The data and the rules | `Student`, `StudentForm`, `StudentDao` | ❌ No | ❌ No |
| **View** | Show the data | `student-list.jsp`, `student-form.jsp` | barely | ✅ Yes |
| **Controller** | Receive the request, decide, connect the other two | `StudentController` (a servlet) | ✅ Yes | ❌ No |

---

## Step 3: Model

The **Model** is plain Java: no `HttpServletRequest`, no `out.println`.

```java
public class Temperature {                       // the data
    private final double celsius;
    public Temperature(double celsius) {
        if (celsius < -273.15) throw new IllegalArgumentException("Nothing is colder than -273.15 °C.");
        this.celsius = celsius;
    }
    public double getFahrenheit() { return celsius * 9 / 5 + 32; }   // a rule
}
```

In the project, the model has three classes:
- **`Student`**: one student (getters only).
- **`StudentForm`**: what the user typed, and `validate()`, the rules.
- **`StudentDao`**: storage and CRUD (`insert`, `findAll`, `search`, `findById`, `update`, `delete`).

Because the model doesn't know about the web, you could test it, use it in a phone app, or swap the in-memory DAO for
topic 08's database DAO **without touching the controller or the views**.

---

## Step 4: View

The **View** is a JSP that **only shows** what it is given. It doesn't check, calculate, or save:

```jsp
<% Temperature t = (Temperature) request.getAttribute("temperature"); %>
<% if (t != null) { %>
    <p class="msg ok"><%= t.getCelsius() %> °C = <%= t.getFahrenheit() %> °F</p>
<% } %>
```

A good test: **could a web designer edit this file without breaking any rule?** If yes, it's a good view.

(In topic 12, even these small scriptlets will be replaced by `${temperature.fahrenheit}`.)

---

## Step 5: Controller

The **Controller** is a servlet. For every request it does **four things**, always in this order:

```java
@WebServlet("/convert")
public class ConvertController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ... {
        String input = request.getParameter("celsius");                     // 1. READ the request
        Temperature t = new Temperature(Double.parseDouble(input));         // 2. ASK the model
        request.setAttribute("temperature", t);                             // 3. HAND OVER the result
        request.getRequestDispatcher("/WEB-INF/views/convert.jsp")          // 4. CHOOSE the view
               .forward(request, response);
    }
}
```

It never prints HTML. Like the waiter, it doesn't cook and it doesn't design plates; it **decides** and **connects**.

**One controller for many actions.** The project uses one servlet for every student page, with an `action` parameter:

| Request | Action | Controller does |
|---|---|---|
| `GET /students` | list | `dao.findAll()` (or `search`) → `student-list.jsp` |
| `GET /students?action=new` | new | empty form → `student-form.jsp` |
| `GET /students?action=edit&id=2` | edit | `dao.findById(2)` → `student-form.jsp` |
| `POST /students` `action=insert` | insert | validate → `dao.insert` → redirect |
| `POST /students` `action=update` | update | validate → `dao.update` → redirect |
| `POST /students` `action=delete` | delete | `dao.delete` → redirect |

```java
switch (action) {
    case "list" -> list(request, response);
    case "new"  -> showForm(...);
    case "edit" -> showEditForm(request, response);
    default     -> response.sendError(400, "Unknown action");
}
```

---

## Step 6: The request journey

```
  ① GET /students?q=ss
Browser ─────────────────────▶ StudentController ── ② dao.search("ss") ──▶ StudentDao (Model)
                                       │            ◀── List<Student> ────
                                       │ ③ request.setAttribute("students", list)
                                       │ ④ forward
                                       ▼
                               student-list.jsp (View) ── ⑤ HTML ──▶ Browser
```

And after a **change** (POST), the controller **redirects** instead of forwarding (Post/Redirect/Get, topic 04):

```
POST /students action=insert ─▶ Controller ─▶ dao.insert(...) ─▶ 302 to /students?msg=added ─▶ GET /students ─▶ ...
```

---

## Step 7: Passing data, request attributes and `forward`

The controller and the view share data through the **request scope** (topic 10):

```java
// controller
request.setAttribute("students", students);
request.setAttribute("q", q);
request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
```
```jsp
<%-- view --%>
<% List<Student> students = (List<Student>) request.getAttribute("students"); %>
```

| Use | When |
|---|---|
| `forward` + request attributes | **Showing** a page (GET), or showing the form again **with errors** |
| `sendRedirect` | **After a change** succeeded (POST), so F5 doesn't repeat it |

Remember from topic 04: attributes **don't survive a redirect**, because the browser makes a new request. That's why the
success message travels as `?msg=added` and the controller turns it into a fixed sentence.

---

## Step 8: Hiding views in `WEB-INF`

Views live in `WEB-INF/views/`. The browser **can't** open them directly (404), but a `forward` can.

Why does it matter? If a user opened `student-list.jsp` directly, the controller would never run, `students` would be
`null`, and the page would crash. Hiding the views **forces** every request through the controller.

🍽️ Customers order from the waiter; they can't walk into the kitchen and grab a plate.

---

## Step 9: Model 1 vs Model 2

| | **Model 1** (topics 06 and 10) | **Model 2** = MVC (from now on) |
|---|---|---|
| Who receives the request? | A JSP | A servlet (the controller) |
| Where are the rules? | Mixed into JSPs | In model classes |
| Where is the HTML? | The same JSPs | Views in `WEB-INF` |
| Good for | Tiny apps, quick demos | Anything real |

Almost every Java web framework you'll meet later (Spring MVC, Jakarta MVC, Struts) is built on the Model 2 idea.

---

## Now try it

The project in [project/](project/) is the **Student List**: topic 04's School Management CRUD and search rebuilt
with MVC. Compare the two: in topic 04, five servlets built HTML in Java strings; now **one controller** decides,
**three model classes** hold the data and the rules, and **two JSP views** show the pages.
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
