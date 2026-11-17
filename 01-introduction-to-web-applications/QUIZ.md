# QUIZ 01: Introduction To Web Applications

Answer first, **then** open the answer.

## Part A: Quick check (choose one)

**1.** Which side always speaks first in a web conversation?
a) Server  b) Client  c) Whichever is faster  d) The database

<details><summary>Answer</summary>b) <b>Client</b>. A server only answers. It never starts the conversation.</details>

**2.** In `http://localhost:8080/shop/cart?item=pen&qty=2`, what is the **path**?
a) `localhost`  b) `8080`  c) `/shop/cart`  d) `item=pen&qty=2`

<details><summary>Answer</summary>c) <code>/shop/cart</code>. <code>item=pen&amp;qty=2</code> is the query string, and it holds two parameters.</details>

**3.** A user submits a login form. Which method should the form use?
a) GET  b) POST

<details><summary>Answer</summary>b) <b>POST</b>. Otherwise the password would appear in the URL, the browser history, and server logs.</details>

**4.** You visit a page and get `500`. Whose fault is it?
a) The user's (client)  b) The server's

<details><summary>Answer</summary>b) <b>The server's</b>. 5xx means the server failed. 4xx means the client asked for something wrong.</details>

**5.** Which header tells the browser that the body is a web page?
a) `Host`  b) `Content-Length`  c) `Content-Type: text/html`  d) `User-Agent`

<details><summary>Answer</summary>c) <code>Content-Type: text/html</code></details>

**6.** The page `/time` shows a different time on every refresh. It is...
a) static  b) dynamic

<details><summary>Answer</summary>b) <b>dynamic</b>. It is built fresh by a program for every request.</details>

## Part B: Explain in your own words

**7.** Using the restaurant picture, explain what a **request**, a **response**, and **HTTP** are.

<details><summary>Model answer</summary>The request is the order slip the customer (browser) hands to the kitchen (server). The response is the plate of food the kitchen sends back. HTTP is the agreed format of the order slip and the plate label, so any customer can order from any kitchen.</details>

**8.** What does "HTTP is stateless" mean, and why is it a problem for an online shop?

<details><summary>Model answer</summary>The server forgets you after every request. Every new click looks like it came from a stranger. A shop needs to remember what is in <i>your</i> cart from one click to the next, so we need extra tools for that (sessions, in topic 05).</details>

**9.** Name the **four parts** of an HTTP response, in order.

<details><summary>Answer</summary>1. Status line (e.g. <code>HTTP/1.1 200 OK</code>), 2. headers, 3. a blank line, 4. the body.</details>

**10.** Why would a developer NOT want to build every web app the way `MiniWebApp.java` does?

<details><summary>Model answer</summary>You'd have to write all the plumbing yourself: reading and decoding requests, matching paths, writing headers and status codes, handling many users and errors. That is a lot of repeated, error-prone work that has nothing to do with the app's real purpose. A framework or container (Jakarta EE) does it for you.</details>

## Part C: Hands-on tasks

**11.** Run `example/RawHttpServer.java`, open the home page, and find the `User-Agent` header in the terminal. What browser does it say you are using?

**12.** In the example, submit the **GET** form and then the **POST** form. Write down **where** the data appeared each time (request line or body).

<details><summary>Answer</summary>GET: inside the request line, after <code>?</code>, e.g. <code>GET /search?q=hello HTTP/1.1</code>. POST: in the body, after the blank line, e.g. <code>username=ada&amp;password=123</code>.</details>

**13.** In `project/MiniWebApp.java`, add a new page `/about` that shows your name and school. Is it static or dynamic?
*Hint:* copy the `time` method, rename it, and register it with `server.createContext("/about", MiniWebApp::about);`

**14. Challenge:** add a page `/square?n=7` that answers "7 squared is 49". What status code should you return if `n` is missing or isn't a number?

<details><summary>Hint</summary>Use <code>getParameter(query, "n")</code> and <code>Integer.parseInt</code> inside a <code>try/catch (NumberFormatException e)</code>. Return <b>400 Bad Request</b> when the input is wrong, because it's the client's mistake.</details>
