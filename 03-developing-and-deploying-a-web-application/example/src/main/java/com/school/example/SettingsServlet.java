package com.school.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Reads APP-WIDE settings (<context-param>) from web.xml.
 * Change the values in web.xml, restart, and the page changes. No Java change needed.
 */
@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext app = getServletContext();
        String school = app.getInitParameter("schoolName");
        String version = app.getInitParameter("appVersion");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html><html><head><link rel="stylesheet" href="css/style.css"></head><body>
                <h1>Settings from web.xml</h1>
                <table>
                  <tr><th>schoolName</th><td>%s</td></tr>
                  <tr><th>appVersion</th><td>%s</td></tr>
                  <tr><th>Context path of this app</th><td>"%s" (empty means the root "/")</td></tr>
                </table>
                <p><a href="./">Back</a></p></body></html>
                """.formatted(school, version, request.getContextPath()));
    }
}
