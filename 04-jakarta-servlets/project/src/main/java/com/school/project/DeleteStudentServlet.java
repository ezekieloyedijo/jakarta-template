package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * DELETE: POST /delete-student (id in a hidden field) -> remove the student; redirect to the list.
 *
 * There is deliberately NO doGet: deleting must never happen just by opening a link
 * (browsers and search engines open links on their own). Visiting /delete-student in the
 * address bar therefore gives 405 Method Not Allowed.
 */
public class DeleteStudentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = StudentForm.toWholeNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number");
            return;
        }
        if (!StudentStore.from(getServletContext()).delete(id)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no student with id " + id);
            return;
        }
        System.out.println("Deleted student #" + id);
        response.sendRedirect("students?msg=deleted");
    }
}
