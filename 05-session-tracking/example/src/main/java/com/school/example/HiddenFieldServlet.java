package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * TECHNIQUE 1: HIDDEN FORM FIELDS.
 * A 2-step form. The answer from step 1 is carried to the end inside <input type="hidden">,
 * so the server never has to remember anything.
 */
@WebServlet("/hidden")
public class HiddenFieldServlet extends HttpServlet {

    // Step 1: ask for the name
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        show(response, """
                <form class="card" method="post" action="hidden">
                  <input type="hidden" name="step" value="2">
                  <label>Step 1 of 2: your name <input name="name" required></label>
                  <button class="btn">Next</button>
                </form>
                """);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String name = Html.escape(request.getParameter("name"));

        if ("2".equals(request.getParameter("step"))) {
            // Step 2: ask for the favourite subject, and CARRY the name in a hidden field
            show(response, """
                    <form class="card" method="post" action="hidden">
                      <input type="hidden" name="step" value="3">
                      <input type="hidden" name="name" value="%s">
                      <p>Hello <b>%s</b>! (Right-click → View Page Source to see the hidden field.)</p>
                      <label>Step 2 of 2: favourite subject <input name="subject" required></label>
                      <button class="btn">Finish</button>
                    </form>
                    """.formatted(name, name));
        } else {
            // The end: both values arrive together, although the user typed them on different pages
            show(response, "<p class='msg ok'><b>%s</b> likes <b>%s</b>.</p>"
                    .formatted(name, Html.escape(request.getParameter("subject"))));
        }
    }

    private void show(HttpServletResponse response, String body) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Hidden form fields", body));
    }
}
