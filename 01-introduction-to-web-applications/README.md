# Topic 01: Introduction To Web Applications

## The problem that existed

Imagine it is the 1990s and your school wants a program to record student results.

**Option A: a desktop program.** Someone installs the program on **every** computer in the school.

- A bug is found, so someone walks to **all 200 computers** to install the fix.
- Results typed on Computer 5 are stored **only on Computer 5**. Computer 9 can't see them.
- A parent at home can't check their child's results at all.

**Option B: put web pages on the internet.** At first, web pages were just **fixed files** (HTML).
A page called `results.html` showed **the same thing to everyone**. It couldn't say *"Hello Amaka, here are YOUR results"*,
because it was like a printed poster: you can look at it, but it can't respond to you.

So people needed something new:

> **One program, living in one place (a server), that everybody reaches through a browser,
> and that builds a fresh, personal page for each person at the moment they ask.**

That "something" is a **web application**.

- Fix a bug **once** on the server and everybody gets the fix instantly.
- The data lives **in one place**, so everyone sees the same, up-to-date information.
- Anyone with a browser and internet can use it: phone, laptop, from home or school.

## The end goal of this topic

By the end you should be able to explain, in your own words, **exactly what happens between
typing a web address and seeing a page**, and to point at any part of that journey and name it:
client, server, URL, HTTP request, HTTP response, status code, GET, POST, static, dynamic.

You will also **see a real HTTP message with your own eyes** (the `example/`) and **build a tiny web
application with plain Java** (the `project/`). Doing it by hand shows you how much work it is,
and that is the reason topic 02 (Jakarta EE) exists.

## The learning path (read the steps in this order)

Each step leads to the next one.

| Step | Concept | Why it comes now |
|---|---|---|
| 1 | **Client and Server** | Before anything else: there are two sides, one that *asks* and one that *answers* |
| 2 | **The URL** | The client needs a way to say *which* server and *which* thing it wants. That is the address |
| 3 | **HTTP: the shared language** | Once the client knows the address, it needs a language both sides understand |
| 4 | **The HTTP Request** | The first half of the conversation: what exactly the client sends |
| 5 | **The HTTP Response and status codes** | The second half: what the server sends back, and how it says "OK" or "not found" |
| 6 | **GET vs POST** | Requests come in different kinds. The two you'll use 99% of the time |
| 7 | **HTML and Forms** | What the response usually contains, and how a user *sends data* inside a request |
| 8 | **Static vs Dynamic content** | Now you can see the difference between a fixed file and a page built fresh for you |
| 9 | **What a Web Application is** | Putting it together: a program on the server that builds dynamic responses |
| 10 | **HTTP is stateless** | A surprising fact: the server forgets you after every request (we fix this in topic 05) |
| 11 | **Where Java and Jakarta fit** | Doing all of this by hand is hard, and that leads into topic 02 |

## What is inside this folder

| Path | What it is | How to run |
|---|---|---|
| [LESSON.md](LESSON.md) | The full lesson, step by step | Read it |
| [example/RawHttpServer.java](example/RawHttpServer.java) | A "naked" server that **prints every HTTP request** it receives, so you can see the language | `cd example` then `java RawHttpServer.java` |
| [project/MiniWebApp.java](project/MiniWebApp.java) | A tiny web app with a home page, a live clock, and a greeting form, using **only plain Java** | `cd project` then `java MiniWebApp.java` |
| [QUIZ.md](QUIZ.md) | Test your understanding | Last |

> There's no Maven in this topic, only plain Java. `java SomeFile.java` compiles and runs a single file in one go (Java 11+).
> Open <http://localhost:8080/> after starting either one. Stop with **Ctrl + C**.

## Check your understanding: 10 basic questions

Try to answer each question out loud or on paper **before** you open the answer.

**1.** What is the difference between a **client** and a **server**?

<details><summary>Answer</summary>The <b>client</b> (usually a web browser) <b>asks</b> for something. The <b>server</b> is a program on another computer that <b>answers</b>. Restaurant: the customer orders, the kitchen cooks and serves.</details>

**2.** What does **HTTP** stand for, and what is it for?

<details><summary>Answer</summary><b>HyperText Transfer Protocol</b>. It is the shared <b>language</b> (set of rules) that browsers and servers use to talk to each other.</details>

**3.** Name the parts of this URL: `http://localhost:8080/greet?name=Ada`

<details><summary>Answer</summary><code>http</code> = protocol, <code>localhost</code> = host (which computer), <code>8080</code> = port (which door on that computer), <code>/greet</code> = path (which thing), <code>?name=Ada</code> = query string (extra data as name=value).</details>

**4.** What are the two halves of every HTTP conversation?

<details><summary>Answer</summary>The <b>request</b> (client → server: "please give me /greet") and the <b>response</b> (server → client: a status code, headers, and the content).</details>

**5.** What do the status codes **200**, **404**, and **500** mean?

<details><summary>Answer</summary><b>200</b> = OK, here is what you asked for. <b>404</b> = Not Found, that address does not exist. <b>500</b> = Internal Server Error, the server's own code crashed.</details>

**6.** What is the main difference between **GET** and **POST**?

<details><summary>Answer</summary><b>GET</b> asks to <b>read</b> something and puts any data in the URL (visible, can be bookmarked). <b>POST</b> <b>sends</b> data to be processed (for example a form) and puts it in the request <b>body</b>, not the URL.</details>

**7.** In an HTML form, what do the `action` and `method` attributes say?

<details><summary>Answer</summary><code>action</code> = <b>where</b> to send the data (the URL). <code>method</code> = <b>how</b> to send it (<code>get</code> or <code>post</code>).</details>

**8.** What is the difference between **static** and **dynamic** content?

<details><summary>Answer</summary><b>Static</b> content is a fixed file sent the same way to everyone (for example <code>about.html</code>). <b>Dynamic</b> content is built fresh by a program for each request (for example "Hello Ada, the time is 10:42").</details>

**9.** What is a **web application**?

<details><summary>Answer</summary>A program that runs on a <b>server</b>, receives HTTP requests, and builds <b>dynamic</b> responses that people use through a browser. Examples: online banking, a school portal.</details>

**10.** "HTTP is **stateless**." What does that mean?

<details><summary>Answer</summary>The server <b>forgets you after every request</b>. Each request is treated as if it came from a stranger. (Topic 05, Session Tracking, solves this.)</details>
