package com.school.project;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * DELETE ALL: POST /cart/clear ends the whole session with invalidate().
 * The cart (and anything else in the session) is thrown away, like "Log out".
 */
@WebServlet("/cart/clear")
public class ClearCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);    // don't create a session just to end it
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/cart?msg=cleared");
    }
}
