# QUIZ 05: Session Tracking

## Part A: Quick check

**1.** What one line replaces a `<servlet>` and `<servlet-mapping>` pair for the URL `/shop`?

<details><summary>Answer</summary><code>@WebServlet("/shop")</code> on the servlet class.</details>

**2.** `request.getCookies()` can return something dangerous. What, and how do you protect your code?

<details><summary>Answer</summary>It returns <code>null</code> when the browser sent no cookies. Check <code>if (cookies != null)</code> before looping, or you get a <code>NullPointerException</code>.</details>

**3.** What is the difference between `request.getSession()` and `request.getSession(false)`?

<details><summary>Answer</summary><code>getSession()</code> finds the session <b>or creates a new one</b>. <code>getSession(false)</code> only finds it, and returns <code>null</code> if there is none.</details>

**4.** A cookie is created with no `setMaxAge(...)`. When is it deleted?

<details><summary>Answer</summary>When the browser is closed. It is a "session cookie".</details>

**5.** `<session-timeout>` is in which unit? And `setMaxInactiveInterval`?

<details><summary>Answer</summary><code>&lt;session-timeout&gt;</code> is in <b>minutes</b>. <code>setMaxInactiveInterval</code> is in <b>seconds</b>.</details>

**6.** You open the School Shop in Chrome and add 3 bags. Then you open it in a Chrome **private window**. How many items are in that cart? Why?

<details><summary>Answer</summary><b>Zero.</b> The private window doesn't have the first window's <code>JSESSIONID</code> cookie, so Tomcat gives it a new, empty session.</details>

**7.** Why must the `Cart` class implement `Serializable`?

<details><summary>Answer</summary>So Tomcat can save the session to disk (for example when it stops and starts again) and load it back. Objects that are not Serializable can't be saved.</details>

## Part B: Explain in your own words

**8.** Use the gym analogy to explain the difference between a **cookie** and an **HttpSession**.

<details><summary>Model answer</summary>A cookie is a card with all your details written on it, and <i>you</i> carry it, so you could change it. An HttpSession is a card with only a member number. Your real file stays in the gym office, where you can't change it. The receptionist reads the number and opens your file.</details>

**9.** Why should a password never be sent with URL rewriting?

<details><summary>Model answer</summary>The URL is shown in the address bar, saved in the browser history and server logs, and copied when someone shares the link. Anyone who sees it sees the password.</details>

**10.** The shop deletes a product with a POST form, not a link like `<a href="cart/remove?code=UNI-01">`. Why?

<details><summary>Model answer</summary>GET should only <b>read</b>. Links can be opened by accident, by browser pre-loading, or by search engines, and they would change the cart. POST is used for anything that changes data.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Show the customer's name. Add a small form on `/cart` that asks "Your name"; save it in the session as `customerName`, and show "Hello, NAME" in the header of every page.

<details><summary>Hint</summary>Make a <code>NameServlet</code> with <code>@WebServlet("/name")</code> and a <code>doPost</code> that calls <code>session.setAttribute("customerName", ...)</code> and redirects. In <code>Page.page</code>, read it with <code>request.getSession(false)</code> (it may be null!). Remember to <b>escape</b> the name.</details>

**12.** Remember the last product added, for 7 days, using a **cookie** named `lastProduct`. Show "Last time you added: ..." on `/shop`.

<details><summary>Hint</summary>In <code>AddToCartServlet</code>: <code>new Cookie("lastProduct", product.getCode())</code>, <code>setMaxAge(7 * 24 * 60 * 60)</code>, <code>addCookie</code>. In <code>ShopServlet</code>, loop over <code>getCookies()</code> and use <code>Catalog.find(value)</code>. A cookie can be edited by the user, so if <code>find</code> returns null, show nothing.</details>

**13.** Change the session timeout to 1 minute in `web.xml`, add items, wait 2 minutes, and refresh the cart. What happens? Change it back afterwards.

<details><summary>Answer</summary>The cart is empty. The session was idle for more than 1 minute, so Tomcat deleted it, and the next request got a new session.</details>

**14. Challenge:** add a "Save for later" list next to the cart: a second object in the session with move-to-cart and remove buttons.

<details><summary>Hint</summary>Copy the <code>Cart</code> idea into a <code>SavedList</code> class stored under a different attribute name, for example <code>"saved"</code>. Moving an item = <code>saved.remove(code)</code> then <code>cart.add(code, 1)</code>.</details>
