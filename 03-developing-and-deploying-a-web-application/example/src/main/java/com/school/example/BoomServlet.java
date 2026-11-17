package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A servlet that crashes ON PURPOSE, to show the custom 500 error page from web.xml.
 * Look at the TERMINAL after visiting /boom: the full error (stack trace) is logged there.
 */
@WebServlet("/boom")
public class BoomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        throw new IllegalStateException("Boom! This crash is on purpose (topic 03 demo).");
    }
}
