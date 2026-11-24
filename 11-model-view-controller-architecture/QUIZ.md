# QUIZ 11: Model-View-Controller Architecture

## Part A: Quick check

**1.** Match each part to its job: Model, View, Controller → "shows the data", "the data and the rules", "receives the request and decides".

<details><summary>Answer</summary>Model = the data and the rules. View = shows the data. Controller = receives the request and decides.</details>

**2.** In a Java web app, what usually plays each role?

<details><summary>Answer</summary>Model = plain Java classes (beans, DAOs, validation). View = JSPs. Controller = servlets.</details>

**3.** Why are the views put in `WEB-INF/views/`?

<details><summary>Answer</summary>So the browser can't open them directly. Every request must go through the controller, which prepares the data the view needs.</details>

**4.** After a successful insert, should the controller `forward` or `sendRedirect`? Why?

<details><summary>Answer</summary><code>sendRedirect</code> (Post/Redirect/Get), so pressing F5 on the next page doesn't insert the student again.</details>

**5.** When the form has errors, should the controller `forward` or `sendRedirect`?

<details><summary>Answer</summary><code>forward</code> back to the form view, with the errors and the typed values in request attributes. A redirect would lose them.</details>

**6.** In the project, what does `GET /students?action=edit&id=2` do, step by step?

<details><summary>Answer</summary><code>doGet</code> reads <code>action=edit</code>, calls <code>showEditForm</code>, which finds student 2 with <code>dao.findById(2)</code> (or sends 400/404), puts <code>StudentForm.of(student)</code>, the id, an empty error list, and the classes into the request, and forwards to <code>student-form.jsp</code>.</details>

**7.** Is "Model 1" or "Model 2" the MVC way?

<details><summary>Answer</summary>Model 2.</details>

## Part B: Explain in your own words

**8.** Explain MVC with the restaurant analogy.

<details><summary>Model answer</summary>The waiter (controller) takes your order, tells the kitchen, and brings back the plate. The kitchen (model) knows the recipes and keeps the food. The plate (view) is how the food is presented. Customers never go into the kitchen, and the cook never takes orders. Each does one job.</details>

**9.** The model classes don't import anything from `jakarta.servlet`. Why is that a good thing?

<details><summary>Model answer</summary>The model doesn't depend on the web, so it can be tested on its own, reused (for example in a phone app or a command-line tool), and changed without touching the web code.</details>

**10.** You want to save students in topic 08's H2 database instead of in memory. Which files change?

<details><summary>Model answer</summary>Only the model's storage: <code>StudentDao</code> (and the listener that creates it, to pass the database URL). The controller still calls <code>dao.findAll()</code> and the views still show a <code>List&lt;Student&gt;</code>, so they don't change.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Add an action `GET /students?action=view&id=2` that shows one student on its own page.

<details><summary>Hint</summary>In <code>doGet</code> add <code>case "view" -&gt;</code>. Find the student (400/404), <code>request.setAttribute("student", s)</code>, forward to a new view <code>WEB-INF/views/student-view.jsp</code>. Make the name in the list a link to it.</details>

**12.** Add a rule: students in SS1 to SS3 must be at least 13. Which file gets the change?

<details><summary>Answer</summary>Only <code>StudentForm.validate()</code> in the model. The controller and views don't change: the new error just appears in the red box.</details>

**13.** Show the count of boys and girls under the table.

<details><summary>Hint</summary>Do the counting in the <b>controller</b> (or a model method like <code>dao.countByGender("Female")</code>), put the numbers in request attributes, and let the view only print them. Don't count in the JSP.</details>

**14. Challenge:** rebuild the controller as a **front controller** mapped to `/students/*`, using `request.getPathInfo()` (`/new`, `/edit`, ...) instead of the `action` parameter.

<details><summary>Hint</summary><code>@WebServlet("/students/*")</code>. <code>getPathInfo()</code> is <code>null</code> for <code>/students</code>, <code>"/new"</code> for <code>/students/new</code>, and so on. Switch on it. Remember that links and the CSS path must change, because the pages are now one folder deeper (use <code>request.getContextPath()</code>).</details>
