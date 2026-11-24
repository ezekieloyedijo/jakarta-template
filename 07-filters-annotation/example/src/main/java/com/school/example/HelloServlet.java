package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** A normal servlet. It has no idea the filters exist, but they still run around it. */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page(request.getContextPath(), "Hello", """
                <p class="msg ok">Hello from HelloServlet!</p>
                <p>Now look at the terminal where Tomcat runs. You'll see lines like:</p>
                <pre>[LOG] GET /hello
                [TIME] /hello took 1 ms</pre>
                <p>Those come from <code>LogFilter</code> and <code>TimingFilter</code>, not from this servlet.</p>
                """));
    }
}
