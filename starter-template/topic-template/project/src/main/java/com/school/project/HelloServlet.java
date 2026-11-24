package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * A starter servlet, there to prove the app works.
 * Visit http://localhost:8080/hello
 * Replace it, or keep it and add your own servlets next to it.
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/hello"
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html>
                <html><head><meta charset="UTF-8"><title>Hello</title>
                <link rel="stylesheet" href="css/style.css"></head>
                <body>
                  <h1>It works! 🎉</h1>
                  <p>This page was written by <code>HelloServlet.java</code>, running inside Tomcat.</p>
                  <p><a href="./">Back</a></p>
                </body></html>
                """);
    }
}
