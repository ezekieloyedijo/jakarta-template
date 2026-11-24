# QUIZ 10: JSP Implicit Objects and Standard Actions

## Part A: Quick check

**1.** Name the 9 implicit objects.

<details><summary>Answer</summary><code>request</code>, <code>response</code>, <code>out</code>, <code>session</code>, <code>application</code>, <code>pageContext</code>, <code>config</code>, <code>page</code>, <code>exception</code>.</details>

**2.** In which pages does `exception` exist?

<details><summary>Answer</summary>Only in pages with <code>&lt;%@ page isErrorPage="true" %&gt;</code>.</details>

**3.** Put the four scopes in order, from shortest life to longest.

<details><summary>Answer</summary>page → request → session → application.</details>

**4.** `application` in a JSP is the same object as what in a servlet?

<details><summary>Answer</summary><code>getServletContext()</code>, the <code>ServletContext</code>.</details>

**5.** What three rules make a class a JavaBean?

<details><summary>Answer</summary>A public no-argument constructor, private fields, and public getters/setters (<code>getX</code>/<code>setX</code>, or <code>isX</code> for a boolean).</details>

**6.** What does `<jsp:setProperty name="contact" property="*"/>` do?

<details><summary>Answer</summary>For every request parameter whose name matches a property of the bean, it calls the setter: <code>name</code> → <code>setName(...)</code>, <code>phone</code> → <code>setPhone(...)</code>, and so on. Text is converted to numbers when needed; empty values are skipped.</details>

**7.** `<jsp:useBean id="book" class="...ContactBook" scope="application"/>` runs on 500 page views. How many `ContactBook` objects are created?

<details><summary>Answer</summary><b>One.</b> The first time, it is created and stored in the application scope; after that it is found there.</details>

## Part B: Explain in your own words

**8.** What is the difference between `<%@ include file="x.jspf" %>` and `<jsp:include page="x.jsp"/>`?

<details><summary>Model answer</summary>The directive pastes the file's <b>text</b> into the page before it is compiled, so they become one servlet and share variables. The action <b>runs</b> the other page at request time and pastes its <b>output</b>; it is a separate servlet that can't see the page's variables, but it can receive values with <code>&lt;jsp:param&gt;</code>.</details>

**9.** Why is `<jsp:getProperty>` dangerous for a name that a user typed?

<details><summary>Model answer</summary>It prints the value without escaping. If the name is <code>&lt;script&gt;...&lt;/script&gt;</code>, the browser runs it (XSS, topic 13). Escape user text before printing, or use <code>&lt;c:out&gt;</code> from topic 12.</details>

**10.** In the Contact Book, `edit.jsp` puts the contact in the **request** scope before forwarding to `form.jsp`. Why request, and not session?

<details><summary>Model answer</summary>The contact is only needed for this one request (to show the form). The request scope travels with the forward and disappears afterwards. The session would keep it for the whole visit and mix it up with the next edit.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Show "Last saved: NAME" in the footer, using the **session** scope.

<details><summary>Hint</summary>In <code>save.jsp</code>, after a successful save: <code>session.setAttribute("lastSaved", contact.getName());</code>. In <code>footer.jsp</code>: read it with <code>session.getAttribute("lastSaved")</code>; it can be <code>null</code>. Escape it.</details>

**12.** Add a **class** field to `Contact` (for example "SS1"). Which files must change, and which don't?

<details><summary>Answer</summary>Change <code>Contact.java</code> (field, getter, setter, and <code>copy()</code>), <code>form.jsp</code> (an input named <code>className</code>), and <code>index.jsp</code> (show it). <code>save.jsp</code> does <b>not</b> change: <code>property="*"</code> fills the new property automatically because the input name matches.</details>

**13.** Make a page `/count.jsp` that shows only "There are N contacts", using `<jsp:useBean>` and **no** `getAttribute` call.

<details><summary>Hint</summary><code>&lt;jsp:useBean id="book" class="com.school.project.ContactBook" scope="application"/&gt;</code> then <code>&lt;%= book.getAll().size() %&gt;</code>.</details>

**14.** Open `/boom.jsp` in the **example** app. Then remove `errorPage=...` from it, restart, and open it again. What changes?

<details><summary>Answer</summary>With <code>errorPage</code>, you see the friendly error page showing <code>java.lang.ArithmeticException</code>. Without it, you see Tomcat's grey "HTTP Status 500" page with a stack trace. Both answer with status 500.</details>
