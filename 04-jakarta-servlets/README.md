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
| [project/](project/) | **School Management**: create, list, edit, delete, and search students. Five servlets registered in `web.xml`, form validation with proper status codes, settings from `web.xml`, a store shared by all users in the `ServletContext`, and Post/Redirect/Get | `cd project` then `mvn package cargo:run` |
| [QUIZ.md](QUIZ.md) | Test yourself | Last |

## How to build the project, step by step

The project is a **School Management** app. It can **create, read, update, delete, and search** students
(people often call the first four "**CRUD**"). Students are kept **in memory**, so they disappear when Tomcat stops.
Saving them in a real database comes in topic 08.

### The plan: one servlet per job

Before writing code, decide what the app does and which URL does it:

| Feature | The user... | Request | Servlet (method) |
|---|---|---|---|
| **Read** | opens the list | `GET /students` | `StudentListServlet` (`doGet`) |
| **Create** | opens the empty form | `GET /add-student` | `AddStudentServlet` (`doGet`) |
| | submits the form | `POST /add-student` | `AddStudentServlet` (`doPost`) |
| **Update** | opens the filled form | `GET /edit-student?id=3` | `EditStudentServlet` (`doGet`) |
| | submits the changes | `POST /edit-student` | `EditStudentServlet` (`doPost`) |
| **Delete** | clicks Delete | `POST /delete-student` | `DeleteStudentServlet` (`doPost`) |
| **Search** | types a name or class | `GET /search?q=amaka` | `SearchStudentServlet` (`doGet`) |

🏫 Think of a **school office** with one desk per job: the *records desk* shows the register, the *admissions desk*
adds new students, and so on. `web.xml` is the **sign at the entrance** saying which desk handles what.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` | ✅ **Yes** | Without it Maven can't build or run the app |
| `WEB-INF/web.xml` | ✅ **Yes** | It **registers all five servlets** (without it every page is a 404), holds the school name and the list of classes, and gives `StudentListServlet` its start-up settings |
| `Student.java` | ✅ **Yes** | The data: one student |
| `StudentStore.java` | ✅ **Yes** | The storage: keeps the students and does create, read, update, delete, and search |
| `StudentForm.java` | ✅ **Yes** | What the user typed in the form, and the rules that check it. Used by Add and Edit |
| `Page.java` | ✅ **Yes** | Builds the HTML (layout, table, form), so the servlets stay short |
| The 5 servlets | ✅ **Yes** | One per job (see the plan above) |
| `index.html` | ⚪ Optional | The welcome page for `http://localhost:8080/`. Without it `/` gives a 404, but `/students` still works |
| `css/style.css` | ⚪ Optional, but recommended | The look: header bar, table, buttons, green success and red error boxes. Without it everything works, just plain |

**The order:** settings first, then the data classes (the servlets use them), then **one servlet at a time**.
After each step, **run** it with `mvn package cargo:run`, check it in the browser, and stop it with Ctrl + C.

### Step 1: `pom.xml`

Copy it from the template and set `<artifactId>topic04-project</artifactId>`.

### Step 2: `WEB-INF/web.xml`, the settings

Create `src/main/webapp/WEB-INF/web.xml` and start with the **app-wide settings** (`<context-param>`):

| Setting | Value | Used for |
|---|---|---|
| `schoolName` | `Bright Future Academy` | The name in the header of every page |
| `classNames` | `JSS1,JSS2,JSS3,SS1,SS2,SS3` | The choices in the "Class" dropdown, and the only classes the server accepts |

Also add a `<welcome-file-list>` with `index.html` (the first page for `/`).
The servlets are added one by one in Steps 7 to 11.

### Step 3: `Student.java`, the data

A plain Java class (no Jakarta in it) for **one student**: `id`, `name`, `gender`, `age`, `className`.

| Part | Purpose |
|---|---|
| `private final` fields | Once a student is created it **never changes**. To edit one, we replace it with a new `Student` that has the same `id`. Objects that never change are safe to share between threads |
| Constructor | Sets all five fields |
| Getters (`getName()`...) | Let other classes read the fields |

(We call it `className` because `class` is a reserved word in Java.)

### Step 4: `StudentStore.java`, the storage

**One** store holds every student for the whole app. Many users can use it at the same time (Step 4 of the lesson),
so it uses thread-safe types: a `ConcurrentHashMap` (id → student) and an `AtomicInteger` for the next id.

| Method | Job | Purpose |
|---|---|---|
| `add(name, gender, age, className)` | **Create** | Takes the next id number, creates the `Student`, and saves it. Returns the new student |
| `findAll()` | **Read** | Every student, sorted by id |
| `findById(id)` | **Read** | One student, or `null` if there is no such id |
| `update(student)` | **Update** | Replaces the student with the same id. Returns `false` if that id doesn't exist |
| `delete(id)` | **Delete** | Removes the student. Returns `false` if that id doesn't exist |
| `search(text)` | **Search** | Students whose name or class **contains** the text, ignoring capital letters |
| `from(servletContext)` | (helper) | Gets **the** store from the app's notice board (`ServletContext`), so every servlet uses the same one |

### Step 5: `StudentForm.java`, what the user typed, and the rules

The Add form and the Edit form have the same four fields and the same rules, so this class holds them **once**.
It keeps every value as **text exactly as typed** (even an age of `"abc"`), so the form can be shown again with the user's input in it.

| Method | Purpose |
|---|---|
| `empty()` | An empty form, for "Add a student" |
| `of(student)` | A form filled with an existing student, for "Edit" |
| `read(request)` | Reads the four fields with `request.getParameter(...)`. A missing field becomes `""`, never `null` |
| `validate(allowedClasses)` | Checks every rule and returns a **list of error messages**. An **empty list** means the form is valid. Rules: a name is required (60 characters at most), the gender is Male or Female, the age is a whole number from 5 to 30, and the class is one from `web.xml` |
| `ageAsNumber()` | The age as an `int`. Only called after `validate` found no errors |
| `allowedClasses(servletContext)` | Reads `classNames` from `web.xml` and splits it at the commas into a list |
| `toWholeNumber(text)` | Turns `"12"` into `12`, and anything else (`"abc"`, empty, `null`) into `null` instead of crashing. Also used for the `id` |

### Step 6: `Page.java`, the HTML

Builds the HTML for every page, so the servlets only do the logic. (In topic 06, JSP pages will do this job.)

| Method | Purpose |
|---|---|
| `page(servletContext, title, content)` | The full page: the stylesheet link, a header with the **school name** from `web.xml`, the menu (Students, Add student, Search), the title, and the content |
| `table(students)` | A table with one row per student. Each row has an **Edit** link (`edit-student?id=3`) and a **Delete** button. The Delete button is a small `method="post"` form with the id in a **hidden field**, plus a "Are you sure?" pop-up. Shows "No students found." for an empty list |
| `form(action, form, errors, classes, buttonText, id)` | The Add/Edit form: a red box listing the errors (if any), then the fields **filled with the values in `form`**, so nothing has to be retyped. For Edit, the `id` goes in a hidden field |
| `escape(text)` | Makes user-typed text safe inside HTML (`<script>` is shown as text and never runs; topic 13) |

### Step 7: `StudentListServlet.java`, **Read**, the first servlet

| Method | Purpose |
|---|---|
| `init()` | Runs **once**. Creates the `StudentStore`, adds three sample students if the init-param `loadSampleData` is `true`, and puts the store in the `ServletContext` with `setAttribute(...)`, where every servlet can find it |
| `doGet(...)` | Gets all students from the store and shows them with `Page.table(...)`, plus a count and an "Add student" button |
| `message(msg)` | After Add, Edit, or Delete, the other servlets redirect here with `?msg=added` (or `updated` / `deleted`). This turns that into a green "Student added." box. It only shows **its own fixed sentences**, never text from the URL |

Register it in `web.xml`, with two extra settings inside `<servlet>`:

```xml
<servlet>
    <servlet-name>studentList</servlet-name>
    <servlet-class>com.school.project.StudentListServlet</servlet-class>
    <init-param>                                  <!-- a setting for THIS servlet only -->
        <param-name>loadSampleData</param-name>
        <param-value>true</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>          <!-- create me when the app STARTS -->
</servlet>
<servlet-mapping>
    <servlet-name>studentList</servlet-name>
    <url-pattern>/students</url-pattern>
</servlet-mapping>
```

**Why `load-on-startup` matters here:** `init()` creates the store that **all** the other servlets need.
Without it, `init()` would only run when someone first opens `/students`. A user who went straight to
"Add student" would find no store, and the app would crash.

✅ Check: the terminal prints "StudentListServlet ready: 3 sample students loaded." and <http://localhost:8080/students>
lists Amaka, Tolu, and Chidi. (The Edit and Delete buttons give 404 until Steps 9 and 10. That is expected.)

### Step 8: `AddStudentServlet.java`, **Create**

| Method | Purpose |
|---|---|
| `doGet(...)` | Shows the empty form (`StudentForm.empty()`) |
| `doPost(...)` | The four stages of every form: **1. Read** with `StudentForm.read(request)` (after `setCharacterEncoding("UTF-8")`). **2. Validate**: if there are errors, set status **400** and show the form again with the errors and the user's values. **3. Save** with `store.add(...)`. **4. Redirect** to `students?msg=added` |
| `show(...)` | Draws the page with `Page.form(...)`. Both `doGet` and `doPost` use it |

Register it: nickname `addStudent`, URL `/add-student`.

**Why redirect after saving?** This is **Post/Redirect/Get** (lesson Step 11). If `doPost` showed the list directly,
pressing **F5** would send the form again and add the student **twice**. After the redirect, F5 only reloads the list.

✅ Check: add a student. You land on the list with a green "Student added." box. Press F5: still only one new student.
Now submit an empty form: you get the red error box, and whatever you typed is still there.

### Step 9: `EditStudentServlet.java`, **Update**

| Method | Purpose |
|---|---|
| `doGet(...)` | Reads `id` from the URL (`edit-student?id=3`). Not a number: **400** with `sendError`. No such student: **404**. Otherwise it shows the form filled with that student (`StudentForm.of(student)`) |
| `doPost(...)` | Reads the `id` from the **hidden field**, then the same stages as Add: read, validate (**400** on errors), save with `store.update(...)` (**404** if the student was deleted in the meantime), and redirect to `students?msg=updated` |

Register it: nickname `editStudent`, URL `/edit-student`.

✅ Check: click Edit on a student, change the age, save. Then try <http://localhost:8080/edit-student?id=99> (404)
and <http://localhost:8080/edit-student?id=abc> (400).

### Step 10: `DeleteStudentServlet.java`, **Delete**

| Method | Purpose |
|---|---|
| `doPost(...)` | Reads the `id` (**400** if it isn't a number), deletes it with `store.delete(id)` (**404** if it doesn't exist), and redirects to `students?msg=deleted` |

There is **no `doGet` on purpose**. Deleting must never happen just by opening a link, because browsers and search
engines open links on their own. So typing `/delete-student` in the address bar gives **405 Method Not Allowed**.

Register it: nickname `deleteStudent`, URL `/delete-student`.

✅ Check: delete a student (click OK in the pop-up). Then open <http://localhost:8080/delete-student> in the address bar: 405.

### Step 11: `SearchStudentServlet.java`, **Search**

| Method | Purpose |
|---|---|
| `doGet(...)` | Reads `q` from the URL. If it's empty, shows a hint. Otherwise it calls `store.search(q)` and shows "*N* result(s) for *q*" and the table. The search box keeps what you typed |

Search uses **GET**, not POST, because it only **reads** data. The words stay in the URL (`search?q=ss1`),
so you can refresh, bookmark, or share a search safely.

Register it: nickname `searchStudent`, URL `/search`.

✅ Check: search for `amaka`, `AMAKA` (same result), and `ss1`. Search for `zzz`: "No students found."

### Step 12: `index.html` (optional)

A plain HTML welcome page in `src/main/webapp/` with the same header and three tiles: All students, Add a student, Search.
It needs no `web.xml` entry for itself, because the `<welcome-file-list>` from Step 2 shows it for `/`.

### Step 13: `css/style.css` (optional, recommended)

The app's own small stylesheet in `src/main/webapp/css/`. A few colour **variables** at the top (`--brand`, `--danger`...)
let you change the whole look in one place. It styles the header bar, the table, the form, the buttons
(`.btn`, `.btn.danger`, `.btn.small`), and the message boxes (`.msg.ok` green, `.msg.error` red).
It also works on a phone: the table scrolls sideways instead of breaking the page.

✅ Final check: click through every feature once more, and watch the **terminal**: each Add, Edit, and Delete prints a line there.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is a **servlet**?

<details><summary>Answer</summary>A Java class (usually <code>extends HttpServlet</code>) that runs inside the container, <b>receives an HTTP request</b>, and <b>writes the HTTP response</b>.</details>

**2.** Which class do we extend to write a web servlet, and which methods do we usually override?

<details><summary>Answer</summary><code>HttpServlet</code>. We override <code>doGet</code> (for GET requests) and/or <code>doPost</code> (for POST requests).</details>

**3.** Name the three life-cycle methods in order, and say how often each is called.

<details><summary>Answer</summary><code>init()</code> once, when the servlet is created. <code>service()</code> (which calls <code>doGet</code>/<code>doPost</code>) <b>once per request</b>. <code>destroy()</code> once, when the app stops.</details>

**4.** How many objects of one servlet class does the container usually create?

<details><summary>Answer</summary><b>One.</b> All requests share it, each running on its own thread.</details>

**5.** Why must you **not** store one user's data in a servlet's field (instance variable)?

<details><summary>Answer</summary>Because the one servlet object is shared by <b>all users at the same time</b>. User B could see or overwrite user A's data. Use local variables inside <code>doGet</code>/<code>doPost</code> instead.</details>

**6.** Which method reads the value of a form field called `email`?

<details><summary>Answer</summary><code>request.getParameter("email")</code>. It returns a <code>String</code>, or <code>null</code> if the field was not sent.</details>

**7.** Which two lines do you write before sending HTML from a servlet?

<details><summary>Answer</summary><code>response.setContentType("text/html;charset=UTF-8");</code> and then <code>PrintWriter out = response.getWriter();</code>. Then you write the HTML with <code>out.println(...)</code>.</details>

**8.** What is the difference between `forward` and `sendRedirect`?

<details><summary>Answer</summary><b>forward</b> passes the <b>same</b> request to another resource on the server, so the address bar does not change. <b>sendRedirect</b> tells the browser to make a <b>new</b> request to a different URL, so the address bar changes.</details>

**9.** What is the difference between `ServletConfig` and `ServletContext`?

<details><summary>Answer</summary><code>ServletConfig</code> holds settings for <b>one servlet</b> (its init parameters). <code>ServletContext</code> is shared by the <b>whole app</b> (context parameters and app-wide attributes).</details>

**10.** What is the **Post/Redirect/Get** pattern, and what problem does it solve?

<details><summary>Answer</summary>After handling a POST, the servlet <b>redirects</b> to a GET page instead of showing the result directly. This stops the form from being <b>submitted twice</b> when the user presses refresh.</details>
