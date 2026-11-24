package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/projects"
public class ProjectsServlet extends HttpServlet {

    /** A tiny "record" to hold one project. (Plain Java 17 feature.) */
    record Project(String name, String description) {
    }

    private final List<Project> projects = List.of(
            new Project("Raw HTTP Server", "A server built with sockets, to see the HTTP language (topic 01)."),
            new Project("Mini Web App", "Home page, clock, and greeting in plain Java, then rebuilt with Jakarta (topic 02)."),
            new Project("My Portfolio", "This website! Packaged as a WAR and deployable to any server (topic 03).")
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder cards = new StringBuilder();
        for (Project p : projects) {
            cards.append("<div class='card'><h3>").append(p.name()).append("</h3><p>")
                    .append(p.description()).append("</p></div>");
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Layout.page(getServletContext(), "My projects", cards.toString()));
    }
}
