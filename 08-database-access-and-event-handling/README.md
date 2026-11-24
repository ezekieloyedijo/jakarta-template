# Topic 08: Database Access and Event Handling

## The problem that existed

So far everything our apps remembered lived **in memory** (in variables, the session, or the `ServletContext`).
Stop Tomcat with Ctrl + C and **everything is gone**: every registered user, every message, every order.

📒 It's like a shop that writes all its sales on a **whiteboard**. At closing time someone wipes the board clean.
A real shop keeps a **ledger book in a safe**, so the records are still there tomorrow, next month, and next year.

That safe is a **database**. Java talks to databases with **JDBC** (Java Database Connectivity).
We use **H2**, a small database written in Java that Maven downloads for us, so there is nothing extra to install.

A second problem: some jobs must happen at a **particular moment**, such as "create the database tables when the
app starts" or "count users as they arrive". No request triggers these. **Listeners** are classes the container calls
automatically when such an **event** happens, like a doorbell that rings when someone arrives.

## The end goal of this topic

You should be able to:

1. Explain what a database, a table, and SQL are, in plain words.
2. Connect to a database with JDBC and run `INSERT` and `SELECT` with a **PreparedStatement**.
3. Close resources safely with **try-with-resources**.
4. Put database code in a **DAO** class instead of inside servlets.
5. Use **listeners** to react to the app starting/stopping and to sessions being created/destroyed.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: memory is not permanent** | The problem we are solving |
| 2 | **Database, table, row, SQL** | The basic words before any code |
| 3 | **JDBC and the H2 database** | How Java talks to a database |
| 4 | **Connecting: `DriverManager.getConnection`** | The first line of every database job |
| 5 | **Running SQL: `PreparedStatement`** | Insert and read data safely (no SQL injection) |
| 6 | **Reading results: `ResultSet`** | Turn rows into Java objects |
| 7 | **Closing resources: try-with-resources** | Leaking connections crashes servers |
| 8 | **The DAO pattern** | Keep SQL out of servlets |
| 9 | **Events and listeners** | Code that runs on app start/stop |
| 10 | **`ServletContextListener` and `HttpSessionListener`** | Create tables at startup; count active users |

## What is inside this folder

| Path | What it will show |
|---|---|
| [LESSON.md](LESSON.md) | The full lesson, step by step |
| [example/](example/) | Small demos: connect, SELECT and INSERT with a PreparedStatement, a startup listener, a session counter |
| [project/](project/) | **School Library**: add, list, edit, and delete books saved in an H2 database file, so they survive a restart (CRUD with SQL) |
| [QUIZ.md](QUIZ.md) | More questions and tasks |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## How to build the project, step by step

The project is a **School Library**. Books are saved in a real **database** (H2, in a file in your home folder),
so they are still there after Tomcat restarts. Each CRUD action is one SQL command:

| Feature | The user... | Request | Servlet | SQL (in `BookDao`) |
|---|---|---|---|---|
| **Read** | opens the list | `GET /books` | `BookListServlet` | `SELECT * FROM book ORDER BY title` |
| **Create** | submits the Add form | `POST /books/add` | `AddBookServlet` | `INSERT INTO book ... VALUES (?, ?, ?, ?)` |
| **Update** | submits the Edit form | `POST /books/edit` | `EditBookServlet` | `UPDATE book SET ... WHERE id = ?` |
| **Delete** | clicks Delete | `POST /books/delete` | `DeleteBookServlet` | `DELETE FROM book WHERE id = ?` |
| *(app starts)* | | | `DatabaseListener` | `CREATE TABLE IF NOT EXISTS book (...)` |
| *(session starts/ends)* | | | `ReadersOnlineListener` | |

🗄️ The records office: servlets are the **front desk**, `BookDao` is the **one clerk** allowed to open the filing
cabinet, and `DatabaseListener` is the **caretaker** who sets up the cabinet before the office opens.

### The files, and whether each is needed

| File | Needed? | Why |
|---|---|---|
| `pom.xml` (with the **H2** dependency) | ✅ **Yes** | Without H2 there is no database driver: the app fails to start |
| `WEB-INF/web.xml` | ✅ **Yes** | Holds the database address (`<context-param>` `dbUrl`) and makes `/` open `/books` |
| `Book.java` | ✅ **Yes** | One row of the table as a Java object |
| `BookDao.java` | ✅ **Yes** | **All the SQL**: create table, count, insert, findAll, findById, update, delete |
| `BookForm.java` | ✅ **Yes** | The typed values and the rules that check them |
| `DatabaseListener.java` | ✅ **Yes** | Creates the table, adds sample books, and shares the DAO when the app starts |
| `ReadersOnlineListener.java` | ⚪ Optional | Counts open sessions for the footer. Shows the second kind of listener |
| `Page.java` | ✅ **Yes** | Layout, book table, book form, escaping |
| The 4 servlets | ✅ **Yes** | One per CRUD job. **No SQL inside them** |
| `css/style.css` | ⚪ Optional, but recommended | The look |
| `index.html` | ❌ **Not used** | The welcome file is the `books` servlet |

### Step 1: `pom.xml`

Copy it from the template, set `<artifactId>topic08-project</artifactId>`, and add the H2 dependency
(`com.h2database:h2:2.3.232`) inside `<dependencies>`.

### Step 2: `web.xml`

- `<context-param>` **`dbUrl`** = `jdbc:h2:~/jakarta-course-data/library` (the file `C:\Users\<you>\jakarta-course-data\library.mv.db`).
- `<welcome-file>books</welcome-file>`, so `/` opens the list.

### Step 3: `Book.java`

`final` fields `id`, `title`, `author`, `year`, `copies`, with getters. One `Book` = one row.

### Step 4: `BookDao.java`, all the SQL

It stores the URL and has a private `connect()` (`DriverManager.getConnection(url, "sa", "")`).
**Every method uses try-with-resources and `?` marks.**

| Method | SQL | Returns |
|---|---|---|
| `createTableIfMissing()` | `CREATE TABLE IF NOT EXISTS book (id INT AUTO_INCREMENT PRIMARY KEY, title, author, published, copies)` | nothing |
| `count()` | `SELECT COUNT(*) FROM book` | the number of rows |
| `insert(title, author, year, copies)` | `INSERT ... VALUES (?, ?, ?, ?)` with `RETURN_GENERATED_KEYS` | the new id |
| `findAll()` | `SELECT * FROM book ORDER BY title` | a `List<Book>` (loop with `while (rs.next())`) |
| `findById(id)` | `SELECT * FROM book WHERE id = ?` | the `Book`, or `null` |
| `update(id, ...)` | `UPDATE book SET title = ?, ... WHERE id = ?` | `true` if 1 row changed |
| `delete(id)` | `DELETE FROM book WHERE id = ?` | `true` if 1 row was deleted |
| `static from(app)` | | the DAO saved in the `ServletContext` |

A private `toBook(rs)` turns the current row into a `Book`. Every `SQLException` becomes a `DataException`
(unchecked), so servlets need no try/catch and a database failure shows a 500 page.

The year column is called `published` because `YEAR` is a reserved word in H2.

### Step 5: `DatabaseListener.java` (`@WebListener`)

`contextInitialized`:
1. Read `dbUrl` with `app.getInitParameter("dbUrl")`.
2. `Class.forName("org.h2.Driver")`.
3. `new BookDao(url)`, `createTableIfMissing()`, and if `count() == 0`, insert 3 sample books.
4. `app.setAttribute("bookDao", dao)`.

`contextDestroyed`: unregister the driver this app loaded (so Tomcat can unload the app cleanly).

✅ **Check:** start the app. The terminal shows `[DB] Library ready at jdbc:h2:~/... with 3 books`, and the file
`library.mv.db` appears in your home folder.

### Step 6: `BookForm.java`

Like topic 04's `StudentForm`: `empty()`, `of(book)`, `read(request)` (trimmed, never null), and `validate()`:
title required (max 100), author required (max 60), year a whole number from 1450 to this year, copies 0–999.
Plus `yearAsNumber()`, `copiesAsNumber()`, and `static toNumber(text)`.

### Step 7: `ReadersOnlineListener.java` (`@WebListener`)

An `HttpSessionListener` with a `static AtomicInteger`: `+1` in `sessionCreated`, `−1` in `sessionDestroyed`.

### Step 8: `Page.java`

`page(...)` calls `request.getSession()` (so each visitor is counted) and shows "Readers online: N" in the footer.
`table(...)` shows the books with a red "none left" badge when `copies` is 0. `form(...)` is the Add/Edit form.

### Step 9: `BookListServlet` (`/books`), Read

`BookDao.from(getServletContext()).findAll()`, then the table, the count, and the fixed `?msg=` message.

✅ **Check:** <http://localhost:8080/> shows the 3 sample books, sorted by title.

### Step 10: `AddBookServlet` (`/books/add`), Create

`doGet`: `BookForm.empty()`. `doPost`: `setCharacterEncoding("UTF-8")`, read, validate (400 + form), `insert(...)`,
redirect with `msg=added`.

### Step 11: `EditBookServlet` (`/books/edit`), Update

A private `find(...)` sends 400 or 404. `doGet` shows `BookForm.of(book)`; `doPost` validates and calls `update(...)`.

### Step 12: `DeleteBookServlet` (`/books/delete`), Delete

`doPost` only: 400 / 404, `delete(id)`, redirect with `msg=deleted`.

✅ **Final check:** add a book, **stop Tomcat, start it again**: the book is still there. That is the whole point of
this topic. To start over with only the sample books, stop Tomcat and delete `library.mv.db`.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** Why do we need a database instead of keeping data in Java variables?

<details><summary>Answer</summary>Variables live in <b>memory</b> and are lost when the server stops. A database stores data <b>permanently</b> on disk, and it can be searched and shared safely.</details>

**2.** What do **JDBC** and **SQL** stand for?

<details><summary>Answer</summary><b>JDBC</b> = Java Database Connectivity: the Java API for talking to databases. <b>SQL</b> = Structured Query Language: the language used to ask the database for things (<code>SELECT</code>, <code>INSERT</code>...).</details>

**3.** Which method opens a connection to a database?

<details><summary>Answer</summary><code>DriverManager.getConnection(url, user, password)</code>, which returns a <code>Connection</code>.</details>

**4.** What is the difference between `executeQuery()` and `executeUpdate()`?

<details><summary>Answer</summary><code>executeQuery()</code> is for <code>SELECT</code> and returns a <b><code>ResultSet</code></b> (the rows). <code>executeUpdate()</code> is for <code>INSERT</code>, <code>UPDATE</code>, and <code>DELETE</code>, and returns an <b><code>int</code></b>: how many rows changed.</details>

**5.** Why should you use a `PreparedStatement` with `?` placeholders instead of joining strings into SQL?

<details><summary>Answer</summary>It stops <b>SQL injection</b>: user input is always treated as <b>data</b>, never as SQL commands. It also handles quotes and special characters for you.</details>

**6.** How do you read every row from a `ResultSet`?

<details><summary>Answer</summary><pre>while (rs.next()) {
    String name = rs.getString("name");
    int age = rs.getInt("age");
}</pre><code>next()</code> moves to the next row and returns <code>false</code> when there are no more.</details>

**7.** Why do we open connections inside **try-with-resources**?

<details><summary>Answer</summary>It <b>closes</b> the <code>Connection</code>, <code>PreparedStatement</code>, and <code>ResultSet</code> automatically, even if an error happens. Connections left open slowly use up the database and crash the app.</details>

**8.** What is a **DAO**?

<details><summary>Answer</summary>A <b>Data Access Object</b>: a class that holds <b>all the SQL</b> for one kind of data (for example <code>MessageDao</code> with <code>save()</code> and <code>findAll()</code>). Servlets call the DAO instead of writing SQL themselves.</details>

**9.** What is a **listener**, and name the two methods of `ServletContextListener`.

<details><summary>Answer</summary>A class the container calls <b>automatically when an event happens</b>. <code>ServletContextListener</code> has <code>contextInitialized()</code> (the app has started) and <code>contextDestroyed()</code> (the app is stopping).</details>

**10.** Which listener would you use to count how many users are online, and which methods does it have?

<details><summary>Answer</summary><code>HttpSessionListener</code>, with <code>sessionCreated()</code> (add 1) and <code>sessionDestroyed()</code> (subtract 1). Mark the class with <code>@WebListener</code>.</details>
