package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * DYNAMIC page built from the user's input: /greet?name=Tolu
 *
 * Compare with MiniWebApp.greet() in topic 01: no manual query-string decoding,
 * no sendResponseHeaders, no path checking. The container did it.
 */
@WebServlet("/greet")
public class GreetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");   // already decoded: "Amaka%20Obi" -> "Amaka Obi"

        response.setContentType("text/html;charset=UTF-8");

        if (name == null || name.isBlank()) {
            response.setStatus(400); // 400 Bad Request: the client forgot something
            response.getWriter().println(Page.wrap("400 - Bad Request", "<p>Please tell me your name.</p>"));
            return;
        }

        response.getWriter().println(Page.wrap("Hello, " + Page.escape(name) + "!",
                "<p>This page was built just for you.</p>"));
    }
}
