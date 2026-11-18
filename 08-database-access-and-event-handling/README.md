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
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: connect and query, PreparedStatement, a startup listener, a session counter |
| [project/](project/) | **Guestbook**: visitors leave messages that are saved in the database and survive a restart |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

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
