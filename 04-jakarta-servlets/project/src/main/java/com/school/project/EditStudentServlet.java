package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * UPDATE:
 *   GET  /edit-student?id=3 -> show the form, already filled with student 3
 *   POST /edit-student      -> check the form; replace the student; redirect to the list
 * The id travels in the URL for GET, and in a hidden form field for POST.
 */
public class EditStudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = StudentForm.toWholeNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number, e.g. edit-student?id=1");
            return;
        }
        Student student = StudentStore.from(getServletContext()).findById(id);
        if (student == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no student with id " + id);
            return;
        }
        show(response, id, StudentForm.of(student), List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        // 1. READ
        Integer id = StudentForm.toWholeNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number");
            return;
        }
        StudentForm form = StudentForm.read(request);

        // 2. VALIDATE (the same rules as Add, because both use StudentForm)
        List<String> errors = form.validate(StudentForm.allowedClasses(getServletContext()));
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(response, id, form, errors);
            return;
        }

        // 3. SAVE: replace the old student with a new one that has the SAME id
        Student changed = new Student(id, form.getName(), form.getGender(), form.ageAsNumber(), form.getClassName());
        if (!StudentStore.from(getServletContext()).update(changed)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no student with id " + id);
            return;
        }
        System.out.println("Updated student #" + id + ": " + changed.getName());

        // 4. REDIRECT (Post/Redirect/Get)
        response.sendRedirect("students?msg=updated");
    }

    private void show(HttpServletResponse response, int id, StudentForm form, List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(getServletContext(), "Edit student #" + id,
                Page.form("edit-student", form, errors, StudentForm.allowedClasses(getServletContext()),
                        "Save changes", id)));
    }
}
