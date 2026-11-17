# LESSON 01: Introduction To Web Applications

> **The big picture we will use for the whole lesson: A RESTAURANT**
>
> - **You (the customer)** = the **browser** (Chrome, Edge, your phone)
> - **The kitchen** = the **server** (a computer somewhere that runs the program)
> - **The address of the restaurant + the item on the menu** = the **URL**
> - **The order slip** you hand over = the **HTTP request**
> - **The plate of food** that comes back = the **HTTP response**
> - **The language on the order slip** that both you and the kitchen understand = **HTTP**
>
> Keep this picture in your head. Every step below adds one more detail to it.

---

## Step 1: Client and Server

A web conversation always has **two sides**:

| Side | Job | Example |
|---|---|---|
| **Client** | **Asks** for something | Your browser, a mobile app |
| **Server** | **Waits**, receives the question, and **answers** | A computer at Google, or *your own laptop* when you run our examples |

🍽️ **In the restaurant:** the customer never walks into the kitchen to cook. They **ask**, and the kitchen **prepares and sends back**.
The kitchen can serve **many customers at the same time**, and one server serves many browsers.

**Key idea:** the client always speaks **first**. A server never "calls" a browser out of nowhere. It only **answers**.

> 💡 "Server" means two things, and both are correct: (1) the **computer**, and (2) the **program** on it that listens for requests.
> In this course, when you run `java RawHttpServer.java`, **your laptop becomes the server** and your browser is the client. Both are on the same machine, and that is fine.

---

## Step 2: The URL (the address)

The client must say **where** to go and **what** it wants. That's the URL:

```
   http://localhost:8080/greet?name=Amaka
   └─┬─┘  └───┬───┘ └─┬┘ └─┬──┘└────┬────┘
 protocol   host    port  path    query string
```

| Part | Meaning | 🍽️ Restaurant |
|---|---|---|
| **protocol** `http` | The language/rules to use | "We will talk in English" |
| **host** `localhost` | Which computer. `localhost` means *"this same computer"* | The restaurant's street address |
| **port** `8080` | Which "door" on that computer. One computer can run many server programs, and each listens at its own numbered door | Which counter: the pizza counter at door 8080, drinks at door 3000 |
| **path** `/greet` | Which thing you want from that server | The item on the menu |
| **query string** `?name=Amaka` | Extra details, written as `key=value` pairs and joined with `&` | "...with extra pepper, no onions" |

The defaults are port **80** for `http` and **443** for `https`. That's why you never type a port for google.com.

---

## Step 3: HTTP (the shared language)

**HTTP = HyperText Transfer Protocol.** A *protocol* is simply **an agreed set of rules for a conversation**.

🍽️ If the customer writes an order in Chinese and the kitchen only reads Yoruba, no food comes out.
So the restaurant agrees on a **standard order slip format**: *item first, then extras, then table number.*
HTTP is that standard slip format for the web. Every browser and every server in the world follows it, which is why
**any** browser can talk to **any** web server.

The surprising part is that **HTTP is plain text**. You could type it by hand. In the `example/` you'll see real HTTP messages printed out.

---

## Step 4: The HTTP Request (the order slip)

When you visit `http://localhost:8080/greet?name=Amaka`, the browser sends text like this:

```http
GET /greet?name=Amaka HTTP/1.1          <- 1. REQUEST LINE: method, path, version
Host: localhost:8080                    <- 2. HEADERS: extra information, "Name: value"
User-Agent: Mozilla/5.0 (Windows...)        (who is asking, which languages they accept, etc.)
Accept: text/html
Accept-Language: en-US
                                        <- 3. A BLANK LINE means "headers finished"
                                        <- 4. BODY (empty for GET; holds form data for POST)
```

| Part | 🍽️ Restaurant |
|---|---|
| Request line (`GET /greet`) | "**Bring me** the *greet* dish" |
| Headers | "Table 4. I'm allergic to nuts. I prefer English menus." |
| Body | A separate sealed note with private details (used by POST, see Step 6) |

---

## Step 5: The HTTP Response (the plate) and Status Codes

The server answers with the **same shape**:

```http
HTTP/1.1 200 OK                         <- 1. STATUS LINE: version, status code, short reason
Content-Type: text/html; charset=UTF-8  <- 2. HEADERS: what kind of content, how long, etc.
Content-Length: 46
                                        <- 3. BLANK LINE
<html><body><h1>Hello Amaka</h1></body></html>   <- 4. BODY: the actual content
```

The **status code** is a 3-digit number that tells the browser *how it went*. The first digit is the family:

| Family | Meaning | Common ones | 🍽️ Restaurant |
|---|---|---|---|
| **2xx** | Success | **200 OK** | "Here's your food" |
| **3xx** | Go somewhere else | **302 Found** (redirect) | "We moved that dish to the counter next door, please go there" |
| **4xx** | **You** (the client) made a mistake | **404 Not Found**, **401 Unauthorized**, **403 Forbidden** | "That dish isn't on our menu" / "Members only" |
| **5xx** | **The server** made a mistake | **500 Internal Server Error** | "Sorry, the kitchen caught fire" |

**`Content-Type`** tells the browser **what is in the body**, so it knows how to show it:
`text/html` (a web page), `text/plain` (plain text), `image/png` (a picture), `application/json` (data for programs).

---

## Step 6: GET vs POST (two kinds of orders)

The first word of the request line is the **method**. It tells the server what you **intend** to do.

| | **GET** | **POST** |
|---|---|---|
| Purpose | **Fetch/read** something | **Send/submit** something (create, change) |
| Where data goes | In the **URL**: `/search?q=shoes` | In the request **body**, hidden from the URL |
| Visible in address bar / history? | Yes | No |
| Can be bookmarked? | Yes | No |
| Use for | Searching, viewing a page, clicking a link | Login forms, registration, payments, uploading |

🍽️ **GET** is like **shouting your order across the room**: quick and simple, but everybody hears it.
**POST** is like **handing a sealed envelope** to the waiter: the details are inside, not on the outside.

> ⚠️ POST hides data from the **address bar**, but it is **not encrypted** by itself. Only **HTTPS** encrypts (see topic 13).
> A sealed envelope can still be opened by a thief, and HTTPS is what puts it in a locked box.

Other methods exist (`PUT`, `DELETE`, `PATCH`), but browsers and HTML forms use only **GET** and **POST**.

---

## Step 7: HTML and Forms

The response body is usually **HTML**, the language that describes a page (headings, paragraphs, buttons).
The browser's job is to **read the HTML and draw it**.

An HTML **form** is how a user **puts data into a request**:

```html
<form action="/greet" method="get">      <!-- WHERE to send it, and HOW (GET or POST) -->
    <input type="text" name="name">       <!-- 'name' becomes the KEY in key=value -->
    <button type="submit">Say hello</button>
</form>
```

If the user types `Amaka` and clicks the button, the browser **builds the request for you**:

- with `method="get"`: `GET /greet?name=Amaka`
- with `method="post"`: `POST /greet` and the body `name=Amaka`

🍽️ The form is the **printed order slip with empty boxes**. The customer fills the boxes, and the waiter (browser) delivers the slip to the kitchen.

---

## Step 8: Static vs Dynamic content

Now that you know a response is "a body of HTML", ask yourself: **where does that HTML come from?**

| | **Static** | **Dynamic** |
|---|---|---|
| What it is | A **ready-made file** sitting on the server | HTML **built by a program** at the moment you ask |
| Same for everyone? | Yes, always identical | No, it can depend on who you are, the time, the data |
| Example | `about.html`, `logo.png`, `style.css` | "Hello **Amaka**, you have **3** new messages", the current time, search results |
| 🍽️ Restaurant | A **pre-packed sandwich** in the fridge: grab and hand it over | A meal **cooked to order** with your choices |

---

## Step 9: So what is a Web Application?

Put Steps 1 to 8 together:

> A **web application** is a **program running on a server** that **receives HTTP requests**,
> **does some work** (reads input, does calculations, talks to a database), and **builds a dynamic HTTP response**,
> usually an HTML page, for the browser to show.

```
 BROWSER (client)                                   SERVER
 ───────────────                                    ──────────────────────────────
 1. User types URL or submits form
 2. Browser builds HTTP REQUEST  ──────────────────► 3. Server program receives it
                                                     4. Reads the input (path, query, form data)
                                                     5. Does the work (calculate, database...)
                                                     6. Builds HTML
 8. Browser draws the HTML       ◄────────────────── 7. Sends HTTP RESPONSE (status + headers + body)
```

Gmail, Facebook, your school portal, and online banking are all web applications.

**Websites vs web applications:** a mostly *static* site (like a brochure) is usually called a **website**.
When it *does things for you* (login, save, calculate, search) we call it a **web application**.

---

## Step 10: HTTP is stateless (the server forgets you)

Here is a surprising fact: **each request is completely independent.** After the server sends the response,
it **forgets** the conversation. The next request from you looks to the server like it came from a total stranger.

🍽️ Imagine a waiter with **zero memory**. You order rice and he brings it. Five minutes later you say
*"and a drink to go with my meal"*, and he replies *"Who are you? What meal?"*

That's a big problem for things like shopping carts and logins (*"I logged in one click ago, remember me!"*).
**Topic 05 (Session Tracking)** is entirely about solving this. For now, just remember: **HTTP itself has no memory.**

---

## Step 11: Where Java and Jakarta fit

To make a web app with **only** plain Java (look at `project/MiniWebApp.java`), *you* must do all of this by hand:

- open a network port and listen
- read the raw request text and split it into method, path, and headers
- decode the query string (`name=Amaka%20Obi` becomes `Amaka Obi`)
- work out which code should handle which path
- write the status line and headers correctly, in the correct format
- handle many users at once, and handle errors...

It is like **opening a restaurant where the chef must also build the building, wash every plate,
seat every guest, and take every payment**. The chef has almost no time left to **cook** (your real business logic).

**Jakarta EE** gives you a ready-made restaurant building with staff (a *container*), so you only write the
**recipe**: *"when someone asks for /greet, say hello to them."* That's **topic 02**.

---

## Now try it

### 1. See HTTP with your own eyes (`example/`)

```bash
cd 01-introduction-to-web-applications/example
java RawHttpServer.java
```

1. Open <http://localhost:8080/> and **look at the terminal**: you'll see the **raw request** your browser sent.
2. Notice a second request for `/favicon.ico`. The browser asks for the little tab icon **by itself**, and our server answers **404**.
3. Fill in the **GET** form and see the data appear **in the request line**.
4. Fill in the **POST** form and see the data appear **in the body**, after the blank line.
5. Try <http://localhost:8080/anything/you/like?color=blue&size=10>
6. Stop with **Ctrl + C**.

### 2. Build a tiny web app by hand (`project/`)

```bash
cd 01-introduction-to-web-applications/project
java MiniWebApp.java
```

| URL | Static or dynamic? |
|---|---|
| <http://localhost:8080/> | Static (same HTML every time) |
| <http://localhost:8080/time> | **Dynamic**: refresh it and the time changes |
| <http://localhost:8080/greet?name=Tolu> | **Dynamic**: depends on your input |
| <http://localhost:8080/nothing-here> | **404** (custom not-found page) |

Read the code and notice how much "plumbing" we wrote. Keep that feeling for topic 02.

Then go to **[QUIZ.md](QUIZ.md)**.
