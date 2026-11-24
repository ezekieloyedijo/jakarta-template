# Topic 11: Model-View-Controller (MVC) Architecture

## The problem that existed

You now know servlets (good at **Java logic**) and JSPs (good at **HTML**). Early apps often mixed them all together:
one JSP that read the form, checked it, ran SQL, did calculations, **and** printed the HTML.

- Pages became hundreds of lines of tangled Java and HTML ("spaghetti code").
- Changing the design risked breaking the logic, and the other way round.
- Two people could not work on the same page at the same time.
- The same logic was copied into many pages.

🍽️ Think of a restaurant where **one person** takes the order, cooks the food, washes the dishes, and serves it,
all at the same time. It works for one customer, but it falls apart as soon as the restaurant gets busy.

A real restaurant **splits the jobs**:

- the **waiter** takes the order and decides where it goes (the **Controller**),
- the **kitchen** prepares the food, the actual work and data (the **Model**),
- the **plate presentation** is what the customer sees (the **View**).

MVC does the same: **servlet = Controller**, **Java classes = Model**, **JSP = View**.

## The end goal of this topic

You should be able to:

1. Explain what the **Model**, **View**, and **Controller** each do.
2. Trace one request through an MVC app, step by step.
3. Build a controller servlet that prepares data, puts it in request **attributes**, and **forwards** to a JSP.
4. Hide views inside `WEB-INF` so they are only reached through the controller.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: everything in one JSP** | The problem we are solving |
| 2 | **Separation of concerns** | The idea behind MVC: one job per part |
| 3 | **Model** | The data and the rules (JavaBeans, DAOs) |
| 4 | **View** | The JSP that only displays |
| 5 | **Controller** | The servlet that receives the request and decides |
| 6 | **The request journey** | Browser → Controller → Model → View → Browser |
| 7 | **Passing data: request attributes and `forward`** | The glue between Controller and View |
| 8 | **Hiding views in `WEB-INF`** | Users must always go through the controller |
| 9 | **Model 1 vs Model 2** | The old way vs the MVC way |

## What is inside this folder

| Path | What it will show |
|---|---|
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | The same temperature converter built without MVC (one JSP) and with MVC |
| [project/](project/) | **Student List**: create, read, update, delete, and search students, in a clean Controller/Model/View split |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is the **Student List**: the same features as topic 04's School Management app (create, read, update,
delete, and search students), rebuilt with **MVC**:

| Feature | Request | Controller method | Model call | View |
|---|---|---|---|---|
| **Read** / search | `GET /students` (`?q=ss`) | `list` | `findAll()` / `search(q)` | `student-list.jsp` |
| **Create** (form) | `GET /students?action=new` | `showForm` | | `student-form.jsp` |
| **Create** (save) | `POST /students` `action=insert` | `insert` | `validate()`, `insert(...)` | redirect, or form with errors |
| **Update** (form) | `GET /students?action=edit&id=2` | `showEditForm` | `findById(2)` | `student-form.jsp` |
| **Update** (save) | `POST /students` `action=update` | `update` | `validate()`, `update(...)` | redirect, or form with errors |
| **Delete** | `POST /students` `action=delete` | `delete` | `delete(id)` | redirect |

🍽️ The restaurant: `StudentController` is the **waiter**, the `model` package is the **kitchen**, and the JSPs in
`WEB-INF/views` are the **plates**.

### The files, and whether each is needed

| File | Layer | Needed? | Why |
|---|---|---|---|
| `pom.xml` | | ✅ **Yes** | Builds and runs the app |
| `model/Student.java` | Model | ✅ **Yes** | One student (getters only) |
| `model/StudentForm.java` | Model | ✅ **Yes** | Typed values + `validate()` + the lists of classes and genders |
| `model/StudentDao.java` | Model | ✅ **Yes** | Storage and CRUD + search |
| `controller/AppListener.java` | Controller | ✅ **Yes** | Creates the DAO with 3 sample students at startup |
| `controller/StudentController.java` | Controller | ✅ **Yes** | **The one servlet**: reads `action`, calls the model, forwards or redirects |
| `view/Html.java` | View | ✅ **Yes** | `escape(...)` for the JSPs (replaced by `<c:out>` in topic 12) |
| `WEB-INF/views/student-list.jsp`, `student-form.jsp` | View | ✅ **Yes** | The two pages |
| `WEB-INF/views/header.jspf`, `footer.jspf` | View | ✅ **Yes** | Shared top and bottom (`<%@ include %>`) |
| `WEB-INF/web.xml` | | ⚪ Recommended | Makes `/` open `/students`, and UTF-8 for JSPs |
| `css/style.css` | | ⚪ Optional, but recommended | The look |

**The order:** model first (it needs nothing), then the controller, then the views. Build and test one action at a time.

### Step 1: `pom.xml` and the three packages

Set `<artifactId>topic11-project</artifactId>`. Create the packages `com.school.project.model`, `.controller`, and `.view`.
The package names say which layer each class belongs to.

### Step 2: The model

- **`Student`**: `final` fields `id`, `name`, `gender`, `age`, `className`, with getters.
- **`StudentForm`**: constants `CLASSES`, `GENDERS`, `MIN_AGE`, `MAX_AGE`; a constructor that trims (and turns `null` into `""`);
  `empty()`, `of(student)`, `validate()`, getters, `ageAsNumber()`, and `static toNumber(text)`.
- **`StudentDao`**: a `ConcurrentHashMap` and an `AtomicInteger`; `insert`, `findAll` (by id), `search(text)`
  (name or class contains, not case-sensitive), `findById`, `update`, `delete`.

None of them imports `jakarta.servlet`. That's the sign of a clean model.

### Step 3: `controller/AppListener.java`

`@WebListener`; in `contextInitialized`, create the DAO, insert Amaka, Tolu, and Chidi, and save it as `"studentDao"`.

### Step 4: `controller/StudentController.java`, the skeleton

`@WebServlet("/students")`. In `init()`, get the DAO from the `ServletContext`.
- `doGet`: `action` (default `"list"`) → `switch`: `list`, `new`, `edit`; anything else → **400**.
- `doPost`: `setCharacterEncoding("UTF-8")`, then `switch`: `insert`, `update`, `delete`; anything else → **400**.

Helpers:

| Helper | Purpose |
|---|---|
| `showForm(request, response, form, id, errors)` | Sets `form`, `id`, `errors`, `classes` and forwards to `student-form.jsp` |
| `read(request)` | Builds a `StudentForm` from the 4 parameters |
| `find(request, response)` | The student for `id`, or `null` after 400 / 404 |
| `message(code)` | `added` / `updated` / `deleted` → a fixed sentence, anything else → `null` |

### Step 5: Read: `list(...)` and `student-list.jsp`

Controller: `q` → `dao.search(q)` or `dao.findAll()`; set `students`, `q`, `message`; forward.
View: read the three attributes, show the green message, the search box (keeps `q`), "N student(s) for ...",
and the table with **Edit** (`students?action=edit&id=N`) and **Delete** (a POST form with hidden `action=delete` and `id`).

✅ **Check:** <http://localhost:8080/> shows the 3 students; `?q=ss` finds all 3 (JSS2 also contains "SS").

### Step 6: Create: `new`, `insert`, and `student-form.jsp`

View: one form for both jobs. `editing = (id != null)`. A hidden `action` is `update` when editing, otherwise `insert`;
a hidden `id` only when editing. Name, gender radios, age, and the class dropdown are filled from `form`.
Controller `insert`: `read` → `validate` → errors? status 400 + `showForm(..., null, errors)`; else `dao.insert`,
`sendRedirect("students?msg=added")`.

✅ **Check:** add a student; submit an empty form → 4 red errors and status 400.

### Step 7: Update: `edit` and `update`

`showEditForm`: `find` → `showForm(StudentForm.of(s), s.getId(), List.of())`.
`update`: `find` → `read` → `validate` → `dao.update` → redirect with `msg=updated`.

### Step 8: Delete

`delete`: `find` → `dao.delete` → redirect with `msg=deleted`.

✅ **Final check:** all CRUD actions and search work. `/students?action=zap` gives 400.
`/WEB-INF/views/student-list.jsp` gives 404: the view can only be reached through the controller.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What do the letters **M**, **V**, and **C** stand for?

<details><summary>Answer</summary><b>Model</b>, <b>View</b>, <b>Controller</b>.</details>

**2.** What is the job of the **Model**?

<details><summary>Answer</summary>It holds the <b>data</b> and the <b>business rules</b> (for example a <code>Student</code> class and a <code>StudentDao</code>). It knows nothing about HTML or HTTP.</details>

**3.** What is the job of the **View**?

<details><summary>Answer</summary>It <b>shows</b> the data to the user, usually as HTML. In our apps the View is a <b>JSP</b>. It should contain as little logic as possible.</details>

**4.** What is the job of the **Controller**?

<details><summary>Answer</summary>It <b>receives the request</b>, reads the input, asks the Model to do the work, and <b>chooses which View</b> to show. In our apps the Controller is a <b>servlet</b>.</details>

**5.** In a Jakarta web app, which technology usually plays each role?

<details><summary>Answer</summary>Controller = <b>servlet</b>. View = <b>JSP</b>. Model = <b>plain Java classes</b> (JavaBeans, DAOs).</details>

**6.** Put these steps in order: *View builds HTML*, *browser sends request*, *Controller asks Model for data*, *Controller forwards to View*.

<details><summary>Answer</summary>1. Browser sends request. 2. Controller asks Model for data. 3. Controller forwards to View. 4. View builds HTML (which is sent back to the browser).</details>

**7.** How does the Controller pass data to the View?

<details><summary>Answer</summary>It stores the data as a <b>request attribute</b>, then <b>forwards</b>:<pre>request.setAttribute("students", list);
request.getRequestDispatcher("/WEB-INF/views/students.jsp").forward(request, response);</pre></details>

**8.** Why do we put the JSP views inside `WEB-INF`?

<details><summary>Answer</summary>So the browser <b>cannot open them directly</b>. Users must go through the controller, which prepares the data first. Otherwise the page would open with no data.</details>

**9.** What is the difference between **Model 1** and **Model 2**?

<details><summary>Answer</summary><b>Model 1</b>: the browser goes straight to JSPs, and each JSP does its own logic. <b>Model 2</b> (MVC): every request goes to a <b>controller servlet</b> first, which then forwards to a JSP.</details>

**10.** Give two benefits of MVC.

<details><summary>Answer</summary>Any two of: code is <b>easier to read</b> and change; designers and programmers can work <b>separately</b>; logic is <b>reused</b> instead of copied; each part can be <b>tested</b> on its own; changing the design doesn't break the logic.</details>
