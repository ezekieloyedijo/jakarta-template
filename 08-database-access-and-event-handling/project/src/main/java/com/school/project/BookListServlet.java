package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/** READ: GET /books lists every book (SELECT). Note: no SQL here, only DAO calls. */
@WebServlet("/books")
public class BookListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Book> books = BookDao.from(getServletContext()).findAll();

        String msg = switch (String.valueOf(request.getParameter("msg"))) {
            case "added" -> "<p class='msg ok'>Book added.</p>";
            case "updated" -> "<p class='msg ok'>Book updated.</p>";
            case "deleted" -> "<p class='msg ok'>Book deleted.</p>";
            default -> "";
        };

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Page.page(request, "All books", msg
                + "<p class='toolbar'><span>" + books.size() + " book(s)</span>"
                + "<a class='btn' href='" + request.getContextPath() + "/books/add'>➕ Add book</a></p>"
                + Page.table(request, books)));
    }
}
