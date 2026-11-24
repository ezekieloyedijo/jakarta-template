package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** A deliberately slow servlet (about half a second), so TimingFilter has something to measure. */
@WebServlet("/slow")
public class SlowServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page(request.getContextPath(), "A slow page",
                "<p class='msg ok'>Done. The terminal shows <code>[TIME] /slow took 50x ms</code>.</p>"));
    }
}
