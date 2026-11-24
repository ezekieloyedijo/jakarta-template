package com.school.project;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * CREATE:
 *   GET  /add-student -> show an empty form
 *   POST /add-student -> check the form; save the student; redirect to the list
 */
public class AddStudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        show(response, StudentForm.empty(), List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");                 // before reading any parameter

        // 1. READ what the user typed
        StudentForm form = StudentForm.read(request);

        // 2. VALIDATE: if anything is wrong, show the form again with the errors (status 400)
        List<String> errors = form.validate(StudentForm.allowedClasses(getServletContext()));
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(response, form, errors);
            return;
        }

        // 3. SAVE
        Student saved = StudentStore.from(getServletContext())
                .add(form.getName(), form.getGender(), form.ageAsNumber(), form.getClassName());
        System.out.println("Added student #" + saved.getId() + ": " + saved.getName());

        // 4. REDIRECT (Post/Redirect/Get): pressing F5 on the list page won't add the student twice
        response.sendRedirect("students?msg=added");
    }

    private void show(HttpServletResponse response, StudentForm form, List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(getServletContext(), "Add a student",
                Page.form("add-student", form, errors, StudentForm.allowedClasses(getServletContext()),
                        "Save student", null)));
    }
}
