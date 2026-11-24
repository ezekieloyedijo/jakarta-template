package com.school.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A controller that puts a bean, a list, and a map in the request, then lets an EL view show them. */
@WebServlet("/el-basics")
public class ElBasicsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Maths", 78);
        scores.put("English", 64);
        scores.put("Biology", 55);

        request.setAttribute("pupil", new Pupil("Amaka Obi", 15, "SS1", true));
        request.setAttribute("subjects", List.of("Maths", "English", "Biology"));
        request.setAttribute("scores", scores);
        request.setAttribute("classmates", List.of(
                new Pupil("Tolu Adeyemi", 12, "JSS2", false),
                new Pupil("Chidi Okeke", 17, "SS3", true),
                new Pupil("Ngozi Eze", 14, "JSS3", false)));

        request.getRequestDispatcher("/WEB-INF/views/el-basics.jsp").forward(request, response);
    }
}
