# QUIZ 08: Database Access and Event Handling

## Part A: Quick check

**1.** Which SQL command goes with each CRUD letter?

<details><summary>Answer</summary>Create = <code>INSERT</code>, Read = <code>SELECT</code>, Update = <code>UPDATE</code>, Delete = <code>DELETE</code>.</details>

**2.** Which `PreparedStatement` method runs a `SELECT`, and which runs a `DELETE`?

<details><summary>Answer</summary><code>executeQuery()</code> for <code>SELECT</code> (returns a <code>ResultSet</code>). <code>executeUpdate()</code> for <code>DELETE</code> (returns the number of rows changed).</details>

**3.** In `ps.setString(1, title)`, what does the `1` mean?

<details><summary>Answer</summary>The <b>first</b> <code>?</code> in the SQL. Counting starts at 1, not 0.</details>

**4.** A `ResultSet` has just been returned. Can you call `rs.getString("title")` straight away?

<details><summary>Answer</summary><b>No.</b> The cursor starts <i>before</i> the first row. Call <code>rs.next()</code> first.</details>

**5.** What does try-with-resources guarantee?

<details><summary>Answer</summary>Every resource opened in the brackets (connection, statement, result set) is <b>closed</b> at the end of the block, even when an exception happens.</details>

**6.** Which listener interface and method run when the app starts?

<details><summary>Answer</summary><code>ServletContextListener</code>, method <code>contextInitialized</code>.</details>

**7.** In the library, `BookDao.delete(id)` returns `ps.executeUpdate() == 1`. When is that `false`?

<details><summary>Answer</summary>When no row has that id (0 rows were deleted). The servlet then answers 404.</details>

## Part B: Explain in your own words

**8.** Explain SQL injection with an example, and say how a `PreparedStatement` stops it.

<details><summary>Model answer</summary>If you build SQL by joining strings, <code>"... WHERE name = '" + name + "'"</code>, a user can type <code>' OR '1'='1</code> and the command becomes "every row". A PreparedStatement sends the command with <code>?</code> marks and the values separately, so the database treats the value only as data, never as part of the command.</details>

**9.** What is a DAO, and why do we use one?

<details><summary>Model answer</summary>A Data Access Object is one class that holds all the SQL for one table. Servlets call simple methods like <code>findAll()</code>. The SQL is in one place, the servlets stay short, and changing the database only means changing the DAO.</details>

**10.** Why is the table created in a listener and not in the first servlet that needs it?

<details><summary>Model answer</summary>A listener runs once when the app starts, before any request, so the table is always ready. Otherwise every servlet would have to check "does the table exist yet?", and it would depend on which page a user happened to open first.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Prove the data is permanent: add a book, stop Tomcat (Ctrl + C), start it again, and open `/books`.

<details><summary>Answer</summary>The book is still there, because it was saved in the file <code>~/jakarta-course-data/library.mv.db</code>. (In topic 04's School Management app, it would be gone.)</details>

**12.** Add a **search** box: `GET /books?q=achebe` shows only books whose title or author contains the text.

<details><summary>Hint</summary>Add <code>search(String text)</code> to <code>BookDao</code> with <code>SELECT * FROM book WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? ORDER BY title</code>, and fill both <code>?</code> with <code>"%" + text.toLowerCase() + "%"</code>. Never join the text into the SQL string.</details>

**13.** Add a column `shelf VARCHAR(10)` (like "B3") to the table and the form.

<details><summary>Hint</summary>Because the table already exists, <code>CREATE TABLE IF NOT EXISTS</code> won't change it. Either delete the database file (you lose your books), or add <code>ALTER TABLE book ADD COLUMN IF NOT EXISTS shelf VARCHAR(10) DEFAULT ''</code> in <code>createTableIfMissing</code>. Then update <code>Book</code>, <code>BookForm</code>, every SQL in <code>BookDao</code>, and <code>Page</code>.</details>

**14.** Write a `ServletRequestListener` that prints `[REQUEST] started` and `[REQUEST] ended` for every request. Which order do you see when you also have a filter?

<details><summary>Hint</summary><code>@WebListener public class RequestLog implements ServletRequestListener</code> with <code>requestInitialized</code> and <code>requestDestroyed</code>. The listener's "started" comes before every filter, and "ended" comes after everything has finished.</details>
