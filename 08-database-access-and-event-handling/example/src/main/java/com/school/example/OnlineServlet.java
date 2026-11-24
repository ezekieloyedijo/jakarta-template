package com.school.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Shows the counter kept by SessionCounterListener. "Leave" ends your session, so the counter goes down. */
@WebServlet("/online")
public class OnlineServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("leave") != null) {
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();                    // -> sessionDestroyed
            response.sendRedirect("online?left=1");
            return;
        }
        if (request.getParameter("left") == null) {
            request.getSession();                                 // -> sessionCreated (only the first time)
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(Db.page("Visitors online", """
                <p class="msg ok">Sessions open right now: <b>%d</b></p>
                <p>Open this page in another browser or a private window, then refresh: the number goes up.</p>
                <p><a class="btn danger" href="online?leave=1">Leave (end my session)</a>
                   <a class="btn secondary" href="online">Come back</a></p>
                <p>The terminal shows a <code>[SESSION]</code> line for every session created or destroyed.</p>
                """.formatted(SessionCounterListener.active())));
    }
}
