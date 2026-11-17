package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** DYNAMIC page: the time is calculated fresh for every request. */
@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.wrap("The time on the server is " + now,
                "<p>Refresh the page: this is <b>dynamic</b> content.</p>"));
    }
}
