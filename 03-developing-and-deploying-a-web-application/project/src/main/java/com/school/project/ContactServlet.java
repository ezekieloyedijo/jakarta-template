package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * ONE servlet, TWO jobs:
 *   GET  /contact -> show the empty form
 *   POST /contact -> receive the filled form and say thank you
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/contact"
public class ContactServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Layout.page(getServletContext(), "Contact me", """
                <form action="contact" method="post">
                  <label>Your name <input name="name" required></label>
                  <label>Your message <textarea name="message" rows="4" required></textarea></label>
                  <button type="submit">Send</button>
                </form>
                """));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String message = request.getParameter("message");

        // A real app would save this or email it. We just write it to the server log (the terminal).
        System.out.println("NEW CONTACT MESSAGE from " + name + ": " + message);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Layout.page(getServletContext(), "Thank you!", """
                <p>Thanks <b>%s</b>, I received your message:</p>
                <blockquote>%s</blockquote>
                <p><a href="./">Back home</a></p>
                """.formatted(Layout.escape(name), Layout.escape(message))));
    }
}
