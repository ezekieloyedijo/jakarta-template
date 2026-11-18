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
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: the same page built without MVC and with MVC |
| [project/](project/) | **Student List**: list students and add a new one, in a clean Controller/Model/View split |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

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
