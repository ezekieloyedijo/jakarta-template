package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** CREATE: GET /reports/add shows the form; POST saves a PENDING request. */
@WebServlet("/reports/add")
public class AddReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        show(request, response, "", "", "30", List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String title = trim(request.getParameter("title"));
        String className = trim(request.getParameter("className"));
        String students = trim(request.getParameter("students"));

        List<String> errors = ReportStore.validate(title, className, students);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, title, className, students, errors);
            return;
        }
        ReportStore.from(getServletContext()).add(title, className, ReportStore.toNumber(students));
        response.sendRedirect(request.getContextPath() + "/reports?msg=added");
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String title, String className,
                      String students, List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "New report request",
                Page.form(request, "add", null, title, className, students, errors, "Save request")));
    }

    static String trim(String text) {
        return text == null ? "" : text.trim();
    }
}
