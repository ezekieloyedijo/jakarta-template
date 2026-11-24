package com.school.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * Asks the CONTAINER to describe itself and the work it already did for this request.
 * Visit http://localhost:8080/container-info and REFRESH several times:
 * the thread name changes, because the container uses different worker threads ("waiters").
 */
// Registered in WEB-INF/web.xml: <servlet> + <servlet-mapping> with the URL "/container-info"
public class ContainerInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext app = getServletContext();   // the container's view of our whole app

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html><head><title>Container info</title>");
        out.println("<link rel='stylesheet' href='css/style.css'></head><body>");
        out.println("<h1>What the container did for me</h1>");

        out.println("<h2>1. Who is the container?</h2><table>");
        row(out, "Server product", app.getServerInfo());
        row(out, "Servlet spec version it follows", app.getMajorVersion() + "." + app.getMinorVersion());
        row(out, "Java version", System.getProperty("java.version"));
        out.println("</table>");

        out.println("<h2>2. It read and split the raw HTTP request for me</h2><table>");
        row(out, "Method", request.getMethod());
        row(out, "Path", request.getRequestURI());
        row(out, "Query string", String.valueOf(request.getQueryString()));
        row(out, "Your browser (User-Agent header)", request.getHeader("User-Agent"));
        row(out, "All header names", String.join(", ", Collections.list(request.getHeaderNames())));
        out.println("</table>");

        out.println("<h2>3. It gave this request its own worker thread</h2><table>");
        row(out, "Thread serving you right now", Thread.currentThread().getName());
        out.println("</table><p>Refresh the page: the thread name may change. Many 'waiters' serve many customers.</p>");

        out.println("<p>Try adding <code>?color=blue&amp;size=10</code> to the URL and see the query string.</p>");
        out.println("<p><a href='./'>Back</a></p></body></html>");
    }

    private void row(PrintWriter out, String label, String value) {
        out.println("<tr><th>" + label + "</th><td>" + escape(value) + "</td></tr>");
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
