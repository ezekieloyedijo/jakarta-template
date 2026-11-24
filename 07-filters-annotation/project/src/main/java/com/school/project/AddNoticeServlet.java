package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * CREATE:
 *   GET  /members/notices/add -> empty form
 *   POST /members/notices/add -> check, save (the author is the logged-in member), redirect
 * No setCharacterEncoding here: EncodingFilter already did it.
 */
@WebServlet("/members/notices/add")
public class AddNoticeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        show(request, response, "", "", List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = trim(request.getParameter("title"));
        String body = trim(request.getParameter("body"));

        List<String> errors = NoticeStore.validate(title, body);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, title, body, errors);
            return;
        }
        NoticeStore.from(getServletContext()).add(title, body, Page.user(request));
        response.sendRedirect(request.getContextPath() + "/members/notices?msg=added");
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String title, String body,
                      List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "New notice",
                Page.form(request, "add", null, title, body, errors, "Post notice")));
    }

    static String trim(String text) {
        return text == null ? "" : text.trim();
    }
}
