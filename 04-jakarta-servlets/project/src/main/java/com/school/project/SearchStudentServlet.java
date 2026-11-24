package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * SEARCH: GET /search?q=amaka shows the students whose name or class contains the text.
 *
 * Search uses GET (not POST) because it only READS data. The search words stay in the URL,
 * so you can refresh, bookmark, or share a search safely.
 */
public class SearchStudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String q = request.getParameter("q");
        q = (q == null) ? "" : q.trim();

        String results;
        if (q.isEmpty()) {
            results = "<p class='empty'>Type part of a name or a class (for example <b>SS1</b>) and press Search.</p>";
        } else {
            List<Student> found = StudentStore.from(getServletContext()).search(q);
            results = "<p>" + found.size() + " result(s) for <b>" + Page.escape(q) + "</b></p>" + Page.table(found);
        }

        String content = """
                <form class="search" action="search" method="get">
                  <input name="q" value="%s" placeholder="Name or class" autofocus>
                  <button class="btn" type="submit">Search</button>
                </form>
                %s
                """.formatted(Page.escape(q), results);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(getServletContext(), "Search students", content));
    }
}
