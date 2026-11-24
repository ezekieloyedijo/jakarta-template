package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** CREATE: GET /books/add shows the form; POST /books/add checks it and INSERTs a row. */
@WebServlet("/books/add")
public class AddBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        show(request, response, BookForm.empty(), List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        BookForm form = BookForm.read(request);
        List<String> errors = form.validate();
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, form, errors);
            return;
        }
        int id = BookDao.from(getServletContext())
                .insert(form.getTitle(), form.getAuthor(), form.yearAsNumber(), form.copiesAsNumber());
        System.out.println("[DB] inserted book #" + id);
        response.sendRedirect(request.getContextPath() + "/books?msg=added");
    }

    private void show(HttpServletRequest request, HttpServletResponse response, BookForm form, List<String> errors)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Add a book",
                Page.form(request, "add", null, form, errors, "Save book")));
    }
}
