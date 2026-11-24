package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** DELETE: POST /reports/delete (id=1) */
@WebServlet("/reports/delete")
public class DeleteReportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Report r = EditReportServlet.find(request, response);
        if (r == null) return;
        ReportStore.from(getServletContext()).delete(r.getId());
        response.sendRedirect(request.getContextPath() + "/reports?msg=deleted");
    }
}
