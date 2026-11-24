package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** DELETE: POST /books/delete (id=2) DELETEs the row. */
@WebServlet("/books/delete")
public class DeleteBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = BookForm.toNumber(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The id must be a number.");
            return;
        }
        if (!BookDao.from(getServletContext()).delete(id)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no book with id " + id + ".");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/books?msg=deleted");
    }
}
