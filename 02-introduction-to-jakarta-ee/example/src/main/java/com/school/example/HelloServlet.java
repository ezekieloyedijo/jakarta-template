package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The smallest useful Jakarta web component.
 *
 * There is NO main() method. The CONTAINER (Tomcat):
 *   - finds this class when the app starts (because of @WebServlet)
 *   - creates ONE object of it
 *   - calls doGet(...) every time someone visits http://localhost:8080/hello
 *
 * "Don't call us, we'll call you."
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("""
                <!DOCTYPE html>
                <html><head><title>Hello</title><link rel="stylesheet" href="css/style.css"></head>
                <body>
                  <h1>Hello from my first Jakarta Servlet! 👋</h1>
                  <p>I didn't open a port, read raw text, or write a status line.
                     The <b>container</b> did all of that. I only wrote these few lines.</p>
                  <p><a href="./">Back</a></p>
                </body></html>
                """);
    }
}
