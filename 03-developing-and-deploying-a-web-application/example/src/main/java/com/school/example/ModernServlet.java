package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Registered with an ANNOTATION. The container finds it by scanning WEB-INF/classes at deploy time. */
@WebServlet("/modern")
public class ModernServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html><html><head><link rel="stylesheet" href="css/style.css"></head><body>
                <h1>I am ModernServlet</h1>
                <p>My address is written right on my class: <code>@WebServlet("/modern")</code>.
                   No XML needed.</p>
                <p><a href="./">Back</a></p></body></html>
                """);
    }
}
