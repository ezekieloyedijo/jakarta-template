package com.school.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * SERVLET + JSP WORKING TOGETHER.
 * The servlet does the Java work (prepares the data). The JSP does the HTML.
 * The servlet puts the data in the request ("attributes"), then FORWARDS to the JSP.
 */
@WebServlet("/timetable")
public class TimetableServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. the Java work
        String day = LocalDate.now().getDayOfWeek().toString();
        List<String> lessons = List.of("08:00 Mathematics", "09:00 English", "10:00 Break", "10:30 Physics");

        // 2. hand the results to the JSP
        request.setAttribute("day", day);
        request.setAttribute("lessons", lessons);

        // 3. let the JSP build the page. It lives in WEB-INF, so users can't open it directly.
        request.getRequestDispatcher("/WEB-INF/views/timetable.jsp").forward(request, response);
    }
}
