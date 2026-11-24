# LESSON 08: Database Access and Event Handling

> **The big picture for this lesson: THE SCHOOL RECORDS OFFICE**
>
> - **A whiteboard** that is wiped every night = memory (`ServletContext`, `HttpSession`): gone when Tomcat stops
> - **The filing cabinet** = a **database**. A **drawer** = a **table**. One **card** in the drawer = a **row**
> - **The form you hand the clerk** ("find every card where class = SS1") = **SQL**
> - **The clerk who speaks to the cabinet for you** = **JDBC** (and the **driver** is the clerk's key to this brand of cabinet)
> - **A form with blank boxes the clerk fills in carefully** = a **PreparedStatement** with `?` marks
> - **The one office worker who handles all records** = the **DAO**
> - **The caretaker who unlocks the building at 7 am and locks it at 6 pm** = a **listener**

---

## Step 1: Recap, memory is not permanent

In topics 04 to 07 every app kept its data in **memory** (a `Map` in the `ServletContext` or the session).
Stop Tomcat, start it again, and **everything is gone**. Real apps need data that survives restarts, power cuts,
and new versions of the app. That is the job of a **database**.

---

## Step 2: Database, table, row, SQL

| Word | Meaning | In the library project |
|---|---|---|
| **Database** | A program that stores data safely on disk | H2, in the file `library.mv.db` |
| **Table** | One kind of thing, with fixed columns (like a spreadsheet) | `book` |
| **Column** | One piece of information, with a type | `title VARCHAR(100)`, `copies INT` |
| **Row** | One thing | *Things Fall Apart, Chinua Achebe, 1958, 4* |
| **Primary key** | A column that is unique for every row | `id` (the database numbers them: `AUTO_INCREMENT`) |
| **SQL** | The language for asking the database | `SELECT * FROM book` |

The four SQL commands match the four letters of **CRUD**:

| CRUD | SQL | Example |
|---|---|---|
| **C**reate | `INSERT` | `INSERT INTO book (title, author, published, copies) VALUES ('Arrow of God', 'Achebe', 1964, 2)` |
| **R**ead | `SELECT` | `SELECT * FROM book WHERE id = 3` |
| **U**pdate | `UPDATE` | `UPDATE book SET copies = 5 WHERE id = 3` |
| **D**elete | `DELETE` | `DELETE FROM book WHERE id = 3` |

⚠️ An `UPDATE` or `DELETE` **without** `WHERE` changes **every row**.

---

## Step 3: JDBC and the H2 database

**JDBC** (Java Database Connectivity) is the standard Java API for talking to **any** database: H2, MySQL,
PostgreSQL, Oracle... Your code only uses JDBC's interfaces (`Connection`, `PreparedStatement`, `ResultSet`).
The **driver**, a `.jar` file from the database maker, does the real talking.

```
Your servlet ──▶ JDBC (java.sql.*) ──▶ H2 driver ──▶ the database file
                 same for every DB     changes per DB
```

We use **H2** because it is written in Java and runs **inside** your app: no installation, no server. Add it to `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.3.232</version>
</dependency>
```

Maven puts the jar in `WEB-INF/lib`, so it travels inside the WAR. Switching to MySQL later means changing this
dependency and the URL; the JDBC code stays almost the same.

---

## Step 4: Connecting, `DriverManager.getConnection`

```java
String url = "jdbc:h2:~/jakarta-course-data/library";   // which database
Connection con = DriverManager.getConnection(url, "sa", "");   // user "sa", empty password
```

| URL | Meaning |
|---|---|
| `jdbc:h2:mem:fruitshop;DB_CLOSE_DELAY=-1` | In **memory**: fast, but empty after every restart (the example) |
| `jdbc:h2:~/jakarta-course-data/library` | A **file** in your home folder: survives restarts (the project) |
| `jdbc:mysql://localhost:3306/school` | A MySQL server (for later) |

Inside Tomcat, call `Class.forName("org.h2.Driver")` once at startup, so the driver is loaded from `WEB-INF/lib`.

**Demo:** `example/` → `/connect`

---

## Step 5: Running SQL, `PreparedStatement`

```java
String sql = "INSERT INTO fruit (name, price) VALUES (?, ?)";   // ? = "a value goes here"
PreparedStatement ps = con.prepareStatement(sql);
ps.setString(1, name);         // fill the 1st ?  (counting starts at 1, not 0!)
ps.setInt(2, price);           // fill the 2nd ?
int rows = ps.executeUpdate(); // INSERT / UPDATE / DELETE -> how many rows changed
```

| Method | Use for | Returns |
|---|---|---|
| `executeQuery()` | `SELECT` | a `ResultSet` (the rows) |
| `executeUpdate()` | `INSERT`, `UPDATE`, `DELETE`, `CREATE TABLE` | the number of rows changed |

**Why `?` and never `+`?** Look at this:

```java
// ❌ NEVER: the user's text becomes part of the SQL command
"SELECT * FROM users WHERE name = '" + name + "'"
// if name is   ' OR '1'='1   the command becomes   ... WHERE name = '' OR '1'='1'   → every user!
```

With `?`, the value is sent **separately** from the command, so it can never change the command. This attack is
called **SQL injection** (topic 13). **Rule: every value from a user goes in through a `?`.**

**Demo:** `/fruits`. Add a fruit called `Kiwi'); DROP TABLE fruit; --` and watch it saved as plain text.

---

## Step 6: Reading results, `ResultSet`

A `ResultSet` is like a **cursor** that starts **above** the first row. `next()` moves it down one row and returns
`false` when there are no more.

```java
ResultSet rs = ps.executeQuery();       // SELECT id, title FROM book
while (rs.next()) {                      // for each row...
    int id = rs.getInt("id");            // read a column by name
    String title = rs.getString("title");
    books.add(new Book(id, title, ...)); // turn the row into a Java object
}
```

For one row (`WHERE id = ?`): `if (rs.next()) { ... found ... } else { ... not found ... }`.

---

## Step 7: Closing resources, try-with-resources

Every `Connection`, `PreparedStatement`, and `ResultSet` holds something real (a file lock, memory, a network socket).
If you don't **close** them, they pile up until the database refuses new connections and the app stops working.

**try-with-resources** closes them for you, in reverse order, **even if an exception happens**:

```java
try (Connection con = connect();
     PreparedStatement ps = con.prepareStatement("SELECT * FROM book");
     ResultSet rs = ps.executeQuery()) {
    ...
}   // ← rs, ps, con are all closed here, automatically
```

📚 Like a library rule: "every book you take out is returned when you leave", checked by the door, so nobody can forget.

---

## Step 8: The DAO pattern

Putting SQL inside servlets mixes two jobs (web and database) and repeats code. The **DAO** (Data Access Object)
is **one class that does all the SQL** for one table:

```java
public class BookDao {
    public int insert(String title, String author, int year, int copies) { ... INSERT ... }
    public List<Book> findAll()                                          { ... SELECT ... }
    public Book findById(int id)                                         { ... SELECT ... WHERE id = ? }
    public boolean update(int id, ...)                                   { ... UPDATE ... }
    public boolean delete(int id)                                        { ... DELETE ... }
}
```

The servlet now reads like plain English:

```java
List<Book> books = BookDao.from(getServletContext()).findAll();
```

| Without a DAO | With a DAO |
|---|---|
| SQL spread over many servlets | All SQL for `book` in one file |
| Change a column → edit many servlets | Change a column → edit one class |
| Servlets need try/catch for `SQLException` | The DAO turns it into one unchecked `DataException` (→ 500 page) |

---

## Step 9: Events and listeners

Some code must run when **no request** is happening: when the app **starts** (create the tables), when it **stops**
(clean up), or when a **session** begins or ends. Servlets can't do that, because they only run for requests.

A **listener** is a class that Tomcat calls when an **event** happens. You choose events by implementing an interface,
and register it with **`@WebListener`** (or `<listener>` in `web.xml`).

| Interface | Events (methods) |
|---|---|
| `ServletContextListener` | the app starts (`contextInitialized`), the app stops (`contextDestroyed`) |
| `HttpSessionListener` | a session is created (`sessionCreated`), destroyed (`sessionDestroyed`) |
| `ServletRequestListener` | a request begins / ends |
| `HttpSessionAttributeListener` | something is added to / removed from a session |

🔑 The caretaker: they don't teach any class, but they open the building before anyone arrives and lock it after everyone leaves.

---

## Step 10: `ServletContextListener` and `HttpSessionListener`

**Set up the database before the first visitor:**

```java
@WebListener
public class DatabaseListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext app = event.getServletContext();
        String url = app.getInitParameter("dbUrl");          // from <context-param> in web.xml
        BookDao dao = new BookDao(url);
        dao.createTableIfMissing();
        app.setAttribute("bookDao", dao);                    // every servlet can now use it
    }
}
```

Compare with topic 04, where `<load-on-startup>` made a servlet's `init()` prepare the store. A listener is the
proper place: it is not a servlet, and it always runs **before** any servlet or filter.

**Count the visitors:**

```java
@WebListener
public class ReadersOnlineListener implements HttpSessionListener {
    private static final AtomicInteger ONLINE = new AtomicInteger();
    public void sessionCreated(HttpSessionEvent e)   { ONLINE.incrementAndGet(); }
    public void sessionDestroyed(HttpSessionEvent e) { ONLINE.decrementAndGet(); }   // logout or timeout
}
```

**Demos:** `StartupListener` (look for `[STARTUP]` in the terminal) and `/online`.

---

## Now try it

The project in [project/](project/) is a **School Library**. Books are saved in an **H2 database file**, so they survive
a restart: add, list, edit, and delete books (CRUD with `INSERT`, `SELECT`, `UPDATE`, `DELETE`). All SQL lives in
`BookDao`; a `ServletContextListener` creates the table at startup; an `HttpSessionListener` counts the readers online.
The build steps are in the [README](README.md#how-to-build-the-project-step-by-step).
