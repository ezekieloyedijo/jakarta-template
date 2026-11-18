# Topic 13: Securing Java Web Applications

## The problem that existed

Once an app is on the internet, **anyone** can send it requests, including people trying to break in.
Early web apps were full of holes:

- Admin pages that anyone could open, just by guessing the URL.
- Passwords stored as plain text, so one stolen database exposed every user.
- Search boxes where typing a bit of SQL (`' OR '1'='1`) logged you in as anyone (**SQL injection**).
- Comment boxes where typing `<script>` ran code in other visitors' browsers (**XSS**, cross-site scripting).

🏦 Think of a **bank**. It doesn't just have a front door. It has:

- a guard who checks **who you are** (your ID): **authentication**,
- rules about **where you may go** (customers at the counter, staff in the back, managers in the vault): **authorization** and **roles**,
- an **armoured van** for moving money: **HTTPS**,
- and staff trained not to be **tricked** (by fake forms or fake notes): protection against **attacks**.

A secure web app needs all of these, and Jakarta EE gives you the tools.

## The end goal of this topic

You should be able to:

1. Explain the difference between **authentication** and **authorization**.
2. Protect pages by **role** using `web.xml` (security constraints and a login page).
3. Read who is logged in with `request.getRemoteUser()` and `request.isUserInRole(...)`.
4. Explain and prevent **SQL injection** and **XSS**.
5. Store passwords safely with **hashing and salt**, never as plain text.
6. Explain why apps must use **HTTPS**.

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Why security matters** | The problem we are solving |
| 2 | **Authentication vs authorization** | The two questions: *who are you?* and *what may you do?* |
| 3 | **Users and roles** | Groups of permissions (e.g. `student`, `admin`) |
| 4 | **Declarative security in `web.xml`** | `security-constraint`, `auth-constraint`, `login-config` |
| 5 | **BASIC vs FORM login** | Two ways to ask for a password |
| 6 | **Checking the user in code** | `getRemoteUser()`, `isUserInRole()`, `logout()` |
| 7 | **HTTPS** | Protecting data on its way across the network |
| 8 | **Attack 1: SQL injection** | And why PreparedStatement (topic 08) stops it |
| 9 | **Attack 2: XSS** | And why escaping (`<c:out>`, topic 12) stops it |
| 10 | **Storing passwords: hashing and salt** | Never store a real password |

## What is inside this folder

| Path | What it will show |
|---|---|
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: a BASIC-protected page, a FORM login, role checks, SQL injection and XSS shown and fixed |
| [project/](project/) | **School Portal**: student pages and admin pages protected by role, with a login form |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is the difference between **authentication** and **authorization**?

<details><summary>Answer</summary><b>Authentication</b> = checking <b>who you are</b> (login with username and password). <b>Authorization</b> = checking <b>what you are allowed to do</b> (for example, only admins may open <code>/admin</code>).</details>

**2.** What is a **role**?

<details><summary>Answer</summary>A <b>named group of permissions</b>, such as <code>student</code> or <code>admin</code>. You give roles to users, and protect pages by role instead of by individual person.</details>

**3.** Which `web.xml` element protects a group of URLs, and which inner element says which roles may enter?

<details><summary>Answer</summary><code>&lt;security-constraint&gt;</code> (with a <code>&lt;url-pattern&gt;</code> inside <code>&lt;web-resource-collection&gt;</code>), and <code>&lt;auth-constraint&gt;</code> containing <code>&lt;role-name&gt;</code>.</details>

**4.** What is the difference between **BASIC** and **FORM** login?

<details><summary>Answer</summary><b>BASIC</b>: the <b>browser</b> shows its own grey pop-up box asking for username and password. <b>FORM</b>: <b>your own HTML login page</b> is shown, which looks nicer and matches your site.</details>

**5.** In a FORM login page, what must the form's `action` and its two field names be?

<details><summary>Answer</summary><code>action="j_security_check"</code>, with fields named <code>j_username</code> and <code>j_password</code>. The form uses <code>method="post"</code>.</details>

**6.** What is the difference between status codes **401** and **403**?

<details><summary>Answer</summary><b>401 Unauthorized</b>: "I don't know who you are, please log in." <b>403 Forbidden</b>: "I know who you are, but you are <b>not allowed</b> here."</details>

**7.** Which methods tell you in code who is logged in and whether they are an admin?

<details><summary>Answer</summary><code>request.getRemoteUser()</code> returns the username (or <code>null</code>). <code>request.isUserInRole("admin")</code> returns <code>true</code> or <code>false</code>.</details>

**8.** What is **SQL injection**, and how do you prevent it?

<details><summary>Answer</summary>An attack where the user types <b>SQL code</b> into an input box, and the app pastes it into its own SQL so it runs as a command. Prevent it by <b>always using <code>PreparedStatement</code> with <code>?</code> placeholders</b>, never by joining strings.</details>

**9.** What is **XSS** (cross-site scripting), and how do you prevent it?

<details><summary>Answer</summary>An attack where the user types <b>HTML or JavaScript</b> (for example <code>&lt;script&gt;</code>) that is then shown to other visitors and <b>runs in their browsers</b>. Prevent it by <b>escaping</b> user text before showing it, for example with <code>&lt;c:out&gt;</code>.</details>

**10.** Why must passwords be **hashed** (with a salt) and not stored as plain text?

<details><summary>Answer</summary>If the database is stolen, plain passwords are exposed immediately, and people reuse passwords on other sites. A <b>hash</b> is a one-way scramble that cannot be turned back into the password. A <b>salt</b> (random extra text per user) means two identical passwords give different hashes, so attackers can't use ready-made lookup tables. Use a slow algorithm made for passwords, such as <b>PBKDF2</b> or <b>bcrypt</b>.</details>
