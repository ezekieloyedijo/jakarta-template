package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The home page.
 * The special pattern "" (empty string) means EXACTLY the root of the app: http://localhost:8080/
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL ""
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String owner = getServletContext().getInitParameter("ownerName");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Layout.page(getServletContext(), "Welcome!", """
                <p>Hi, I'm <b>%s</b>. This is my portfolio, a real Java web application.</p>
                <p>Have a look at <a href="projects">my projects</a> or <a href="contact">send me a message</a>.</p>
                """.formatted(owner)));
    }
}
