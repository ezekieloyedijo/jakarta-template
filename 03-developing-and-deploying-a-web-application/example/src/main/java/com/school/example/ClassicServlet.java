package com.school.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * A servlet is just a Java class. Tomcat only knows about it because it is registered in WEB-INF/web.xml:
 *   <servlet>         = WHO   (nickname "classic" -> this class)
 *   <servlet-mapping> = WHERE (URL /classic -> nickname "classic")
 */
public class ClassicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html><html><head><link rel="stylesheet" href="css/style.css"></head><body>
                <h1>I am ClassicServlet</h1>
                <p>The container found me because <code>web.xml</code> gives my class a nickname
                   (<code>&lt;servlet&gt;</code>) and maps <code>/classic</code> to that nickname
                   (<code>&lt;servlet-mapping&gt;</code>).</p>
                <p><a href="./">Back</a></p></body></html>
                """);
    }
}
