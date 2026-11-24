package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Ends the session: everything stored in it is thrown away. This is what "Log out" does. */
@WebServlet("/session-end")
public class SessionEndServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);     // false = "don't create one if there is none"
        String text;
        if (session != null) {
            session.invalidate();
            text = "The session has ended. Its data is gone.";
        } else {
            text = "There was no session to end.";
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Html.page("Session ended", """
                <p class="msg ok">%s</p>
                <p><a href="session-counter">Back to the session counter</a> (it starts again at 1)</p>
                """.formatted(text)));
    }
}
