package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** Shows the trail the two filters wrote on the way in. The "out" steps happen after this page is written. */
@WebServlet("/chain")
public class ChainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> trail = TrailFilter.trail(request);
        trail.add("ChainServlet runs");
        System.out.println("[TRAIL] ChainServlet runs");

        StringBuilder steps = new StringBuilder();
        for (String step : trail) {
            steps.append("<li>").append(step).append("</li>");
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page(request.getContextPath(), "The filter chain", """
                <p>The trail so far (written while the request travelled IN):</p>
                <ol class="demos">%s</ol>
                <p>The filters go <b>out</b> in the opposite order <i>after</i> this page is written.
                   Look at the terminal:</p>
                <pre>[TRAIL] first: in
                [TRAIL] second: in
                [TRAIL] ChainServlet runs
                [TRAIL] second: out
                [TRAIL] first: out</pre>
                <p>Swap the two <code>&lt;filter-mapping&gt;</code> blocks in <code>web.xml</code>, restart, and the order flips.</p>
                """.formatted(steps)));
    }
}
