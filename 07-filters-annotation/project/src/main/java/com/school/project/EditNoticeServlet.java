package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * UPDATE:
 *   GET  /members/notices/edit?id=2 -> the form filled with notice 2
 *   POST /members/notices/edit      -> check, save the changes, redirect
 */
@WebServlet("/members/notices/edit")
public class EditNoticeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Notice notice = find(request, response);
        if (notice == null) return;                                // find() already sent 400 or 404
        show(request, response, notice.getId(), notice.getTitle(), notice.getBody(), List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Notice notice = find(request, response);
        if (notice == null) return;

        String title = AddNoticeServlet.trim(request.getParameter("title"));
        String body = AddNoticeServlet.trim(request.getParameter("body"));
        List<String> errors = NoticeStore.validate(title, body);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, notice.getId(), title, body, errors);
            return;
        }
        NoticeStore.from(getServletContext()).update(notice.getId(), title, body);
        response.sendRedirect(request.getContextPath() + "/members/notices?msg=updated");
    }

    /** The notice named by the "id" parameter. Sends 400 or 404 and returns null if there isn't one. */
    private Notice find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = NoticeStore.toId(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number.");
            return null;
        }
        Notice notice = NoticeStore.from(getServletContext()).findById(id);
        if (notice == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no notice with id " + id + ".");
        }
        return notice;
    }

    private void show(HttpServletRequest request, HttpServletResponse response, int id, String title, String body,
                      List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Edit notice",
                Page.form(request, "edit", id, title, body, errors, "Save changes")));
    }
}
