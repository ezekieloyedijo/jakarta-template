package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** UPDATE: GET /reports/edit?id=1 shows the filled form; POST saves it (the report goes back to PENDING). */
@WebServlet("/reports/edit")
public class EditReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Report r = find(request, response);
        if (r == null) return;
        show(request, response, r.getId(), r.getTitle(), r.getClassName(), String.valueOf(r.getStudents()), List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Report r = find(request, response);
        if (r == null) return;

        String title = AddReportServlet.trim(request.getParameter("title"));
        String className = AddReportServlet.trim(request.getParameter("className"));
        String students = AddReportServlet.trim(request.getParameter("students"));
        List<String> errors = ReportStore.validate(title, className, students);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, r.getId(), title, className, students, errors);
            return;
        }
        ReportStore.from(getServletContext()).update(r.getId(), title, className, ReportStore.toNumber(students));
        response.sendRedirect(request.getContextPath() + "/reports?msg=updated");
    }

    /** The report named by "id", or null after sending 400 / 404. Also used by View and Generate. */
    static Report find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = ReportStore.toNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number.");
            return null;
        }
        Report r = ReportStore.from(request.getServletContext()).findById(id);
        if (r == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no report with id " + id + ".");
        }
        return r;
    }

    private void show(HttpServletRequest request, HttpServletResponse response, int id, String title,
                      String className, String students, List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Edit report request",
                Page.form(request, "edit", id, title, className, students, errors, "Save changes")));
    }
}
