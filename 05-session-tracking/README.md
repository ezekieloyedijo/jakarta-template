# Topic 05: Session Tracking

## The problem that existed

In topic 01 you learned that **HTTP is stateless**: the server forgets you after every request.
That is fine for reading a news page, but it breaks almost every real app:

- You log in, click "My Account"... and the server asks "who are you?" again.
- You add shoes to a shopping cart, open the next page... and the cart is empty.

🏋️ It's like a gym receptionist with **no memory at all**. Every time you walk through the door, even
five minutes after your last visit, they ask for your name, your membership, and your payment again.

The fix is a **membership card**. The first time you arrive, the gym gives you a card with a number.
After that you just **show your card**, and the receptionist looks up your file.
On the web, that card is usually a **cookie** holding a **session ID**, and the file kept at the desk is the **session**.

## The end goal of this topic

You should be able to:

1. Explain **why** a server needs extra help to remember users.
2. Name the four classic techniques: **hidden form fields**, **URL rewriting**, **cookies**, and **HttpSession**.
3. Create, read, and delete **cookies**.
4. Store and read data in an **HttpSession**, and end a session (log out).

## The learning path (read the steps in this order)

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Recap: HTTP is stateless** | The problem we are solving |
| 2 | **What "tracking a session" means** | Name the goal before the tools |
| 3 | **Hidden form fields** | The simplest trick: carry the data inside each form |
| 4 | **URL rewriting** | The same idea for links: carry the data in the URL |
| 5 | **Cookies** | Let the **browser** carry small data for you automatically |
| 6 | **HttpSession** | Keep the data on the **server**; the browser carries only an ID (a cookie called `JSESSIONID`) |
| 7 | **Session timeout and logout (`invalidate`)** | Sessions must end: when the user is idle or clicks log out |
| 8 | **Choosing the right technique** | Comparing all four |

## What is inside this folder

| Path | What it will show |
|---|---|
| `LESSON.md` | The full lesson (being written) |
| [example/](example/) | Small demos: a visit counter with a cookie, a visit counter with a session, hidden fields, URL rewriting |
| [project/](project/) | **Shopping Cart**: add items, view the cart, and empty it, with the cart stored in the session |
| `QUIZ.md` | More questions and tasks (being written) |

Run any app with `cd example` (or `cd project`) then `mvn package cargo:run`.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** Why does a web app need session tracking at all?

<details><summary>Answer</summary>Because <b>HTTP is stateless</b>: the server forgets the user after each request. Without session tracking, logins and shopping carts could not work.</details>

**2.** What is a **session**?

<details><summary>Answer</summary>A series of requests from the <b>same user</b> (same browser) over a period of time, which the server treats as <b>one conversation</b> and can store data for.</details>

**3.** Name the four session-tracking techniques.

<details><summary>Answer</summary><b>Hidden form fields</b>, <b>URL rewriting</b>, <b>cookies</b>, and <b>HttpSession</b>.</details>

**4.** What is a **cookie**, and where is it stored?

<details><summary>Answer</summary>A small piece of text (a name and a value) that the server asks the browser to keep. It is stored <b>in the browser</b>, and the browser sends it back with every request to that server.</details>

**5.** Write the code to create a cookie `theme=dark` that lasts one hour, and send it to the browser.

<details><summary>Answer</summary><pre>Cookie c = new Cookie("theme", "dark");
c.setMaxAge(60 * 60);      // in seconds
response.addCookie(c);</pre></details>

**6.** How do you **delete** a cookie?

<details><summary>Answer</summary>Send a cookie with the <b>same name</b> and <code>setMaxAge(0)</code>. The browser then throws it away.</details>

**7.** Where is the data of an `HttpSession` stored: in the browser or on the server?

<details><summary>Answer</summary><b>On the server.</b> The browser only holds the session's <b>ID</b>, in a cookie called <code>JSESSIONID</code>.</details>

**8.** Write the code to save a username in the session and read it back later.

<details><summary>Answer</summary><pre>HttpSession session = request.getSession();
session.setAttribute("username", "ada");
// later, in another request:
String name = (String) session.getAttribute("username");</pre></details>

**9.** Which method ends a session, for example when the user logs out?

<details><summary>Answer</summary><code>session.invalidate()</code>. All data in that session is thrown away.</details>

**10.** What happens to a session when the user does nothing for a long time?

<details><summary>Answer</summary>It <b>times out</b> and the server deletes it. On Tomcat the default is <b>30 minutes</b>. You can change it with <code>session.setMaxInactiveInterval(seconds)</code> or in <code>web.xml</code>.</details>
