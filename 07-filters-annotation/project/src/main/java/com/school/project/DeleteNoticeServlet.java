package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** DELETE: POST /members/notices/delete (id=2) */
@WebServlet("/members/notices/delete")
public class DeleteNoticeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = NoticeStore.toId(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number.");
            return;
        }
        if (!NoticeStore.from(getServletContext()).delete(id)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no notice with id " + id + ".");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/members/notices?msg=deleted");
    }
}
