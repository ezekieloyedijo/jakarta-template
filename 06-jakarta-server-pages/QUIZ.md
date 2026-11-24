# QUIZ 06: Jakarta Server Pages

## Part A: Quick check

**1.** Match each tag to its name: `<%= %>`, `<% %>`, `<%! %>`, `<%@ %>`, `<%-- --%>`.

<details><summary>Answer</summary><code>&lt;%= %&gt;</code> expression, <code>&lt;% %&gt;</code> scriptlet, <code>&lt;%! %&gt;</code> declaration, <code>&lt;%@ %&gt;</code> directive, <code>&lt;%-- --%&gt;</code> JSP comment.</details>

**2.** What is wrong with `<%= student.getName(); %>`?

<details><summary>Answer</summary>An expression must <b>not</b> end with a semicolon. It becomes <code>out.print(student.getName(););</code>, which doesn't compile.</details>

**3.** Where do you put a `.jsp` file so a browser can open it directly? And where so it can't?

<details><summary>Answer</summary>Directly: in <code>src/main/webapp/</code>. Private: inside <code>src/main/webapp/WEB-INF/</code>, where it can only be reached through a forward or an include.</details>

**4.** The first visit to a JSP is slower than the next ones. Why?

<details><summary>Answer</summary>The first time, Tomcat <b>translates</b> the JSP into a Java servlet and <b>compiles</b> it. After that it only runs the compiled class.</details>

**5.** Which comment can a visitor read with View Page Source: `<!-- a -->` or `<%-- b --%>`?

<details><summary>Answer</summary><code>&lt;!-- a --&gt;</code>, the HTML comment. The JSP comment is removed on the server.</details>

**6.** Which import is correct?
a) `<%@ import java.util.List %>`  b) `<%@ page import="java.util.List" %>`  c) `<% import java.util.List; %>`

<details><summary>Answer</summary>b)</details>

**7.** In the Grade Book, `add.jsp` calls `response.sendRedirect(...)` and then `return;`. What would go wrong without `return`?

<details><summary>Answer</summary>The rest of the page (the header and the form) would still run and try to write a page after the redirect. The <code>return</code> stops <code>_jspService</code> straight away.</details>

## Part B: Explain in your own words

**8.** "A JSP is a servlet in disguise." Explain this sentence.

<details><summary>Model answer</summary>Tomcat turns every JSP into a Java class that extends a servlet. The HTML becomes <code>out.write(...)</code> calls, and the Java in scriptlets and expressions is copied into the <code>_jspService</code> method. So a JSP is just an easier way to write a servlet that mostly prints HTML.</details>

**9.** Why are fields made with `<%! %>` dangerous? Use an example.

<details><summary>Model answer</summary>There is only one servlet object for the JSP, so a field is shared by all users. If <code>&lt;%! String user; %&gt;</code> stores the logged-in name, user B can see user A's name. Use local variables in <code>&lt;% %&gt;</code>, or the session.</details>

**10.** Why does the Grade Book keep its rules (`validate`, `gradeFor`) in `GradeBook.java` instead of in the JSPs?

<details><summary>Model answer</summary>Java in a normal class is easier to read, reuse, and test. Two pages (add and edit) need the same rules, so they live in one place. The JSPs stay mostly HTML, which is the job JSP is good at.</details>

## Part C: Practice tasks (do them in `project/`)

**11.** Show "🎉 Excellent!" under the table when the average is 70 or more, and "Keep working!" when it is below 50.

<details><summary>Hint</summary>In <code>index.jsp</code>, after the table: <code>&lt;% if (book.average() &gt;= 70) { %&gt; ... &lt;% } else if (book.average() &lt; 50) { %&gt; ... &lt;% } %&gt;</code></details>

**12.** Add a **teacher's comment** field to each subject (text, at most 100 characters, optional).

<details><summary>Hint</summary>Add a <code>comment</code> field to <code>Subject</code>; add a parameter to <code>add</code> and <code>update</code>; add an input to <code>subject-form.jspf</code>; give <code>add.jsp</code> and <code>edit.jsp</code> a <code>String comment</code> variable; add a column in <code>index.jsp</code>. Escape it!</details>

**13.** Put "Grade Book, <current year>" in the footer of every page by changing **one file**.

<details><summary>Answer</summary>Edit <code>WEB-INF/fragments/footer.jspf</code>: <code>Grade Book, &lt;%= java.time.Year.now() %&gt;</code>. Every page includes it, so all of them change.</details>

**14.** Break something on purpose: delete one `<% } %>` in `index.jsp` and open the page. Read the error. Which line does it point to? Put it back.

<details><summary>Answer</summary>You get a <b>500</b> with "Unable to compile class for JSP" and a syntax error such as "reached end of file while parsing". It points to the generated Java file and the JSP line near the problem. This shows that the JSP really is compiled into Java.</details>
