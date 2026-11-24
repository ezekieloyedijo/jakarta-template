package com.school.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * C = CONTROLLER: receives the request, asks the MODEL, chooses the VIEW.
 * It writes no HTML at all.
 */
@WebServlet("/convert")
public class ConvertController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String input = request.getParameter("celsius");

        // 1. read the request
        if (input != null) {
            try {
                // 2. ask the model
                Temperature t = new Temperature(Double.parseDouble(input.trim()));
                // 3. put the result where the view can find it
                request.setAttribute("temperature", t);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Please type a number.");
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
            }
        }
        request.setAttribute("input", input == null ? "" : input);

        // 4. choose the view and forward. It is in WEB-INF, so users can only reach it through here.
        request.getRequestDispatcher("/WEB-INF/views/convert.jsp").forward(request, response);
    }
}
