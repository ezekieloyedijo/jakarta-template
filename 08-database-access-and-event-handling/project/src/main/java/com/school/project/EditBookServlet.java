package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** UPDATE: GET /books/edit?id=2 shows the filled form; POST /books/edit UPDATEs the row. */
@WebServlet("/books/edit")
public class EditBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Book book = find(request, response);
        if (book == null) return;
        show(request, response, book.getId(), BookForm.of(book), List.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Book book = find(request, response);
        if (book == null) return;

        BookForm form = BookForm.read(request);
        List<String> errors = form.validate();
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            show(request, response, book.getId(), form, errors);
            return;
        }
        BookDao.from(getServletContext()).update(book.getId(), form.getTitle(), form.getAuthor(),
                form.yearAsNumber(), form.copiesAsNumber());
        response.sendRedirect(request.getContextPath() + "/books?msg=updated");
    }

    /** The book named by "id", or null after sending 400 (not a number) or 404 (no such book). */
    private Book find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = BookForm.toNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number.");
            return null;
        }
        Book book = BookDao.from(getServletContext()).findById(id);
        if (book == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no book with id " + id + ".");
        }
        return book;
    }

    private void show(HttpServletRequest request, HttpServletResponse response, int id, BookForm form,
                      List<String> errors) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "Edit book",
                Page.form(request, "edit", id, form, errors, "Save changes")));
    }
}
