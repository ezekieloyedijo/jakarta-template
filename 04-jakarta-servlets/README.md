# Topic 04: Jakarta Servlets

## The problem that existed

In the early web (mid-1990s), dynamic pages were made with **CGI** (Common Gateway Interface).
The idea was simple: **for every single request, the server started a brand-new program**, let it print the HTML,
and then **killed it**.

- 1,000 visitors meant **1,000 programs started and stopped**. That's slow and eats memory.
- Nothing stayed in memory between requests, so every request had to redo all the setup (open files, connect to the database...).
- Every CGI program parsed the raw request in its own way.

🍳 It's like a restaurant that **hires a new chef for every customer**, waits while the chef puts on the uniform
and learns the kitchen, has them cook **one** meal, and then **fires** them. Ten customers means ten hires and ten firings.

**Servlets** (1997) fixed this:

- The container creates **ONE servlet object** and **keeps it alive** in memory.
- Each request is handled by a lightweight **thread** that runs that same object's method, which is fast.
- Setup happens **once** (`init`), and cleanup happens **once** (`destroy`).
- The container gives you the request already **parsed** into a neat Java object.

🍳 Now it's **one experienced chef who stays all day**, and many waiters (threads) bring orders to them at the same time.

## The end goal of this topic

You should be able to write servlets that:

- **receive** any input from a browser (links, forms, checkboxes, dropdowns),
- **validate** it and **respond** correctly (HTML, status codes),
- **pass work to each other** (forward, include, redirect),
- use **configuration** values,

and you should understand the servlet **life cycle**, which explains *why* you must never store one user's data
in a servlet's fields.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **What a servlet is** | The definition, and the CGI story above |
| 2 | **The family tree: `Servlet` → `GenericServlet` → `HttpServlet`** | Why we always write `extends HttpServlet`, and where `doGet`/`doPost` come from |
| 3 | **The life cycle: `init` → `service` → `destroy`** | Once you know what the class is, learn *when* the container calls it |
| 4 | **One object, many threads (thread safety)** | A direct consequence of the life cycle, and the #1 beginner bug |
| 5 | **URL mapping patterns** | How a request finds *your* servlet |
| 6 | **The request object: reading input** | The servlet has been found. Now read what the user sent |
| 7 | **The response object: writing output** | Then answer them |
| 8 | **Handling forms: `doGet` shows, `doPost` processes** | Steps 6 and 7 together, the most common job of a servlet |
| 9 | **Configuration: `ServletConfig` and `ServletContext`** | Avoid hard-coding values, and share data across the app |
| 10 | **Working together: forward, include, redirect** | Real apps have many servlets that hand work to each other |
| 11 | **The Post/Redirect/Get pattern** | A practical rule that follows from Step 10 |

## What is inside this folder

| Path | What it shows | Run it |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson | Read |
| [example/](example/) | `/lifecycle` (watch init/service/destroy), `/register` (every kind of form input), `/config` (init params), and `/dispatch` (forward vs include vs redirect, side by side) | `cd example` then `mvn package cargo:run` |
| [project/](project/) | **Simple Calculator**: form, validation, errors with proper status codes, configuration, and an app-wide counter shared by all users | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |
