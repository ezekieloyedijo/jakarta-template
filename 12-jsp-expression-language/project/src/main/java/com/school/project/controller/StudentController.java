package com.school.project.controller;

import com.school.project.model.Student;
import com.school.project.model.StudentDao;
import com.school.project.model.StudentForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * C = THE CONTROLLER: ONE servlet is the "traffic officer" for every student page.
 * The "action" parameter says what the user wants:
 *
 *   GET  /students                      -> list   (also ?q=... to search)
 *   GET  /students?action=new           -> empty form
 *   GET  /students?action=edit&id=2     -> filled form
 *   POST /students  action=insert       -> create
 *   POST /students  action=update       -> update
 *   POST /students  action=delete       -> delete
 *
 * For every request it: 1) reads the request, 2) asks the MODEL, 3) puts results in request attributes,
 * 4) FORWARDS to a VIEW (a JSP in WEB-INF), or REDIRECTS after a change. It never writes HTML.
 */
@WebServlet("/students")
public class StudentController extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/views/student-list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/student-form.jsp";

    private StudentDao dao;

    @Override
    public void init() {
        dao = (StudentDao) getServletContext().getAttribute(AppListener.DAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list" -> list(request, response);
            case "new" -> showForm(request, response, StudentForm.empty(), null, List.of());
            case "edit" -> showEditForm(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = String.valueOf(request.getParameter("action"));

        switch (action) {
            case "insert" -> insert(request, response);
            case "update" -> update(request, response);
            case "delete" -> delete(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
        }
    }

    // ---------- READ ----------

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        boolean searching = q != null && !q.isBlank();
        List<Student> students = searching ? dao.search(q.trim()) : dao.findAll();

        request.setAttribute("students", students);
        request.setAttribute("q", searching ? q.trim() : "");
        request.setAttribute("message", message(request.getParameter("msg")));
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    // ---------- CREATE ----------

    private void insert(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StudentForm form = read(request);
        List<String> errors = form.validate();
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            showForm(request, response, form, null, errors);
            return;
        }
        dao.insert(form.getName(), form.getGender(), form.ageAsNumber(), form.getClassName());
        response.sendRedirect("students?msg=added");
    }

    // ---------- UPDATE ----------

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Student s = find(request, response);
        if (s == null) return;
        showForm(request, response, StudentForm.of(s), s.getId(), List.of());
    }

    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Student s = find(request, response);
        if (s == null) return;
        StudentForm form = read(request);
        List<String> errors = form.validate();
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            showForm(request, response, form, s.getId(), errors);
            return;
        }
        dao.update(s.getId(), form.getName(), form.getGender(), form.ageAsNumber(), form.getClassName());
        response.sendRedirect("students?msg=updated");
    }

    // ---------- DELETE ----------

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Student s = find(request, response);
        if (s == null) return;
        dao.delete(s.getId());
        response.sendRedirect("students?msg=deleted");
    }

    // ---------- helpers ----------

    /** Hand everything the form view needs to it, then forward. id is null for a new student. */
    private void showForm(HttpServletRequest request, HttpServletResponse response, StudentForm form, Integer id,
                          List<String> errors) throws ServletException, IOException {
        request.setAttribute("form", form);
        request.setAttribute("id", id);
        request.setAttribute("errors", errors);
        request.setAttribute("classes", StudentForm.CLASSES);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private StudentForm read(HttpServletRequest request) {
        return new StudentForm(request.getParameter("name"), request.getParameter("gender"),
                request.getParameter("age"), request.getParameter("className"));
    }

    /** The student named by "id", or null after sending 400 (not a number) or 404 (not found). */
    private Student find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = StudentForm.toNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number.");
            return null;
        }
        Student s = dao.findById(id);
        if (s == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no student with id " + id + ".");
        }
        return s;
    }

    private String message(String code) {
        return switch (String.valueOf(code)) {
            case "added" -> "Student added.";
            case "updated" -> "Student updated.";
            case "deleted" -> "Student deleted.";
            default -> null;
        };
    }
}
